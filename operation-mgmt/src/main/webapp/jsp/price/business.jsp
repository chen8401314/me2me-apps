<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@include file="../common/meta.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta charset="utf-8" />
    <title>ZX_IMS 2.0 - 王国交易</title>

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
    
    function modalShow(){
    	$("#myModal").modal('show');
    }
    
    var searchUser = function(){
    	var uid = $("#s_uid").val();
    	var nickName = $("#s_nickName").val();
    	if(uid == '' && nickName == ''){
    		alert('请至少输入一个条件');
    		return;
    	}
    	if(uid == ''){
    		uid = 0;
    	}
    	$.ajax({
    		url : "${ctx}/price/searchUser?uid="+uid+"&nickName="+nickName,
    		async : false,
    		type : "GET",
    		contentType : "application/json;charset=UTF-8",
    		success : function(resp) {
    			buildTableBody(resp);
    		}
    	});
    }
    
    var buildTableBody = function(resp){
    	var r = eval("("+resp+")");
    	var result = r.result;
    	
    	var bodyHtml = "";
    	if(result && result.length > 0){
    		for(var i=0;i<result.length;i++){
    			bodyHtml = bodyHtml + "<tr class=\"gradeX\">";
    			bodyHtml = bodyHtml + "<th>"+result[i].uid+"</th>";
    			bodyHtml = bodyHtml + "<th>"+result[i].nickName+"</th>";
    			bodyHtml = bodyHtml + "<th><a class=\"btn btn-warning btn-xs\" href=\"javascript:packSelect("+result[i].uid+",'"+result[i].nickName+"');\">选择</a></th>";
    			bodyHtml = bodyHtml + "</tr>";
    		}
    	}
    	$("#tbody").html(bodyHtml);
    }
    
    var packSelect = function(newUid, newNickName){
    	$("#newUid").val(newUid);
    	$("#newNickName").val(newNickName);
    	$("#myModal").modal('hide');
    }
    
    var business = function(){
    	var kid = $("#topicId").val();
    	var ouid = $("#uid").val();
    	var nuid = $("#newUid").val();
    	
    	var newNickName = $("#newNickName").val();
    	if(newNickName == ''){
    		alert('请选择一个新的用户');
    		return;
    	}
    	
    	 if(confirm("确定变更王国["+$("#title").val()+"]的国王由["+$("#nickName").val()+"]变更为["+$("#newNickName").val()+"]？")){
    		 
    	 }
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
    			<jsp:param name="t" value="13"/>
    			<jsp:param name="s" value="13_1"/>
    		</jsp:include>
            <!--sidebar end-->

            <!--main content start-->
            <section id="main-content">
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-12">
                            <section class="panel">
                                <header class="panel-heading">基本信息</header>
                                <div class="panel-body">
                                    <div role="form">
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">王国标题</label>
                                            <input type="text" id="title" name="title" class="form-control" value="${dataObj.title }" style="width: 100%" readonly>
                                            <input type="hidden" id="topicId" name="topicId" class="form-control" value="${dataObj.topicId }">
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">国王价值</label>
                                            <input type="text" id="price" name="price" class="form-control" value="${dataObj.price }" style="width: 100%" readonly>
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">原国王UID</label>
                                            <input type="text" id="uid" name="uid" class="form-control" value="${dataObj.uid }" style="width: 100%" readonly>
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">原国王昵称</label>
                                            <input type="text" id="nickName" name="nickName" class="form-control" value="${dataObj.nickName }" style="width: 100%" readonly>
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputEmail1">新国王昵称</label>
                                            <input type="text" id="newNickName" name="newNickName" class="form-control" readonly>
                                            <input type="button"  value="选择新国王" class="btn btn-danger"  onclick="modalShow();"/>
                                            <input type="hidden" id="newUid" name="newUid" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                    <input type="button" id="btnSave" value="交 易" class="btn btn-danger" onclick="business()"/>
                    <span class="btn btn-default"><a href="${ctx}/price/kingdomQuery">返回</a></span>
                </section>
                <!-- page end-->
            </section>

            <!--main content end-->

            <!-- Right Slidebar start -->
            <%@include file="../common/rightSlidebar.jsp"%>
            <!-- Right Slidebar end -->

            <!--footer start-->
            <%@include file="../common/footer.jsp"%>
            <!--footer end-->
        </section>
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<section class="panel">
					<header class="panel-heading">执行操作<span class="tools pull-right"><button type="button" class="btn btn-default" data-dismiss="modal">关闭</button></span>
					</header>
					<div class="panel-body">
						<div class="form-inline" role="form">
							用户UID
							<input type="text" id="s_uid" name="s_uid" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
							用户昵称
							<input type="text" id="s_nickName" name="s_nickName" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" id="btnSearch" name="btnSearch" onclick="searchUser()" value="搜索" class="btn btn-info" />
						</div>
					</div>
				</section>
				<section class="panel">
					<header class="panel-heading">
						| 用户列表 
					</header>
					<div class="panel-body">
						<div class="adv-table">
							<table class="display table table-bordered table-striped" id="dynamic-table">
								<thead>
									<tr>
										<th>UID</th>
										<th>昵称</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody id="tbody">
								</tbody>
							</table>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>

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
