package com.xdzn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xdzn.common.BusinessException;
import com.xdzn.common.excel.ExcelService;
import com.xdzn.mapper.FinanceRecordMapper;
import com.xdzn.mapper.UserMapper;
import com.xdzn.model.dto.FinanceExcelRow;
import com.xdzn.model.dto.FinanceRecordDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.entity.FinanceRecord;
import com.xdzn.model.entity.User;
import com.xdzn.model.vo.FinanceRecordVO;
import com.xdzn.model.vo.FinanceSummaryVO;
import com.xdzn.service.FinanceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FinanceServiceImpl
 * <p>
 * 经费管理服务实现。
 * <p>
 * 余额统计方式：实时聚合 {@code SUM(income) - SUM(expense)}，
 * 通过一次 {@code selectMaps + GROUP BY type} 查询得出，空表归零。
 * <p>
 * 缓存联动：写操作方法命名 create/update/delete 前缀，
 * 命中 {@link com.xdzn.aspect.CacheInvalidationAspect}，记账后自动清空看板缓存，
 * 使看板经费余额实时刷新（配合 {@code @Cacheable("dashboard")}）。
 *
 * @author xdzn
 */
@Service
public class FinanceServiceImpl implements FinanceService {

    /**
     * 经费记录表 Mapper
     */
    private final FinanceRecordMapper financeMapper;

    /**
     * 用户表 Mapper（查操作人姓名）
     */
    private final UserMapper userMapper;

    /**
     * 通用 Excel 导出服务
     */
    private final ExcelService excelService;

    /**
     * 构造注入依赖
     *
     * @param financeMapper 经费记录表 Mapper
     * @param userMapper    用户表 Mapper
     * @param excelService  通用 Excel 导出服务
     */
    public FinanceServiceImpl(FinanceRecordMapper financeMapper, UserMapper userMapper, ExcelService excelService) {
        this.financeMapper = financeMapper;
        this.userMapper = userMapper;
        this.excelService = excelService;
    }

    /**
     * 记账（进账或支出），操作人取当前登录用户
     *
     * @param dto 收支 DTO
     * @return 创建后的记录视图
     */
    @Override
    @Transactional
    public FinanceRecordVO create(FinanceRecordDto dto) {
        FinanceRecord record = new FinanceRecord();
        BeanUtils.copyProperties(dto, record);
        record.setOperatorId(StpUtil.getLoginIdAsLong());
        if (record.getOccurredAt() == null) {
            record.setOccurredAt(LocalDateTime.now());
        }
        financeMapper.insert(record);
        return toVO(financeMapper.selectById(record.getId()));
    }

