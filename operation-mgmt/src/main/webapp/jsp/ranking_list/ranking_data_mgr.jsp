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
    			<jsp:param name="t" value="10"/>
    			<jsp:param name="s" value="10_1"/>
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
                                <header class="panel-heading">榜单：${item.name}， 模式：
                                	<b class="text-danger">
                               	 	<c:if test="${item.mode==0}">
	                                	手动
                                	</c:if>
                                	<c:if test="${item.mode!=0}">
                                		自动
                                	</c:if>
                                	</b>，
                                	类型：
                                	<b class="text-danger">
	                                	<c:if test="${item.type==1}">
	                                		王国榜单
	                                	</c:if>
	                                	<c:if test="${item.type==2}">
	                                		用户榜单
	                                	</c:if>
	                                	<c:if test="${item.type==3}">
	                                		榜单集合
	                                	</c:if>
                                	</b>&emsp;
                                	<button onclick="location.href='./list_ranking'" class="btn btn-primary">返回</button>
                                	<c:if test="${item.type==1 && item.mode==0}">
                                		<a class="btn btn-danger dialog" href="./listKingdoms">添加王国</a>
                                	</c:if>
                                	<c:if test="${item.type==2 && item.mode==0 }">
                                		<a class="btn btn-danger dialog" href="./listUsers">添加用户</a>
                                	</c:if>
                                	<c:if test="${item.type==3 && item.mode==0}">
                                		<a class="btn btn-danger dialog" href="./listRankings">添加榜单</a>
                                	</c:if>
                                	
                                	
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
	    "ajax":"./ajaxLoadRankingData?id=${param.id}",
	    "columns": [
			//{title:"<button class='btn btn-warning btn-xs'>全/反选</button> ",width:50,orderable:false,render:function(data, type, row, meta){
			//	var txt= "<input type='checkbox'/> ";
			//	return txt;
			//}},
			<c:if test="${item.type==1}">
			 	{data: "title",title: "王国名称",render:function(data,type,row,meta){
		        	if(data!=null){
		        		return "<a target='_blank' href='"+baseKingdomURL+row.topicId+"'>"+data+"</a>";
		        	}
		        }},
		        {data: "aggregation",title: "类型",render:function(data,type,row,meta){
		        	if(data!=null){
			        	var map ={0:"普通王国",1000:"聚合王国"};
			        	return map[data];
		        	}
		        }},
			</c:if>
			<c:if test="${item.type==2}">
			 	{data: "nickName",title: "昵称"},
		        {data: "vLv",title: "大V",render:function(data,type,row,meta){
		        	if(data!=null){
			        	var map ={0:"否",1:"是"};
			        	return map[data];
		        	}
		        }},
			</c:if>
	    	<c:if test="${item.type==3}">
		        {data: "rankingName",title: "榜单名"},
		        {data: "rankingType",title: "榜单类型",render:function(data,type,row,meta){
		        	if(data!=null){
			        	var map ={1:"王国榜单",2:"用户榜单",3:"榜单"};
			        	return map[data];
		        	}
		        }}
	    	</c:if>
	    	<c:if test="${item.mode==0}">
	        ,{title:"操作",width:160,render:function(data, type, row, meta){
	        	var txt='<a class="btn btn-danger btn-xs del" href="#">删除</a>&nbsp;&nbsp;'
	        	txt+='<a class="btn btn-warning btn-xs moveup" href="#">上移</a>&nbsp;&nbsp;'
	        	txt+='<a class="btn btn-warning btn-xs movedown" href="#">下移</a>'
	        	return txt;
	        }}
	        </c:if>
	     ]
	}).on('xhr.dt', function ( e, settings, json, xhr ) {		// 获取最大order值。
        for ( var i=0 ; i<json.data.length ; i++ ) {
        	if(json.data[i].sort>maxSortNum){
        		maxSortNum=json.data[i].sort;
        	}
        }
    });
	$(document).on("click",".del",function(){
		var tr=$(this).closest("tr");
		var data =dataTable.row(tr).data();
		if(confirm("确认删除吗？")){
			$.post("./deleteRankingData",{id:data.id,r:Math.random()},function(data){
				if(data.code==1){
					dataTable.ajax.reload();
					
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
		var dataArr=[];
		$("#dataTable>tbody>tr").each(function(index,obj){
			var tr=$(this);
			var data =dataTable.row(tr).data();
			dataArr.push({
				id:data.id,
				sort:index
			})
		})
		var json = $.toJSON(dataArr);
		$.post("./updateRankingData",{json:json,r:Math.random()},function(data){
			if(data.code==1){
				
			}else{
				alert(data.desc);
			}
		})
	})
	
	// 子窗口添加数据
	function onAdd(dataArr){
		var objs=[];		
		for(var i=0;i<dataArr.length;i++){
			var item= dataArr[i];
			objs.push({
				sourceId:${param.id},
				targetId:item,
				sort:++maxSortNum,
				type:${item.type}
			})
		}		
		var json = $.toJSON(objs);
		$.post("./doSaveRankingData",{json:json,r:Math.random()},function(data){
			if(data.code==1){
				dataTable.ajax.reload();
				Messenger().post({message:"添加成功！",hideAfter: 2});
			}else{
				alert(data.desc);
			}
		})
	}
    </script>
</body>
</html>
