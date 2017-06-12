<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 榜单列表</title>

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
			<jsp:param name="t" value="11" />
			<jsp:param name="s" value="11_1" />
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
								| 任务控制台
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<p>
									<form action="" method="post" class="form form-inline">
										<input type="text" name="keyword" value="${keyword}" class="form-control"/>
										<select name="type" class="form-control" data-value="${type}">
											<option value="kingdom">kingdom</option>
											<option value="ugc">ugc</option>
											<option value="user">user</option>
										</select>
										<button type="submit" class="btn btn-primary">
											<i  class=" fa fa-plus "></i>
											查询
										</button>
										<a class="btn btn-danger" href="./startTask?task=ugc">
											<i  class=" fa fa-plus "></i>
											启动UGC全量索引
										</a>
										<a class="btn btn-danger" href="./startTask?task=user">
											<i  class=" fa fa-plus "></i>
											启动用户全量索引
										</a>
										<a class="btn btn-danger" href="./startTask?task=kingdom">
											<i  class=" fa fa-plus "></i>
											启动王国全量索引
										</a>
										<a class="btn btn-danger" href="./startTask?task=history">
											<i  class=" fa fa-plus "></i>
											搜索历史全量索引
										</a>
									</form>
								</p>
								<div>
									<ul>
										<c:forEach items="${dataList }" var="item">
										<li style="border-bottom:1px solid #eee;padding:5px;">
											<c:if test="${param.type=='ugc'}">
											<h4><a class="text-primary" target="_blank" href="http://webapp.me-to-me.com/fd/${item.id}"><small class="text-muted">${item.nick_name }</small></a></h4>
											<p>
												 ${item.content}
											 </p> 
											</c:if>
											<c:if test="${param.type=='user'}">
											<small class="text-muted">${item.nick_name }</small>
											
											</c:if>
											<c:if test="${param.type=='kingdom'}">
												<h4><a class="text-primary" target="_blank" href="http://webapp.me-to-me.com/ld/${item.id}">${item.title}</a> &emsp;<small class="text-muted">${item.nick_name }</small></h4>
												<p>
												 ${item.fragments}
												 </p> 
											</c:if>
											
										</li>
										</c:forEach>
									</ul>
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
	<script>
		$("a.btn-danger").click(function(){
			$(this).attr("disabled","disabled");
			return confirm("确定执行此操作吗？索引将会执行一段时间，请不要重复点击或者刷新")
		})
		$("select[data-value]").each(function(){
			var val = $(this).attr("data-value");
			console.log(val);
			$(this).find("option[value='"+val+"']").attr("selected","selected")
		})
	</script>
</body>
</html>