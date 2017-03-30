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
      <script src="${ctx}/js/jquery.js"></script>
        <script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
        <script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
        <script src="${ctx}/js/bootstrap.min.js"></script>
        <script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
        <script src="${ctx}/js/jquery.scrollTo.min.js"></script>
        <script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
        <script src="${ctx}/js/respond.min.js"></script>
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
    			<jsp:param name="t" value="10"/>
    			<jsp:param name="s" value="10_1"/>
    		</jsp:include>
            <!--sidebar end-->

            <!--main content start-->
            <section id="main-content">
            	<form id="form1" action="./doSaveRanking" method="POST"  enctype="multipart/form-data">
            		<input type="hidden" name="id" value="${item.id}"/>
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-12">
                            <section class="panel">
                                <header class="panel-heading">基本信息</header>
                                <div class="panel-body">
                                    <div role="row">
                                    	<div class="col-md-6">
	                                        <div class="form-group">
	                                            <label for="exampleInputFile">名称</label>
	                                            <input name="name"  class="form-control" value="${item.name}" required/>
	                                        </div>
	                                       <c:if test="${item==null }">
		                                       <div class="form-group">
		                                            <label for="exampleInputEmail1">榜单模式</label>
		                                            <select name="mode" class="form-control" value="${item.type}">
														<option value="0">手动榜单</option>
														<option value="1">最活跃的米汤新鲜人</option>
														<option value="2">最受追捧的米汤大咖</option>
														<option value="3">最爱叨逼叨的话痨国王</option>
														<option value="4">这里的互动最热闹</option>
														<option value="5">最丰富多彩的王国</option>
														<option value="6">求安慰的孤独王国</option>
														<option value="7">最新更新的王国</option>
													</select>
													<label class="text-danger">保存之后不可修改。</label>
		                                        </div>
		                                        <div class="form-group">
		                                            <label for="exampleInputEmail1">榜单类型</label>
		                                            <select name="type" class="form-control" value="${item.type}">
														<option value="1">王国榜单</option>
														<option value="2">用户榜单</option>
														<option value="3">榜单集合</option>
													</select>
													<label class="text-danger">保存之后不可修改。</label>
		                                        </div>
		                                        <script type="text/javascript">
		                                        	var target=$("select[name='type']");
		                                        	$("select[name='mode']").change(function(){
		                                        		target.find("option[selected]").removeAttr("selected");
		                                        		var val =$(this).val();
		                                        		if(val=="0"){
		                                        			target.removeAttr("readonly");
		                                        		}else{
		                                        			target.attr("readonly","readonly")
		                                        			if(val=="1" || val=="2" || val=="3" ){
		                                        				target.find("option[value='2']").attr("selected","selected");
		                                        			}else{
		                                        				target.find("option[value='1']").attr("selected","selected");
		                                        			}
		                                        		}
		                                        	})
		                                        </script>
	                                        </c:if>
	                                        <div class="form-group">
	                                            <label for="exampleInputFile">摘要内容</label>
	                                            <textarea rows="4" style="width:100%" name="summary" >${item.summary}</textarea>
	                                        </div>
                                        </div>
                                        <div class="col-md-6">
                                       		<div class="form-group">
	                                            <label for="exampleInputFile">背景色</label>
	                                            <input name="bgColor"  class="form-control" value="${item.bgColor}" />
	                                        </div>
	                                        
	                                        <div class="form-group">
	                                            <label for="exampleInputFile">背景图</label>
                                                <div class="fileupload fileupload-new" data-provides="fileupload">
                                                    <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;">
                                                    	<c:if test="${item.image!=null }">
                                                        <img src="http://cdn.me-to-me.com/${item.image}" alt="" />
                                                        </c:if>
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
	                                        <div class="form-group">
	                                            <label for="exampleInputFile">宽</label>
	                                            <input name="imgWidth"  class="form-control" value="${item.imgWidth}" />
	                                        </div>
	                                        <div class="form-group">
	                                            <label for="exampleInputFile">高</label>
	                                            <input name="imgHeight"  class="form-control" value="${item.imgHeight}" />
	                                        </div>
                                        </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                    <input type="submit" id="btnSave" value="提交" class="btn btn-danger" />
                    <span class="btn btn-default"><a href="./list_ranking">返回</a></span>
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

        <!--custom switch-->
        <script src="${ctx}/js/bootstrap-switch.js"></script>
        <!--custom tagsinput-->
        <script src="${ctx}/js/jquery.tagsinput.js"></script>
        <!--script for this page-->
        <!--common script for all pages-->
        <script src="${ctx}/js/common-scripts.js"></script>

        <script src="${ctx}/js/xheditor-1.2.2.min.js"></script>
        <script src="${ctx}/js/xheditor_lang/zh-cn.js"></script>
        <script src="${ctx}/js/xheditSelf.js"></script>
</body>
</html>
