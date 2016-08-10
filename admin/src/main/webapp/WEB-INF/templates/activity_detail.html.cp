<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org" lang="en">
    <head th:fragment="header" xmlns:th="http://www.w3.org/1999/xhtml">
        <meta charset="UTF-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="author" content="" />
        <link rel="stylesheet" th:href="@{/assets/css/bootstrap.css}"/>
        <link rel="stylesheet" th:href="@{/assets/css/admin.css}"/>
        <script th:src="@{/assets/js/jquery-1.11.1.min.js}"></script>
        <script th:src="@{/assets/js/bootstrap.min.js}"></script>
        <script>
            $(function(){
                // add img-responsive to img tags
                $('img').addClass('img-responsive');
            });
        </script>
    </head>
<body>
    <div class="container">
        <th:block th:if="${share} eq 1">
            <section id="DLArea">
                <aside id="DLAreaas">
                    <img id="DLArealogo" th:src="@{/assets/images/logo.png}"/>
                    <div id="DLAreatitle1">米汤</div>
                    <div id="DLAreatitle2">经历过总有感受！</div>
                    <a href="http://qrcode.me-to-me.com/common.html" id="DLAreaOpen">打开</a>
                    <p id="DLAreacancel"></p>
                </aside>
            </section>
        </th:block>
        <div class="row">
                <h3 class="activity-title" th:text="${root.title}"></h3>
                <th:block th:if="${root.coverImage} ne ''">
                    <div class="image-cover">
                        <img class="img-responsive" th:src="${root.coverImage}" />
                    </div>
                </th:block>
                <div class="ue-content-wrapper">
                    <p th:utext="${root.activityContent}"></p>
                </div>
        </div>
    </div>
</body>
</html>