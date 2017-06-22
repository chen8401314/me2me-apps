<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 短信控制台</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_page.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_table.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script type="text/javascript">
var sendMsg = function(){
	var msgId = $("#msgId").val();
	if(msgId == ''){
		alert('请输入正确的短信模板ID');
		return;
	}
	var mode = $("#mode").val();
	var mobiles = $("#mobiles").val();
	if(mode == 1 && mobiles == ''){
		alert('请指定发送人，多个以英文逗号分隔');
		return;
	}

	$("#btnSearch").attr("disabled",true);
	$.ajax({
		url : "${ctx}/appuser/sendMsg?id="+msgId+"&m="+mode+"&ms="+mobiles,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			alert(resp);
			$("#btnSearch").attr("disabled",false);
		}
	});
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
			<jsp:param name="t" value="5" />
			<jsp:param name="s" value="5_3" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										短信模板ID
										<input type="text" id="msgId" name="msgId" class="form-control" >
									</div><br/>
									<div class="form-inline" role="form">
										人群选择
										<select name="mode" id="mode" class="form-control">
											<option value="1">指定手机号</option>
											<option value="2">后台所有注册手机号</option>
										</select>
									</div><br/>
									<div class="form-inline" role="form">
										指定手机号（多个之间以英文逗号分隔）
										<input type="text" id="mobiles" name="mobiles" class="form-control" >
									</div>
									<div class="form-inline" role="form">
										<input type="button" id="btnSearch" name="btnSearch" value="发 送" onclick="sendMsg()" class="btn btn-info" />
									</div>
								</div>
							</section>
						</div>
					</div>
				<!-- page start-->
				
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
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
		<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
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
	$('#startTime').datetimepicker({
		format: 'yyyy-mm-dd',
		language: 'zh',
		startView: 2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1,
		minView:2
		});
	</script>
</body>
</html>