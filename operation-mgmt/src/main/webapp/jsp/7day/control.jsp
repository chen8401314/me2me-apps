<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 7天活动 各种控制</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_page.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_table.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script type="text/javascript">
var auditSuccess = function(){
	if(confirm('确定要一键审核通过?\n此功能会群发短信')){
		$.ajax({
			url : "${ctx}/7day/control/auditSuccess",
			async : false,
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(resp) {
				if(resp == "0"){
					alert('执行成功');
				}else{
					alert('执行失败');
				}
			},
			error : function(){
				alert('执行失败');
			}
		});
	}
}

var noticeBind = function(){
	if(confirm('确定要一键通知绑定?\n此功能会群发短信')){
		$.ajax({
			url : "${ctx}/7day/control/noticeBind",
			async : false,
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(resp) {
				if(resp == "0"){
					alert('执行成功');
				}else{
					alert('执行失败');
				}
			},
			error : function(){
				alert('执行失败');
			}
		});
	}
}

var noticeActivityStart = function(){
	if(confirm('确定要通知所有人活动开始?\n此功能会群发短信给报名审核通过的人')){
		$.ajax({
			url : "${ctx}/7day/control/activityStartNotice",
			async : false,
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(resp) {
				if(resp == "0"){
					alert('执行成功');
				}else{
					alert('执行失败');
				}
			},
			error : function(){
				alert('执行失败');
			}
		});
	}
}

var sexSend = function(sex){
	var p = "男";
	if(sex == 0){
		p = "女";
	}
	if(confirm('确定要通知所有人审核通过的'+p+'用户吗？')){
		$.ajax({
			url : "${ctx}/7day/control/sexSend/"+sex,
			async : false,
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(resp) {
				if(resp == "0"){
					alert('向'+p+'用户发送成功');
				}else{
					alert('执行失败');
				}
			},
			error : function(){
				alert('执行失败');
			}
		});
	}
}
</script>
</head>
<body>
	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="8" />
			<jsp:param name="s" value="8_2" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">短信通知操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										一键审核通过&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="button" id="btn1" name="btn1" value="一键审核" class="btn btn-info" onclick="auditSuccess()"/>
									</div>
								</div>
								<div class="panel-body">
									<div class="form-inline" role="form">
										一键提醒绑定&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="button" id="btn1" name="btn1" value="一键提醒" class="btn btn-info" onclick="noticeBind()"/>
									</div>
								</div>
								<div class="panel-body">
									<div class="form-inline" role="form">
										提醒活动开始&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="button" id="btn1" name="btn1" value="提醒活动开始" class="btn btn-info" onclick="noticeActivityStart()"/>
									</div>
								</div>
								<div class="panel-body">
									<div class="form-inline" role="form">
										针对男女推送&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="button" id="btn1" name="btn1" value="男推送" class="btn btn-info" onclick="sexSend(1)"/>
										&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="button" id="btn1" name="btn1" value="女推送" class="btn btn-info" onclick="sexSend(0)"/>
									</div>
								</div>
							</section>
						</div>
					</div>
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
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
</body>
</html>