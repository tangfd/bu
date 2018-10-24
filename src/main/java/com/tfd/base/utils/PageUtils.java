package com.tfd.base.utils;

/**
 * 分页对象的工具类
 *
 * @since TangFD@HF 2018/2/5
 */
public class PageUtils {

    /**
     * 分页对象的校验（是否为空，是否自动分页，是否设置合理的首页）
     *
     * @param page 需要校验的分页对象
     */
    public static void validPage(Page<?> page) {
        if (page == null) {
            throw new IllegalArgumentException("Page must be not null!");
        }

        if (page.isAutoPaging()) {
            int pageSize = page.getPageSize();
            if (pageSize < 1) {
                throw new IllegalArgumentException("Page size[" + pageSize + "] must bigger than 1!");
            }

            int first = page.getFirst();
            if (first < 1) {
                throw new IllegalArgumentException("First page[" + first + "] must bigger than 1!");
            }
        }
    }
}
