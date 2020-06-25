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
        <form:form
                   action="${pageContext.request.contextPath}/transactiontoken/confirmToken">
            <button id="transactionTokenCheck" type="submit">transaction token check</button>
        </form:form>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/transactiontoken/generateToken" 
                   id="transactionTokenGenerate">transaction token generate</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/logger/logging" id="logging">logging</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/showCustomView" id="showCustomView">showCustomView</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/customError" id="customError">customError</a>
    </p>
    <p>
        <form:form action="${pageContext.request.contextPath}/logout">
            <button id="logout" type="submit">Logout</button>
        </form:form>
    </p>
</div>
</body>
</html>
