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
    			<jsp:param name="t" value="11"/>
    			<jsp:param name="s" value="11_2"/>
    		</jsp:include>
            <!--sidebar end-->

            <!--main content start-->
            <section id="main-content">
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-12">
                        	 <a href="./addHotKeyword" class="btn btn-primary">添加热词</a>
                            <section class="panel">
                                <div class="panel-body">
                                    <table class="table  table-striped" id="dataTable" width="100%">
                                    	<thead>
                                    	<tr>
                                    		<th>名称</th>
                                    		<th>状态</th>
                                    		<th>排序号</th>
                                    		<th>创建日期</th>
                                    		<th>操作</th>
                                    	</tr>
                                    	</thead>
                                    	<tbody>
                                    	<c:forEach items="${dataList}" var="item">
	                                    	<tr data-id="${item.id }">
	                                    		<td>${item.keyword }</td>
	                                    		<td>${item.isValid }</td>
	                                    		<td>${item.orderNum }</td>
	                                    		<td>${item.creationDate }</td>
	                                    		<td>
	                                    			<a class="btn btn-warning btn-xs moveup" href="#">上移</a>
																<a class="btn btn-warning btn-xs movedown" href="#">下移</a>
																<a class="btn btn-danger btn-xs del" href="#" data-id="${item.id}">删除</a>
	                                    		</td>
	                                    	</tr>
                                    	</c:forEach>
	                                    </tbody>
                                    </table>
                                </div>
                            </section>
                        </div>
                    </div>
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


        <script src="${ctx}/js/jquery.js"></script>
        <script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
        <script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
        <script src="${ctx}/js/bootstrap.min.js"></script>
        <script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
        <script src="${ctx}/js/jquery.scrollTo.min.js"></script>
        <script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
        <script src="${ctx}/js/respond.min.js"></script>
		<script src="${ctx}/js/jquery.json-2.4.min.js"></script>
		<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
		<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
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
        
        <script type="text/javascript" src="${ctx}/js/bootbox.min.js"></script>
      
        <!--right slidebar-->
        <script src="${ctx}/js/slidebars.min.js"></script>

        <!--custom switch-->
        <script src="${ctx}/js/bootstrap-switch.js"></script>
        <!--custom tagsinput-->
        <script src="${ctx}/js/jquery.tagsinput.js"></script>
        <!--common script for all pages-->
        <script src="${ctx}/js/common-scripts.js"></script>

        <script src="${ctx}/js/xheditor-1.2.2.min.js"></script>
        <script src="${ctx}/js/xheditor_lang/zh-cn.js"></script>
        <script src="${ctx}/js/xheditSelf.js"></script>
        <script src="${ctx}/js/messager/js/messenger.min.js"></script>
		<script src="${ctx}/js/messager/js/messenger-theme-flat.js"></script>
		<link rel="stylesheet" href="${ctx}/js/messager/css/messenger.css" />
		<link rel="stylesheet" href="${ctx}/js/messager/css/messenger-theme-flat.css" />

    <script>
    var baseKingdomURL = "${baseKingdomURL}";
    Messenger.options = {
    		hideAfter:1,
		    extraClasses: 'messenger-fixed messenger-on-top',
		    theme: 'flat'
		}
    	$("a.dialog").click(function(){
    		var url =$(this).attr("href");
    		bootbox.dialog({ 
    			size:"large",
    			message: "<iframe src='"+url+"' style='width:100%;min-height:750px;border:0px;'></iframe>" 
    		})
    		return false;
    	})
    	

	var maxSortNum= 0;
	$(document).on("click",".del",function(){
		var tr=$(this).closest("tr");
		if(confirm("确认删除吗？")){
			$.post("./deleteHotKeyword",{id:tr.attr("data-id"),r:Math.random()},function(data){
				if(data.code==1){
					location.reload();
				}else{
					alert(data.desc);
				}
			})
		}
	})
	$(document).on("click",".moveup,.movedown",function(){		// 添加榜单
		//交换位置。
		var cur=$(this).closest("tr");
		if($(this).hasClass("movedown")){
			cur.insertAfter(cur.next());
		}else{
			cur.insertBefore(cur.prev())
		}
		// 扫描顺序
		var data=[];
		$("tbody>tr[data-id]").each(function(index,obj){
			data.push({
				id:$(this).attr("data-id"),
				orderNum:index
			})
		})
		var json = $.toJSON(data);
		$.post("./updateHotKeyword",{json:json,r:Math.random()},function(data){
			if(data.code==1){
				
			}else{
				alert(data.desc);
			}
		})
	})
	
    </script>
</body>
</html>
