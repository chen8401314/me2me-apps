<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 缓存配置列表</title>

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
//1 sring ; 2 set ; 3 list ; 4 map
var modifyConfig = function(key, type){
	if(type == 1){//string
		$("#cstringKey").val(key);
		$("#stringConfigModal").modal();
	}else if(type == 2){//set
		$("#csetKey").val(key);
		$("#setConfigModal").modal();
	}
}

var modifyCommit = function(type){
	var key = "";
	var value = "";
	if(type == 1){
		key = $("#cstringKey").val();
		value = $("#cstringValue").val();
	}else if(type == 2){
		key = $("#csetKey").val();
		value = $("#csetValue").val();
	}
	if(key == ""){
		alert("key不能为空");
		return;
	}
	if(value == ""){
		alert("value不能为空");
		return;
	}
	
	$.ajax({
		url : "${ctx}/appconfig/cache/modify?k="+key+"&v="+value+"&t="+type,
		async : false,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(resp) {
			if(resp == '0'){
				window.location.reload();
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
			<jsp:param name="t" value="7" />
			<jsp:param name="s" value="7_2" />
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
								| 缓存配置列表 
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
												<th>配置项</th>
												<th>KEY</th>
												<th>VALUE</th>
												<th>类型</th>
												<th>操作</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${dataObj}" var="configItem" varStatus="status">
												<tr class="gradeX">
													<th>${status.index + 1}</th>
													<th>${configItem.desc }</th>
													<th>${configItem.key }</th>
													<th>${configItem.value }</th>
													<th>${configItem.type.desc }</th>
													<th><a href="#" onclick="modifyConfig('${configItem.key }', ${configItem.type.type })">编辑</a></th>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<th>序号</th>
												<th>配置项</th>
												<th>KEY</th>
												<th>VALUE</th>
												<th>类型</th>
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
	<div class="modal fade" id="stringConfigModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">缓存配置更新[String]</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">Key</label>
                        <input type="text" id="cstringKey" name="cstringKey" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Value</label>
                        <input type="text" id="cstringValue" name="cstringValue" class="form-control" style="width: 100%">
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn" data-dismiss="modal" aria-hidden="true" onclick="modifyCommit(1)">更改</button>
					<button data-dismiss="modal" class="btn btn-default" type="button">关闭</button>
				</div>
			</div>
		</div>
	</div>

	<!-- modal VIEW -->
	<div class="modal fade" id="setConfigModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">缓存配置更新[SET]</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">Key</label>
                        <input type="text" id="csetKey" name="csetKey" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Value</label>
                        <input type="text" id="csetValue" name="csetValue" class="form-control" style="width: 100%" required>
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn" data-dismiss="modal" aria-hidden="true" onclick="modifyCommit(2)">增加</button>
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
	<script src="${ctx}/js/dynamic_table_init_0_asc.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
</body>
</html>