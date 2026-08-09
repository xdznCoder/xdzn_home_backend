package com.xdzn.model.dto;

import lombok.Data;

import java.util.List;

/**
 * PageResult
 * <p>
 * 统一分页响应对象，包装分页查询结果。
 *
 * @param <T> 数据类型
 * @author xdzn
 */
@Data
public class PageResult<T> {

    /**
     * 当前页码（从1开始）
     */
    private long current;

    /**
     * 每页大小
     */
    private long size;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 构造分页结果
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param total   总记录数
     * @param pages   总页数
     * @param records 数据列表
     */
    public PageResult(long current, long size, long total, long pages, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = pages;
        this.records = records;
    }

    /**
     * 空构造方法
     */
    public PageResult() {
    }
}