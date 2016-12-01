<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - APP用户内容列表</title>

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

var currentPage1 = 1;
var currentPage2 = 1;
var currentPage3 = 1;
var currentPage4 = 1;
var currentPage5 = 1;

var getCurrentPage = function(type){
	if(type == 1){
		return currentPage1;
	}else if(type == 2){
		return currentPage2;
	}else if(type == 3){
		return currentPage3;
	}else if(type == 4){
		return currentPage4;
	}else if(type == 5){
		return currentPage5;
	}else{
		alert("无效的指令");
		return 0;
	}
}

var setCurrentPage = function(type, num){
	if(type == 1){
		currentPage1 = currentPage1 + num;
	}else if(type == 2){
		currentPage2 = currentPage2 + num;
	}else if(type == 3){
		currentPage3 = currentPage3 + num;
	}else if(type == 4){
		currentPage4 = currentPage4 + num;
	}else if(type == 5){
		currentPage5 = currentPage5 + num;
	}
}

//上一页
var previous = function(type){
	var currPage = getCurrentPage(type);
	if(currPage <= 1){
		return;
	}
	var page = currPage-1;
	
	$.ajax({
		url : "${ctx}/appcontent/query?uid="+$("#uid").val()+"&type="+type+"&page="+page+"&pageSize="+pageSize,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			setCurrentPage(type, -1);
			buildTable(resp, type);
		}
	});
}

//下一页
var next = function(type){
	var currPage = getCurrentPage(type);
	var totalPage = $("#totalPage"+type).val();
	if(currPage >= totalPage){
		return;
	}
	var page = currPage+1;
	
	$.ajax({
		url : "${ctx}/appcontent/query?uid="+$("#uid").val()+"&type="+type+"&page="+page+"&pageSize="+pageSize,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			setCurrentPage(type, 1);
			buildTable(resp, type);
		}
	});
}

var buildTable = function(resp, type){
	var result = eval("("+resp+")");
	var totalPage = 0;
	var currentPage = getCurrentPage(type);
	
	if(result && result.totalPage){
		totalPage = result.totalPage;
		if(type == 1){
			buildArticleReviewTableBody(result.result);
		}else if(type == 2){
			buildUgcTableBody(result.result);
		}else if(type == 3){
			buildUgcReviewTableBody(result.result);
		}else if(type == 4){
			buildKingdomTableBody(result.result);
		}else if(type == 5){
			buildKingdomFragmentTableBody(result.result);
		}
	}
	
	//记录分页信息
	$("#totalPage"+type).val(totalPage);
	$("#DataTables_Table_"+type+"_info").html("当前第 "+currentPage+" 页，共 "+totalPage+" 页");
	$("#prev_"+type).removeClass("disabled");
	$("#next_"+type).removeClass("disabled");
	if(currentPage == 1){
		$("#prev_"+type).addClass("disabled");
	}
	if(currentPage >= totalPage){
		$("#next_"+type).addClass("disabled");
	}
}

