<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 王国列表查询</title>

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
		url : "${ctx}/price/kingdomPage?title="+$("#title").val()+"&orderbyParam="+$("#orderbyParam").val()+"&orderby="+$("#orderby").val()+"&page="+page+"&pageSize="+pageSize,
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
		url : "${ctx}/price/kingdomPage?title="+$("#title").val()+"&orderbyParam="+$("#orderbyParam").val()+"&orderby="+$("#orderby").val()+"&page="+page+"&pageSize="+pageSize,
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
	totalPage = $("#totalPage").val();
	if(result){
		buildTableBody(result.result);
	}
	
	//记录分页信息
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
			bodyHtml = bodyHtml + "<td>"+dataList[i].id+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].title+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].uid+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].nickName+"</td>";
			bodyHtml = bodyHtml + "<td>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</td>";
			bodyHtml = bodyHtml + "<td>"+parserDatetimeStr(new Date(dataList[i].updateTime))+"</td>";
			bodyHtml = bodyHtml + "<td>";
			if(dataList[i].type == 1000){
				bodyHtml = bodyHtml + "聚合王国";
			}else{
				bodyHtml = bodyHtml + "个人王国";
			}
			bodyHtml = bodyHtml + "</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].price+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].lastPriceIncr+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].stealPrice+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].diligently+"</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].approve+"</td>";
			bodyHtml = bodyHtml + "<td><a target='_blank'  href='./kingdomUser?topicId="+dataList[i].id+"'>成员列表</a></td>";
			//bodyHtml = bodyHtml + "<th><a href=\"${ctx}/price/kingdom/"+dataList[i].id+"\">交易</a></th>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody").html(bodyHtml);
}

var parserDatetimeStr = function(time){
	var year=time.getYear()+1900;
	var m=time.getMonth()+1;
	var month;
	if(m<10){
		month = "0" + m;
	}else{
		month = "" + m;
	}
	var d=time.getDate();
	var date;
	if(d<10){
		date = "0" + d;
	}else{
		date = "" + d;
	}
	var h=time.getHours();
	var hour;
	if(h<10){
		hour = "0" + h;
	}else{
		hour = "" + h;
	}
	var mm=time.getMinutes();
	var minute;
	if(mm<10){
		minute = "0" + mm;
	}else{
		minute = "" + mm;
	}
	var s=time.getSeconds();
	var second;
	if(s<10){
		second = "0" + s;
	}else{
		second = "" + s;
	}
	return year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second;
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
			<jsp:param name="t" value="13" />
			<jsp:param name="s" value="13_1" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/price/kingdomQuery" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										王国标题
										<input type="text" id="title" name="title" value="${dataObj.title }" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
										排序属性
										<select name="orderbyParam" id="orderbyParam" class="form-control">
											<option value="create_time" ${dataObj.orderbyParam=='create_time'?'selected':''}>创建时间</option>
											<option value="update_time" ${dataObj.orderbyParam=='update_time'?'selected':''}>更新时间</option>
											<option value="price" ${dataObj.orderbyParam=='price'?'selected':''}>价值</option>
											<option value="last_price_incr" ${dataObj.orderbyParam=='last_price_incr'?'selected':''}>昨日增长</option>
										</select>&nbsp;&nbsp;&nbsp;&nbsp;
										排序
										<select name="orderby" id="orderby" class="form-control">
											<option value="desc" ${dataObj.orderby=='desc'?'selected':''}>倒序</option>
											<option value="asc" ${dataObj.orderby=='asc'?'selected':''}>正序</option>
										</select>&nbsp;&nbsp;&nbsp;&nbsp;
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
								| 王国列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table">
										<thead>
											<tr>
												<th>王国ID</th>
												<th>王国标题</th>
												<th>国王UID</th>
												<th>国王昵称</th>
												<th>创建时间</th>
												<th>更新时间</th>
												<th>王国类型</th>
												<th>价值</th>
												<th>昨日增长值</th>
												<th>可被偷</th>
												<th>用心度x</th>
												<th>认可度y</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody">
											<c:forEach items="${dataObj.result}" var="item">
												<tr class="gradeX">
													<td>${item.id }</td>
													<td>${item.title }</td>
													<td>${item.uid }</td>
													<td>${item.nickName }</td>
													<td><fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													<td><fmt:formatDate value="${item.updateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													<td>
													<c:choose>
                                                		<c:when test="${item.type == '1000'}">
                                                			聚合王国
                                                		</c:when>
                                                		<c:otherwise>
                                                			个人王国
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													<td>${item.price }</td>
													<td>${item.lastPriceIncr }</td>
													<td>${item.stealPrice }</td>
													<td>${item.diligently }</td>
													<td>${item.approve }</td>
													<td>
													<a target="_blank" href='./kingdomUser?topicId=${item.id}'>成员列表</a>
													<!-- <a href="${ctx}/price/kingdom/${item.id }">交易</a> -->
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage" value="${dataObj.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_info" class="dataTables_info">当前第 1 页，共 ${dataObj.totalPage } 页</div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev" onclick="previous()" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next" onclick="next()" class="next ${dataObj.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
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
		<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
	</script>
</body>
</html>