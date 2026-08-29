package com.xdzn.service;

import com.xdzn.model.dto.AwardRecordDto;
import com.xdzn.model.vo.AwardRecordVO;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AwardRecordService
 * <p>
 * 获奖记录服务接口（成员一对多获奖，支持按时间范围/获奖人/比赛导出 Excel）。
 *
 * @author xdzn
 */
public interface AwardRecordService {

    /**
     * 查询成员的全部获奖记录
     *
     * @param memberId 成员 id
     * @return 获奖记录列表
     */
    List<AwardRecordVO> listByMember(Long memberId);

    /**
     * 分页/筛选查询获奖记录（全量，可按获奖人/比赛筛选）
     *
     * @param memberId    获奖人成员 id（可选）
     * @param competition 比赛名（可选，模糊）
     * @return 获奖记录列表
     */
    List<AwardRecordVO> list(Long memberId, String competition);

    /**
     * 新增获奖记录
     *
     * @param dto 获奖 DTO
     * @return 创建后的获奖记录
     */
    AwardRecordVO create(AwardRecordDto dto);

    /**
     * 修改获奖记录
     *
     * @param id  获奖 id
     * @param dto 获奖 DTO
     * @return 修改后的获奖记录
     */
    AwardRecordVO update(Long id, AwardRecordDto dto);

    /**
     * 删除获奖记录
     *
     * @param id 获奖 id
     */
    void delete(Long id);

    /**
     * 导出获奖记录 Excel（按时间范围/获奖人/比赛筛选）
     *
     * @param response   HTTP 响应
     * @param startTime  开始时间（可选）
     * @param endTime    结束时间（可选）
     * @param memberId   获奖人成员 id（可选）
     * @param competition 比赛名（可选，模糊）
     */
    void export(HttpServletResponse response, LocalDateTime startTime, LocalDateTime endTime,
                Long memberId, String competition);
}
