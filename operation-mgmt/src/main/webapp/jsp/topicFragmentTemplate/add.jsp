<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@include file="../common/meta.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta charset="utf-8" />
    <title>ZX_IMS 2.0 - 新建APP版本</title>

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
    			<jsp:param name="t" value="7"/>
    			<jsp:param name="s" value="7_6"/>
    		</jsp:include>
            <!--sidebar end-->

            <!--main content start-->
            <section id="main-content">
            	<form id="form1" action="./doSave" method="POST">
            		<input type="hidden" name="id" value="${item.id}"/>
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-12">
                            <section class="panel">
                                <header class="panel-heading">基本信息</header>
                                <div class="panel-body">
                                    <div role="form">
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">模板类型</label>
                                            <select name="type" id="type" class="form-control" value="${item.type}">
												<option value="1" selected="selected">足迹模板</option>
											</select>
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputFile">内容类型</label>
                                            <select name="contenttype" id="contenttype" class="form-control" value="${item.contenttype}">
												<option value="0">文本</option>
												<!-- 暂时没有下面的几种类型，等有了再放开
												<option value="1">图片</option>
												<option value="2">视频</option>
												<option value="3">音频</option>
												 -->
											</select>
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputFile">模板内容</label>
                                            <textarea rows="4" style="width:100%" id="content" name="content" required>${item.content}</textarea>
                                        </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                    <input type="submit" id="btnSave" value="提交" class="btn btn-danger" />
                    <span class="btn btn-default"><a href="${ctx}/appconfig/version/query">返回</a></span>
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

        <!--this page plugins-->

        <script type="text/javascript" src="${ctx}/assets/fuelux/js/spinner.min.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-fileupload/bootstrap-fileupload.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-wysihtml5/wysihtml5-0.3.0.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-wysihtml5/bootstrap-wysihtml5.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-daterangepicker/moment.min.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-daterangepicker/daterangepicker.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-colorpicker/js/bootstrap-colorpicker.js"></script>
        <script type="text/javascript" src="${ctx}/assets/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
        <script type="text/javascript" src="${ctx}/assets/jquery-multi-select/js/jquery.multi-select.js"></script>
        <script type="text/javascript" src="${ctx}/assets/jquery-multi-select/js/jquery.quicksearch.js"></script>

        <script type="text/javascript" src="${ctx}/assets/bootstrap-inputmask/bootstrap-inputmask.min.js"></script>
        <!--right slidebar-->
        <script src="${ctx}/js/slidebars.min.js"></script>

        <!--dynamic table initialization -->
        <script src="${ctx}/js/dynamic_table_init.js"></script>
        <!--custom switch-->
        <script src="${ctx}/js/bootstrap-switch.js"></script>
        <!--custom tagsinput-->
        <script src="${ctx}/js/jquery.tagsinput.js"></script>
        <!--script for this page-->
        <script src="${ctx}/js/form-component.js"></script>
        <!--common script for all pages-->
        <script src="${ctx}/js/common-scripts.js"></script>
        <script src="${ctx}/js/advanced-form-components.js"></script>

        <script src="${ctx}/js/xheditor-1.2.2.min.js"></script>
        <script src="${ctx}/js/xheditor_lang/zh-cn.js"></script>
        <script src="${ctx}/js/xheditSelf.js"></script>
    
</body>
</html>
