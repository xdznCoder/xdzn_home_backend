package com.xdzn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xdzn.model.entity.FinanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * FinanceRecordMapper
 * <p>
 * 经费收支记录表 Mapper。
 * 基础 CRUD 由 MyBatis-Plus {@link BaseMapper} 提供；
 * 余额聚合（SUM income/expense）在 Service 层通过 {@code selectMaps} + GROUP BY 实现。
 *
 * @author xdzn
 */
@Mapper
public interface FinanceRecordMapper extends BaseMapper<FinanceRecord> {
}
