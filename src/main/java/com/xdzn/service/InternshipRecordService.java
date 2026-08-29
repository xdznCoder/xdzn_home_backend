package com.xdzn.service;

import com.xdzn.model.dto.InternshipRecordDto;
import com.xdzn.model.vo.InternshipRecordVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * InternshipRecordService
 * <p>
 * 实习记录服务接口（成员一对多实习，支持导出 Excel）。
 *
 * @author xdzn
 */
public interface InternshipRecordService {

    /**
     * 查询成员的全部实习记录
     *
     * @param memberId 成员 id
     * @return 实习记录列表
     */
    List<InternshipRecordVO> listByMember(Long memberId);

    /**
     * 筛选查询实习记录（全量，可按成员/公司筛选）
     *
     * @param memberId 成员 id（可选）
     * @param company  公司名（可选，模糊）
     * @return 实习记录列表
     */
    List<InternshipRecordVO> list(Long memberId, String company);

    /**
     * 新增实习记录
     *
     * @param dto 实习 DTO
     * @return 创建后的实习记录
     */
    InternshipRecordVO create(InternshipRecordDto dto);

    /**
     * 修改实习记录
     *
     * @param id  实习 id
     * @param dto 实习 DTO
     * @return 修改后的实习记录
     */
    InternshipRecordVO update(Long id, InternshipRecordDto dto);

    /**
     * 删除实习记录
     *
     * @param id 实习 id
     */
    void delete(Long id);

    /**
     * 导出实习记录 Excel
     *
     * @param response HTTP 响应
     * @param memberId 成员 id（可选）
     * @param company  公司（可选，模糊）
     */
    void export(HttpServletResponse response, Long memberId, String company);
}
