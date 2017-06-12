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
			<jsp:param name="s" value="12_5" />
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
								| 情绪列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div>
									<a class="btn btn-primary" href="./add_emotion">
										<i  class=" fa fa-plus "></i>
										添加情绪
									</a>
								</div>
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th>序号</th>
												<th>名称</th>
												<th>开心指数范围</th>
												<th>空闲指数范围</th>
												<th>对应大表情</th>
												<th>对应王国ID</th>
												<th>王国名</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${data.emotionInfoData}" var="item" varStatus="status">
												<tr class="gradeX">
													<td>${status.index + 1}</td>
													<td>
														${item.emotionName }
													</td>
													<td >
														${item.happyMin }  到  ${item.happyMax } 
													</td>
													<td>
														${item.freeMin }  到 ${item.freeMax } 
													</td>
													<td>
														<img src="http://cdn.me-to-me.com/${item.emotionPack.thumb }" height="50"/>
													</td>
													<td>
														${item.topicId }
													</td>
													<td>
														${item.topicTitle }
													</td>
													<td>
														<a class="btn btn-warning btn-xs " href="./modify_emotion?id=${item.id}">编辑</a>
														<c:if test="${item.status==1 }">
                                                        <a class="btn btn-info btn-xs " href="./off_emotion?id=${item.id}" onclick="return confirm('确认下架吗？')">下架</a>
                                                        </c:if>
                                                        <c:if test="${item.status==2 }">
                                                        <a class="btn btn-primary btn-xs " href="./up_emotion?id=${item.id}">上架</a>
                                                        </c:if>
														<a class="btn btn-danger btn-xs " href="./delete_emotion?id=${item.id}" onclick="return confirm('确认删除吗？')">删除</a>
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