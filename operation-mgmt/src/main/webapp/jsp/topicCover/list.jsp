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
    			<jsp:param name="s" value="12_6"/>
    		</jsp:include>
            <!--sidebar end-->

            <!--main content start-->
            <section id="main-content">
            		<input type="hidden" name="id" value="${item.id}"/>
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-12">
                            <section class="panel">
                                <header class="panel-heading">
                                	王国默认封面图管理 &emsp;
									<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">
									  批量上传
									</button>
                                </header>
                                <div class="panel-body">
                                    <table class="table  table-striped" id="dataTable" width="100%"></table>
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

        
        <script type="text/javascript" src="${ctx}/js/bootbox.min.js"></script>
        <!--right slidebar-->
        <script src="${ctx}/js/slidebars.min.js"></script>

        <!--custom switch-->
        <script src="${ctx}/js/bootstrap-switch.js"></script>
        <!--custom tagsinput-->
        <script src="${ctx}/js/jquery.tagsinput.js"></script>
        <!--common script for all pages-->
        <script src="${ctx}/js/common-scripts.js"></script>

        <script src="${ctx}/js/messager/js/messenger.min.js"></script>
		<script src="${ctx}/js/messager/js/messenger-theme-flat.js"></script>
		<link rel="stylesheet" href="${ctx}/js/messager/css/messenger.css" />
		<link rel="stylesheet" href="${ctx}/js/messager/css/messenger-theme-flat.css" />
		
		<link rel="stylesheet" href="${ctx}/js/bootstrap-fileinput-master/css/fileinput.min.css" />
		<script src="${ctx}/js/bootstrap-fileinput-master/js/fileinput.min.js"></script>

    <script>
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
    	
  
	$.fn.dataTable.ext.errMode="console";
	$.extend( $.fn.dataTable.defaults, {
		pageLength: 999999,
		searching: false,
		sorting:false,
        dom: 'tp',
	    processing: true,
	    serverSide: true,
    	language: {
    		processing:     "加载中...",
    	    search:         "搜索中&nbsp;:",
    	    "lengthMenu":     "每页显示 _MENU_ 条",
    	    "info":           "当前 _START_ 到 _END_ 条，共 _TOTAL_ 条",
    	    "infoEmpty":      "当前第0条",
    	    "infoFiltered":   "(filtered from _MAX_ total entries)",
    	    infoPostFix:    "",
    	    loadingRecords: "加载数据中",
    	    zeroRecords:    "没有数据",
    	    emptyTable:     "没有数据",
    	    paginate: {
    	        first:      "第一页",
    	        previous:   "上一页",
    	        next:       "下一页",
    	        last:       "最后一页"
    	    },
    	    aria: {
    	        sortAscending:  "升序",
    	        sortDescending: "降序"
    	    },
    	    decimal: ","
        }
	} );

	var maxSortNum= 0;
	var dataTable=$('#dataTable').DataTable( {
	    "ajax":"./ajaxLoadPics",
	    "columns": [
		
		 	{data: "id",title: "ID"},
	        
	        {data: "pic",title: "类型",render:function(data,type,row,meta){
		        	return "<img src=\"http://cdn.me-to-me.com/"+row.pic+"\" height=\"50\"/>";
	        }},
		
	        {title:"操作",width:160,render:function(data, type, row, meta){
	        	var txt='<a class="btn btn-danger btn-xs del" href="#">删除</a>&nbsp;&nbsp;'
	        	return txt;
	        }}
	     ]
	})
	$(document).on("click",".del",function(){
		var tr=$(this).closest("tr");
		var data =dataTable.row(tr).data();
		if(confirm("确认删除吗？")){
			$.post("./delete",{id:data.id,r:Math.random()},function(data){
				if(data.code==1){
					dataTable.ajax.reload();
					
				}else{
					alert(data.desc);
				}
			})
		}
	})
    </script>
 
<!-- Modal -->
<div class="modal fade " id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">批量上传</h4>
      </div>
      <div class="modal-body">
        <label class="control-label">上传图片</label>
		<input id="input-24" name="file" type="file" multiple class="file-loading" accept="image/*"/>
      </div>
    </div>
  </div>
</div>
    
    <script>
	$(document).on('ready', function() {
	    $("#input-24").fileinput({
	        uploadUrl:"./doSave",
	        deleteUrl: "/site/file-delete",
	        overwriteInitial: false,
	        maxFileSize: 10000,
	        allowedFileExtensions: ["jpg", "png", "gif"],
	        uploadClass: "btn btn-danger",
	        initialCaption: "请选择图片"
	    }).on("filebatchuploadcomplete",function(){
	    	location.reload();
	    })
	});
	</script>
</body>
</html>
