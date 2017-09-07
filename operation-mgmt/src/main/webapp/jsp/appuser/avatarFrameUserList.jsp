<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 头像框用户列表</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>

</head>
<body>
	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="12" />
			<jsp:param name="s" value="12_9" />
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
								用户列表&nbsp;&nbsp;&nbsp;&nbsp;
								<a class="btn btn-primary" href="javascript:addUserAvatarFrame()">
										<i  class=" fa fa-plus "></i>
										添加用户
									</a>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="table" width="100%">
										
									</table>
								</div>
							</div>
						</section>
					</div>
				</div>
				<!-- page end-->
				<span class="btn btn-default"><a href="${ctx}/appuser/avatarFrame/list">返回</a></span>
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
	<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
	<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript">
	Date.prototype.Format = function(fmt)   
	{ //author: meizz   
	  var o = {   
	    "M+" : this.getMonth()+1,                 //月份   
	    "d+" : this.getDate(),                    //日   
	    "h+" : this.getHours(),                   //小时   
	    "m+" : this.getMinutes(),                 //分   
	    "s+" : this.getSeconds(),                 //秒   
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
	    "S"  : this.getMilliseconds()             //毫秒   
	  };   
	  if(/(y+)/.test(fmt))   
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	  for(var k in o)   
	    if(new RegExp("("+ k +")").test(fmt))   
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	  return fmt;   
	}  
	$.fn.dataTable.ext.errMode="console";
	$.extend( $.fn.dataTable.defaults, {
		pageLength: 10,
		searching: false,
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
	
	var sourceTable=$('#table').DataTable( {
		"ajax": {
            "url": "${ctx}/appuser/avatarFrame/userListPage",
            "type": "POST",
            "data": function (d) {
            	d.avatarFrame = "${param.avatarFrame}";
            }
        },
	    processing:true,
	    "columns": [
	        {data: "uid",orderable:false,title: "UID"},
	        {data: "nick_name",orderable:false,title: "昵称"},
	        {data: "third_part_bind",orderable:false,title: "注册方式",render:function(data){
	        	var r = "";
	        	if(data.indexOf("mobile") > -1){
	        		r = r + "手机";
	        	}
	        	if(data.indexOf("qq") > -1){
	        		if(r != ""){
	        			r = r + ",";
	        		}
	        		r = r + "QQ";
	        	}
	        	if(data.indexOf("weixin") > -1){
	        		if(r != ""){
	        			r = r + ",";
	        		}
	        		r = r + "微信";
	        	}
	        	return r;
	        }},
	        {data: "mobile",orderable:false,title: "手机号"},
	        {data: "create_time",orderable:false,title: "注册时间",render:function(data){
	        	if(data!=null){
	        		return new Date(data).Format("yyyy-MM-dd hh:mm:ss");
	        	}
	        }},
	        {title:"操作",render:function(){
	        	var txt='<a class="btn btn-danger btn-xs del" href="#">删除</a>&nbsp;&nbsp;'
	        	return txt;
	        }}
	     ]
	});
	$(document).on("click",".del",function(){
		var tr=$(this).closest("tr");
		var data =sourceTable.row(tr).data();
		if(confirm("确认取消该用户的头像框吗？")){
			$.post("${ctx}/appuser/avatarFrame/remove",{uid:data.uid,r:Math.random()},function(data){
				if(data.code==1){
					sourceTable.ajax.reload();
				}else{
					alert(data.desc);
				}
			})
		}
	});
	</script>
	<div class="modal inmodal fade" id="modal" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					用户列表
				</div>
				<div class="modal-body">
				<form id="form2" action="${ctx}/tag/query" method="post" class="form form-inline">
					<div class="form-group">
						用户昵称：
						<input type="text" id="snickName" name="snickName" value="" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
						UID：
						<input type="text" id="suid" name="suid" value="" class="form-control">&nbsp;&nbsp;
						<a class="btn btn-primary" href="javascript:searchUser();">搜索</a>
					</div>
					</form>
					<div class="ibox-content">
						<div class="adv-table">
							<table class=" table  table-striped" id="mytable1" width="100%">
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	var sourceTable1=$('#mytable1').DataTable( {
	    "ajax": {
            "url": "${ctx}/appuser/ajaxLoadUsers",
            "type": "POST",
            "data": function (d) {
                d.nickName = $("#snickName").val();
                d.uid = $("#suid").val();
            }
        },
	    processing:true,
	    "columns": [
			{"data": null,title: "序号",orderable:false,width:50},
	        {data: "nickName",width:200,orderable:false,title: "用户昵称",render:function(data,type,row,meta){
	        	if(data!=null){
	        		return data;
	        	}
	        }},
	        {data: "avatar",width:150,orderable:false,title: "用户头像",render:function(data,type,row,meta){
	        	if(data!=null){
	        		return '<img src="'+data+'" height="50"/>';
	        	}
	        }},
	        {title:"操作",orderable:false,render:function(data, type, row, meta){
	        	var txt='';
	        	txt='<a class="btn btn-primary btn-xs " href="javascript:addUserAvatarInfo('+row.uid+')">添加</a>';
	        	return txt;
	        }}
	     ],
	     "fnDrawCallback": function(){
       var api = this.api();
	    	var startIndex= api.context[0]._iDisplayStart;//获取到本页开始的条数
	    	         api.column(0).nodes().each(function(cell, i) {
	    	cell.innerHTML = startIndex + i + 1;
	    	}); 
	    	         }  
	});
	
	function addUserAvatarFrame(){
		sourceTable1.ajax.reload();
		$('#modal').modal('show');
	}
	function addUserAvatarInfo(uid){
		var param = {uid:uid,avatarFrame:"${param.avatarFrame}"};
		$.ajax({
            cache: true,
            type: "POST",
            dataType :"json",
            url:"${ctx}/appuser/avatarFrame/add",
            data:param,
            async: true,
            error: function(request) {
                alert('服务器出错'); 
            },
            success: function(data) {
            	  if(data.code==1){
            		  alert('操作成功');
            		  sourceTable.draw(false);
            		  $('#modal').modal('hide');
            	  }else{
					  alert("操作失败");
                  }
            }
        });
   }
function searchUser(){
	 sourceTable1.draw(true);
}
	</script>
</body>
</html>