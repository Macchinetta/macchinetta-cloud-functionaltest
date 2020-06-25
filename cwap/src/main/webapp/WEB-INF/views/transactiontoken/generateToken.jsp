<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>confirm transaction token check.</title>
</head>
<body>
<div id="result">Token check is valid.</div>
<p>
    <form:form
               action="${pageContext.request.contextPath}/transactiontoken/confirmToken">
        <button id="transactionTokenCheck" type="submit">transaction token check</button>
    </form:form>
</p>
</body>
</html>
