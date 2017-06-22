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
                    <li <%if("3_4".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appchannel/query">渠道管理</a></li>
                    <li <%if("3_5".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/channelRegister/query">渠道注册统计</a></li>
                    <li <%if("3_6".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/king/day/query">王国[按天]统计</a></li>
                    <li <%if("3_7".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/userRegister/detail/query">注册用户明细统计</a></li>
                    <li <%if("3_8".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/kingdomCreate/detail/query">新建王国明细统计</a></li>
                    <li <%if("3_9".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/userRegister/query">用户注册统计</a></li>
                    <li <%if("3_10".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/stat/iosWapx/query">IOS推广激活统计</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("4".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-gift"></i>
                    <span>抽奖活动管理</span>
                </a>
                <ul class="sub">
                    <li <%if("4_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/lottery/query">抽奖活动管理</a></li>
                    <li <%if("4_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/lottery/statusQuery">抽奖状态查询</a></li>
                    <li <%if("4_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/lottery/statusStatQuery">抽奖统计</a></li>
                    <li <%if("4_4".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/lottery/winnerQuery">获奖信息查询</a></li>
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
                    <li <%if("5_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appuser/msgsender">用户短信发送</a></li>
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
                	<li <%if("7_0".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/version/query">APP版本管理</a></li>
                	<li <%if("7_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/version/channel/query">APP版本渠道下载管理</a></li>
                    <li <%if("7_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/cache/query">缓存配置管理</a></li>
                    <li <%if("7_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/dbconfig/query">数据库配置管理</a></li>
                    <li <%if("7_8".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/newconfig/query">新APP配置管理</a></li>
                    <li <%if("7_4".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/ui/query">APP主题管理</a></li>
                    <li <%if("7_5".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/appconfig/lightbox/query">APP灯箱页管理</a></li>
                    <li <%if("7_6".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/topicFragmentTemplate/query">足迹语言模板管理</a></li>
                    <li <%if("7_7".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/topicFragmentTemplate/dropAroundKingdomMgr">可串门王国管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("8".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>七天活动管理</span>
                </a>
                <ul class="sub">
                	<li <%if("8_0".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/7day/getActivityInfo">活动阶段管理</a></li>
                	<li <%if("8_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/7day/stat/user">报名用户统计</a></li>
                	<li <%if("8_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/7day/control/index">后台控制管理</a></li>
                	<li <%if("8_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/7day/milidata/query">米粒管理</a></li>
                	<li <%if("8_4".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/7day/task/query">活动任务管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("9".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>春节活动管理</span>
                </a>
                <ul class="sub">
                	<li <%if("9_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/spring/getActivityInfo">活动阶段管理</a></li>
                	<li <%if("9_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/spring/control/index">后台控制管理</a></li>
                	<li <%if("9_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/spring/milidata/query">米粒管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("zmjx".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>最美家乡活动</span>
                </a>
                <ul class="sub">
                	<li <%if("zmjx_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/zmjx/config/kingdomQuery">活动王国管理</a></li>
                	<li <%if("zmjx_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/zmjx/config/query">配置管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("10".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>榜单管理</span>
                </a>
                <ul class="sub">
                	<li <%if("10_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/ranking/list_ranking">榜单列表</a></li>
                	<li <%if("10_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/ranking/list_online_ranking?type=1">上线榜单管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("11".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>搜索管理</span>
                </a>
                <ul class="sub">
                	<li <%if("11_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/search/console">控制台</a></li>
                	<li <%if("11_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/search/hotkeywordList">热词管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("12".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>素材管理</span>
                </a>
                <ul class="sub">
                	<li <%if("12_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/tag/query">标签管理</a></li>
                	<li <%if("12_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/emotion/list_pack">表情包管理</a></li>
                	<li <%if("12_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/tease/list_tease">逗一逗管理</a></li>
                	<li <%if("12_4".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/mbtiMapping/query">MBTI王国映射管理</a></li>
                	<li <%if("12_5".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/emotionInfo/list_emotion">情绪管理</a></li>
                </ul>
            </li>
            <li class="sub-menu">
                <a href="javascript:;" <%if("13".equals(tab)){ %>class="active"<%} %>>
                    <i class="fa fa-bar-chart-o"></i>
                    <span>王国价值管理</span>
                </a>
                <ul class="sub">
                	<li <%if("13_1".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/price/kingdomQuery">王国交易管理</a></li>
                	<li <%if("13_2".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/price/allConfig">全局系统配置</a></li>
                	<li <%if("13_3".equals(sub)){ %>class="active"<%} %>><a href="${ctx}/price/taskConsole">任务控制台</a></li>
                </ul>
            </li>
        </ul>
        <!-- sidebar menu end-->
    </div>
</aside>

