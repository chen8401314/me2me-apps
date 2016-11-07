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
}
