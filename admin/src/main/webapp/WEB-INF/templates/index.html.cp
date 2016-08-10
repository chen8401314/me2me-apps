<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>笑林杂谈</title>
    <meta name="description" content="笑林杂谈是个提供搞笑、段子、美女、热舞、性感、街拍、自拍、笑话的内容平台。" />
    <meta name="keywords" content="搞笑,段子,美女,热舞,性感,街拍,自拍,笑话"/>
    <meta name="HandheldFriendly" content="True" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="shortcut icon" th:href="@{/assets/favicon.ico}"/>
    <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="//cdn.bootcss.com/font-awesome/4.3.0/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="//cdn.bootcss.com/highlight.js/8.5/styles/monokai_sublime.min.css"/>
    <link href="//cdn.bootcss.com/magnific-popup.js/1.0.0/magnific-popup.min.css" rel="stylesheet"/>
    <link rel="stylesheet" type="text/css" th:href="@{/assets/css/screen.css}"/>
    <link rel="canonical" href="http://www.51nick.com/" />
    <meta name="referrer" content="origin" />
    <script type="text/javascript" src="/shared/ghost-url.min.js?v=82931f6358"></script>
</head>
<body class="home-template">
<!-- start navigation -->
<nav class="navbar navbar-default">
    <div class="container">
        <div class="row">
            <div class="col-sm-12">
                <div class="navbar-header">
                        <span class="nav-toggle-button collapsed" data-toggle="collapse" data-target="#main-menu">
                        <span class="sr-only">Toggle navigation</span>
                        <i class="fa fa-bars"></i>
                        </span>
                    <a class="navbar-brand" href="/">笑林杂谈</a>
                </div>
                <div class="collapse navbar-collapse" id="main-menu">
                    <ul class="nav navbar-nav">
                        <li class="active" role="presentation"><a href="/">首页</a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">热门 <b class="caret"></b></a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a href="#">24小时</a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a href="#">一周</a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a href="#">一个月</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#">趣图</a>
                        </li>
                        <li>
                            <a href="#">段子</a>
                        </li>
                        <li>
                            <a href="#">妹子</a>
                        </li>
                        <li>
                            <a href="#">街拍</a>
                        </li>
                        <li>
                            <a href="#">热舞</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</nav>
<!-- end navigation -->


<!-- start site's main content area -->
<section class="content-wrap">
    <div class="container">
        <div class="row">
            <main class="col-md-8 main-content">
                <th:block th:each="article:${root.elements}">
                    <article class="post">
                        <div class="post-head">
                            <h1 class="post-title"><a th:href="@{/show_detail/(id=${article.id})}" th:text="${article.title}"></a></h1>
                            <div class="post-meta">
                                <span class="author">作者：<a href="#" th:text="${article.author}"></a></span> &bull;
                                <time class="post-date" th:text="${#calendars.format(article.createTime,'yyyy-MM-dd HH:mm:ss')}"></time>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-5">
                                <div class="featured-media">
                                    <a th:href="@{/show_detail/(id=${article.id})}"><img class="thumbnail img-responsive" th:src="${article.thumb}" th:alt="${article.title}"/></a>
                                </div>
                            </div>
                            <div class="col-sm-7">
                                <div class="post-content" style="max-height: 150px; overflow:hidden; text-overflow:ellipsis;">
                                    <p th:utext="${article.content}"></p>
                                </div>
                            </div>
                        </div>
                        <div class="post-permalink">
                            <a th:href="@{/show_detail/(id=${article.id})}" class="btn btn-default">阅读全文</a>
                        </div>

                        <footer class="post-footer clearfix">
                            <div class="pull-left tag-list">
                                <i class="fa fa-folder-open-o"></i>
                            </div>
                            <div class="pull-right share">
                            </div>
                        </footer>
                    </article>
                </th:block>
                <nav class="pagination" role="navigation">
                    <span class="page-number">第 1 页 &frasl; 共 8 页</span>
                    <a class="older-posts" href="/page/2/"><i class="fa fa-angle-right"></i></a>
                </nav>


            </main>

            <aside class="col-md-4 sidebar">
                <!-- start widget -->
                <!-- end widget -->

                <!-- start tag cloud widget -->
                <div class="widget">
                    <h4 class="title">阅读排行榜</h4>
                    <div class="content community">
                        <p><span class="label label-danger">1</span> <a href="#">大姨妈的现代生活</a></p>
                        <p><span class="label label-danger">2</span> <a href="#">大姨妈的现代生活</a></p>
                        <p><span class="label label-danger">3</span> <a href="#">大姨妈的现代生活</a></p>
                    </div>
                </div>
                <!-- end tag cloud widget -->

                <!-- start widget -->
                <div class="widget">
                    <h4 class="title">热门推荐</h4>
                    <div class="content download">
                        <a href="/download/" class="">fdsfds</a>
                    </div>
                </div>
                <!-- end widget -->

                <!-- start tag cloud widget -->
                <div class="widget">
                    <h4 class="title">笑林标签</h4>
                    <div class="content tag-cloud">
                        <a href="/tag/jquery/">jQuery</a>
                        <a href="/tag/ghost-0-7-ban-ben/">Ghost 0.7 版本</a>
                        <a href="/tag/opensource/">开源</a>
                        <a href="/tag/zhu-shou-han-shu/">助手函数</a>
                        <a href="/tag/tag-cloud/">标签云</a>
                        <a href="/tag/navigation/">导航</a>
                        <a href="/tag/customize-page/">自定义页面</a>
                        <a href="/tag/static-page/">静态页面</a>
                        <a href="/tag/roon-io/">Roon.io</a>
                        <a href="/tag/configuration/">配置文件</a>
                        <a href="/tag/upyun/">又拍云存储</a>
                        <a href="/tag/upload/">上传</a>
                        <a href="/tag/handlebars/">Handlebars</a>
                        <a href="/tag/email/">邮件</a>
                        <a href="/tag/shortcut/">快捷键</a>
                        <a href="/tag/yong-hu-zhi-nan/">用户指南</a>
                        <a href="/tag/theme-market/">主题市场</a>
                        <a href="/tag/release/">新版本发布</a>
                        <a href="/tag-cloud/">...</a>
                    </div>
                </div>
                <!-- end tag cloud widget -->

                <!-- start widget -->
                <!-- end widget -->

                <!-- start widget -->
                <!-- end widget -->
            </aside>
        </div>
    </div>
