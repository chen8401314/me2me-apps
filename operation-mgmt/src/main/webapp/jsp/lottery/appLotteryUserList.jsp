<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 王国抽奖用户列表</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>

</head>
<body>
	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="12" />
			<jsp:param name="s" value="12_12" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<!-- page start-->
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								查询条件&nbsp;&nbsp;&nbsp;&nbsp;
							</header>
							<div class="panel-body">
								<div class="form-inline" role="form">
									用户名称： 
									<input type="text" id="nickName" name="nickName" value="" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
									是否指定：
									<select name="appoint" id="appoint" class="form-control">
										<option value="0">全部</option>
										<option value="1">是</option>
										<option value="2">否</option>
									</select>&nbsp;&nbsp;&nbsp;&nbsp;
									<a class="btn btn-primary" href="javascript:searchLotteryUser();">搜索</a>
								</div>
							</div>
						</section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								抽奖参与用户列表
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table" width="100%">
										
									</table>
								</div>
							</div>
						</section>
					</div>
				</div>
				<!-- page end-->
				<span class="btn btn-default"><a href="${ctx}/appLottery/list?kingdomName=${param.kingdomName }&lotteryName=${param.lotteryName }">返回</a></span>
			</section>
		</section>
		<!--main content end-->

		<!-- Right Slidebar start -->
		<%@include file="../common/rightSlidebar.jsp"%>
		<!-- Right Slidebar end -->

		<!--footer start-->
		<%@include file="../common/footer.jsp"%>
		<!--footer end-->
	</section>
	<!-- js placed at the end of the document so the pages load faster -->
	<script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
	<script src="${ctx}/js/jquery.scrollTo.min.js"></script>
	<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
	<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
	<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
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
		pageLength: 30,
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
	
	var sourceTable=$('#table').DataTable({
		"ajax": {
            "url": "${ctx}/appLottery/userListPage",
            "type": "POST",
            "data": function (d) {
            	d.nickName = $("#nickName").val();
            	d.appoint = $("#appoint").val();
            	d.lotteryId = "${param.lotteryId }";
            }
        },
	    processing:true,
	    "columns": [
	        {data: "nick_name",orderable:false,title: "昵称"},
	        {data: "uid",orderable:false,title: "UID"},
	        {data: "mintime",orderable:false,title: "参与时间",render:function(data){
	        	if(data!=null){
	        		return new Date(data).Format("yyyy-MM-dd hh:mm:ss");
	        	}
	        }},
	        {data: "status_str",orderable:false,title: "状态"},
	        {data: "appoint",orderable:false,title: "是否指定",render:function(data){
	        	if(data>0){
	        		return "<font color='green'>是</font>";
	        	}else{
	        		return "否"
	        	}
	        }},
	        {data: "icount",orderable:false,title: "邀请人数"},
	        {title:"操作",render:function(data, type, row, meta){
	        	if(row.appoint > 0){
	        		return '<a class="btn btn-danger btn-xs setAppoint" href="#">取消指定</a>&nbsp;&nbsp;'
	        	}else{
	        		return '<a class="btn btn-danger btn-xs setAppoint" href="#">指定</a>&nbsp;&nbsp;'
	        	}
	        }}
	     ]
	});
	function searchLotteryUser(){
		sourceTable.draw(true);
	}
	
	$(document).on("click",".setAppoint",function(){
		var tr=$(this).closest("tr");
		var data =sourceTable.row(tr).data();
		var action = 1;
		if(data.appoint > 0){
			action = 0;
		}
		
		$.ajax({
			url : "${ctx}/appLottery/setAppoint?lid=${param.lotteryId }&uid="+data.uid+"&a="+action,
			async : false,
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(resp) {
				if(resp == '0'){
					alert('设置成功');
					sourceTable.draw(true);
				}else{
					alert(resp);
				}
			}
		});
	});
	</script>
</body>
</html>