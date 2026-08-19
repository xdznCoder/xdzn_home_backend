package com.xdzn.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FinanceRecord
 * <p>
 * 经费收支记录实体，对应数据库表 {@code finance_records}。
 * 每行代表一笔进账（income）或支出（expense），类似微信零钱明细：
 * 含方向（type）、金额、分类、说明、发生时间与操作人。
 *
 * @author xdzn
 */
@Data
@TableName("finance_records")
public class FinanceRecord {

    /**
     * 主键，雪花算法自动生成
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 收支类型：income（进账）/ expense（支出）
     */
    private String type;

    /**
     * 金额（元，保留两位小数）
     */
    private BigDecimal amount;

    /**
     * 分类（如 团费/报销/赞助/物资/活动）
     */
    private String category;

    /**
     * 说明
     */
    private String description;

    /**
     * 操作人 id（关联 users，记账/修改人）
     */
    private Long operatorId;

    /**
     * 发生时间（交易时间，缺省时由服务层补当前时间）
     */
    private LocalDateTime occurredAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
