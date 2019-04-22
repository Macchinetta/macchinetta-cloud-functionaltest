<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Logged</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
<div id="wrapper">
    <h1>Logged</h1>
    <p>Show application log and confirm output UUID.</p>
</div>
<p>
    <form:form id="logout" action="${pageContext.request.contextPath}/logout">
        <button type="submit">Logout</button>
    </form:form>
</p>
</body>
</html>
