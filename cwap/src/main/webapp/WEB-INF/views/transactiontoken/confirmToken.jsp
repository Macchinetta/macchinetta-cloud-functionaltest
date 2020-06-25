<%--
  Created by IntelliJ IDEA.
  User: terafwXXXX
  Date: 2017/10/20
  Time: 19:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>confirm transaction token check.</title>
</head>
<body>
<div id="result">Token check is valid.</div>
<p>
    <form:form action="${pageContext.request.contextPath}/logout">
        <button id="logout" type="submit">Logout</button>
    </form:form>
</p>
</body>
</html>
