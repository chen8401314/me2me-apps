<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 最美家乡活动 王国列表</title>

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
var modifyConfig = function(topicId,alias,score){
	$("#mtopicId").val(topicId);
	$("#malias").val(alias);
	$("#moldScore").val(score);
	$("#mnewScore").val('');
	$("#modifyHotModal").modal();
}

var modifyCommit = function(){
	var topicId = $("#mtopicId").val();
	var score = $("#mnewScore").val();

	if(score == ''){
		alert("请输入新的热度值");
		return;
	}
	
	$.ajax({
		url : "${ctx}/zmjx/kingdom/hot/modify?topicId="+topicId+"&score="+score,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			if(resp == '0'){
				window.location.reload();
			}else{
				alert(resp);
			}
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
			<jsp:param name="t" value="zmjx" />
			<jsp:param name="s" value="zmjx_1" />
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
								| 王国列表 
								<span class="tools pull-right">
									<a href="${ctx}/jsp/zmjx/kingdomNew.jsp" class="fa fa-plus add_link" title="新增王国" ></a>
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th>王国名</th>
												<th>王国ID</th>
												<th>别名</th>
												<th>热度</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj.result}" var="item">
												<tr class="gradeX">
													<td><a href="${item.h5url }" target="_blank">${item.title }</a></td>
													<td>${item.topicId }</td>
													<td>${item.alias }</td>
													<td>${item.score }</td>
													<td>
													<a href="${ctx}/zmjx/config/kingdomDel?topicId=${item.topicId }">移除</a>&nbsp;
													|&nbsp;<a href="#" onclick="modifyConfig('${item.topicId }','${item.alias }','${item.score }')">调整热度</a>
													</td>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<th>王国名</th>
												<th>王国ID</th>
												<th>别名</th>
												<th>热度</th>
												<th>操作</th>
											</tr>
										</tfoot>
									</table>
								</div>
							</div>
						</section>
					</div>
				</div>
				<!-- page end-->
				<div class="modal fade" id="modifyHotModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">王国内容分值配置修改</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">地区</label>
                        <input type="text" id="malias" name="malias" class="form-control" style="width: 100%" readonly>
                        <input type="hidden" id="mtopicId" name="mtopicId">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">当前热度</label>
                        <input type="text" id="moldScore" name="moldScore" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">调整为</label>
                        <input type="text" id="mnewScore" name="mnewScore" class="form-control" style="width: 100%">
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn" data-dismiss="modal" aria-hidden="true" onclick="modifyCommit()">更改</button>
					<button data-dismiss="modal" class="btn btn-default" type="button">关闭</button>
				</div>
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
	<script src="${ctx}/js/dynamic_table_init_0_asc.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
</body>
</html>