<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 新建PGC</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet">
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet">
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-fileupload/bootstrap-fileupload.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-wysihtml5/bootstrap-wysihtml5.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datepicker/css/datepicker.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-timepicker/compiled/timepicker.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-colorpicker/css/colorpicker.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-daterangepicker/daterangepicker-bs3.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/jquery-multi-select/css/multi-select.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet">
<link href="${ctx}/css/style.css" rel="stylesheet">
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<script type="text/javascript">
    var errMsg = '${errMsg}';
    if(errMsg && errMsg != 'null' && errMsg != ''){
    	alert(errMsg);
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
			<jsp:param name="t" value="2" />
			<jsp:param name="s" value="2_2" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<form id="form1" action="${ctx}/pgc/create" method="POST" enctype="multipart/form-data">
				<section class="wrapper">
					<!-- page start-->
					<div class="row">
						<div class="col-lg-6">
							<section class="panel">
								<header class="panel-heading">基本信息</header>
								<div class="panel-body">
									<div role="form">
										<div class="form-group">
											<label for="exampleInputFile">文章标题</label>
											<input type="text" id="txtTitle" name="txtTitle" class="form-control" style="width: 100%" required>
											<input type="hidden" id="HFImg" name="HFImg" />
										</div>
										<div class="form-group">
											<label for="exampleInputFile">文章标签</label>
											<input type="text" id="txtTags" name="txtTags" class="tagsinput" style="width: 100%">
										</div>
									</div>
								</div>
							</section>
						</div>
						<div class="col-lg-6">
							<section class="panel">
								<header class="panel-heading">文章信息</header>
								<div class="panel-body">
									<div role="form">
										<div class="col-md-9">
											<div class="fileupload fileupload-new" data-provides="fileupload">
												<div class="fileupload-new thumbnail" style="width: 200px; height: 150px;">
													<img src="http://www.placehold.it/200x150/EFEFEF/AAAAAA&text=no+image" alt="" />
												</div>
												<div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
												<div>
													<span class="btn btn-white btn-file">
														<span class="fileupload-new"><i class="fa fa-paper-clip"></i>Select image</span>
														<span class="fileupload-exists"><i class="fa fa-undo"></i>Change</span>
														<input type="file" id="fuCoverImg" name="fuCoverImg" class="default" />
													</span>
													<a href="#" class="btn btn-danger fileupload-exists" data-dismiss="fileupload"><i class="fa fa-trash"></i>Remove</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</section>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">文章内容</header>
								<div class="panel-body">
									<div role="form">
										<div class="form-group">
											<textarea id="txtContent" name="txtContent" rows="20" cols="200" style="width: 100%"></textarea>
										</div>
									</div>
								</div>
							</section>
						</div>
					</div>
					<input type="submit" id="btnSave" Value="提交" class="btn btn-danger" />
					<span class="btn btn-default"><a href="${ctx}/pgc/query">返回</a></span>
				</section>
				<!-- page end-->
			</form>
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
	<script src="${ctx}/js/jquery.js"></script>
	<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
	<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
	<script src="${ctx}/js/bootstrap.min.js"></script>
	<script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
	<script src="${ctx}/js/jquery.scrollTo.min.js"></script>
	<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctx}/assets/advanced-datatable/media/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="${ctx}/assets/data-tables/DT_bootstrap.js"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script type="text/javascript" src="${ctx}/assets/bootstrap-fileupload/bootstrap-fileupload.js"></script>
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/dynamic_table_init.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
	<script src="${ctx}/js/xheditor-1.2.2.min.js"></script>
	<script src="${ctx}/js/xheditor_lang/zh-cn.js"></script>
	<script src="${ctx}/js/xheditSelf.js"></script>
</body>
</html>

