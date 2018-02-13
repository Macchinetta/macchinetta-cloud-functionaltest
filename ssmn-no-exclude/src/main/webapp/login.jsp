<!DOCTYPE html>
<html>
<head>
<title>Login Page</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/style.css">
</head>
<body>
    <div id="wrapper">
        <h3 id="title">Login with Username and Password</h3>

        <c:if test="${param.containsKey('error')}">
            <t:messagesPanel messagesType="error"
                messagesAttributeName="SPRING_SECURITY_LAST_EXCEPTION" />
        </c:if>

        <form:form action="${pageContext.request.contextPath}/login">
            <table>
                <tr>
                    <td><label for="username">User:</label></td>
                    <td><input type="text" id="userId"
                        name="username" value='0000000001'></td>
                </tr>
                <tr>
                    <td><label for="password">Password:</label></td>
                    <td><input type="password" id="password"
                        name="password" value="aaaaa11111" /></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><input name="submit" type="submit" value="Login" /></td>
                </tr>
            </table>
        </form:form>
    </div>
</body>
</html>