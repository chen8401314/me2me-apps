<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 渠道注册统计</title>

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
$(function(){
	$("#checkall").click(function(){
		$("input[name='checkItem']").attr("checked",this.checked);
	});
	$("#totalBtn").click(function(){
		var uCount = 0;
		var kCount = 0;
		
		var chkCount =0;
		$('input[name="checkItem"]:checked').each(function(){
			chkCount++;
			var tt = $(this).val().split(",");
			uCount = uCount + parseInt(tt[0]);
			kCount = kCount + parseInt(tt[1]);
		});
		if(chkCount==0){
			alert('请至少选择一个渠道');
		}else{
			$("#totalRegisterCount").val(uCount);
			$("#totalKingdomCount").val(kCount);
			$("#alertModal").modal();
		}
	});
});
</script>
</head>
<body>
	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="3" />
			<jsp:param name="s" value="3_5" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/stat/channelRegister/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										开始时间
										<input type="text" id="startTime" name="startTime" value="${dataObj.startTime }" class="form-control" required>&nbsp;&nbsp;
										结束时间
										<input type="text" id="endTime" name="endTime" value="${dataObj.endTime }" class="form-control" required>&nbsp;&nbsp;
										渠道标识
										<input type="text" id="channelCode" name="channelCode" value="${dataObj.channelCode }" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="submit" id="btnSearch" name="btnSearch" value="搜索" class="btn btn-info" />
									</div>
								</div>
							</section>
						</div>
					</div>
				</form>
				<!-- page start-->
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| 渠道列表&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="合计" class="btn btn-info" id="totalBtn">
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th><input type="checkbox" name="checkall" id="checkall"/></th>
												<th>渠道标识</th>
												<th>注册数</th>
												<th>王国创建数</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj.result}" var="item">
												<tr class="gradeX">
													<td><input type="checkbox" name="checkItem" value="${item.registerCount },${item.kingdomCount }"/></td>
													<td>${item.channelCode }</td>
													<td>${item.registerCount }</td>
													<td>${item.kingdomCount }</td>
													<td>
													<a href="${ctx}/stat/channelRegister/detail?channelCode=${item.channelCode }&startTime=${dataObj.startTime }&endTime=${dataObj.endTime }">查看详细</a>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</section>
					</div>
				</div>
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
	
	<!-- modal VIEW -->
	<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">渠道合计信息</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">合计注册数</label>
                        <input type="text" id="totalRegisterCount" name="totalRegisterCount" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">合计王国创建数</label>
                        <input type="text" id="totalKingdomCount" name="totalKingdomCount" class="form-control" style="width: 100%" readonly>
					</div>
				</div>
				<div class="modal-footer">
					<button data-dismiss="modal" class="btn btn-default" type="button">关闭</button>
				</div>
			</div>
		</div>
	</div>
	
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
		format: 'yyyy-mm-dd hh:ii:ss',
		language: 'zh',
		startView: 2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1,
		minView:2
		});
	$('#endTime').datetimepicker({
		format: 'yyyy-mm-dd hh:ii:ss',
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