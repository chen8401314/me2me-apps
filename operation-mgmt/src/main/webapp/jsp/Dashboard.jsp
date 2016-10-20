<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@include file="common/meta.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>ZX_IMS 2.0 - 仪表盘</title>

    <link href="${ctx}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet">
    <link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
    <link href="${ctx}/assets/jquery-easy-pie-chart/jquery.easy-pie-chart.css" rel="stylesheet" type="text/css" media="screen" />
    <link rel="stylesheet" href="${ctx}/css/owl.carousel.css" type="text/css">
    <link href="${ctx}/css/slidebars.css" rel="stylesheet">
    <link href="${ctx}/css/style.css" rel="stylesheet">
    <link href="${ctx}/css/style-responsive.css" rel="stylesheet" />
</head>

<body>

<section id="container">
    <!--header start-->
    <%@include file="common/header.jsp"%>
    <!--header end-->

    <!--sidebar start-->
    <jsp:include page="common/leftmenu.jsp" flush="false">
    	<jsp:param name="t" value="1"/>
    </jsp:include>
    <!--sidebar end-->

    <!--main content start-->
    <section id="main-content">
        <section class="wrapper">
            <!--state overview start-->
            <div class="row state-overview">
                <div class="col-lg-3 col-sm-6">
                    <section class="panel">
                        <div class="symbol terques">
                            <i class="fa fa-user"></i>
                        </div>
                        <div class="value">
                            <h1 class="count">${dataObj.todayCount }</h1>
                            <p>今日活跃</p>
                        </div>
                    </section>
                </div>
                <div class="col-lg-3 col-sm-6">
                    <section class="panel">
                        <div class="symbol red">
                            <i class="fa fa-book"></i>
                        </div>
                        <div class="value">
                            <h1 class="count2">${dataObj.yesterdayCount }</h1>
                            <p>昨日活跃</p>
                        </div>
                    </section>
                </div>
                <div class="col-lg-3 col-sm-6">
                    <section class="panel">
                        <div class="symbol yellow">
                            <i class="fa fa-share"></i>
                        </div>
                        <div class="value">
                            <h1 class="count3">${dataObj.threedayCount }</h1>
                            <p>三日活跃</p>
                        </div>
                    </section>
                </div>
                <div class="col-lg-3 col-sm-6">
                    <section class="panel">
                        <div class="symbol blue">
                            <i class="fa fa-fire"></i>
                        </div>
                        <div class="value">
                            <h1 class="count4">${dataObj.sevendayCount }</h1>
                            <p>七日活跃</p>
                        </div>
                    </section>
                </div>
            </div>
            <!--state overview end-->
        </section>
    </section>
    <!--main content end-->

    <!-- Right Slidebar start -->
    <%@include file="common/rightSlidebar.jsp"%>
    <!-- Right Slidebar end -->

    <!--footer start-->
    <%@include file="common/footer.jsp"%>
    <!--footer end-->
</section>

<!-- js placed at the end of the document so the pages load faster -->
<script src="${ctx}/js/jquery.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script class="include" type="text/javascript" src="${ctx}/js/jquery.dcjqaccordion.2.7.js"></script>
<script src="${ctx}/js/jquery.scrollTo.min.js"></script>
<script src="${ctx}/js/jquery.nicescroll.js" type="text/javascript"></script>
<script src="${ctx}/js/jquery.sparkline.js" type="text/javascript"></script>
<script src="${ctx}/assets/jquery-easy-pie-chart/jquery.easy-pie-chart.js"></script>
<script src="${ctx}/js/owl.carousel.js"></script>
<script src="${ctx}/js/jquery.customSelect.min.js"></script>
<script src="${ctx}/js/respond.min.js"></script>
<script src="${ctx}/js/slidebars.min.js"></script>
<script src="${ctx}/js/common-scripts.js"></script>
<script src="${ctx}/js/sparkline-chart.js"></script>
<script src="${ctx}/js/easy-pie-chart.js"></script>
<script src="${ctx}/js/count.js"></script>
</body>
</html>