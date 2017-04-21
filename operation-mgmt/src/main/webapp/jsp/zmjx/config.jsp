<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 最美家乡活动 配置列表</title>

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
var modifyConfig = function(key, title, personScoreAdd, personScoreDel, areaScoreAdd, areaScoreDel, limit){
	$("#ckey").val(key);
	$("#ctitle").val(title);
	$("#cpersonScoreAdd").val(personScoreAdd);
	$("#cpersonScoreDel").val(personScoreDel);
	$("#careaScoreAdd").val(areaScoreAdd);
	$("#careaScoreDel").val(areaScoreDel);
	$("#climit").val(limit);
	$("#configModal").modal();
}

var modifyCommit = function(){
	var key = $("#ckey").val();
	var title = $("#ctitle").val();
	var personScoreAdd = $("#cpersonScoreAdd").val();
	var personScoreDel = $("#cpersonScoreDel").val();
	var areaScoreAdd = $("#careaScoreAdd").val();
	var areaScoreDel = $("#careaScoreDel").val();
	var limit = $("#climit").val();

	$.ajax({
		url : "${ctx}/zmjx/config/modify?key="+key+"&personScoreAdd="+personScoreAdd+"&personScoreDel="+personScoreDel+"&areaScoreAdd="+areaScoreAdd+"&areaScoreDel="+areaScoreDel+"&limit="+limit,
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
			<jsp:param name="s" value="zmjx_2" />
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
								| 活动配置
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table0">
										<thead>
											<tr>
												<th>配置名</th>
												<th>配置值</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody0">
												<tr class="gradeX">
													<td>分值开关</td>
													<td>
													<c:choose>
                                                		<c:when test="${dataObj.activitySwitch == 1}">
                                                			<font color='green'>开启</font>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>关闭</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													<td>
													<c:choose>
                                                		<c:when test="${dataObj.activitySwitch == 1}">
                                                			<a href="${ctx}/zmjx/config/switch/2">关闭</a>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<a href="${ctx}/zmjx/config/switch/1">开启</a>
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
												</tr>
										</tbody>
									</table>
								</div>
							</div>
						</section>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| 王国内容分值配置列表 
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th>内容类型</th>
												<th>增加个人荣誉</th>
												<th>减少个人荣誉</th>
												<th>增加地域热度</th>
												<th>减少地域热度</th>
												<th>次数上限</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj.limitList}" var="item">
												<tr class="gradeX">
													<td>${item.title }</td>
													<td>${item.personScoreAdd }</td>
													<td>${item.personScoreDel }</td>
													<td>${item.areaScoreAdd }</td>
													<td>${item.areaScoreDel }</td>
													<td>${item.limit }</td>
													<td><a href="#" onclick="modifyConfig('${item.key }', '${item.title }','${item.personScoreAdd }','${item.personScoreDel }','${item.areaScoreAdd }','${item.areaScoreDel }','${item.limit }')">修改</a></td>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<th>内容类型</th>
												<th>增加个人荣誉</th>
												<th>减少个人荣誉</th>
												<th>增加地域热度</th>
												<th>减少地域热度</th>
												<th>次数上限</th>
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
				<!-- modal VIEW -->
	<div class="modal fade" id="configModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">王国内容分值配置修改</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">内容类型</label>
                        <input type="text" id="ctitle" name="ctitle" class="form-control" style="width: 100%" readonly>
                        <input type="hidden" id="ckey" name="ckey">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">增加个人荣誉</label>
                        <input type="text" id="cpersonScoreAdd" name="cpersonScoreAdd" class="form-control" style="width: 100%">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">减少个人荣誉</label>
                        <input type="text" id="cpersonScoreDel" name="cpersonScoreDel" class="form-control" style="width: 100%">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">增加地域热度</label>
                        <input type="text" id="careaScoreAdd" name="careaScoreAdd" class="form-control" style="width: 100%">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">减少地域热度</label>
                        <input type="text" id="careaScoreDel" name="careaScoreDel" class="form-control" style="width: 100%">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">次数上限</label>
                        <input type="text" id="climit" name="climit" class="form-control" style="width: 100%">
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