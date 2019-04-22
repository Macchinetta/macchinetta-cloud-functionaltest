<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Home</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
<div id="wrapper">
    <h1>Hello world!</h1>
    <p>The time on the server is ${serverTime}.</p>
    <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
    <p>
    <div id="counter">counter:${counter}</div>
    </p>
    <p>
        <form:form id="command"
                   action="${pageContext.request.contextPath}/confirmToken">
            <button type="submit">transaction token check</button>
        </form:form>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/logging" id="logging">logging</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/showCustomView" id="showCustomView">showCustomView</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/customError" id="customError">customError</a>
    </p>
    <p>
        <form:form id="logout" action="${pageContext.request.contextPath}/logout">
            <button type="submit">Logout</button>
        </form:form>
    </p>
</div>
</body>
</html>
