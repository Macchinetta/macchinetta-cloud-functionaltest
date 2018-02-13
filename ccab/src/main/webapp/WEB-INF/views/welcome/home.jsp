<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<body>
    <div id="wrapper">
        <h1>Hello world!</h1>
        <p>The time on the server is ${serverTime}.</p>
        <p>Member Information</p>
        <table border="1">
            <tr>
                <th>member number</th>
                <th>member name</th>
                <th>furigana name</th>
                <th>random number</th>
            </tr>
            <tr>
                <td>${f:h(member.customerNo)}</td>
                <td>${f:h(member.name)}</td>
                <td>${f:h(member.furiName)}</td>
                <td id="randomNo">${f:h(member.randomNo)}</td>
            </tr>
        </table>
        <p>
            <form:form action="${pageContext.request.contextPath}/logout">
                <button type="submit">Logout</button>
            </form:form>
        </p>
    </div>
</body>
</html>
