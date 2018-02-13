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
        <h1>ElastiCache inspection.</h1>
        <p>Exist FQCN Classpath:<span id="existFQCNClasspath">${caapProjectInfo.existFQCNClasspath}</span></p>
        <p>Exist in ApplicationContext:<span id="existInApplicationContext">${caapProjectInfo.existInApplicationContext}</span></p>
    </div>
</body>
</html>
