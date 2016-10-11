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
</head>

<body class="login-body">
    <form id="form1" action="/login/login" method="post">
        <div class="container">
            <div class="form-signin">
                <h2 class="form-signin-heading">系统登录</h2>
                <div class="login-wrap">
                    <input type="text" id="name" name="name" class="form-control" placeholder="用户名" autofocus required />
                    <input type="password" id="password" name="password" class="form-control" placeholder="登录密码" required />
                    <input type="submit" id="btnSumbit" Value="登 录" class="btn btn-lg btn-login btn-block"/>
                </div>
            </div>
        </div>
    </form>
    <!-- js placed at the end of the document so the pages load faster -->
    <script src="js/jquery.js"></script>
    <script src="js/bootstrap.min.js"></script>

</body>
</html>
