package com.me2me.common.page;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/30.
 */
@Data
public class Page<T> {

    /**
     * 总记录条数
     */
    private long totalRecord;

    /**
     * 总页数
     */
    private long totalPage;

    /**
     * 当前页
     */
    private int currentPage = 1;
    /**
     * 每页显示记录数
     */
    private int pageSize = 20;

    /**
     * 每页显示页码数量
     */
    private int pageNumber = 5;

    /**
     * 页码索引
     */
    private PageIndex pageIndex;

    /**
     * 结果集
     */
    private List<T> result = Lists.newArrayList();

    public Page(long totalRecord,int pageSize,int currentPage){
        this.totalRecord = totalRecord;
        this.pageSize = pageSize;
        this.totalPage = (totalRecord + pageSize -1)/pageSize;
        this.currentPage = currentPage;
        pageIndex = new PageIndex(this.getCurrentPage(),this.getPageNumber(),totalPage);
    }
}
