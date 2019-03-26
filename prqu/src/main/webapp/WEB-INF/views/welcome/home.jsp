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
        <h1>ログインユーザのサービスレベルに応じたキュー処理</h1>
        <p>The time on the server is ${serverTime}.</p>
        <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
        <label>message process time: <input id="processtime" type="text" readonly="readonly" value="${processTime}" style="text-align: right;"></label>
        <p>
            <form:form action="${pageContext.request.contextPath}/logout">
                <button id="logout" type="submit">Logout</button>
            </form:form>
        </p>
    </div>
</body>
</html>
