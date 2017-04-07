
package com.me2me.search.dto;

import java.io.Serializable;


public class QueryBase
    implements Serializable
{

    public QueryBase()
    {
        totalSum = new Float( 0);
        needQeryTotal = false;
        needDelete = false;
        needQueryAll = false;
    }

    public Float getTotalSum()
    {
        return totalSum;
    }

    public void setTotalSum(Float cent)
    {
        totalSum=cent;
    }

    protected final Integer getDefaultPageSize()
    {
        return defaultPageSize;
    }

    public Integer getCurrentPage()
    {
        if(currentPage == null)
            return defaultFriatPage;
        else
            return currentPage;
    }

    public void setCurrentPage(Integer cPage)
    {
        if(cPage == null || cPage.intValue() <= 0)
            currentPage = defaultFriatPage;
        else
            currentPage = cPage;
    }

    public Integer getPageSize()
    {
        if(pageSize == null)
            return getDefaultPageSize();
        else
            return pageSize;
    }

    public boolean hasSetPageSize()
    {
        return pageSize != null;
    }

    public void setPageSize(Integer pSize)
    {
        if(pSize == null)
            throw new IllegalArgumentException("PageSize can't be null.");
        if(pSize.intValue() <= 0)
        {
            throw new IllegalArgumentException("PageSize must great than zero.");
        } else
        {
            pageSize = pSize;
            return;
        }
    }

    public Long getTotalItem()
    {
        if(totalItem == null)
            return defaultTotleItem;
        else
            return totalItem;
    }

    public void setTotalItem(Long tItem)
    {
        if(tItem == null)
            tItem = new Long(0);
        totalItem = tItem;
    }

    public int getTotalPage()
    {
        int pgSize = getPageSize().intValue();
        int total = getTotalItem().intValue();
        int result = total / pgSize;
        if(total % pgSize != 0)
            result++;
        return result;
    }

    public int getPageFristItem()
    {
        if(!needQeryTotal)
        {
            int cPage = getCurrentPage().intValue();
            if(cPage == 1)
            {
                return 1;
            } else
            {
                cPage--;
                int pgSize = getPageSize().intValue();
                return pgSize * cPage + 1;
            }
        } else
        {
            return 0;
        }
    }

    public int getPageLastItem()
    {
        if(!needQeryTotal)
        {
            int cPage = getCurrentPage().intValue();
            int pgSize = getPageSize().intValue();
            int assumeLast = pgSize * cPage;
            int totalItem = getTotalItem().intValue();
            if(assumeLast > totalItem)
                return totalItem;
            else
                return assumeLast;
        } else
        {
            return getTotalItem().intValue();
        }
    }

    public int getEndRow()
    {
        return endRow;
    }

    public void setEndRow(int endRow)
    {
        this.endRow = endRow;
    }

    public int getStartRow()
    {
        return startRow;
    }
    
    public int calPageStart(int row) {
        int pgSize = getPageSize().intValue();
        int total = row;
        int result = total / pgSize;
        if(total % pgSize != 0)
            result++;
        return result;
    }
    
    public int calStartRow() {
        int p = this.currentPage == null ? 0 : this.currentPage;
        if(p > 0) {
            return this.pageSize * (p - 1);
        }else
            return 0;
    }
    
    public int calEndRow() {

        int p = this.currentPage == null ? 0 : this.currentPage;
        if(p > 0) {
            int last = p * this.pageSize;
            return (int) (totalItem != null && last > totalItem ? totalItem : last);
        }else
            return this.pageSize;
    }

    public void setStartRow(int startRow)
    {
        this.startRow = startRow;
    }

    protected String getSQLBlurValue(String value)
    {
        if(value == null)
            return null;
        else
            return (new StringBuilder(String.valueOf(value))).append('%').toString();
    }

    public boolean isNeedQeryTotal()
    {
        return needQeryTotal;
    }

    public void setNeedQeryTotal(boolean needQeryTotal)
    {
        this.needQeryTotal = needQeryTotal;
    }

    public boolean isNeedDelete()
    {
        return needDelete;
    }

    public void setNeedDelete(boolean needDelete)
    {
        this.needDelete = needDelete;
    }

    public boolean isNeedQueryAll()
    {
        return needQueryAll;
    }

    public void setNeedQueryAll(boolean needQueryAll)
    {
        this.needQueryAll = needQueryAll;
    }

    public void copyProperties(QueryBase k)
    {
        if(k == null)
            return;
        k.setCurrentPage(currentPage);
        k.setEndRow(endRow);
        k.setNeedDelete(needDelete);
        k.setNeedQeryTotal(needQeryTotal);
        k.setNeedQueryAll(needQueryAll);
        k.setStartRow(startRow);
        k.setTotalItem(totalItem);
        k.setTotalSum(k.getTotalSum());
    }

    private static final long serialVersionUID = 0x7570ab2517f4b46dL;
    private static final Integer defaultPageSize = new Integer(20);
    private static final Integer defaultFriatPage = new Integer(1);
    private static final Long defaultTotleItem = new Long(0);
    private Long totalItem;
    private Integer pageSize;
    private Integer currentPage;
    private float totalSum;
    private int startRow;
    private int endRow;
    private boolean needQeryTotal;
    private boolean needDelete;
    private boolean needQueryAll;

}
