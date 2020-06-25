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
        <table id="rdbInfoTable" border="1">
            <tr>
                <th>RDB url</th>
                <th>RDB username</th>
                <th>RDB password</th>
                <th>RDB Driver Class Name</th>
            </tr>
            <tr>
                <td>${f:h(rdbInfo.url)}</td>
                <td>${f:h(rdbInfo.username)}</td>
                <td>${f:h(rdbInfo.password)}</td>
                <td>${f:h(rdbInfo.driverClassName)}</td>
            </tr>
        </table>
    </div>
</body>
</html>
