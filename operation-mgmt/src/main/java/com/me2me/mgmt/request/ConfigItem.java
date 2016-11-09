package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class ConfigItem {

	@Getter
	@Setter
	private String key;
	@Getter
	@Setter
	private String value;
	@Getter
	@Setter
	private String desc;
	@Getter
	@Setter
	private ConfigType type;
	
	public ConfigItem(String key, String desc, ConfigType type, String value){
		this.key = key;
		this.value = value;
		this.desc = desc;
		this.type = type;
	}
	
	public ConfigItem(String key, String desc, ConfigType type){
		this.key = key;
		this.desc = desc;
		this.type = type;
	}
	
	public enum ConfigType{
		DB(0, "数据库"),
		STRING(1,"字符串"),
		SET(2,"Set集合"),
		LIST(3, "List集合"),
		MAP(4, "Map集合");
		
		private final int type;
		private final String desc;
		
		private ConfigType(int type, String desc) {
	        this.type = type;
	        this.desc = desc;
	    }
		
		public static ConfigType getByType(int type){
			for(ConfigType ct : ConfigType.values()){
				if(ct.getType() == type){
					return ct;
				}
			}
			return null;
		}

		public int getType() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
	}
	
	public enum DBConfig{
		DEFAULT_FOLLOW("default_follow", "默认关注，多个以逗号分隔"),
		READ_COUNT_START("read_count_start", "阅读随机数开始;"),
		READ_COUNT_END("read_count_end", "阅读随机数结束 如果开始时1，则结束想要啥就设置啥，如果开始是非1，则结束时想要的值减1");
		
		private final String key;
		private final String desc;
		
		private DBConfig(String key, String desc){
			this.key = key;
			this.desc = desc;
		}
		
		public static DBConfig getByKey(String key){
			for(DBConfig c : DBConfig.values()){
				if(c.getKey().equals(key)){
					return c;
				}
			}
			return null;
		}

		public String getKey() {
			return key;
		}

		public String getDesc() {
			return desc;
		}
	}
}
