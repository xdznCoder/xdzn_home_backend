package com.xdzn.service;

import com.xdzn.model.dto.FinanceRecordDto;
import com.xdzn.model.dto.PageResult;
import com.xdzn.model.vo.FinanceRecordVO;
import com.xdzn.model.vo.FinanceSummaryVO;

/**
 * FinanceService
 * <p>
 * 经费管理服务接口，定义收支明细的记账（进账/支出）、修改、删除、
 * 明细分页查询与余额实时聚合的契约。
 * <p>
 * 权限约定（配合 Sa-Token 路由层）：
 * <ul>
 *     <li>create / update / delete：仅 admin（全局写操作规则校验）</li>
 *     <li>分页 / 汇总 / 详情：登录成员（member）可读</li>
 * </ul>
 *
 * @author xdzn
 */
public interface FinanceService {

    /**
     * 记账（进账或支出）
     *
     * @param dto 收支 DTO（type/amount/category/description/occurredAt）
     * @return 创建后的记录视图
     */
    FinanceRecordVO create(FinanceRecordDto dto);

    /**
     * 修改收支记录
     *
     * @param id  记录 id
     * @param dto 收支 DTO
     * @return 修改后的记录视图
     */
    FinanceRecordVO update(Long id, FinanceRecordDto dto);

    /**
     * 删除收支记录
     *
     * @param id 记录 id
     */
    void delete(Long id);

    /**
     * 根据 id 查询记录详情
     *
     * @param id 记录 id
     * @return 记录视图；不存在时返回 null
     */
    FinanceRecordVO findById(Long id);

    /**
     * 分页查询收支明细（可按类型/分类筛选，按发生时间倒序）
     *
     * @param current  当前页码
     * @param size     每页大小
     * @param type     收支类型（income/expense，可选）
     * @param category 分类（可选，模糊匹配）
     * @return 分页结果
     */
    PageResult<FinanceRecordVO> findAllByPage(long current, long size, String type, String category);

    /**
     * 实时汇总经费（累计进账/累计支出/余额）
     *
     * @return 汇总视图
     */
    FinanceSummaryVO summary();
}
