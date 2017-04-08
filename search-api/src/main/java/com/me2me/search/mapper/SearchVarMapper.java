package com.me2me.search.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 搜索变量表。
 * @author zhangjiwei
 * @date Apr 7, 2017
 */
public interface SearchVarMapper {
   
   @Select("select strKey,strVal from search_vars")
   public Map<String,String> getAllVars();
   
   @Select("select strVal from search_vars where strKey=#{0}")
   public String getVar(String key);
   
   @Insert("insert into search_vars(strKey,strVal) values(#{0},#{1})")
   public void addVar(String key,String value);

   @Update("update search_vars set strVal=#{1} where strKey=#{0}")
   public void updateVar(String key,String value);
   
   @Select("select count(1) from search_vars where strKey=#{0}")
   public boolean existsVar(String key);
   
   @Delete("delete from search_vars where strKey = ${0}")
   public void deleteByKey(String key);
}