</section>

<footer class="main-footer">
    <div class="container">
        <div class="row">
            <div class="col-sm-4">
                <div class="widget">
                    <h4 class="title">最新文章</h4>
                    <div class="content recent-post">
                        <div class="recent-single-post">
                            <a href="#" class="post-title">Ghost 桌面版 APP 正式发布，能同时管理多个 Ghost 博客</a>
                            <div class="date">2016年4月28日</div>
                        </div>
                        <div class="recent-single-post">
                            <a href="#" class="post-title">为 Ubuntu 和 Debian 安装最新版本的 Node.js</a>
                            <div class="date">2016年3月23日</div>
                        </div>
                        <div class="recent-single-post">
                            <a href="#" class="post-title">Ghost 0.7.4 正式发布</a>
                            <div class="date">2015年12月29日</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-sm-4">
                <div class="widget">
                    <h4 class="title">标签云</h4>
                    <div class="content tag-cloud">
                        <a href="/tag/about-ghost/">Ghost</a>
                        <a href="/tag/release/">新版本发布</a>
                        <a href="/tag/javascript/">JavaScript</a>
                        <a href="/tag/promise/">Promise</a>
                        <a href="/tag/zhuti/">主题</a>
                        <a href="/tag/nodejs/">Node.js</a>
                        <a href="/tag/mysql/">MySQL</a>
                        <a href="/tag/nginx/">Nginx</a>
                        <a href="/tag/aliyun-ecs/">阿里云服务器</a>
                        <a href="/tag/ubuntu/">Ubuntu</a>
                        <a href="/tag/ghost-in-depth/">深度玩转 Ghost</a>
                        <a href="/tag/theme/">Theme</a>
                        <a href="/tag/markdown/">Markdown</a>
                        <a href="/tag/proxy/">反向代理</a>
                        <a href="/tag/apache/">Apache</a>
                        <a href="/tag-cloud/">...</a>
                    </div>
                </div>
            </div>

            <div class="col-sm-4">
                <div class="widget">
                    <h4 class="title">合作伙伴</h4>
                    <div class="content tag-cloud friend-links">
                        <a href="http://www.bootcss.com" title="Bootstrap中文网" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'bootcsscom'])" target="_blank">Bootstrap中文网</a>
                        <a href="http://www.bootcdn.cn" title="开放CDN服务" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'bootcdncn'])" target="_blank">开放CDN服务</a>
                        <a href="http://www.gruntjs.net" title="Grunt中文网" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'gruntjsnet'])" target="_blank">Grunt中文网</a>
                        <a href="http://www.gulpjs.com.cn/" title="Gulp中文网" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'gulpjscomcn'])" target="_blank">Gulp中文网</a>
                        <hr/>
                        <a href="http://lodashjs.com/" title="Lodash中文文档" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'lodashjscom'])" target="_blank">Lodash中文文档</a>
                        <a href="http://www.jquery123.com/" title="jQuery中文文档" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'jquery123com'])" target="_blank">jQuery中文文档</a>
                        <hr/>
                        <a href="http://www.aliyun.com/" title="阿里云" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'aliyun'])" target="_blank">阿里云</a>
                        <hr/>
                        <a href="https://www.upyun.com/" title="又拍云" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'upyun'])" target="_blank">又拍云</a>
                        <a href="http://www.ucloud.cn/" title="UCloud" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'ucloud'])" target="_blank">UCloud</a>
                        <a href="http://www.qiniu.com/" title="七牛云存储" onclick="_hmt.push(['_trackEvent', 'link', 'click', 'qiniu'])" target="_blank">七牛云存储</a>
                    </div>
                </div></div>
        </div>
    </div>
</footer>

<div class="copyright">
    <div class="container">
        <div class="row">
            <div class="col-sm-12">
                <span>Copyright &copy; <a href="http://www.ghostchina.com/">Ghost中文网</a></span> |
                <span><a href="http://www.miibeian.gov.cn/" target="_blank">京ICP备11008151号</a></span> |
                <span>京公网安备11010802014853</span>
            </div>
        </div>
    </div>
</div>

<a href="#" id="back-to-top"><i class="fa fa-angle-up"></i></a>

<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="//cdn.bootcss.com/fitvids/1.1.0/jquery.fitvids.min.js"></script>
<script src="//cdn.bootcss.com/highlight.js/8.5/highlight.min.js"></script>
<script src="//cdn.bootcss.com/magnific-popup.js/1.0.0/jquery.magnific-popup.min.js"></script>
<script src="/assets/js/main.js?v=82931f6358"></script>
<script>
    $(function(){
        $('.post-content img').each(function(item){
            var src = $(this).attr('src');

            $(this).wrap('<a href="' + src + '" class="mfp-zoom" style="display:block;"></a>');
        });

        /*$('.post-content').magnificPopup({
         delegate: 'a',
         type: 'image'
         });*/
    });
</script>
</body>
</html>
