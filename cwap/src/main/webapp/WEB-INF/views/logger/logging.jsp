<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Logging Confirm</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
<div id="wrapper">
    <h1>Logging confirm</h1>
    <form:form id="command"
               action="${pageContext.request.contextPath}/logger/logged"
               method="post">
        <input type="text" name="uuid">
        <button id="send" type="submit">send</button>
    </form:form>
</div>
<p>
    <form:form action="${pageContext.request.contextPath}/logout">
        <button id="logout" type="submit">Logout</button>
    </form:form>
</p>
</body>
</html>
