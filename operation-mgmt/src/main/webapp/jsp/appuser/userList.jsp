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
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script type="text/javascript">
var modifyLevel = function(uid, nickName, level){
	$("#cUid").val(uid);
	$("#cName").val(nickName);
	$("#cLevel").val(level);
	$("#cNewLevel").val('');
	$("#changeLevelModal").modal();
}

var modifyCommit = function(){
	var uid = $("#cUid").val();
	var newLevel = $("#cNewLevel").val();
	if(newLevel == ''){
		alert('请填写新等级');
		return;
	}
	if(newLevel<=0 || newLevel>9){
		alert('请填写合适的等级（1-9）');
		return;
	}

	var $tr=$("tr[key='"+uid+"']");
	$.ajax({
		url : "${ctx}/appuser/userLevel/modify?u="+uid+"&l="+newLevel,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			if(resp == '0'){
				alert('保存成功');
				$tr.find("th:eq(7)").text(newLevel);
			}else{
				alert(resp);
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
				<form id="form1" action="${ctx}/appuser/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										手机号
										<input type="text" id="mobile" name="mobile" value="${dataObj.mobile }" class="form-control">&nbsp;&nbsp;
										昵称
										<input type="text" id="nickName" name="nickName" value="${dataObj.nickName }" class="form-control">&nbsp;&nbsp;
										是否大V
										<select name="isV" id="isV" class="form-control">
											<option value="0" ${dataObj.isV==0?'selected':''}>全部</option>
											<option value="1" ${dataObj.isV==1?'selected':''}>是</option>
											<option value="2" ${dataObj.isV==2?'selected':''}>否</option>
										</select>&nbsp;&nbsp;&nbsp;&nbsp;
										ME号
										<input type="text" id="meCode" name="meCode" value="${dataObj.meCode }" class="form-control">
									</div>
									<br/>
									<div class="form-inline" role="form">
										开始时间
										<input type="text" id=startTime name="startTime" value="${dataObj.startTime }" class="form-control">&nbsp;&nbsp;
										结束时间
										<input type="text" id="endTime" name="endTime" value="${dataObj.endTime }" class="form-control">&nbsp;&nbsp;
										状态
										<select name="status" id="status" class="form-control">
											<option value="0" ${dataObj.status==0?'selected':''}>全部</option>
											<option value="1" ${dataObj.status==1?'selected':''}>正常</option>
											<option value="2" ${dataObj.status==2?'selected':''}>封号</option>
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
												<th>ME号</th>
												<th>手机</th>
												<th>昵称</th>
												<th>性别</th>
												<th>生日</th>
												<th>是否大V</th>
												<th>等级</th>
												<th>状态</th>
												<th>创建时间</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj.data.result}" var="userItem">
												<tr class="gradeX" key="${userItem.uid }">
													<th>${userItem.uid }</th>
													<th>${userItem.meCode }</th>
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
													<th>${userItem.level }</th>
													<th>
													<c:choose>
                                                		<c:when test="${userItem.status == '0'}">
                                                			正常
                                                		</c:when>
                                                		<c:otherwise>
                                                			封号
                                                		</c:otherwise>
                                                	</c:choose>
													</th>
													<th><fmt:formatDate value="${userItem.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></th>
													<th>
													<c:choose>
                                                		<c:when test="${userItem.vlv == '1'}">
                                                			<a href="${ctx}/appuser/option/vlv?m=${userItem.nickName }&a=2&i=${userItem.uid}">取消大V</a>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<a href="${ctx}/appuser/option/vlv?m=${userItem.nickName }&a=1&i=${userItem.uid}">上大V</a>
                                                		</c:otherwise>
                                                	</c:choose>
                                                	|<a href="${ctx}/appuser/gaguser/add/${userItem.uid }">禁言</a>
                                                	|<c:choose>
                                                		<c:when test="${userItem.status == '0'}">
                                                			<a href="${ctx}/appuser/option/status?m=${userItem.nickName }&a=1&i=${userItem.uid}">封号</a>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<a href="${ctx}/appuser/option/status?m=${userItem.nickName }&a=2&i=${userItem.uid}">恢复</a>
                                                		</c:otherwise>
                                                	</c:choose>
                                                	|<a href="${ctx}/appcontent/init/${userItem.uid }">查看内容</a>
                                                	|<a href="#" onclick="modifyLevel(${userItem.uid }, '${userItem.nickName }', ${userItem.level })">修改等级</a>
													</th>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<th>UID</th>
												<th>ME号</th>
												<th>手机</th>
												<th>昵称</th>
												<th>性别</th>
												<th>生日</th>
												<th>是否大V</th>
												<th>等级</th>
												<th>状态</th>
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
	
	<!-- modal VIEW -->
	<div class="modal fade" id="changeLevelModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">修改用户等级</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">用户</label>
                        <input type="text" id="cName" name="cName" class="form-control" style="width: 100%" readonly>
                        <input type="hidden" id="cUid" name="cUid">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">当前等级</label>
                        <input type="text" id="cLevel" name="cLevel" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">新等级</label>
                        <input type="number" id="cNewLevel" name="cNewLevel" class="form-control" style="width: 100%">
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn" data-dismiss="modal" aria-hidden="true" onclick="modifyCommit()">更改</button>
					<button data-dismiss="modal" class="btn btn-default" type="button">关闭</button>
				</div>
			</div>
		</div>
	</div>
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
		format: 'yyyy-mm-dd hh:ii:ss',
		language: 'zh',
		startView: 4,
		autoclose:true,
		weekStart:1,
		todayBtn:  1
		});
	$('#endTime').datetimepicker({
		format: 'yyyy-mm-dd hh:ii:ss',
		language: 'zh',
		startView: 4,
		autoclose:true,
		weekStart:1,
		todayBtn:  1
		});
	</script>
</body>
</html>