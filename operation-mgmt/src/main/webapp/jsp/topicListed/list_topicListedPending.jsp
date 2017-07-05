<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 情绪列表</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_page.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_table.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
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
			<jsp:param name="t" value="14" />
			<jsp:param name="s" value="14_2" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
						<form id="form2" action="${ctx}/tag/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">搜索</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										王国名称：
										<input type="text" id="title" name="title" value="" class="form-control">&nbsp;
										<a class="btn btn-primary" href="javascript:search();">搜索</a>
									</div>
								</div>
							</section>
						</div>
					</div>
				</form>
				<!-- page start-->
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| 上市王国列表
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
								<table class="display table table-bordered table-striped" id="mytable" width="100%">
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
	<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
		<script type="text/javascript">
	$.fn.datetimepicker.dates['zh'] = {  
            days:       ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日"],  
            daysShort:  ["日", "一", "二", "三", "四", "五", "六","日"],  
            daysMin:    ["日", "一", "二", "三", "四", "五", "六","日"],  
            months:     ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月","十二月"],  
            monthsShort:  ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],  
            meridiem:    ["上午", "下午"],  
            //suffix:      ["st", "nd", "rd", "th"],  
            today:       "今天"  
    };
	$('.date').datetimepicker({
		format: 'yyyy-mm-dd',
		language: 'zh',
		startView: 2,
		maxView:3,
		language:"zh",
		minView:2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1
	});
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
	
	
	var sourceTable=$('#mytable').DataTable( {
	    "ajax": {
            "url": ctx+"/topicListed/ajaxLoadTopicListedPending",
            "type": "POST",
            "data": function (d) {
                d.title = $("#title").val();
            }
        },
	    processing:true,
	    "columns": [
			{"data": null,title: "序号",orderable:false,width:50},
	        {data: "title",width:250,orderable:false,title: "王国名称",render:function(data,type,row,meta){
	        	if(data!=null){
	        		return data;
	        	}
	        }},
	        {data: "price",width:100,orderable:false,title: "王国价值",render:function(data,type,row,meta){
	        	if(data!=null){
	        		return data;
	        	}
	        }},
	        {data: "nickName",width:200,orderable:false,title: "持有人",render:function(data,type,row,meta){
	        	if(data!=null){
	        		return data;
	        	}
	        }},
	        {data: "createTime",width:150,orderable:false,title: "上市时间",render:function(data,type,row,meta){
	        	if(data!=null){
	        		return new Date(data).Format("yyyy-MM-dd hh:mm:ss");
	        	}
	        }},
	        {data: "meNumber",width:100,orderable:false,title: "新国王米汤号",render:function(data,type,row,meta){
                      if(row.meNumber!='' && data!=null){
                    	  return '<input type="text" value="'+row.meNumber+'" readOnly id="meNumber_'+row.id+'">';
                      }else{
                          return '<input type="text" value=""  id="meNumber_'+row.id+'">';
                      }
	        }},
	        {title:"操作",orderable:false,width:200,render:function(data, type, row, meta){
	        	var txt='';
	        	txt='<a class="btn btn-primary btn-xs " href="javascript:handleTopicListed('+row.id+')">确定</a>';
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
	$("#search_form").on("submit",function(){
		var data= $(this).serialize();
		var url = ctx+"/ranking/ajaxLoadKingdoms?"+data;
		sourceTable.ajax.url(url).load();
		return false;
	})
	$("#mytable").on("click","th:eq(0)",function(){
		$("input[type='checkbox']").each(function(){
			if($(this).attr("checked")!=null){
				$(this).removeAttr("checked")
			}else{
				$(this).attr("checked","checked")
			}
		})
	})
	$(document).on("click",".btnAdd",function(){		// 父窗口添加选中用户。
		var tr = $(this).closest("tr");
		var data =sourceTable.row(tr).data();
		parent.onAdd([data.topicId],[data]);
	})
	function addBatch(){
		var idArr=[],dataArr=[];
		$("#mytable input:checked").each(function(){
			var tr = $(this).closest("tr");
			var data =sourceTable.row(tr).data()
			dataArr.push(data);
			idArr.push(data.topicId);
		})
		parent.onAdd(idArr,dataArr);
	}
	$("#flushKingdomCache").click(function(){
		if(confirm("确认刷新吗？")){
			 var $btn = $(this).button('loading');
			 $.getJSON("./ajaxUpdateKingdomsCache",{r:Math.random()},function(d){
				 alert(d.desc);		 
				 $btn.button('reset')
			 })
		}
	})
		function search(){
		 sourceTable.draw(true);
	}
	
	   function handleTopicListed(id){
		 var msg = "您真的确定要提交吗？"; 
		 if (confirm(msg)==true){ 
			 var meNumber = $("#meNumber_"+id).val();
			 var param = {id:id,meNumber:meNumber};
			  	$.ajax({
		            cache: true,
		            type: "POST",
		            dataType :"json",
		            url:"./handleTransaction",
		            data:param,
		            async: true,
		            error: function(request) {
		                alert('服务器出错'); 
		            },
		            success: function(data) {
		            	if(data.code==1){
		            		  alert('操作成功');
		            		  sourceTable.draw(false);
		    			}else{
		    				alert(data.desc);
		    			}
		            }
		        });
		  return true; 
		 }else{ 
		  return false; 
		 }
    }
	</script>
</body>
</html>