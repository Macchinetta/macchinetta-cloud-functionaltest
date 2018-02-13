<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
<script src="${pageContext.request.contextPath}/resources/vendor/jquery/jquery-3.2.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/app/js/img-load-check.js"></script>
</head>
<sec:authentication property="principal.account" var="account" />
<body>
    <div id="wrapper">
        <h1>Hello world!</h1>
        <p>The time on the server is ${serverTime}.</p>
        <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
        <p>
            <form:form action="${pageContext.request.contextPath}/logout">
                <button type="submit">Logout</button>
            </form:form>
        </p>
        <div>
            <!-- ${contentUrl} は include.jsp の spring:eval から取得している。 -->
        	<img id="imgFromCloudFront" alt="" src="${contentUrl}/resources/image/ochiboHiroi.jpg"/>
            <p id="imgLoadState">Image loading isn't complete</p>
        </div>
    </div>
</body>
</html>
