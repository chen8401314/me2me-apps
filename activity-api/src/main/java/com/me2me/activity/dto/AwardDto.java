package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by Administrator on 2016/10/15.
 */
@Data
public class AwardDto implements BaseEntity {

		/**编号*/
		public int id;
		/**概率（0.1代表10%，最多3位小数，即千分之一级）*/
		public float probability;
		/**数量（该类奖品剩余数量）*/
		public int count;
		/**抽奖次数(数据库读取)*/
		private int awardCount;
		//所有奖品剩余数量
		private String prizeNumber;

		private String avatar;

		private String nickName;
		//中奖物品名称
		private String awardName;

		private String proof;
		//me号
		private String me_number;

		public AwardDto(int id, float probability, int count) {
			super();
			this.id = id;
			this.probability = probability;
			this.count = count;
		}

		public AwardDto(){

		}

}
