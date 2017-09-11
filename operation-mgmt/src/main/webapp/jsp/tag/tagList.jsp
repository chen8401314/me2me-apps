<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 标签管理</title>

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
			<jsp:param name="s" value="12_1" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<form id="search_form" method="post"  class="form-inline">
										<div class="form-group">
											<label>标签名</label>
											<input type="text" id="tagName" name="tagName" value="${dataObj.tagName }" class="form-control">
										</div>
										<div class="form-group">
											<label>创建时间</label>
											<input type="text" id="startTime" name="startTime" value="${dataObj.startTime }" class="form-control">
											-
											<input type="text" id="endTime" name="endTime" value="${dataObj.endTime }" class="form-control">&nbsp;&nbsp;&nbsp;&nbsp;
										</div>
										<div class="form-group">
											<label>是否体系</label>
											<select name="isSys" id="isSys" class="form-control">
												<option value="-1" ${dataObj.isSys==-1?'selected':''}>所有</option>
												<option value="0" ${dataObj.isSys==0?'selected':''}>否</option>
												<option value="1" ${dataObj.isSys==1?'selected':''}>是</option>
											</select>
										</div>											
										<div class="form-group">
											<label>王国数</label>
											<input type="text" id="topicCountStart" name="topicCountStart" value="${dataObj.topicCountStart }" class="form-control">
											-
											<input type="text" id="topicCountEnd" name="topicCountEnd" value="${dataObj.topicCountEnd }" class="form-control">
										</div>
										<div class="form-group">
											<label>是否推荐</label>
											<select name="isRec" id="isRec" class="form-control">
												<option value="-1" ${dataObj.isRec==-1?'selected':''}>所有</option>
												<option value="0" ${dataObj.isRec==0?'selected':''}>否</option>
												<option value="1" ${dataObj.isRec==1?'selected':''}>是</option>
											</select>&emsp;
										</div>
										<div class="form-group">
											<label>父标签</label>
											<select name="pid" id="pid" class="form-control">
												<option value="">全部</option>
												<c:forEach  var="tag" items="${sysTagList }">
													<option value="${tag.id}" ${dataObj.pid==tag.id?'selected':''}>${tag.tag}</option>
												</c:forEach>
											</select>
											<label><input type="checkbox" class="form-control" name="noParent" value="1"/>无大类标签</label>
										</div>
										<div class="form-group">
											<input type="submit" id="btnSearch" name="btnSearch" value="搜索" class="btn btn-info" />
											<a class="btn btn-danger" href="./tagNew">新建标签</a>
										</div>
									</form>
								</div>
							</section>
						</div>
					</div>
				<!-- page start-->
				<div class="row">
					<div class="col-sm-12">
						<section class="panel">
							<header class="panel-heading">
								| 标签列表
								<span class="tools pull-right">
									<a href="${ctx}/tag/tagNew" class="fa fa-plus add_link" title="新增标签" ></a>
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
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
	$('#startTime,#endTime').datetimepicker({
		format: 'yyyy-mm-dd',
		language: 'zh',
		startView: 2,
		autoclose:true,
		weekStart:1,
		todayBtn:  1,
		minView:2
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
	
	
	var sourceTable=$('#table').DataTable( {
	    "ajax": "page",
	    processing:true,
	    "columns": [
	        {data: "tagName",orderable:false,title: "标签名"},
	        {data: "parentTagName",orderable:false,title: "大类标签"},
	        {data: "createTime",title: "创建时间",render:function(data){
	        	if(data!=null){
	        		return new Date(data).Format("yyyy-MM-dd hh:mm:ss");
	        	}
	        }},
	        {data: "isSys",orderable:false,title: "是否体系",render:function(data){
	        	return data==1?'Y':'N';
	        }},
	        {data: "isRec",title: "是否推荐",render:function(data){
	        	return data==1?'Y':'N';
	        }},
	       // {data: "orderNum",title: "排序值"},
	        {data: "status",title: "状态",render:function(data){
	        	return data=='0'?'正常':'禁用';
	        }},
	        {data: "readCountDummy",title: "王国数"},
	        {title:"操作",orderable:false,width:150,render:function(data, type, row, meta){
	        	var txt= '<a href="./f/'+row.id+'">编辑</a> | ';
	        	txt+='<a href="./topicList/query?tagId='+row.id+'">查看王国列表</a>'
	        	return txt;
	        }}
	     ]
	});
	$("#search_form").on("submit",function(){
		var data= $(this).serialize();
		var url ="./page?"+data;
		sourceTable.ajax.url(url).load();
		return false;
	})
	</script>
</body>
</html>