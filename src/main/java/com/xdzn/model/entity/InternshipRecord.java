package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * InternshipRecord
 * <p>
 * 实习记录实体，对应数据库表 {@code internship_records}。
 * 与成员为一对多关系（{@code member_id} 关联 members），
 * 每条实习单独存储（公司/岗位/起止时间/描述）。
 *
 * @author xdzn
 */
@Data
@TableName("internship_records")
public class InternshipRecord {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 成员 ID（关联 members）
     */
    private Long memberId;

    /**
     * 实习公司
     */
    private String company;

    /**
     * 岗位
     */
    private String position;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 实习描述/成果
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