    /**
     * 修改收支记录：校验存在性；occurredAt 为空时保留原时间（避免覆盖）；
     * operatorId 不参与更新，保持首次记账人。
     *
     * @param id  记录 id
     * @param dto 收支 DTO
     * @return 修改后的记录视图
     */
    @Override
    @Transactional
    public FinanceRecordVO update(Long id, FinanceRecordDto dto) {
        FinanceRecord old = financeMapper.selectById(id);
        if (old == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "经费记录不存在");
        }
        FinanceRecord record = new FinanceRecord();
        record.setId(id);
        BeanUtils.copyProperties(dto, record);
        if (record.getOccurredAt() == null) {
            record.setOccurredAt(old.getOccurredAt());
        }
        financeMapper.updateById(record);
        return toVO(financeMapper.selectById(id));
    }

    /**
     * 删除收支记录（物理删除，校验存在性）
     *
     * @param id 记录 id
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (financeMapper.selectById(id) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "经费记录不存在");
        }
        financeMapper.deleteById(id);
    }

    /**
     * 根据 id 查询记录详情
     *
     * @param id 记录 id
     * @return 记录视图；不存在时返回 null
     */
    @Override
    public FinanceRecordVO findById(Long id) {
        FinanceRecord record = financeMapper.selectById(id);
        return record == null ? null : toVO(record);
    }

    /**
     * 分页查询收支明细（按类型等值 / 分类模糊筛选，按发生时间倒序）
     *
     * @param current  当前页码
     * @param size     每页大小
     * @param type     收支类型（可选）
     * @param category 分类（可选）
     * @return 分页结果
     */
    @Override
    public PageResult<FinanceRecordVO> findAllByPage(long current, long size, String type, String category) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isBlank()) {
            wrapper.eq(FinanceRecord::getType, type);
        }
        if (category != null && !category.isBlank()) {
            wrapper.like(FinanceRecord::getCategory, category);
        }
        wrapper.orderByDesc(FinanceRecord::getOccurredAt);

        Page<FinanceRecord> result = financeMapper.selectPage(new Page<>(current, size), wrapper);
        List<FinanceRecordVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), result.getPages(), voList);
    }

    /**
     * 实时汇总经费：一次 GROUP BY type 聚合出进账/支出总额，余额 = 进账 - 支出。
     * 空表时 GROUP BY 返回 0 行，三项均归零（不返回 null）。
     * 同时计算本月收支与按分类汇总。
     *
     * @return 汇总视图
     */
    @Override
    public FinanceSummaryVO summary() {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        // 1. 累计总额（按 type 分组）
        List<Map<String, Object>> rows = financeMapper.selectMaps(
                new QueryWrapper<FinanceRecord>()
                        .select("type", "COALESCE(SUM(amount), 0) AS total")
                        .groupBy("type"));
        for (Map<String, Object> row : rows) {
            Object type = row.get("type");
            Object total = row.get("total");
            if (type == null || total == null) {
                continue;
            }
            BigDecimal amount = new BigDecimal(String.valueOf(total));
            if ("income".equals(type)) {
                income = amount;
            } else if ("expense".equals(type)) {
                expense = amount;
            }
        }

        // 2. 本月收支（按本月初过滤）
        LocalDateTime monthStart = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        BigDecimal monthlyIncome = BigDecimal.ZERO;
        BigDecimal monthlyExpense = BigDecimal.ZERO;

        List<Map<String, Object>> monthRows = financeMapper.selectMaps(
                new QueryWrapper<FinanceRecord>()
                        .select("type", "COALESCE(SUM(amount), 0) AS total")
                        .ge("occurred_at", monthStart)
                        .groupBy("type"));
        for (Map<String, Object> row : monthRows) {
            Object type = row.get("type");
            Object total = row.get("total");
            if (type == null || total == null) {
                continue;
            }
            BigDecimal amount = new BigDecimal(String.valueOf(total));
            if ("income".equals(type)) {
                monthlyIncome = amount;
            } else if ("expense".equals(type)) {
                monthlyExpense = amount;
            }
        }

        // 3. 按分类汇总（按 category + type 分组）
        List<Map<String, Object>> catRows = financeMapper.selectMaps(
                new QueryWrapper<FinanceRecord>()
                        .select("category", "type", "COALESCE(SUM(amount), 0) AS total")
                        .groupBy("category", "type"));
        List<FinanceSummaryVO.CategorySummary> categorySummaries = new ArrayList<>();
        for (Map<String, Object> row : catRows) {
            Object category = row.get("category");
            Object type = row.get("type");
            Object total = row.get("total");
            if (category == null || type == null || total == null) {
                continue;
            }
            FinanceSummaryVO.CategorySummary cs = new FinanceSummaryVO.CategorySummary();
            cs.setCategory(String.valueOf(category));
            cs.setType(String.valueOf(type));
            cs.setTotalAmount(new BigDecimal(String.valueOf(total)));
            categorySummaries.add(cs);
        }

        FinanceSummaryVO vo = new FinanceSummaryVO();
        vo.setTotalIncome(income);
        vo.setTotalExpense(expense);
        vo.setBalance(income.subtract(expense));
        vo.setMonthlyIncome(monthlyIncome);
        vo.setMonthlyExpense(monthlyExpense);
        vo.setCategorySummaries(categorySummaries);
        return vo;
    }

    /**
     * 导出经费收支明细到 Excel（支持筛选条件，导出全部匹配记录）
     *
     * @param response HTTP 响应
     * @param type     收支类型（可选）
     * @param category 分类（可选）
     */
    @Override
    public void export(HttpServletResponse response, String type, String category) {
        List<FinanceRecord> records = financeMapper.selectList(buildWrapper(type, category));
        List<FinanceExcelRow> rows = records.stream().map(this::toExcelRow).collect(Collectors.toList());
        excelService.export(response, rows, FinanceExcelRow.class, "经费明细", "经费收支明细");
    }

    /**
     * 构建经费明细查询条件（列表与导出共用）
     *
     * @param type     收支类型（可选）
     * @param category 分类（可选）
     * @return 查询条件
     */
    private LambdaQueryWrapper<FinanceRecord> buildWrapper(String type, String category) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isBlank()) {
            wrapper.eq(FinanceRecord::getType, type);
        }
        if (category != null && !category.isBlank()) {
            wrapper.like(FinanceRecord::getCategory, category);
        }
        wrapper.orderByDesc(FinanceRecord::getOccurredAt);
        return wrapper;
    }

    /**
     * 经费记录转导出行
     *
     * @param record 经费记录实体
     * @return 导出行
     */
    private FinanceExcelRow toExcelRow(FinanceRecord record) {
        FinanceExcelRow row = new FinanceExcelRow();
        row.setOccurredAt(record.getOccurredAt() != null ? record.getOccurredAt().toString() : "");
        row.setType("income".equals(record.getType()) ? "进账" : "支出");
        row.setCategory(record.getCategory());
        row.setAmount(record.getAmount());
        row.setDescription(record.getDescription());
        if (record.getOperatorId() != null) {
            User operator = userMapper.selectById(record.getOperatorId());
            row.setOperatorName(operator != null ? operator.getName() : "");
        }
        return row;
    }

    // ── 内部方法 ──────────────────────

    /**
     * 将经费记录实体转换为视图对象（含操作人姓名）
     *
     * @param record 经费记录实体
     * @return 经费记录视图
     */
    private FinanceRecordVO toVO(FinanceRecord record) {
        FinanceRecordVO vo = new FinanceRecordVO();
        BeanUtils.copyProperties(record, vo);
        if (record.getOperatorId() != null) {
            User operator = userMapper.selectById(record.getOperatorId());
            vo.setOperatorName(operator != null ? operator.getName() : null);
        }
        return vo;
    }
}
