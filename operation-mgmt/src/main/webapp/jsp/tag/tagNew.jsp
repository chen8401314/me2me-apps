<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@include file="../common/meta.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta charset="utf-8" />
    <title>ZX_IMS 2.0 - 标签新建</title>

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
    			<jsp:param name="t" value="12"/>
    			<jsp:param name="s" value="12_1"/>
    		</jsp:include>
            <!--sidebar end-->

            <!--main content start-->
            <section id="main-content">
            	<form id="form1" action="${ctx}/tag/createTag" method="POST" enctype="multipart/form-data">
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-12">
                            <section class="panel">
                                <header class="panel-heading">基本信息</header>
                                <div class="panel-body">
                                    <div class="row">
										<div class="col-xs-4">
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">标签名</label>
                                            <input type="text" id="tag" name="tag" class="form-control" value="${dataObj.tag }" style="width: 100%" maxlength="8" required>
                                        </div>
                                        </div>
										<div class="col-xs-4">
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">排序号(小数排前面，升序排序)</label>
                                            <input type="text" id="orderNum" name="orderNum" class="form-control" value="0" >
                                        </div>
                                        </div>
										<div class="col-xs-4">
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">父标签</label>
                                            <select name="pid" id="pid" class="form-control">
                                            	<option value="">无</option>
												<c:forEach  var="tag" items="${sysTagList }">
													<option value="${tag.id}" >${tag.tag}</option>
												</c:forEach>
											</select>
                                        </div>
                                        </div>
										<div class="col-xs-4">
                                        <div class="form-group">
                                            <label for="exampleInputFile">是否体系</label>
                                            <select name="isSys" id="isSys" class="form-control">
												<option value="0" ${dataObj.isSys==0?'selected':''}>否</option>
												<option value="1" ${dataObj.isSys==1?'selected':''}>是</option>
											</select>
                                        </div>
                                        </div>
										<div class="col-xs-4">
                                        <div class="form-group">
                                            <label for="exampleInputFile">是否推荐</label>
                                            <select name="isRec" id="isRec" class="form-control">
												<option value="0" ${dataObj.isRec==0?'selected':''}>否</option>
												<option value="1" ${dataObj.isRec==1?'selected':''}>是</option>
											</select>
                                        </div>
                                        </div>
                                        <div class="col-xs-4">
                                        <div class="form-group">
                                            <label for="exampleInputFile">状态</label>
                                            <select name="status" id="status" class="form-control">
												<option value="0" ${dataObj.status==0?'selected':''}>正常</option>
												<option value="1" ${dataObj.status==1?'selected':''}>禁用</option>
											</select>
                                        </div>
                                        </div>
										<div class="col-xs-4">
                                         <div class="form-group">
                                            <label for="exampleInputFile">对应用户喜好</label>
                                             <p>ctrl+鼠标左键多选，误点击可按F5刷新还原，不选时请选中“不选”</p>
                                            <select name="userHobbyIds"  class="form-control" multiple="multiple" size="${userHobbyList.size()+1}"> 
												<option value="">不选</option>
												<c:forEach  var="hobby" items="${userHobbyList }">
													<option value="${hobby.id}" >${hobby.value}</option>
												</c:forEach>
											</select>
                                        </div>
                                        </div>
										<div class="col-xs-4">
										<div class="form-group">
	                                            <label for="exampleInputFile">背景图</label>
                                                <div class="fileupload fileupload-new" data-provides="fileupload">
                                                    <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;">
                                                    </div>
                                                    <div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
                                                    <div>
                                                        <span class="btn btn-white btn-file">
                                                            <span class="fileupload-new"><i class="fa fa-paper-clip"></i>选择上传图片</span>
                                                            <span class="fileupload-exists"><i class="fa fa-undo"></i>修改</span>
                                                            <input type="file" id="image2" name="image2" class="default">
                                                        </span>
                                                        <a href="#" class="btn btn-danger fileupload-exists" data-dismiss="fileupload"><i class="fa fa-trash"></i>删除</a>
                                                    </div>
                                                </div>
	                                        </div>
	                                     </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                    <input type="submit" id="btnSave" value="提交" class="btn btn-danger" />
                    <span class="btn btn-default"><a href="${ctx}/tag/query">返回</a></span>
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
