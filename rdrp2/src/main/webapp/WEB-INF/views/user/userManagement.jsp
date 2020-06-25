<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>User</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>

<body>
    <div id="wrapper">
        <h1>User Management</h1>
        <p><a href="/rdrp2/list" >reload</a></p>
        <div id="userRegister">
        <h2>Register Form</h2>
        <form:form action="${pageContext.request.contextPath}/register"  modelAttribute="userForm">
            <form:label path="inputLastName">姓</form:label>
            <form:input path="inputLastName" />
            <form:label path="inputFirstName">名</form:label>
            <form:input path="inputFirstName" />
            <form:button id="button_register" type="submit">register</form:button>
            </form:form>
        </div>
        
        
        <div id="userList">
            <h2>Registered User</h2>
            <table id="userListTable">
                <thead>
                    <tr>
                        <th class="userId">ID</th>
                        <th class="lastName">姓</th>
                        <th class="firstName">名</th>
                    </tr>
                </thead>
                <c:forEach items="${userInfoList}" var="userInfo">
                    <tr>
                        <th class="userId">${f:h(userInfo.userId)}</th>
                        <th class="lastName">${f:h(userInfo.lastName)}</th>
                        <th class="firstName">${f:h(userInfo.firstName)}</th>
                    </tr>
                </c:forEach>
            </table>
       </div>
    </div>
</body>
</html>
