<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 7天活动 米粒数据管理</title>

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
var pageSize = 10;

var currentPage = 1;

var getCurrentPage = function(){
	return currentPage;
}

var setCurrentPage = function(num){
	currentPage = currentPage + num;
}

//上一页
var previous = function(){
	var currPage = getCurrentPage();
	if(currPage <= 1){
		return;
	}
	var page = currPage-1;
	
	$.ajax({
		url : "${ctx}/7day/task/queryJson?title="+$("#title").val()+"&page="+page+"&pageSize="+pageSize,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			setCurrentPage(-1);
			buildTable(resp);
		}
	});
}

//下一页
var next = function(type){
	var currPage = getCurrentPage();
	var totalPage = $("#totalPage").val();
	if(currPage >= totalPage){
		return;
	}
	var page = currPage+1;
	
	$.ajax({
		url : "${ctx}/7day/task/queryJson?title="+$("#title").val()+"&page="+page+"&pageSize="+pageSize,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			setCurrentPage(1);
			buildTable(resp);
		}
	});
}

var buildTable = function(resp){
	var result = eval("("+resp+")");
	var currentPage = getCurrentPage();
	var totalPage = result.data.totalPage;
	if(result){
		buildTableBody(result.data.result);
	}
	
	//记录分页信息
	$("#totalPage").val(totalPage);
	$("#DataTables_Table_info").html("当前第 "+currentPage+" 页，共 "+totalPage+" 页");
	$("#prev").removeClass("disabled");
	$("#next").removeClass("disabled");
	if(currentPage == 1){
		$("#prev").addClass("disabled");
	}
	if(currentPage >= totalPage){
		$("#next").addClass("disabled");
	}
}

var buildTableBody = function(dataList){
	var bodyHtml = "";
	if(dataList && dataList.length > 0){
		for(var i=0;i<dataList.length;i++){
			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
			bodyHtml = bodyHtml + "<td>"+dataList[i].title+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].content+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].miliContent+"</td>";
			bodyHtml = bodyHtml + "<td>";
			if(dataList[i].type == 1){
				bodyHtml = bodyHtml + "单人任务";
			}else{
				bodyHtml = bodyHtml + "双人任务";
			}
			bodyHtml = bodyHtml + "</td>";
			bodyHtml = bodyHtml + "<td>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<font color=\"green\">启用</font>";
			}else{
				bodyHtml = bodyHtml + "<font color=\"red\">不启用</font>";
			}
			bodyHtml = bodyHtml + "</td>";
			bodyHtml = bodyHtml + "<td><a href=\"${ctx}/7day/task/f/"+dataList[i].id+"\">编辑</a></td>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody").html(bodyHtml);
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
			<jsp:param name="t" value="8" />
			<jsp:param name="s" value="8_4" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/7day/task/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										标题
										<input type="text" id="title" name="title" value="${dataObj.title }" class="form-control">&nbsp;&nbsp;
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
								| 任务列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table">
										<thead>
											<tr>
												<th>任务名称</th>
												<th>任务列表HTML</th>
												<th>米粒HTML</th>
												<th>任务类型</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody">
											<c:forEach items="${dataObj.data.result}" var="item">
												<tr class="gradeX">
													<td>${item.title }</td>
													<td>${item.content }</td>
													<td>${item.miliContent }</td>
													<td>
													<c:choose>
                                                		<c:when test="${item.type == '1'}">
                                                			单人任务
                                                		</c:when>
                                                		<c:otherwise>
                                                			双人任务
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													<td>
													<c:choose>
                                                		<c:when test="${item.status == '0'}">
                                                			<font color='green'>启用</font>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>不启用</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													<td>
													<a href="${ctx}/7day/task/f/${item.id }">编辑</a>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage" value="${dataObj.data.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_info" class="dataTables_info">当前第 1 页，共 ${dataObj.data.totalPage } 页</div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev" onclick="previous()" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next" onclick="next()" class="next ${dataObj.data.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
											</ul>
										</div>
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