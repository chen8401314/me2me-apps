<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - APP用户列表</title>

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
			<jsp:param name="s" value="5_1" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/appuser/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										手机号
										<input type="text" id="mobile" name="mobile" value="${dataObj.mobile }" class="form-control">&nbsp&nbsp;
										昵称
										<input type="text" id="nickName" name="nickName" value="${dataObj.nickName }" class="form-control">&nbsp&nbsp
										是否大V
										<select name="isV" id="isV" class="form-control">
											<option value="0" ${dataObj.isV==0?'selected':''}>全部</option>
											<option value="1" ${dataObj.isV==1?'selected':''}>是</option>
											<option value="2" ${dataObj.isV==2?'selected':''}>否</option>
										</select>
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
								| 用户列表 
								<span class="tools pull-right">
									<a href="${ctx}/jsp/appuser/appUserNew.jsp" class="fa fa-plus add_link" title="添加马甲号" ></a>
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th>UID</th>
												<th>手机</th>
												<th>昵称</th>
												<th>性别</th>
												<th>生日</th>
												<th>是否大V</th>
												<th>创建时间</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj.data.result}" var="userItem">
												<tr class="gradeX">
													<th>${userItem.uid }</th>
													<th>${userItem.mobile }</th>
													<th>${userItem.nickName }</th>
													<th>
													<c:choose>
                                                		<c:when test="${userItem.gender == '1'}">
                                                			男
                                                		</c:when>
                                                		<c:otherwise>
                                                			女
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>${userItem.birthday }</th>
													<th>
													<c:choose>
                                                		<c:when test="${userItem.vlv == '1'}">
                                                			是
                                                		</c:when>
                                                		<c:otherwise>
                                                			否
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th><fmt:formatDate value="${userItem.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
													<c:choose>
                                                		<c:when test="${userItem.vlv == '1'}">
                                                			<a href="${ctx}/appuser/option/vlv?m=${dataObj.mobile }&s=${dataObj.nickName }&v=${dataObj.isV }&a=2&i=${userItem.uid}">取消大V</a>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<a href="${ctx}/appuser/option/vlv?m=${dataObj.mobile }&s=${dataObj.nickName }&v=${dataObj.isV }&a=1&i=${userItem.uid}">上大V</a>
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<th>UID</th>
												<th>手机</th>
												<th>昵称</th>
												<th>性别</th>
												<th>生日</th>
												<th>是否大V</th>
												<th>创建时间</th>
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
	<script src="${ctx}/js/dynamic_table_init.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
</body>
</html>