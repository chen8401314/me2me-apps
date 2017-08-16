<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<%@include file="common/meta.jsp"%>
    <meta charset="utf-8" />
    <title>ZX_IMS 2.0 - 系统登录</title>
    <!-- Bootstrap core CSS -->
    <link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
    <!--external css-->
    <link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
    <!-- Custom styles for this template -->
    <link href="${ctx}/css/style.css" rel="stylesheet" />
    <link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
    <script src="${ctx}/js/jquery.js"></script>
    <script src="${ctx}/js/bootstrap.min.js"></script>
    <style>
.log{background:#efefef;border:1px solid #ccc;width:40%;position:fixed;right:0px;bottom:0px;top:0px;}
.dataList li{padding:3px 5px;border-bottom:1px solid #efefef;}
.dataList li:hover{background:#ffd2d3;}
.dataList .taskInfo{display:inline-block;width:500px;}
li.sel{background:#eee;}
</style>
</head>

<body>
<h3>系统任务列表</h3><hr>	
<ul class="dataList">
<c:forEach var ="item" items="${taskList }">
<li>
	<span class="taskInfo">
		${item.className}<br>
		<b>${item.cron }-${item.methodName}</b>
	</span>
	<a href="#stop" onclick="startTask('${item.className}','${item.methodName}') ">执行</a> 
	<span></span>
</li>
</c:forEach>
</ul>

<textarea class="log"></textarea>
<script>
var logDiv = jQuery(".log")

function startTask(className,methodName){
	if(confirm("确认执行["+methodName+"]任务吗？")){
		jQuery.post("run_task.do",{className:className,methodName:methodName,r:Math.random()},function(data){
			if(data!=""){
				logDiv.val(logDiv.val()+"\n"+data);
			}
		})
	}
}
$(".dataList li").click(function(){
	jQuery(this).addClass("sel");
})
// load status

var iv = setInterval(function(){
	jQuery.get("getTaskStatus.do",{r:Math.random()},function(data){
		if(data!=""){
			logDiv.val(logDiv.val()+"\n"+data);
		}else{
			//clearInterval(iv);
		}
	})
},2000)


</script>

</body>
</html>
