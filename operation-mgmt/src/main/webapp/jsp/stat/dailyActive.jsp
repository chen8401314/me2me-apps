<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@include file="../common/meta.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta charset="utf-8" />
<title>ZX_IMS 2.0 - 日活统计</title>

<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet" />
<link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet" />
<link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_page.css" rel="stylesheet" />
<link href="${ctx}/assets/advanced-datatable/media/css/demo_table.css" rel="stylesheet" />
<link rel="stylesheet" href="${ctx}/assets/data-tables/DT_bootstrap.css" />
<link href="${ctx}/css/slidebars.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet" />
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
<link href="${ctx}/css/style.css" rel="stylesheet">
<link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
</head>
<body>

	<section id="container" class="">
		<!--header start-->
		<%@include file="../common/header.jsp"%>
		<!--header end-->

		<!--sidebar start-->
		<jsp:include page="../common/leftmenu.jsp" flush="false">
			<jsp:param name="t" value="3" />
			<jsp:param name="s" value="3_1" />
		</jsp:include>
		<!--sidebar end-->

		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<form id="form1" action="${ctx}/stat/dailyActive/query" method="post">
					<div class="row">
						<div class="col-lg-12">
							<section class="panel">
								<header class="panel-heading">执行操作</header>
								<div class="panel-body">
									<div class="form-inline" role="form">
										渠道选择 
										<select name="ddlClass" id="ddlClass" class="form-control">
											<option value="0" ${dataObj.ddlClass==0?'selected':''}>ALL</option>
											<option value="118" ${dataObj.ddlClass==118?'selected':''}>baidu</option>
											<option value="119" ${dataObj.ddlClass==119?'selected':''}>91zhushaou</option>
											<option value="120" ${dataObj.ddlClass==120?'selected':''}>360</option>
											<option value="121" ${dataObj.ddlClass==121?'selected':''}>jifeng</option>
											<option value="122" ${dataObj.ddlClass==122?'selected':''}>anzhi</option>
											<option value="123" ${dataObj.ddlClass==123?'selected':''}>xiaomi</option>
											<option value="124" ${dataObj.ddlClass==124?'selected':''}>uc</option>
											<option value="125" ${dataObj.ddlClass==125?'selected':''}>yyb</option>
											<option value="126" ${dataObj.ddlClass==126?'selected':''}>meizu</option>
											<option value="127" ${dataObj.ddlClass==127?'selected':''}>huawei</option>
											<option value="128" ${dataObj.ddlClass==128?'selected':''}>lianxiang</option>
											<option value="129" ${dataObj.ddlClass==129?'selected':''}>sogo</option>
											<option value="130" ${dataObj.ddlClass==130?'selected':''}>mumayi</option>
											<option value="131" ${dataObj.ddlClass==131?'selected':''}>liqu</option>
											<option value="132" ${dataObj.ddlClass==132?'selected':''}>jinli</option>
											<option value="133" ${dataObj.ddlClass==133?'selected':''}>yybei</option>
											<option value="134" ${dataObj.ddlClass==134?'selected':''}>kuchuan</option>
											<option value="135" ${dataObj.ddlClass==135?'selected':''}>smartisan</option>
											<option value="136" ${dataObj.ddlClass==136?'selected':''}>youyi</option>
											<option value="137" ${dataObj.ddlClass==137?'selected':''}>maopao</option>
											<option value="138" ${dataObj.ddlClass==138?'selected':''}>wandoujia</option>
											<option value="139" ${dataObj.ddlClass==139?'selected':''}>yyh</option>
											<option value="140" ${dataObj.ddlClass==140?'selected':''}>tianyi</option>
											<option value="141" ${dataObj.ddlClass==141?'selected':''}>nduo</option>
											<option value="142" ${dataObj.ddlClass==142?'selected':''}>shoujizg</option>
											<option value="143" ${dataObj.ddlClass==143?'selected':''}>nearme</option>
											<option value="144" ${dataObj.ddlClass==144?'selected':''}>apple</option>
										</select>
										开始日期
										<input type="text" id="txtStartDate" name="txtStartDate" value="${dataObj.txtStartDate }" data-mask="9999-99-99" class="form-control" required> 
										结束日期
										<input type="text" id="txtEndDate" name="txtEndDate" value="${dataObj.txtEndDate }" data-mask="9999-99-99" class="form-control" required>
										<input type="submit" id="btnSearch" value="搜索" class="btn btn-info" />
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
								|${dataObj.ddlClassName } 报表信息
								 <span class="tools pull-right">
									<a href="javascript:;" class="fa fa-chevron-down"></a>
								</span>
							</header>
							<div class="panel-body">
								<div class="adv-table">
									<table class="display table table-bordered table-striped" id="dynamic-table">
										<thead>
											<tr>
												<th>启动</th>
												<th>登录</th>
												<th>注册</th>
												<th>浏览</th>
												<th>发布内容</th>
												<th>发布直播</th>
												<th>点赞</th>
												<th>取消点赞</th>
												<th>评论</th>
												<th>感受标签</th>
												<th>关注</th>
												<th>取消关注</th>
												<th>转发内容</th>
												<th>阅读热门</th>
												<th>阅读最新</th>
												<th>关注文章</th>
											</tr>
										</thead>
										<tbody>
											<tr class="gradeX">
												<th>${dataObj.boot}</th>
												<th>${dataObj.login}</th>
												<th>${dataObj.reg}</th>
												<th>${dataObj.view}</th>
												<th>${dataObj.pubCon}</th>
												<th>${dataObj.pubLive}</th>
												<th>${dataObj.zan}</th>
												<th>${dataObj.czan}</th>
												<th>${dataObj.common}</th>
												<th>${dataObj.tags}</th>
												<th>${dataObj.attention}</th>
												<th>${dataObj.cattention}</th>
												<th>${dataObj.forwarding}</th>
												<th>${dataObj.hot}</th>
												<th>${dataObj.anew}</th>
												<th>${dataObj.aarticle}</th>
											</tr>
										</tbody>
										<tfoot>
											<tr>
												<th>启动</th>
												<th>登录</th>
												<th>注册</th>
												<th>浏览</th>
												<th>发布内容</th>
												<th>发布直播</th>
												<th>点赞</th>
												<th>取消点赞</th>
												<th>评论</th>
												<th>感受标签</th>
												<th>关注</th>
												<th>取消关注</th>
												<th>转发内容</th>
												<th>阅读热门</th>
												<th>阅读最新</th>
												<th>关注文章</th>
											</tr>
										</tfoot>
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
	<script src="${ctx}/js/slidebars.min.js"></script>
	<script src="${ctx}/js/dynamic_table_init.js"></script>
	<script src="${ctx}/js/bootstrap-switch.js"></script>
	<script src="${ctx}/js/jquery.tagsinput.js"></script>
	<script src="${ctx}/js/form-component.js"></script>
	<script src="${ctx}/js/common-scripts.js"></script>
	<script src="${ctx}/js/advanced-form-components.js"></script>
</body>
</html>
