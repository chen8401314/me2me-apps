package com.me2me.ml.core.type;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;

/**
 * 类型映射管理器。
 * 
 * @author zhangjiwei
 * @date May 22, 2017
 */
public class TypeMgr {

	private List<TypeMapper> mapperList;
	private Map<Integer, TypeMapper> indexMapping;
	private Map<String, TypeMapper> nameMapping;

	private TypeMgr(List<TypeMapper> mappers) {
		this.mapperList = mappers;
		this.indexMapping=new HashMap<>();
		this.nameMapping=new HashMap<>();
		for(TypeMapper mapper:mapperList){
			this.indexMapping.put(mapper.getIndex(), mapper);
			this.nameMapping.put(mapper.getName(), mapper);
		}
	}
	/**
	 * 按分类索引号返回分类信息
	 * @author zhangjiwei
	 * @date May 22, 2017
	 * @param index
	 * @return
	 */
	public TypeMapper getTypeByIndex(int index){
		return this.indexMapping.get(index);
	}
	/**
	 *  按分类名称返回分类信息。
	 * @author zhangjiwei
	 * @date May 22, 2017
	 * @param name
	 * @return
	 */
	public TypeMapper getTypeByName(String name){
		return this.nameMapping.get(name);
	}
	
	public static TypeMgr loadTypes(InputStream file) {
		try {
			String text = IOUtils.toString(file, "utf-8");
			List<TypeMapper> mapperList = JSON.parseArray(text, TypeMapper.class);
			return new TypeMgr(mapperList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
