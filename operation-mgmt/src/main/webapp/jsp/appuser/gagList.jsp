<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - APP禁言用户列表</title>

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
</head>
<body>
	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="5" />
			<jsp:param name="s" value="5_2" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/appuser/gaguser/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										禁言用户UID
										<input type="text" id="uid" name="uid" value="${dataObj.uid }" class="form-control">&nbsp;&nbsp;
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
								| 禁言用户列表 
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th>序号</th>
												<th>禁言UID</th>
												<th>昵称</th>
												<th>禁言类型</th>
												<th>禁言级别</th>
												<th>操作内容ID</th>
												<th>操作人</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj.data.result}" var="gagItem" varStatus="status">
												<tr class="gradeX">
													<th>${status.index + 1}</th>
													<th>${gagItem.targetUid }</th>
													<th>${gagItem.targetUserName }</th>
													<th>
													<c:choose>
                                                		<c:when test="${gagItem.type == '0'}">
                                                			全部
                                                		</c:when>
                                                		<c:when test="${gagItem.type == '1'}">
                                                			王国
                                                		</c:when>
                                                		<c:when test="${gagItem.type == '2'}">
                                                			UGC
                                                		</c:when>
                                                	</c:choose>
													</th>
													<th>
													<c:choose>
                                                		<c:when test="${gagItem.gagLevel == '0'}">
                                                			全部
                                                		</c:when>
                                                		<c:when test="${gagItem.gagLevel == '1'}">
                                                			新建
                                                		</c:when>
                                                		<c:when test="${gagItem.gagLevel == '2'}">
                                                			发言
                                                		</c:when>
                                                		<c:when test="${gagItem.gagLevel == '3'}">
                                                			点赞
                                                		</c:when>
                                                	</c:choose>
													</th>
													<th>
													<c:choose>
                                                		<c:when test="${gagItem.cid == '0'}">
                                                			全部
                                                		</c:when>
                                                		<c:otherwise>
                                                			${gagItem.cid }
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>${gagItem.userName }</th>
													<th><a href="${ctx}/appuser/gaguser/remove/${gagItem.id }">取消禁言</a></th>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<th>序号</th>
												<th>UID</th>
												<th>昵称</th>
												<th>禁言类型</th>
												<th>禁言级别</th>
												<th>操作内容ID</th>
												<th>操作人</th>
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