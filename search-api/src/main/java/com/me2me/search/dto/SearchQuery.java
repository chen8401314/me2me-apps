package com.me2me.search.dto;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 * @author carl
 *
 */
public class SearchQuery extends QueryBase {

    private static final long serialVersionUID = 2807231889569337275L;
    /** 关键词 */
    private String keywords;
    private List<HighlightField> hightlightFields = new ArrayList<HighlightField>();
    private List<QueryOrder> orders =new ArrayList<QueryOrder>();		//  排序。
    private List<String> loadFieldList =new ArrayList<String>();		// 待返回的字段列表，避免全量返回。
    /**
     * 索引名
     */
    private String index;
    
    /**
     * 类型
     */
    private String typeName;
    
    /**
     * ES通用查询表达式
     * 格式一：[+|-|>|>=|<|<=]column:value;[+|-|>|>=|<|<=]column:value;...
     * 解释：+开头表示该字段为must
     *       -开头表示该字段为must not
     *       >开头表示该字段值需大于值
     *       >=开头表示该字段值需大于等于值
     *       <开头表示该字段值需大于值
     *       <=开头表示该字段值需大于等于值
     *       没有符号开头表示该字段为should
     *       column：字段名
     *       value：字段查询值
     */
    private String esExpression;

    public String getEsExpression() {
        return esExpression;
    }
    /**
     * ES通用查询表达式
     * 格式一：[+|-|>|>=|<|<=]column:value;[+|-|>|>=|<|<=]column:value;...
     * 解释：+开头表示该字段为must
     *       -开头表示该字段为must not
     *       >开头表示该字段值需大于值
     *       >=开头表示该字段值需大于等于值
     *       <开头表示该字段值需大于值
     *       <=开头表示该字段值需大于等于值
     *       没有符号开头表示该字段为should
     *       column：字段名
     *       value：字段查询值
     */
    public void setEsExpression(String esExpression) {
        this.esExpression = esExpression;
    }
    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }
    
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public List<HighlightField> getHightlightFields() {
		return hightlightFields;
	}
	public void setHightlightFields(List<HighlightField> hightlightFields) {
		this.hightlightFields = hightlightFields;
	}
	/**
	 * 添加高亮字段
	 * @param field
	 */
	public void addHightlightField(HighlightField field){
		this.hightlightFields.add(field);
	}
	/**
	 * 添加升序字段
	 * @param fieldName
	 * @return
	 */
	public void addAscOrder(String fieldName){
		this.orders.add(new QueryOrder(fieldName,true));
	}
	/**
	 * 添加降序字段
	 * @param fieldName
	 * @return
	 */
	public void addDescOrder(String fieldName){
		this.orders.add(new QueryOrder(fieldName,false));
	}
	public List<QueryOrder> getOrders() {
		return orders;
	}
	public List<String> getLoadFieldList() {
		return loadFieldList;
	}

	public void setLoadFieldList(List<String> loadFieldList) {
		this.loadFieldList = loadFieldList;
	}
	public void setOrders(List<QueryOrder> orders) {
		this.orders = orders;
	}
	
}
