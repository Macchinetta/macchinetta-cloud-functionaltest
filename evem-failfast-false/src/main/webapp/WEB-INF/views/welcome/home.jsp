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
        <table id="s3ConfigConfigurationPropertiesTable" border="1">
            <tr>
                <th>S3 bucket name</th>
                <th>temp directory name</th>
                <th>save directory name</th>
            </tr>
            <tr>
                <td>${f:h(s3ConfigConfigurationPropertiesDto.bucketname)}</td>
                <td>${f:h(s3ConfigConfigurationPropertiesDto.temproryDirectory)}</td>
                <td>${f:h(s3ConfigConfigurationPropertiesDto.saveDirectory)}</td>
            </tr>
        </table>
        <table id="s3ConfigValueTable" border="1">
            <tr>
                <th>S3 bucket name</th>
                <th>temp directory name</th>
                <th>save directory name</th>
            </tr>
            <tr>
                <td>${f:h(s3ConfigConfigValueDto.bucketname)}</td>
                <td>${f:h(s3ConfigConfigValueDto.temproryDirectory)}</td>
                <td>${f:h(s3ConfigConfigValueDto.saveDirectory)}</td>
            </tr>
        </table>
    </div>
</body>
</html>
