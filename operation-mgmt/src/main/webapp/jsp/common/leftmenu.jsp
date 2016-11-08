<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String tab = request.getParameter("t");
String sub = request.getParameter("s");
%>
<aside>
    <div id="sidebar" class="nav-collapse ">
        <!-- sidebar menu start-->
        <ul class="sidebar-menu" id="nav-accordion">
            <li>
                <a href="${ctx}/dashboard" <%if("1".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-dashboard"></i>
                    <span>Dashboard</span>
                </a>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("2".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-book"></i>
                    <span>APP文章管理</span>
                </a>
                <ul class="sub">
                    <li <%if("2_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/activity/query">活动信息管理</a></li>
                    <li <%if("2_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/pgc/query">PGC文章管理</a></li>
                    <li <%if("2_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/ugc/query">UGC文章管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("3".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>运营统计管理</span>
                </a>
                <ul class="sub">
                    <li <%if("3_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/dailyActive/query">日活统计管理</a></li>
                    <li <%if("3_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/promoter/query">推广统计管理</a></li>
                    <li <%if("3_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/king/query">国王统计管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("4".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-gift"></i>
                    <span>抽奖活动管理</span>
                </a>
                <ul class="sub">
                    <li <%if("4_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/lottery/winnerQuery">获奖信息查询</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("5".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-users"></i>
                    <span>APP用户管理</span>
                </a>
                <ul class="sub">
                    <li <%if("5_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appuser/query">用户管理</a></li>
                    <li <%if("5_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appuser/gaguser/query">禁言用户管理</a></li>
                </ul>
            </li>
			<li class="sub-menu">
                <a href="javascript:;" <%if("6".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-sitemap"></i>
                    <span>系统管理</span>
                </a>
                <ul class="sub">
                    <li <%if("6_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/system/log/query">系统日志查询</a></li>
                    <li <%if("6_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/system/user/query">系统用户查询</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("7".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-mobile"></i>
                    <span>APP配置管理</span>
                </a>
                <ul class="sub">
                	<li <%if("7_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/version/query">APP版本管理</a></li>
                    <li <%if("7_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/cache/query">缓存配置管理</a></li>
                </ul>
            </li>
        </ul>
        <!-- sidebar menu end-->
    </div>
</aside>

