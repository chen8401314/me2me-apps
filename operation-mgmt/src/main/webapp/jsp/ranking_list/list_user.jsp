<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 串门语言管理</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
</head>
<body>
	<section id="container" class="container">
		<div class="panel panel-default">
			<div class="panel-body">
				<form class="form form-inline" method="get" action="" id="search_form">
					<p>
						<div class="form-group">
							<lable class="control-label">用户名</lable>
							<input type="text" name="nick_name" class="form-control"/>
						</div>
						<div class="form-group">
							<lable class="control-label">注册时间</lable>
							<input type="text" name="create_time_min" class="form-control date"/>-
							<input type="text" name="create_time_max" class="form-control date"/>
						</div>
						<div class="form-group">
							<lable class="control-label">大V</lable>
							<select name="v_lv" class="form-control">
								<option value="">全部</option>
								<option value="0">否</option>
								<option value="1">是</option>
							</select>
						</div>
					</p>
					<p>
						<div class="form-group">
							<lable class="control-label">发布王国数量</lable>
							<input type="text" name="kingdomCount_min" class="form-control" style="width:50px;"/>-
							<input type="text" name="kingdomCount_max" class="form-control" style="width:50px;"/>
						</div>
						<div class="form-group">
							<lable class="control-label">关注别人数量</lable>
							<input type="text" name="focusCount_min" class="form-control" style="width:50px;"/>-
							<input type="text" name="focusCount_max" class="form-control" style="width:50px;"/>
						</div>
						<div class="form-group">
							<lable class="control-label">粉丝数量</lable>
							<input type="text" name="fansCount_min" class="form-control" style="width:50px;"/>-
							<input type="text" name="fansCount_max" class="form-control" style="width:50px;"/>
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-primary">
								<i  class=" fa fa-search "></i> 搜索
							</button>
						</div>
					</p>
				</form>
				<button type="button" class="btn btn-danger" onclick="addBatch()">
					<i  class=" fa fa-plus "></i> 批量加入
				</button>
			</div>
		</div>
		<div class="adv-table">
			<table class="display table table-bordered table-striped" id="mytable" width="100%">
			</table>
		</div>
	</section>
	<!-- js placed at the end of the document so the pages load faster -->
	<script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
	<script src="${ctx}/js/jquery.scrollTo.min.js"></script>
	<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/jquery.json-2.4.min.js"></script>
	<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
	<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript">
	$.fn.datetimepicker.dates['zh'] = {  
            days:       ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"],  
            daysShort:  ["日", "一", "二", "三", "四", "五", "六","日"],  
            daysMin:    ["日", "一", "二", "三", "四", "五", "六","日"],  
            months:     ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月","十二月"],  
            monthsShort:  ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],  
            meridiem:    ["上午", "下午"],  
            //suffix:      ["st", "nd", "rd", "th"],  
            today:       "今天"  
    };
	$('.date').datetimepicker({
		format: 'yyyy-mm-dd',
		language: 'zh',
		startView: 2,
		maxView:3,
		language:"zh",
		minView:2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1
	});
	Date.prototype.Format = function(fmt)   
	{ //author: meizz   
	  var o = {   
	    "M+" : this.getMonth()+1,                 //月份   
	    "d+" : this.getDate(),                    //日   
	    "h+" : this.getHours(),                   //小时   
	    "m+" : this.getMinutes(),                 //分   
	    "s+" : this.getSeconds(),                 //秒   
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
	    "S"  : this.getMilliseconds()             //毫秒   
	  };   
	  if(/(y+)/.test(fmt))   
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	  for(var k in o)   
	    if(new RegExp("("+ k +")").test(fmt))   
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	  return fmt;   
	}  
	$.fn.dataTable.ext.errMode="console";
	$.extend( $.fn.dataTable.defaults, {
		pageLength: 10,
		searching: false,
        dom: 'tp',
	    processing: true,
	    serverSide: true,
    	language: {
    		processing:     "加载中...",
    	    search:         "搜索中&nbsp;:",
    	    "lengthMenu":     "每页显示 _MENU_ 条",
    	    "info":           "当前 _START_ 到 _END_ 条，共 _TOTAL_ 条",
    	    "infoEmpty":      "当前第0条",
    	    "infoFiltered":   "(filtered from _MAX_ total entries)",
    	    infoPostFix:    "",
    	    loadingRecords: "加载数据中",
    	    zeroRecords:    "没有数据",
    	    emptyTable:     "没有数据",
    	    paginate: {
    	        first:      "第一页",
    	        previous:   "上一页",
    	        next:       "下一页",
    	        last:       "最后一页"
    	    },
    	    aria: {
    	        sortAscending:  "升序",
    	        sortDescending: "降序"
    	    },
    	    decimal: ","
        }
	} );
	
	
	var sourceTable=$('#mytable').DataTable( {
	    "ajax":"./ajaxLoadUsers",
	    "columns": [
			{title:"<button class='btn btn-warning btn-xs'>全/反选</button> ",width:50,orderable:false,render:function(data, type, row, meta){
				var txt= "<input type='checkbox'/> ";
				return txt;
			}},
	        {data: "userProfile.uid",title: "用户ID",style:"num"},
	        {data: "userProfile.nickName",title: "用户名",style:"num"},
	        {data: "userProfile.createTime",title: "注册时间",render:function(data,type,row,meta){
	        	if(row.userProfile!=null){
	        		return new Date(row.userProfile.createTime).Format("yyyy-MM-dd hh:mm:ss");
	        	}
	        }},
	        {data: "userProfile.vLv",title: "大V",render:function(data,type,row,meta){
	        	//console.log(data)
	        	if(data!=null){
		        	var map ={0:"否",1:"是"};
		        	return map[data];
	        	}
	        }},
	        {data: "kingdomCount",title: "发布王国数量",style:"num"},
	        {data: "focusCount",title: "关注数量",style:"num"},
	        {data: "fansCount",title: "粉丝数量",style:"num"},
	        {title:"操作",width:60,render:function(data, type, row, meta){
	        	var txt= "<a href='#stop' title='uid:"+row.userProfile.uid+"' class='btn btn-danger btn-xs btnAdd'>加入</a> ";
	        	return txt;
	        }}
	     ]
	});
	$("#search_form").on("submit",function(){
		var data= $(this).serialize();
		var url = "./ajaxLoadUsers?"+data;
		sourceTable.ajax.url(url).load();
		return false;
	})
	$("#mytable").on("click","th:eq(0)",function(){
		$("input[type='checkbox']").each(function(){
			if($(this).attr("checked")!=null){
				$(this).removeAttr("checked")
			}else{
				$(this).attr("checked","checked")
			}
		})
	})
	$(document).on("click",".btnAdd",function(){		// 父窗口添加选中用户。
		var tr = $(this).closest("tr");
		var data =sourceTable.row(tr).data();
		
		parent.onAdd([data.userProfile.uid]);
	})
	function addBatch(){
		var dataArr=[]
		$("#mytable input:checked").each(function(){
			var tr = $(this).closest("tr");
			var data =sourceTable.row(tr).data()
			dataArr.push(data.userProfile.uid);
		})
		parent.onAdd(dataArr);
	}
	</script>
</body>
</html>