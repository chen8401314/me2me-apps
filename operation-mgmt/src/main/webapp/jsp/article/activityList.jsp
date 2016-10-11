<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@include file="../common/meta.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>ZX_IMS 2.0 - 活动列表</title>

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
    <script type="text/javascript">

    </script>
</head>

<body>
<section id="container">
    <!--header start-->
    <%@include file="../common/header.jsp"%>
    <!--header end-->

    <!--sidebar start-->
    <jsp:include page="../common/leftmenu.jsp" flush="false">
    	<jsp:param name="t" value="2"/>
    	<jsp:param name="s" value="2_1"/>
    </jsp:include>
    <!--sidebar end-->

    <!--main content start-->
    <section id="main-content">
        <section class="wrapper">
        	<form id="form1" action="${ctx}/activity/query" method="post">
            <div class="row">
                <div class="col-lg-12">
                    <section class="panel">
                        <header class="panel-heading">执行操作</header>
                        <div class="panel-body">
                            <div class="form-inline" role="form">
                                <input type="text" id="keyword" name="keyword" class="form-control">
                                <input type="submit" id="btnSearch" value="按标题关键字搜索" class="btn btn-info"/>
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
                            |活动信息列表
							<span class="tools pull-right">
								<a href="${ctx}/jsp/article/activityNew.jsp" class="fa fa-plus add_link" title="添加数据" ></a>
								<a href="javascript:;" class="fa fa-chevron-down"></a>
							</span>
                        </header>
                        <div class="panel-body">
                            <div class="adv-table">
                                <table class="display table table-bordered table-striped" id="dynamic-table">
                                    <thead>
                                    <tr>
                                        <th>期次</th>
                                        <th>活动状态</th>
                                        <th>公告状态</th>
                                        <th>标题</th>
                                        <th>标识</th>
                                        <th>结束时间</th>
                                        <th>作者</th>
                                        <th>执行</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${dataObj.result}" var="activity">
                                            <tr class="gradeX">
                                                <th>${activity.issue}</th>
                                                <th>
                                                <c:choose>
                                                	<c:when test="${activity.status == '1'}">
                                                		<font color='red'>已下架</font>
                                                	</c:when>
                                                	<c:otherwise>
                                                		<font color='green'>已上架</font>
                                                	</c:otherwise>
                                                </c:choose>
                                                </th>
                                                <th>
                                                <c:choose>
                                                	<c:when test="${activity.internalStatus == '1'}">
                                                		<font color='green'>有公告</font>
                                                	</c:when>
                                                	<c:otherwise>
                                                		<font color='red'>无公告</font>
                                                	</c:otherwise>
                                                </c:choose>
												</th>
                                                <th>${activity.title}</th>
                                                <th>${activity.hashTitle}</th>
                                                <th><fmt:formatDate value="${activity.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/></th>
                                                <c:set var="appuid">${activity.uid}</c:set>
                                                <th>${userMap[appuid]}</th>
                                                <th>
                                                	<c:choose>
                                                		<c:when test="${activity.status == '1'}">
                                                			<a href="${ctx}/activity/option?a=1&i=${activity.id}">上架</a>
                                                		</c:when>
                                                		<c:otherwise>
                                                			<a href="${ctx}/activity/option?a=0&i=${activity.id}">下架</a>
                                                		</c:otherwise>
                                                	</c:choose>
                                                    <c:if test="${activity.internalStatus == '0'}">
                                                    |<a href="${ctx}/jsp/article/activityNoticeNew.jsp?aid=${activity.id}">添加公告</a>
                                                    </c:if>
                                                </th>
                                            </tr>
                                    </c:forEach>
                                    </tbody>
                                    <tfoot>
                                    <tr>
                                        <th>期次</th>
                                        <th>活动状态</th>
                                        <th>公告状态</th>
                                        <th>标题</th>
                                        <th>标识</th>
                                        <th>结束时间</th>
                                        <th>作者</th>
                                        <th>执行</th>
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
<script type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
	<script src="${ctx}/js/jquery.scrollTo.min.js" type="text/javascript"></script>
	<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctx}/assets/advanced-datatable/media/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="${ctx}/assets/data-tables/DT_bootstrap.js"></script>
	<script src="${ctx}/js/respond.min.js" type="text/javascript"></script>
	<script src="${ctx}/js/slidebars.min.js" type="text/javascript"></script>
	<script src="${ctx}/js/dynamic_table_init.js" type="text/javascript"></script>
	<script src="${ctx}/js/bootstrap-switch.js" type="text/javascript"></script>
	<script src="${ctx}/js/jquery.tagsinput.js" type="text/javascript"></script>
	<script src="${ctx}/js/form-component.js" type="text/javascript"></script>
	<script src="${ctx}/js/common-scripts.js" type="text/javascript"></script>
	<script src="${ctx}/js/advanced-form-components.js" type="text/javascript"></script>
</body>
</html>