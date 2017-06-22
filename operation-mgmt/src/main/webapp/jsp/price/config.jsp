<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />

<title>ZX_IMS 2.0 - 基础配置管理</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_page.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_table.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />

<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/jquery-ui-1.9.2.custom.min.js"></script>
<script src="${ctx}/js/jquery-migrate-1.2.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<style>
.adv-table{margin-top:20px;}
.changed{background:#E6FED8}
</style>
<script type="text/javascript">
function format(txt,compress/*是否为压缩模式*/){/* 格式化JSON源码(对象转换为JSON文本) */  
    var indentChar = '    ';   
    if(/^\s*$/.test(txt)){   
        alert('数据为空,无法格式化! ');   
        return;   
    }   
    try{var data=eval('('+txt+')');}   
    catch(e){   
        alert('数据源语法错误,格式化失败! 错误信息: '+e.description,'err');   
        return;   
    };   
    var draw=[],last=false,This=this,line=compress?'':'\n',nodeCount=0,maxDepth=0;   
       
    var notify=function(name,value,isLast,indent/*缩进*/,formObj){   
        nodeCount++;/*节点计数*/  
        for (var i=0,tab='';i<indent;i++ )tab+=indentChar;/* 缩进HTML */  
        tab=compress?'':tab;/*压缩模式忽略缩进*/  
        maxDepth=++indent;/*缩进递增并记录*/  
        if(value&&value.constructor==Array){/*处理数组*/  
            draw.push(tab+(formObj?('"'+name+'":'):'')+'['+line);/*缩进'[' 然后换行*/  
            for (var i=0;i<value.length;i++)   
                notify(i,value[i],i==value.length-1,indent,false);   
            draw.push(tab+']'+(isLast?line:(','+line)));/*缩进']'换行,若非尾元素则添加逗号*/  
        }else   if(value&&typeof value=='object'){/*处理对象*/  
                draw.push(tab+(formObj?('"'+name+'":'):'')+'{'+line);/*缩进'{' 然后换行*/  
                var len=0,i=0;   
                for(var key in value)len++;   
                for(var key in value)notify(key,value[key],++i==len,indent,true);   
                draw.push(tab+'}'+(isLast?line:(','+line)));/*缩进'}'换行,若非尾元素则添加逗号*/  
            }else{   
                    if(typeof value=='string')value='"'+value+'"';   
                    draw.push(tab+(formObj?('"'+name+'":'):'')+value+(isLast?'':',')+line);   
            };   
    };   
    var isLast=true,indent=0;   
    notify('',data,isLast,indent,false);   
    return draw.join('');   
}  

$(document).on("click","a.edit",function(){
	var $tr=$(this).closest("tr");
	
	var desc =$.trim($tr.find("td:eq(0)").text());
	var key =$.trim($tr.find("td:eq(1)").text());
	var value =$.trim($tr.find("td:eq(2)").text());
	if(value.indexOf("{")>-1 && value.indexOf("}")>-1){
		try{
			var myvalue=format(value,false)
			value=myvalue
		}catch(e){
			
		}
	}
	$("#cdesc").val(desc);
	$("#ckey").val(key);
	$("#cvalue").val(value);
	$("#newConfigModal").modal();
})

var modifyCommit = function(){
	var key = $("#ckey").val();
	var value = $("#cvalue").val();
	var $tr=$("tr[key='"+key+"']");
	$.post("./saveConfig",{k:key,v:value,r:Math.random()},function(data){
		if(data == '0'){
			alert("保存成功");
			$tr.find("td:eq(2)").text(value)
			//location.reload();
		}else{
			alert(resp);
		}
	})
	
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
			<jsp:param name="t" value="13" />
			<jsp:param name="s" value="13_2" />
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
								| 基础配置列表 &nbsp;&nbsp;&nbsp;&nbsp;
								<span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div>

								  <!-- Nav tabs -->
								  <ul class="nav nav-tabs" role="tablist">
								    <li role="presentation" class="active"><a href="#t0" aria-controls="home" role="tab" data-toggle="tab">基础配置</a></li>
								    <li role="presentation"><a href="#t1" aria-controls="profile" role="tab" data-toggle="tab">用户等级权限配置</a></li>
								    <li role="presentation"><a href="#t2" aria-controls="messages" role="tab" data-toggle="tab">操作分值配置</a></li>
								    <li role="presentation"><a href="#t3" aria-controls="settings" role="tab" data-toggle="tab">王国价值任务参数</a></li>
								    <li role="presentation"><a href="#t4" aria-controls="settings" role="tab" data-toggle="tab">个人级别累计分值配置</a></li>
								  </ul>
								
								  <!-- Tab panes -->
								  <div class="tab-content">
								    <div role="tabpanel" class="tab-pane active" id=t0>
										<div class="adv-table">
											<table class="display table table-bordered table-striped" id="dynamic-table">
												<thead>
													<tr>
														<th>描述</th>
														<th>KEY</th>
														<th width="40%">VALUE</th>
														<th>操作</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${configList}" var="configItem">
														<c:if test="${configItem.typeName=='基础配置'}">
														<tr class="gradeX" key="${configItem.configKey }">
															<td>${configItem.name }</td>
															<td>${configItem.configKey }</td>
															<td>${configItem.configValue }</td>
															<td><a href="#" class="edit">编辑</a></td>
														</tr>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								    <div role="tabpanel" class="tab-pane" id="t1">
										<div class="adv-table">
											<table class="display table table-bordered table-striped" id="dynamic-table">
												<thead>
													<tr>
														<th>描述</th>
														<th>KEY</th>
														<th width="40%">VALUE</th>
														<th>操作</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${configList}" var="configItem">
														<c:if test="${configItem.typeName=='用户等级权限配置'}">
														<tr class="gradeX" key="${configItem.configKey }">
														<td>${configItem.name }</td>
															<td>${configItem.configKey }</td>
															<td>${configItem.configValue }</td>
															<td><a href="#" class="edit">编辑</a></td>
														</tr>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								    <div role="tabpanel" class="tab-pane" id="t2">
										<div class="adv-table">
											<table class="display table table-bordered table-striped" id="dynamic-table">
												<thead>
													<tr>
														<th>描述</th>
														<th>KEY</th>
														<th width="40%">VALUE</th>
														<th>操作</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${configList}" var="configItem">
														<c:if test="${configItem.typeName=='操作分值配置'}">
														<tr class="gradeX" key="${configItem.configKey }">
															<td>${configItem.name }</td>
															<td>${configItem.configKey }</td>
															<td>${configItem.configValue }</td>
															<td><a href="#" class="edit">编辑</a></td>
														</tr>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								    <div role="tabpanel" class="tab-pane" id="t3">
								    	<div class="adv-table">
											<table class="display table table-bordered table-striped" id="dynamic-table">
												<thead>
													<tr>
														<th>描述</th>
														<th>KEY</th>
														<th width="40%">VALUE</th>
														<th>操作</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${configList}" var="configItem">
														<c:if test="${configItem.typeName=='王国价值任务参数'}">
														<tr class="gradeX" key="${configItem.configKey }">
															<td>${configItem.name }</td>
															<td>${configItem.configKey }</td>
															<td>${configItem.configValue }</td>
															<td><a href="#" class="edit">编辑</a></td>
														</tr>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<div role="tabpanel" class="tab-pane" id="t4">
								    	<div class="adv-table">
											<table class="display table table-bordered table-striped" id="dynamic-table">
												<thead>
													<tr>
														<th>描述</th>
														<th>KEY</th>
														<th width="40%">VALUE</th>
														<th>操作</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${configList}" var="configItem">
														<c:if test="${configItem.typeName=='个人级别累计分值配置'}">
														<tr class="gradeX" key="${configItem.configKey }">
															<td>${configItem.name }</td>
															<td>${configItem.configKey }</td>
															<td>${configItem.configValue }</td>
															<td><a href="#" class="edit">编辑</a></td>
														</tr>
														</c:if>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								  </div>
								
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

	<!-- modal VIEW -->
	<div class="modal fade" id="newConfigModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">基础配置更新</h4>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<label for="exampleInputEmail1">配置项</label>
                        <input type="text" id="cdesc" name="cdesc" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Key</label>
                        <input type="text" id="ckey" name="ckey" class="form-control" style="width: 100%" readonly>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Value</label>
                        <textarea type="text" id="cvalue" name="cvalue" class="form-control" style="width: 100%;height:400px"></textarea>
					</div>
				</div>
				<div class="modal-footer">
					<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="modifyCommit()">更改</button>
					<button data-dismiss="modal" class="btn btn-default" type="button">关闭</button>
				</div>
			</div>
		</div>
	</div>
	<!-- js placed at the end of the document so the pages load faster -->
	<script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
	<script src="${ctx}/js/jquery.scrollTo.min.js"></script>
	<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctx}/assets/advanced-datatable/media/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="${ctx}/assets/data-tables/DT_bootstrap.js"></script>
	<script src="${ctx}/js/respond.min.js"></script>
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
</body>
</html>