var buildArticleReviewTableBody = function(dataList){
	var bodyHtml = "";
	if(dataList && dataList.length > 0){
		for(var i=0;i<dataList.length;i++){
			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
			bodyHtml = bodyHtml + "<th>"+dataList[i].articleId+"</th>";
			bodyHtml = bodyHtml + "<th>"+dataList[i].review+"</th>";
			bodyHtml = bodyHtml + "<th>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<font color=\"green\">正常</font>";
			}else{
				bodyHtml = bodyHtml + "<font color=\"red\">删除</font>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<a href=\"#\" onclick=\"delContent(1,"+dataList[i].id+")\">删除</a>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody1").html(bodyHtml);
}

var buildUgcTableBody = function(dataList){
	var bodyHtml = "";
	if(dataList && dataList.length > 0){
		for(var i=0;i<dataList.length;i++){
			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
			bodyHtml = bodyHtml + "<th>"+dataList[i].title+"</th>";
			bodyHtml = bodyHtml + "<th>"+dataList[i].feeling+"</th>";
			bodyHtml = bodyHtml + "<th>"+dataList[i].content+"</th>";
			bodyHtml = bodyHtml + "<th>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<font color=\"green\">正常</font>";
			}else{
				bodyHtml = bodyHtml + "<font color=\"red\">删除</font>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<a href=\"#\" onclick=\"delContent(2,"+dataList[i].id+")\">删除</a>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody2").html(bodyHtml);
}

var buildUgcReviewTableBody = function(dataList){
	var bodyHtml = "";
	if(dataList && dataList.length > 0){
		for(var i=0;i<dataList.length;i++){
			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
			bodyHtml = bodyHtml + "<th>"+dataList[i].cid+"</th>";
			bodyHtml = bodyHtml + "<th>"+dataList[i].review+"</th>";
			bodyHtml = bodyHtml + "<th>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<font color=\"green\">正常</font>";
			}else{
				bodyHtml = bodyHtml + "<font color=\"red\">删除</font>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "<a href=\"#\" onclick=\"delContent(3,"+dataList[i].id+")\">删除</a>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody3").html(bodyHtml);
}

var buildKingdomTableBody = function(dataList){
	var bodyHtml = "";
	if(dataList && dataList.length > 0){
		for(var i=0;i<dataList.length;i++){
			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
			bodyHtml = bodyHtml + "<th>"+dataList[i].title+"</th>";
			bodyHtml = bodyHtml + "<th>"+dataList[i].coreCircle+"</th>";
			bodyHtml = bodyHtml + "<th>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</th>";
			bodyHtml = bodyHtml + "<th><a href=\"#\" onclick=\"delContent(4,"+dataList[i].id+")\">删除</a></th>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody4").html(bodyHtml);
}

var buildKingdomFragmentTableBody = function(dataList){
	var bodyHtml = "";
	if(dataList && dataList.length > 0){
		for(var i=0;i<dataList.length;i++){
			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
			bodyHtml = bodyHtml + "<th>"+dataList[i].topicId+"</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].contentType == 1){
				bodyHtml = bodyHtml + "图片";
			}else{
				bodyHtml = bodyHtml + "文字";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].contentType == 1){
				bodyHtml = bodyHtml + dataList[i].fragmentImage;
			}else{
				bodyHtml = bodyHtml + dataList[i].fragment;
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "<th>" + getStrByLiveType(dataList[i].type) + "</th>";
			bodyHtml = bodyHtml + "<th>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 1){
				bodyHtml = bodyHtml + "<font color=\"green\">正常</font>";
			}else{
				bodyHtml = bodyHtml + "<font color=\"red\">删除</font>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "<th>";
			if(dataList[i].status == 1){
				bodyHtml = bodyHtml + "<a href=\"#\" onclick=\"delContent(5,"+dataList[i].id+")\">删除</a>";
			}
			bodyHtml = bodyHtml + "</th>";
			bodyHtml = bodyHtml + "</tr>";
		}
	}
	$("#tbody5").html(bodyHtml);
}

var getStrByLiveType = function(liveType){
	if(liveType == 0){
		return "主播发言";
	}else if(liveType ==1){
		return "粉丝回复";
	}else if(liveType == 3){
		return "主播贴标";
	}else if(liveType == 4){
		return "粉丝贴标";
	}else if(liveType == 5){
		return "点赞";
	}else if(liveType == 6){
		return "订阅";
	}else if(liveType == 7){
		return "分享";
	}else if(liveType == 8){
		return "关注";
	}else if(liveType == 9){
		return "邀请";
	}else if(liveType == 10){
		return "有人@";
	}else if(liveType == 11){
		return "主播@";
	}else if(liveType == 12){
		return "视频";
	}else if(liveType == 13){
		return "语音";
	}else if(liveType == 14){
		return "国王收红包";
	}else if(liveType == 15){
		return "@核心圈";
	}else{
		return "";
	}
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

var delContent = function(type, id){
	$.ajax({
		url : "${ctx}/appcontent/del/"+type+"/"+id,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			if(resp && resp == "0"){
				//先退一页，再下一页，相当于刷新下当前页
				setCurrentPage(type, -1);
				next(type);
			}else{
				alert("删除失败!");
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
			<jsp:param name="t" value="5" />
			<jsp:param name="s" value="5_1" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<input type="hidden" id="uid" value="${dataObj.uid }" >
				<!-- page start-->
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| 文章评论列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table1">
										<thead>
											<tr>
												<th>文章ID</th>
												<th>评论</th>
												<th>时间</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody1">
											<c:forEach items="${dataObj.articleReviewDTO.result}" var="item1">
												<tr class="gradeX">
													<th>${item1.articleId }</th>
													<th>${item1.review }</th>
													<th><fmt:formatDate value="${item1.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
													<c:choose>
                                                		<c:when test="${item1.status == '0'}">
                                                			<font color='green'>正常</font>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>删除</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>
													<c:if test="${item1.status == '0'}">
														<a href="#" onclick="delContent(1,${item1.id })">删除</a>
													</c:if>
													</th>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage1" value="${dataObj.articleReviewDTO.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_1_info" class="dataTables_info">当前第 1 页，共 ${dataObj.articleReviewDTO.totalPage } 页</div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev_1" onclick="previous(1)" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next_1" onclick="next(1)" class="next ${dataObj.articleReviewDTO.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| UGC列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table2">
										<thead>
											<tr>
												<th>标题</th>
												<th>感受标签</th>
												<th>内容</th>
												<th>创建时间</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody2">
											<c:forEach items="${dataObj.ugcDTO.result}" var="item2">
												<tr class="gradeX">
													<th>${item2.title }</th>
													<th>${item2.feeling }</th>
													<th>${item2.content }</th>
													<th><fmt:formatDate value="${item2.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
													<c:choose>
                                                		<c:when test="${item2.status == '0'}">
                                                			<font color='green'>正常</font>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>删除</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>
													<c:if test="${item2.status == '0'}">
														<a href="#" onclick="delContent(2,${item2.id })">删除</a>
													</c:if>
													</th>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage2" value="${dataObj.ugcDTO.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_2_info" class="dataTables_info">当前第 1 页，共 ${dataObj.ugcDTO.totalPage } 页</div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev_2" onclick="previous(2)" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next_2" onclick="next(2)" class="next ${dataObj.ugcDTO.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| UGC评论列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table3">
										<thead>
											<tr>
												<th>UGCID</th>
												<th>评论</th>
												<th>时间</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody3">
											<c:forEach items="${dataObj.ugcReviewDTO.result}" var="item3">
												<tr class="gradeX">
													<th>${item3.cid }</th>
													<th>${item3.review }</th>
													<th><fmt:formatDate value="${item3.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
													<c:choose>
                                                		<c:when test="${item3.status == '0'}">
                                                			<font color='green'>正常</font>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>删除</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>
													<c:if test="${item3.status == '0'}">
														<a href="#" onclick="delContent(3,${item3.id })">删除</a>
													</c:if>
													</th>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage3" value="${dataObj.ugcReviewDTO.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_3_info" class="dataTables_info">当前第 1 页，共 ${dataObj.ugcReviewDTO.totalPage } 页</div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev_3" onclick="previous(3)" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next_3" onclick="next(3)" class="next ${dataObj.ugcReviewDTO.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
				
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
									<table class="display table table-bordered table-striped" id="table4">
										<thead>
											<tr>
												<th>标题</th>
												<th>核心圈</th>
												<th>创建时间</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody4">
											<c:forEach items="${dataObj.topicDTO.result}" var="item4">
												<tr class="gradeX">
													<th>${item4.title }</th>
													<th>${item4.coreCircle }</th>
													<th><fmt:formatDate value="${item4.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
														<a href="#" onclick="delContent(4,${item4.id })">删除</a>
													</th>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage4" value="${dataObj.topicDTO.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_4_info" class="dataTables_info">当前第 1 页，共 ${dataObj.topicDTO.totalPage } 页</div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev_4" onclick="previous(4)" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next_4" onclick="next(4)" class="next ${dataObj.topicDTO.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
				
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| 王国发言/评论列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table5">
										<thead>
											<tr>
												<th>王国ID</th>
												<th>内容类型</th>
												<th>发言/评论</th>
												<th>类型</th>
												<th>创建时间</th>
												<th>状态</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody5">
											<c:forEach items="${dataObj.topicFragmentDTO.result}" var="item5">
												<tr class="gradeX">
													<th>${item5.topicId }</th>
													<th>
													<c:choose>
                                                		<c:when test="${item5.contentType == 1}">
                                                			图片
                                                		</c:when>
                                                		<c:otherwise>
                                                			文字
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>
													<c:choose>
                                                		<c:when test="${item5.contentType == 1}">
                                                			${item5.fragmentImage}
                                                		</c:when>
                                                		<c:otherwise>
                                                			${item5.fragment}
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>
													<c:choose>
                                                		<c:when test="${item5.type == 0}">
                                                			主播发言
                                                		</c:when>
                                                		<c:when test="${item5.type == 1}">
                                                			粉丝回复
                                                		</c:when>
                                                		<c:when test="${item5.type == 3}">
                                                			主播贴标
                                                		</c:when>
                                                		<c:when test="${item5.type == 4}">
                                                			粉丝贴标
                                                		</c:when>
                                                		<c:when test="${item5.type == 5}">
                                                			点赞
                                                		</c:when>
                                                		<c:when test="${item5.type == 6}">
                                                			订阅
                                                		</c:when>
                                                		<c:when test="${item5.type == 7}">
                                                			分享
                                                		</c:when>
                                                		<c:when test="${item5.type == 8}">
                                                			关注
                                                		</c:when>
                                                		<c:when test="${item5.type == 9}">
                                                			邀请
                                                		</c:when>
                                                		<c:when test="${item5.type == 10}">
                                                			有人@
                                                		</c:when>
                                                		<c:when test="${item5.type == 11}">
                                                			主播@
                                                		</c:when>
                                                		<c:when test="${item5.type == 12}">
                                                			视频
                                                		</c:when>
                                                		<c:when test="${item5.type == 13}">
                                                			语音
                                                		</c:when>
                                                		<c:when test="${item5.type == 14}">
                                                			国王收红包
                                                		</c:when>
                                                		<c:when test="${item5.type == 15}">
                                                			@核心圈
                                                		</c:when>
                                                	</c:choose>
													</th>
													<th><fmt:formatDate value="${item5.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
													<c:choose>
                                                		<c:when test="${item5.status == '1'}">
                                                			<font color='green'>正常</font>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>删除</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th>
													<c:if test="${item5.status == '1'}">
														<a href="#" onclick="delContent(5,${item5.id })">删除</a>
													</c:if>
													</th>
												</tr>
											</c:forEach>
										</tbody>
									</table>
									<input type="hidden" id="totalPage5" value="${dataObj.topicFragmentDTO.totalPage }" >
								</div>
								<div id="bottomTool" class="row-fluid">
									<div class="span6">
										<div id="DataTables_Table_5_info" class="dataTables_info">当前第 1 页，共 ${dataObj.topicFragmentDTO.totalPage } 页，共${dataObj.topicFragmentDTO.totalPage } </div>
									</div>
									<div class="span6">
										<div class="dataTables_paginate paging_bootstrap pagination">
											<ul id="previousNext">
												<li id="prev_5" onclick="previous(5)" class="prev disabled"><a href="#">上一页</a></li>
												<li id="next_5" onclick="next(5)" class="next ${dataObj.topicFragmentDTO.totalPage<=1?'disabled':''}"><a href="#">下一页</a></li>
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