<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 上线榜单管理</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
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
			<jsp:param name="t" value="10" />
			<jsp:param name="s" value="10_2" />
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
								| 上线榜单管理 
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							
							<div class="panel-body">
								<p>
									<ul class="nav nav-tabs" id="type_sec">
									  <li role="presentation" data="1"><a href="?type=1">找谁</a></li>
									  <li role="presentation" data="2"><a href="?type=2">找组织</a></li>
									</ul>
								</p>
								<script>
									$("#type_sec li[data='${param.type}']").addClass("active");
								</script>
								
								<div class="row">
									<div class="col-md-5">
										<h3>待选榜单</h3>
										<table class="display table table-bordered table-striped" id="dynamic-table">
											<thead>
												<tr>
													<th>序号</th>
													<th>类型</th>
													<th>名称</th>
													<th>操作</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${dataList}" var="item" varStatus="status">
													<tr class="gradeX">
														<td>${status.index + 1}</td>
														<td>
															<c:choose>
		                                                		<c:when test="${item.type == '1'}">
		                                                			王国榜单
		                                                		</c:when>
		                                                		<c:when test="${item.type == '2'}">
		                                                			用户榜单
		                                                		</c:when>
		                                               			<c:when test="${item.type == '3'}">
		                                               				榜单集合
		                                               			</c:when>
		                                                	</c:choose>
														</td>
														<td >
															${item.name }
														</td>
														<td>
															<button class="btn btn-primary btn-xs add" data-bid="${item.id}">加入</button>
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									
									</div>
									<div class="col-md-7">
										<h3>已选榜单</h3>
										<table class="display table table-bordered table-striped" id="mytable">
												<thead>
													<tr>
														<th>序号</th>
														<th>类型</th>
														<th>名称</th>
														<th>状态</th>
														<th>操作</th>
													</tr>
												</thead>
												<tbody>
													<c:set var="maxId" value="0"/>
													<c:forEach items="${myDataList}" var="item" varStatus="status">
														<tr class="gradeX" data-id="${item.detail.id}">
															<td>${status.index + 1}</td>
															<td>
																<c:choose>
			                                                		<c:when test="${item.billbord.type == '1'}">
			                                                			王国榜单
			                                                		</c:when>
			                                                		<c:when test="${item.billbord.type == '2'}">
			                                                			用户榜单
			                                                		</c:when>
			                                               			<c:when test="${item.billbord.type == '3'}">
			                                               				榜单集合
			                                               			</c:when>
			                                                	</c:choose>
															</td>
															<td >
																${item.billbord.name }
															</td>
															<td >
																<c:choose>
			                                                		<c:when test="${item.detail.status == '0'}">
			                                                			<b class="text-danger">未上架</b>
			                                                		</c:when>
			                                                		<c:when test="${item.detail.status == '1'}">
			                                                			<b class="text-success">已上架</b>
			                                                		</c:when>
			                                                	</c:choose>
															</td>
															<td>
																<a class="btn btn-warning btn-xs moveup" href="#">上移</a>
																<a class="btn btn-warning btn-xs movedown" href="#">下移</a>
																<a class="btn btn-danger btn-xs delRanking" href="#" data-id="${item.detail.id}">删除</a>
																<c:choose>
			                                                		<c:when test="${item.detail.status == '0'}">
			                                                			<a class="btn btn-primary btn-xs status-on" href="#" data-id="${item.detail.id}">上架</a>
			                                                		</c:when>
			                                                		<c:when test="${item.detail.status == '1'}">
			                                                			<a class="btn btn-primary btn-xs status-off" href="#" data-id="${item.detail.id}">下架</a>
			                                                		</c:when>
			                                                	</c:choose>
															</td>
														</tr>
														<c:if test="${item.detail.id>maxId}">
															<c:set var="maxId" value="${item.detail.id}"/>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										
									</div>
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
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/jquery.json-2.4.min.js"></script>
	<script>
		var sort=${maxId};
		$(".add").click(function(){		// 添加榜单
			var data = {
					type:${param.type},
					bid:$(this).attr("data-bid"),
					sort:++sort,
					status:0
			}
			$.post("./doSaveOnlineRanking",data,function(data){
				if(data.code==1){
					location.reload();
				}else{
					alert(data.desc);
				}
			})
		})
		$(".delRanking").click(function(){
			if(confirm("确认删除吗？")){
				$.post("./deleteOnlineRanking",{id:$(this).attr("data-id"),r:Math.random()},function(data){
					if(data.code==1){
						location.reload();
					}else{
						alert(data.desc);
					}
				})
			}
		})
		$(".moveup,.movedown").click(function(){		// 添加榜单
			//交换位置。
			var cur=$(this).closest("tr");
			if($(this).hasClass("movedown")){
				cur.insertAfter(cur.next());
			}else{
				cur.insertBefore(cur.prev())
			}
			// 扫描顺序
			var data=[];
			$("#mytable tr[data-id]").each(function(index,obj){
				data.push({
					id:$(this).attr("data-id"),
					sort:index
				})
			})
			var json = $.toJSON(data);
			$.post("./updateOnlineRanking",{json:json,r:Math.random()},function(data){
				if(data.code==1){
					
				}else{
					alert(data.desc);
				}
			})
		})
		$(".status-on,.status-off").click(function(){		// 添加榜单
			//交换位置。
			var json = $.toJSON([{
				id:$(this).attr("data-id"),
				status:$(this).hasClass("status-on")?1:0
			}]);
			$.post("./updateOnlineRanking",{json:json,r:Math.random()},function(data){
				if(data.code==1){
					location.reload();
				}else{
					alert(data.desc);
				}
			})
		})
	</script>
</body>
</html>