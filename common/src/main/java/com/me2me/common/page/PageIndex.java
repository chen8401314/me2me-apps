package com.me2me.common.page;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/30.
 */
@Data
public class PageIndex {

    /**
     * 页码起始
     */
    private long startIndex;

    /**
     * 页码结束
     */
    private long endIndex;


    public PageIndex(long currentPage,int viewPageCount,long totalPage){
        long startPage = currentPage-(viewPageCount%2==0? viewPageCount/2-1 : viewPageCount/2);
        long endPage = currentPage+viewPageCount/2;
        if(startPage<1){
            startPage = 1;
            if(totalPage>=viewPageCount) endPage = viewPageCount;
            else endPage = totalPage;
        }
        if(endPage>totalPage){
            endPage = totalPage;
            if((endPage-viewPageCount)>0) startPage = endPage-viewPageCount+1;
            else startPage = 1;
        }
        this.startIndex = startPage;
        this.endIndex = endPage;
    }


}
