<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 情绪列表</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_page.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_table.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
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
			<jsp:param name="t" value="6" />
			<jsp:param name="s" value="6_4" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/tag/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										<a class="btn btn-danger" href="javascript:allRefresh();"><span id="textFresh">全量刷新融云用户信息</span></a>
									<font color="red">慎用！</font>
									</div>
									</br>
								 <div class="form-inline" role="form">
										UID：
										<input type="text" id="uid" name="uid" value="" class="form-control">
										<a class="btn btn-primary" href="javascript:singleRefresh();">单用户信息刷新</a>
									</div>
								</div>
							</section>
						</div>
					</div>
				</form>
				<!-- page end-->
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
	<script type="text/javascript" src="${ctx}/assets/advanced-datatable/media/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="${ctx}/assets/data-tables/DT_bootstrap.js"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/dynamic_table_init_0_asc.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>	
	<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
		<script type="text/javascript">
		var status = 0;
	   function allRefresh(){
		   if(status==0){
			 var msg = "您真的确定要全量刷新用户信息吗？"; 
			 if (confirm(msg)){ 
				 status=1;
				 $("#textFresh").html('正在执行。。。');
				  	$.ajax({
			            cache: true,
			            type: "POST",
			            dataType :"json",
			            url:"./refreshAllUser",
			            async: true,
			            error: function(request) {
			            	status=0;
			            	$("#textFresh").html('全量刷新融云用户信息');
			                alert('服务器出错'); 
			            },
			            success: function(data) {
			            	status=0;
			            	$("#textFresh").html('全量刷新融云用户信息');
			            	  if(data=='1'){
			            		  //sourceTable.ajax.reload();
			            		  alert('操作成功');
			            	  }else{
			                    	alert("操作失败");
			                        }
			            }
			        });
			 }
		   }
	    }
	   function singleRefresh(){
			 var msg = "您真的确定要刷新用户信息吗？"; 
			 if (confirm(msg)){ 
				 var uid = $("#uid").val();
				 var endTime = $("#endTime").val();
				 var param = {uid:uid};
				  	$.ajax({
			            cache: true,
			            type: "POST",
			            dataType :"json",
			            url:"./refreshUser",
			            data:param,
			            async: true,
			            error: function(request) {
			                alert('服务器出错'); 
			            },
			            success: function(data) {
			            	  if(data=='1'){
			            		  alert('操作成功');
			            	  }else if(data=='-1'){
			            		  alert('找不到该用户信息');
			            	  }
			            	  else{
			                    	alert("操作失败");
			                        }
			            }
			        });
			 }
	    }
	
	</script>
</body>
</html>