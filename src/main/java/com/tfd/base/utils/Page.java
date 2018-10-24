package com.tfd.base.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * <p>
 * 注意所有序号从1开始.
 *
 * @param <T> Page中记录的类型.
 */
public class Page<T> {
    //-- 公共变量 --//
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    protected String id;

    //-- 分页参数 --//
    protected int pageNo = 1;
    protected int pageSize = 10;
    protected boolean autoCount = true;
    /**
     * 是否自动分页
     */
    protected boolean autoPaging = true;

    /**
     * 排序字段名称
     */
    private String sortName;
    /**
     * 排序方向
     */
    private String sortOrder;

    //-- 返回结果 --//
    protected List<T> rows = new ArrayList<T>();
    protected long total = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //-- 构造函数 --//
    public Page() {
    }

    public Page(int pageSize) {
        this.pageSize = pageSize;
    }

    //-- 分页参数访问函数 --//

    /**
     * 获得当前页的页号,序号从1开始,默认为1.
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
     */
    public void setPageNo(final int pageNo) {
        this.pageNo = pageNo;

        if (pageNo < 1) {
            this.pageNo = 1;
        }
    }

    /**
     * 获得每页的记录数量, 默认为10.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数量.
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
     */
    public int getFirst() {
        return ((pageNo - 1) * pageSize) + 1;
    }

    /**
     * 获得查询对象时是否先自动执行count查询获取总记录数, 默认为false.
     */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * 设置查询对象时是否自动先执行count查询获取总记录数.
     */
    public void setAutoCount(final boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * 返回Page对象自身的setAutoCount函数,可用于连续设置。
     */
    public Page<T> autoCount(final boolean theAutoCount) {
        setAutoCount(theAutoCount);
        return this;
    }

    //-- 访问查询结果函数 --//

    /**
     * 获得页内的记录列表.
     */
    public List<T> getRows() {
        return rows;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setRows(final List<T> result) {
        this.rows = result;
    }

    /**
     * 获得总记录数, 默认值为0.
     */
    public long getTotal() {
        return total;
    }

    /**
     * 设置总记录数.
     */
    public void setTotal(final long totalCount) {
        this.total = totalCount;
    }

    /**
     * 根据pageSize与totalCount计算总页数, 默认值为-1.
     */
    public long getTotalPages() {
        long count = total / pageSize;
        if (total % pageSize > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean isHasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 取得下页的页号, 序号从1开始.
     * 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        if (isHasNext()) {
            return pageNo + 1;
        } else {
            return pageNo;
        }
    }

    /**
     * 是否还有上一页.
     */
    public boolean isHasPre() {
        return (pageNo - 1 >= 1);
    }

    /**
     * 取得上页的页号, 序号从1开始.
     * 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        if (isHasPre()) {
            return pageNo - 1;
        } else {
            return pageNo;
        }
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isAutoPaging() {
        return autoPaging;
    }

    public void setAutoPaging(boolean autoPaging) {
        this.autoPaging = autoPaging;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", autoCount=" + autoCount +
                ", sortName='" + sortName + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}
