<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <%@include file="common/meta.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>ZX_IMS 2.0 - 仪表盘</title>

    <!-- Bootstrap core CSS -->
    <link href="${ctx}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctx}/css/bootstrap-reset.css" rel="stylesheet">
    <!--external css-->
    <link href="${ctx}/assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
    <link href="${ctx}/assets/jquery-easy-pie-chart/jquery.easy-pie-chart.css" rel="stylesheet" type="text/css" media="screen" />
    <link rel="stylesheet" href="${ctx}/css/owl.carousel.css" type="text/css">

    <!--right slidebar-->
    <link href="${ctx}/css/slidebars.css" rel="stylesheet">

    <!-- Custom styles for this template -->

    <link href="${ctx}/css/style.css" rel="stylesheet">
    <link href="${ctx}/css/style-responsive.css" rel="stylesheet" />

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 tooltipss and media queries -->
    <!--[if lt IE 9]>
    <script src="${ctx}/js/html5shiv.js"></script>
    <script src="${ctx}/js/respond.min.js"></script>
    <![endif]-->
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
                            <h1 class="count"></h1>
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
                            <h1 class=" count2">
                            </h1>
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
                            <h1 class=" count3">
                            </h1>
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
                            <h1 class=" count4">
                            </h1>
                            <p>七日活跃</p>
                        </div>
                    </section>
                </div>
            </div>
            <!--state overview end-->

            <div class="row">
                <div class="col-lg-8">
                    <!--custom chart start-->
                    <div class="border-head">
                        <h3>每月活跃量</h3>
                    </div>
                    <div class="custom-bar-chart">
                        <ul class="y-axis">
                            <li><span>100</span></li>
                            <li><span>80</span></li>
                            <li><span>60</span></li>
                            <li><span>40</span></li>
                            <li><span>20</span></li>
                            <li><span>0</span></li>
                        </ul>
                        <div class="bar">
                            <div class="title">一月</div>
                            <div class="value tooltips" data-original-title="80%" data-toggle="tooltip" data-placement="top">80</div>
                        </div>
                        <div class="bar ">
                            <div class="title">二月</div>
                            <div class="value tooltips" data-original-title="50%" data-toggle="tooltip" data-placement="top">50%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">三月</div>
                            <div class="value tooltips" data-original-title="40%" data-toggle="tooltip" data-placement="top">40%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">四月</div>
                            <div class="value tooltips" data-original-title="55%" data-toggle="tooltip" data-placement="top">55%</div>
                        </div>
                        <div class="bar">
                            <div class="title">五月</div>
                            <div class="value tooltips" data-original-title="20%" data-toggle="tooltip" data-placement="top">20%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">六月</div>
                            <div class="value tooltips" data-original-title="39%" data-toggle="tooltip" data-placement="top">39%</div>
                        </div>
                        <div class="bar">
                            <div class="title">七月</div>
                            <div class="value tooltips" data-original-title="75%" data-toggle="tooltip" data-placement="top">75%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">八月</div>
                            <div class="value tooltips" data-original-title="45%" data-toggle="tooltip" data-placement="top">45%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">九月</div>
                            <div class="value tooltips" data-original-title="50%" data-toggle="tooltip" data-placement="top">50%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">十月</div>
                            <div class="value tooltips" data-original-title="42%" data-toggle="tooltip" data-placement="top">42%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">十一月</div>
                            <div class="value tooltips" data-original-title="60%" data-toggle="tooltip" data-placement="top">60%</div>
                        </div>
                        <div class="bar ">
                            <div class="title">十二月</div>
                            <div class="value tooltips" data-original-title="90%" data-toggle="tooltip" data-placement="top">90%</div>
                        </div>
                    </div>
                    <!--custom chart end-->
                </div>
                <div class="col-lg-4">
                    <!--new earning start-->
                    <div class="panel terques-chart">
                        <div class="panel-body chart-texture">
                            <div class="chart">
                                <div class="heading">
                                    <span>Friday</span>
                                    <strong>$ 57,00 | 15%</strong>
                                </div>
                                <div class="sparkline" data-type="line" data-resize="true" data-height="75" data-width="90%" data-line-width="1" data-line-color="#fff" data-spot-color="#fff" data-fill-color="" data-highlight-line-color="#fff" data-spot-radius="4" data-data="[200,135,667,333,526,996,564,123,890,564,455]"></div>
                            </div>
                        </div>
                        <div class="chart-tittle">
                            <span class="title">今日活跃量</span>
                                <span class="value">
                                    <a href="#" class="active">Market</a>
                                    |
                                  <a href="#">Referal</a>
                                    |
                                  <a href="#">Online</a>
                                </span>
                        </div>
                    </div>
                    <!--new earning end-->

                    <!--total earning start-->
                    <div class="panel green-chart">
                        <div class="panel-body">
                            <div class="chart">
                                <div class="heading">
                                    <span>June</span>
                                    <strong>23 Days | 65%</strong>
                                </div>
                                <div id="barchart"></div>
                            </div>
                        </div>
                        <div class="chart-tittle">
                            <span class="title">当月活跃</span>
                            <span class="value">76,54,678</span>
                        </div>
                    </div>
                    <!--total earning end-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-4">
                    <!--user info table start-->
                    <section class="panel">
                        <div class="panel-body">
                            <a href="#" class="task-thumb">
                                <img src="img/avatar1.jpg" alt="">
                            </a>
                            <div class="task-thumb-details">
                                <h1><a href="#">无所事事</a></h1>
                                <p>当前最热话题：1000</p>
                            </div>
                        </div>
                        <table class="table table-hover personal-task">
                            <tbody>
                            <tr>
                                <td>
                                    <i class=" fa fa-tasks"></i>
                                </td>
                                <td>New Task Issued</td>
                                <td>02</td>
                            </tr>
                            <tr>
                                <td>
                                    <i class="fa fa-exclamation-triangle"></i>
                                </td>
                                <td>Task Pending</td>
                                <td>14</td>
                            </tr>
                            <tr>
                                <td>
                                    <i class="fa fa-envelope"></i>
                                </td>
                                <td>Inbox</td>
                                <td>45</td>
                            </tr>
                            <tr>
                                <td>
                                    <i class=" fa fa-bell-o"></i>
                                </td>
                                <td>New Notification</td>
                                <td>09</td>
                            </tr>
                            </tbody>
                        </table>
                    </section>
                    <!--user info table end-->
                </div>
                <div class="col-lg-8">
                    <!--work progress start-->
                    <section class="panel">
                        <div class="panel-body progress-panel">
                            <div class="task-progress">
                                <h1>热点关键词</h1>
                                <p>用户输入最多的关键字排行</p>
                            </div>
                            <div class="task-option">
                                <select class="styled">
                                    <option>产品库存</option>
                                    <option>材料库存</option>
                                </select>
                            </div>
                        </div>
                        <table class="table table-hover personal-task">
                            <tbody>
                            <tr>
                                <td>1</td>
                                <td>Target Sell
                                </td>
                                <td>
                                    <span class="badge bg-important">75%</span>
                                </td>
                                <td>
                                    <div id="work-progress1"></div>
                                </td>
                            </tr>
                            <tr>
                                <td>2</td>
                                <td>Product Delivery
                                </td>
                                <td>
                                    <span class="badge bg-success">43%</span>
                                </td>
                                <td>
                                    <div id="work-progress2"></div>
                                </td>
                            </tr>
                            <tr>
                                <td>3</td>
                                <td>Payment Collection
                                </td>
                                <td>
                                    <span class="badge bg-info">67%</span>
                                </td>
                                <td>
                                    <div id="work-progress3"></div>
                                </td>
                            </tr>
                            <tr>
                                <td>4</td>
                                <td>Work Progress
                                </td>
                                <td>
                                    <span class="badge bg-warning">30%</span>
                                </td>
                                <td>
                                    <div id="work-progress4"></div>
                                </td>
                            </tr>
                            <tr>
                                <td>5</td>
                                <td>Delivery Pending
                                </td>
                                <td>
                                    <span class="badge bg-primary">15%</span>
                                </td>
                                <td>
                                    <div id="work-progress5"></div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </section>
                    <!--work progress end-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-8">
                    <!--timeline start-->
                    <section class="panel">
                        <div class="panel-body">
                            <div class="text-center mbot30">
                                <h3 class="timeline-title">Timeline</h3>
                                <p class="t-info">This is a project timeline</p>
                            </div>

                            <div class="timeline">
                                <article class="timeline-item">
                                    <div class="timeline-desk">
                                        <div class="panel">
                                            <div class="panel-body">
                                                <span class="arrow"></span>
                                                <span class="timeline-icon red"></span>
                                                <span class="timeline-date">08:25 am</span>
                                                <h1 class="red">12 July | Sunday</h1>
                                                <p>Lorem ipsum dolor sit amet consiquest dio</p>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                                <article class="timeline-item alt">
                                    <div class="timeline-desk">
                                        <div class="panel">
                                            <div class="panel-body">
                                                <span class="arrow-alt"></span>
                                                <span class="timeline-icon green"></span>
                                                <span class="timeline-date">10:00 am</span>
                                                <h1 class="green">10 July | Wednesday</h1>
                                                <p><a href="#">Jonathan Smith</a> added new milestone <span><a href="#" class="green">ERP</a></span></p>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                                <article class="timeline-item">
                                    <div class="timeline-desk">
                                        <div class="panel">
                                            <div class="panel-body">
                                                <span class="arrow"></span>
                                                <span class="timeline-icon blue"></span>
                                                <span class="timeline-date">11:35 am</span>
                                                <h1 class="blue">05 July | Monday</h1>
                                                <p><a href="#">Anjelina Joli</a> added new album <span><a href="#" class="blue">PARTY TIME</a></span></p>
                                                <div class="album">
                                                    <a href="#">
                                                        <img alt="" src="img/sm-img-1.jpg">
                                                    </a>
                                                    <a href="#">
                                                        <img alt="" src="img/sm-img-2.jpg">
                                                    </a>
                                                    <a href="#">
                                                        <img alt="" src="img/sm-img-3.jpg">
                                                    </a>
                                                    <a href="#">
                                                        <img alt="" src="img/sm-img-1.jpg">
                                                    </a>
                                                    <a href="#">
                                                        <img alt="" src="img/sm-img-2.jpg">
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                                <article class="timeline-item alt">
                                    <div class="timeline-desk">
                                        <div class="panel">
                                            <div class="panel-body">
                                                <span class="arrow-alt"></span>
                                                <span class="timeline-icon purple"></span>
                                                <span class="timeline-date">3:20 pm</span>
                                                <h1 class="purple">29 June | Saturday</h1>
                                                <p>Lorem ipsum dolor sit amet consiquest dio</p>
                                                <div class="notification">
                                                    <i class=" fa fa-exclamation-sign"></i>New task added for <a href="#">Denial Collins</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                                <article class="timeline-item">
                                    <div class="timeline-desk">
                                        <div class="panel">
                                            <div class="panel-body">
                                                <span class="arrow"></span>
                                                <span class="timeline-icon light-green"></span>
                                                <span class="timeline-date">07:49 pm</span>
                                                <h1 class="light-green">10 June | Friday</h1>
                                                <p><a href="#">Jonatha Smith</a> added new milestone <span><a href="#" class="light-green">prank</a></span> Lorem ipsum dolor sit amet consiquest dio</p>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                            </div>

                            <div class="clearfix">&nbsp;</div>
                        </div>
                    </section>
                    <!--timeline end-->
                </div>
                <div class="col-lg-4">
                    <!--revenue start-->
                    <section class="panel">
                        <div class="revenue-head">
                                <span>
                                    <i class="fa fa-bar-chart-o"></i>
                                </span>
                            <h3>Revenue</h3>
                                <span class="rev-combo pull-right">June 2013
                                </span>
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                <div class="col-lg-6 col-sm-6 text-center">
                                    <div class="easy-pie-chart">
                                        <div class="percentage" data-percent="35"><span>35</span>%</div>
                                    </div>
                                </div>
                                <div class="col-lg-6 col-sm-6">
                                    <div class="chart-info chart-position">
                                        <span class="increase"></span>
                                        <span>Revenue Increase</span>
                                    </div>
                                    <div class="chart-info">
                                        <span class="decrease"></span>
                                        <span>Revenue Decrease</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="panel-footer revenue-foot">
                            <ul>
                                <li class="first active">
                                    <a href="javascript:;">
                                        <i class="fa fa-bullseye"></i>
                                        Graphical
                                    </a>
                                </li>
                                <li>
                                    <a href="javascript:;">
                                        <i class=" fa fa-th-large"></i>
                                        Tabular
                                    </a>
                                </li>
                                <li class="last">
                                    <a href="javascript:;">
                                        <i class=" fa fa-align-justify"></i>
                                        Listing
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <!--revenue end-->
                    <!--features carousel start-->
                    <section class="panel">
                        <div class="flat-carousal">
                            <div id="owl-demo" class="owl-carousel owl-theme">
                                <div class="item">
                                    <h1>Flatlab is new model of admin dashboard for happy use</h1>
                                    <div class="text-center">
                                        <a href="javascript:;" class="view-all">View All</a>
                                    </div>
                                </div>
                                <div class="item">
                                    <h1>Fully responsive and build with Bootstrap 3.0</h1>
                                    <div class="text-center">
                                        <a href="javascript:;" class="view-all">View All</a>
                                    </div>
                                </div>
                                <div class="item">
                                    <h1>Responsive Frontend is free if you get this.</h1>
                                    <div class="text-center">
                                        <a href="javascript:;" class="view-all">View All</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="panel-body">
                            <ul class="ft-link">
                                <li class="active">
                                    <a href="javascript:;">
                                        <i class="fa fa-bars"></i>
                                        Sales
                                    </a>
                                </li>
                                <li>
                                    <a href="javascript:;">
                                        <i class=" fa fa-calendar-o"></i>
                                        promo
                                    </a>
                                </li>
                                <li>
                                    <a href="javascript:;">
                                        <i class=" fa fa-camera"></i>
                                        photo
                                    </a>
                                </li>
                                <li>
                                    <a href="javascript:;">
                                        <i class=" fa fa-circle"></i>
                                        other
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <!--features carousel end-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-8">
                    <!--latest product info start-->
                    <section class="panel post-wrap pro-box">
                        <aside>
                            <div class="post-info">
                                <span class="arrow-pro right"></span>
                                <div class="panel-body">
                                    <h1><strong>popular</strong>
                                        <br>
                                        Brand of this week</h1>
                                    <div class="desk yellow">
                                        <h3>Dimond Ring</h3>
                                        <p>Lorem ipsum dolor set amet lorem ipsum dolor set amet ipsum dolor set amet</p>
                                    </div>
                                    <div class="post-btn">
                                        <a href="javascript:;">
                                            <i class="fa fa-chevron-circle-left"></i>
                                        </a>
                                        <a href="javascript:;">
                                            <i class="fa fa-chevron-circle-right"></i>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </aside>
                        <aside class="post-highlight yellow v-align">
                            <div class="panel-body text-center">
                                <div class="pro-thumb">
                                    <img src="img/ring.jpg" alt="">
                                </div>
                            </div>
                        </aside>
                    </section>
                    <!--latest product info end-->
                    <!--twitter feedback start-->
                    <section class="panel post-wrap pro-box">
                        <aside class="post-highlight terques v-align">
                            <div class="panel-body">
                                <h2>Flatlab is new model of admin dashboard <a href="javascript:;">http://demo.com/</a> 4 days ago  by jonathan smith</h2>
                            </div>
                        </aside>
                        <aside>
                            <div class="post-info">
                                <span class="arrow-pro left"></span>
                                <div class="panel-body">
                                    <div class="text-center twite">
                                        <h1>Twitter Feed</h1>
                                    </div>

                                    <footer class="social-footer">
                                        <ul>
                                            <li>
                                                <a href="#">
                                                    <i class="fa fa-facebook"></i>
                                                </a>
                                            </li>
                                            <li class="active">
                                                <a href="#">
                                                    <i class="fa fa-twitter"></i>
                                                </a>
                                            </li>
                                            <li>
                                                <a href="#">
                                                    <i class="fa fa-google-plus"></i>
                                                </a>
                                            </li>
                                            <li>
                                                <a href="#">
                                                    <i class="fa fa-pinterest"></i>
                                                </a>
                                            </li>
                                        </ul>
                                    </footer>
                                </div>
                            </div>
                        </aside>
                    </section>
                    <!--twitter feedback end-->
                </div>
                <div class="col-lg-4">
                    <div class="row">
                        <div class="col-xs-6">
                            <!--pie chart start-->
                            <section class="panel">
                                <div class="panel-body">
                                    <div class="chart">
                                        <div id="pie-chart"></div>
                                    </div>
                                </div>
                                <footer class="pie-foot">
                                    Free: 260GB
                                </footer>
                            </section>
                            <!--pie chart start-->
                        </div>
                        <div class="col-xs-6">
                            <!--follower start-->
                            <section class="panel">
                                <div class="follower">
                                    <div class="panel-body">
                                        <h4>Jonathan Smith</h4>
                                        <div class="follow-ava">
                                            <img src="img/follower-avatar.jpg" alt="">
                                        </div>
                                    </div>
                                </div>

                                <footer class="follower-foot">
                                    <ul>
                                        <li>
                                            <h5>2789</h5>
                                            <p>Follower</p>
                                        </li>
                                        <li>
                                            <h5>270</h5>
                                            <p>Following</p>
                                        </li>
                                    </ul>
                                </footer>
                            </section>
                            <!--follower end-->
                        </div>
                    </div>
                    <!--weather statement start-->
                    <section class="panel">
                        <div class="weather-bg">
                            <div class="panel-body">
                                <div class="row">
                                    <div class="col-xs-6">
                                        <i class="fa fa-cloud"></i>
                                        California
                                    </div>
                                    <div class="col-xs-6">
                                        <div class="degree">
                                            24°
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <footer class="weather-category">
                            <ul>
                                <li class="active">
                                    <h5>humidity</h5>
                                    56%
                                </li>
                                <li>
                                    <h5>precip</h5>
                                    1.50 in
                                </li>
                                <li>
                                    <h5>winds</h5>
                                    10 mph
                                </li>
                            </ul>
                        </footer>

                    </section>
                    <!--weather statement end-->
                </div>
            </div>

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

<!--right slidebar-->
<script src="${ctx}/js/slidebars.min.js"></script>

<!--common script for all pages-->
<script src="${ctx}/js/common-scripts.js"></script>

<!--script for this page-->
<script src="${ctx}/js/sparkline-chart.js"></script>
<script src="${ctx}/js/easy-pie-chart.js"></script>
<script src="${ctx}/js/count.js"></script>

<script>

    //owl carousel

    $(document).ready(function () {
        $("#owl-demo").owlCarousel({
            navigation: true,
            slideSpeed: 300,
            paginationSpeed: 400,
            singleItem: true,
            autoPlay: true

        });
    });

    //custom select box

    $(function () {
        $('select.styled').customSelect();
    });

</script>

</body>
</html>