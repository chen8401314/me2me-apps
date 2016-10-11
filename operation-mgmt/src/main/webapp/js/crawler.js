//var serverUrl="http://192.168.88.166:8080/crawler-server/findIndexDetailsToFlatlab.do";
var serverUrl="http://mibao.me-to-me.com/findIndexDetailsToFlatlab.do";
var userListNum=5;//用户top5
var stationListNum=5;//站点top5
var ajax_userNum;
var ajax_onlUserNum;
var ajax_countNum;
var ajax_todayCountNum;
var ajax_sameMonthCountNum;
var ajax_userList;
var ajax_stationList;
var ajax_yearList;
var ajax_monthList;
var ajax_dayList;
var ajax_week;
var ajax_month;
var ajax_days;

 $.ajax({
           type: "get",
           url: serverUrl,
           cache : false,
           async : false,
           dataType: 'json',           
           success: function(data){          		
             
              var jsonlst = eval(data);
              ajax_userNum=jsonlst.userNum;
							ajax_onlUserNum=jsonlst.onlUserNum;
							ajax_countNum=jsonlst.countNum;
							ajax_todayCountNum=jsonlst.todayCountNum;
							ajax_sameMonthCountNum=jsonlst.sameMonthCountNum;
							ajax_userList=jsonlst.userList;
							ajax_stationList=jsonlst.stationList;
							ajax_yearList=jsonlst.yearList;
							ajax_monthList=jsonlst.monthList;
							ajax_dayList=jsonlst.dayList;
							ajax_week=jsonlst.week;
							ajax_month=jsonlst.month;
							ajax_days=jsonlst.days ;
							      
            
           },            
            error:function(XMLHttpRequest, textStatus, errorThrown){ //获取失败失败 
               //alert(XMLHttpRequest.status);
               //alert(XMLHttpRequest.readyState);
               //alert(textStatus);
            }
        });
        
//今日内容量
var  tmptodaychart = $("#todaychart")[0].outerHTML;
var  ajaxSparklineData='data-data="['+ajax_dayList+']"';
tmptodaychart=tmptodaychart.replace('data-data=""',ajaxSparklineData);
$("#todaychart")[0].outerHTML=tmptodaychart;

$("#ajweek")[0].innerText=ajax_week;
$("#ajdaysTotal")[0].innerText=ajax_todayCountNum;
$("#ajmonth")[0].innerText=ajax_month;
$("#ajdays")[0].innerText=ajax_days;;
$("#ajmonthTotal")[0].innerText=ajax_sameMonthCountNum;
if(ajax_yearList != undefined){
	for ( var i = 0; i < ajax_yearList.length; i++) {
		  var  yearArrays = ajax_yearList[i].split('_');
			var  tmpajyear = $("#ajyear"+(i+1))[0].outerHTML;
			var  ajyearData='data-original-title="'+yearArrays[0]+'"';
			tmpajyear=tmpajyear.replace('data-original-title=""',ajyearData);
			$("#ajyear"+(i+1))[0].outerHTML=tmpajyear;
			var yearNum= yearArrays[1].substr(0,yearArrays[1].length-1);
			if(yearNum>100){
				$("#ajyear"+(i+1))[0].innerText='100%';
			}else{
				$("#ajyear"+(i+1))[0].innerText=yearArrays[1];
			}
		  
	}
}
if(ajax_userList != undefined){
	for ( var i = 0; i < ajax_userList.length; i++) {
			if(i>=userListNum){
					break;
				}
		  var  userArrays = ajax_userList[i].split('_');
			$("#ajtopuser"+(i+1))[0].innerText=userArrays[0];
		  $("#ajtopusernum"+(i+1))[0].innerText=userArrays[1];
	}
}
if(ajax_stationList != undefined){
	for ( var i = 0; i < ajax_stationList.length; i++) {
			if(i>=stationListNum){
				break;
			}
		  var  stationArrays = ajax_stationList[i].split('_');
			$("#ajtopcont"+(i+1))[0].innerText=stationArrays[0];
		  $("#ajtopcontnum"+(i+1))[0].innerText=stationArrays[1];
	}
}