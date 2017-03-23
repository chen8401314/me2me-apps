<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 串门语言管理</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${ctx}/assets/bootstrap-datetimepicker/css/datetimepicker.css" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>
</head>
<body>
	<section id="container" class="container">
		<p>
		<div class="panel panel-default">
			<div class="panel-body">
				<button type="button" class="btn btn-danger" onclick="addBatch()">
					<i  class=" fa fa-plus "></i> 批量加入
				</button>
			</div>
		</div>
		</p>
		<div class="adv-table">
			<table class="display table table-bordered table-striped" id="mytable" width="100%">
				<thead>
					<tr>
						<th width="50">
							<button class='btn btn-warning btn-xs'>全/反选</button> 
						</th>
						<th>序号</th>
						<th>类型</th>
						<th>名称</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${dataList}" var="item" varStatus="status">
						<tr class="gradeX" data-bid="${item.id}">
							<td><input type='checkbox'/></td>
							<td>${status.index + 1}</td>
							<td>
								<c:choose>
                              		<c:when test="${item.type == '1'}">
                              			王国榜单
                              		</c:when>
                              		<c:when test="${item.type == '2'}">
                              			用户榜单
                              		</c:when>
                             			<c:when test="${item.type == '3'}">
                             				榜单集合
                             			</c:when>
                              	</c:choose>
							</td>
							<td >
								${item.name }
							</td>
							<td>
								<button class="btn btn-danger btn-xs btnAdd" >加入</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</section>
	<!-- js placed at the end of the document so the pages load faster -->
	<script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
	<script src="${ctx}/js/jquery.scrollTo.min.js"></script>
	<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/jquery.json-2.4.min.js"></script>
	<link rel="stylesheet" href="${ctx}/js/DataTables-1.10.11/media/css/jquery.dataTables.min.css" />
	<script type="text/javascript" src="${ctx}/js/DataTables-1.10.11/media/js/jquery.dataTables.min.js"></script>
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
		searching: true,
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
	});
	
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
		var data =tr.attr("data-bid")
		parent.onAdd([data]);
	})
	function addBatch(){
		var dataArr=[]
		$("#mytable input:checked").each(function(){
			var tr = $(this).closest("tr");
			var data =tr.attr("data-bid")
			dataArr.push(data);
		})
		parent.onAdd(dataArr);
	}
	</script>
</body>
</html>