<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 标签管理</title>

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
		url : "${ctx}/tag/page?isRec="+$("#isRec").val()+"&topicCountEnd="+$("#topicCountEnd").val()+"&topicCountStart="+$("#topicCountStart").val()+"&isSys="+$("#isSys").val()+"&tagName="+$("#tagName").val()+"&startTime="+$("#startTime").val()+"&endTime="+$("#endTime").val()+"&page="+page+"&pageSize="+pageSize,
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
		url : "${ctx}/tag/page?isRec="+$("#isRec").val()+"&topicCountEnd="+$("#topicCountEnd").val()+"&topicCountStart="+$("#topicCountStart").val()+"&isSys="+$("#isSys").val()+"&tagName="+$("#tagName").val()+"&startTime="+$("#startTime").val()+"&endTime="+$("#endTime").val()+"&page="+page+"&pageSize="+pageSize,
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
			bodyHtml = bodyHtml + "<td>"+dataList[i].tagName+"</td>";
			bodyHtml = bodyHtml + "<td>"+parserDatetimeStr(new Date(dataList[i].createTime))+"</td>";
			bodyHtml = bodyHtml + "<td>";
			if(dataList[i].isSys == 0){
				bodyHtml = bodyHtml + "否";
			}else{
				bodyHtml = bodyHtml + "是";
			}
			bodyHtml = bodyHtml + "</td>";
			bodyHtml = bodyHtml + "<td>";
			if(dataList[i].isRec == 0){
				bodyHtml = bodyHtml + "否";
			}else{
				bodyHtml = bodyHtml + "是";
			}
			bodyHtml = bodyHtml + "</td>";
			bodyHtml = bodyHtml + "<td>"+(dataList[i].ordernum ||0)+"</td>";
			bodyHtml = bodyHtml + "<td>";
			if(dataList[i].status == 0){
				bodyHtml = bodyHtml + "正常";
			}else{
				bodyHtml = bodyHtml + "<font color='red'>禁用</font>";
			}
			bodyHtml = bodyHtml + "</td>";
			bodyHtml = bodyHtml + "<td>"+dataList[i].topicCount+"</td>";
			bodyHtml = bodyHtml + "<td>";
			bodyHtml = bodyHtml + "<a href=\"${ctx}/tag/f/"+dataList[i].id+"\">编辑</a>";
			bodyHtml = bodyHtml + "|<a href=\"${ctx}/tag/topicList/query?tagId="+dataList[i].id+"\">查看王国列表</a>";
			bodyHtml = bodyHtml + "</td>";
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
			<jsp:param name="t" value="12" />
			<jsp:param name="s" value="12_1" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/tag/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										标签名
										<input type="text" id="tagName" name="tagName" value="${dataObj.tagName }" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
										创建时间
										<input type="text" id="startTime" name="startTime" value="${dataObj.startTime }" class="form-control">
										-
										<input type="text" id="endTime" name="endTime" value="${dataObj.endTime }" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
										是否体系
										<select name="isSys" id="isSys" class="form-control">
											<option value="-1" ${dataObj.isSys==-1?'selected':''}>所有</option>
											<option value="0" ${dataObj.isSys==0?'selected':''}>否</option>
											<option value="1" ${dataObj.isSys==1?'selected':''}>是</option>
										</select>
									</div>
									<br/>
									<div class="form-inline" role="form">
										王国数
										<input type="text" id="topicCountStart" name="topicCountStart" value="${dataObj.topicCountStart }" class="form-control">
										-
										<input type="text" id="topicCountEnd" name="topicCountEnd" value="${dataObj.topicCountEnd }" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
										是否推荐
										<select name="isRec" id="isRec" class="form-control">
											<option value="-1" ${dataObj.isRec==-1?'selected':''}>所有</option>
											<option value="0" ${dataObj.isRec==0?'selected':''}>否</option>
											<option value="1" ${dataObj.isRec==1?'selected':''}>是</option>
										</select>&emsp;
										父标签
										<select name="pid" id="pid" class="form-control">
											<option value="">全部</option>
											<c:forEach  var="tag" items="${sysTagList }">
												<option value="${tag.id}" ${dataObj.pid==tag.id?'selected':''}>${tag.tag}</option>
											</c:forEach>
										</select>&nbsp;&nbsp;&nbsp;&nbsp;
										<input type="submit" id="btnSearch" name="btnSearch" value="搜索" class="btn btn-info" />
										
										<a class="btn btn-danger" href="./tagNew">新建标签</a>
										
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
								| 标签列表
								<span class="tools pull-right">
									<a href="${ctx}/tag/tagNew" class="fa fa-plus add_link" title="新增标签" ></a>
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table">
										<thead>
											<tr>
												<th>标签名</th>
												<th>创建时间</th>
												<th>是否体系</th>
												<th>是否推荐</th>
												<th>排序值</th>
												<th>状态</th>
												<th>王国数</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody id="tbody">
											<c:forEach items="${dataObj.result}" var="item">
												<tr class="gradeX">
													<td>${item.tagName }</td>
													<td><fmt:formatDate value="${item.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													<td>
													<c:choose>
                                                		<c:when test="${item.isSys == '0'}">
                                                			否
                                                		</c:when>
                                                		<c:otherwise>
                                                			是
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													<td>
													<c:choose>
                                                		<c:when test="${item.isRec == '0'}">
                                                			否
                                                		</c:when>
                                                		<c:otherwise>
                                                			是
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													<td>${item.orderNum}</td>
													<td>
													<c:choose>
                                                		<c:when test="${item.status == '0'}">
                                                			正常
                                                		</c:when>
                                                		<c:otherwise>
                                                			<font color='red'>禁用</font>
                                                		</c:otherwise>
                                                	</c:choose>
													</td>
													
													<td>${item.topicCount }</th>
													<td>
													<a href="${ctx}/tag/f/${item.id }">编辑</a>
													|<a href="${ctx}/tag/topicList/query?tagId=${item.id }">查看王国列表</a>
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
	<script src="${ctx}/js/common-scripts.js"></script>
	<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
	$.fn.datetimepicker.dates['zh'] = {  
            days:       ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"],  
            daysShort:  ["日", "一", "二", "三", "四", "五", "六","日"],  
            daysMin:    ["日", "一", "二", "三", "四", "五", "六","日"],  
            months:     ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月","十二月"],  
            monthsShort:  ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],  
            meridiem:    ["上午", "下午"],  
            //suffix:      ["st", "nd", "rd", "th"],  
            today:       "今天"  
    };
	$('#startTime').datetimepicker({
		format: 'yyyy-mm-dd',
		language: 'zh',
		startView: 2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1,
		minView:2
		});
	$('#endTime').datetimepicker({
		format: 'yyyy-mm-dd',
		language: 'zh',
		startView: 2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1,
		minView:2
		});
	</script>
</body>
</html>