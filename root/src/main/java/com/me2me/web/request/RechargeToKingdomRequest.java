package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/12 0012.
 */
@Data
public class RechargeToKingdomRequest extends Request {


   private int coin;

   private long topicId;

}
