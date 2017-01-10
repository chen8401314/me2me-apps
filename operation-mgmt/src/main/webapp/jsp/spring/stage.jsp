<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 春节活动 阶段管理</title>

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

</script>
</head>
<body>
	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="9" />
			<jsp:param name="s" value="9_1" />
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
								| 春节活动信息
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table0">
										<thead>
											<tr>
												<th>开始时间</th>
												<th>结束时间</th>
												<th>状态</th>
											</tr>
										</thead>
										<tbody id="tbody0">
												<tr class="gradeX">
													<th><fmt:formatDate value="${dataObj.activityInfo.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th><fmt:formatDate value="${dataObj.activityInfo.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
														<c:choose>
                                                			<c:when test="${dataObj.activityInfo.status == '1'}">
                                                				<font color='green'>正常</font>
                                                			</c:when>
                                                			<c:otherwise>
                                                				<font color='red'>暂停</font>
                                                			</c:otherwise>
                                                		</c:choose>
													</th>
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
								| 活动阶段列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table">
										<thead>
											<tr>
												<th>阶段名</th>
												<th>开始时间</th>
												<th>结束时间</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody">
											<c:forEach items="${dataObj.stageList}" var="item">
												<tr class="gradeX">
													<th>${item.name }</th>
													<th>${item.startTime}</th>
													<th>${item.endTime}</th>
													<th>
														<c:choose>
                                                			<c:when test="${item.status == '0'}">
                                                				<font color='green'>开启</font>
                                                			</c:when>
                                                			<c:otherwise>
                                                				<font color='red'>关闭</font>
                                                			</c:otherwise>
                                                		</c:choose>
													</th>
													<th><a href="${ctx}/spring/stage/f/${item.id }">编辑</a></th>
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