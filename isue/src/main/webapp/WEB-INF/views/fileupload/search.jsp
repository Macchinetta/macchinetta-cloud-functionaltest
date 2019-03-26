<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Search</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
    <div id="wrapper">
        <p>
            <form:form action="${pageContext.request.contextPath}/logout">
                <button id="logout" type="submit">Logout</button>
            </form:form>
        </p>
        <p>
            <form:form action="${pageContext.request.contextPath}/maintenance" method="post"
                    modelAttribute="searchForm">
                <form:button>Maintenance</form:button>
            </form:form>
        </p>
        <h1>DynamoDB Search</h1>
        <p>
            <form:form action="${pageContext.request.contextPath}/search" method="post"
                    modelAttribute="searchForm">
                <table>
                    <tr>
                    	<th width="35%">ObjectKey</th>
                    	<td width="65%" colspan="3">
                    	    <form:input path="objectKey" />
                    	    <form:errors path="objectKey" />
                    	</td>
                    </tr>
                    <tr>
                    	<th width="35%">UploadUser (and UploadDate)</th>
                    	<td width="65%">
                    		UploadUser
                    	    <form:input path="uploadUser" />
                    	    <form:errors path="uploadUser" />
                    	    &nbsp;&nbsp;
                    	    (UploadDate)
                    	    <form:input path="uploadDate" />
                    	    <form:errors path="uploadDate" />
                    	</td>
                    </tr>
                    <tr>
                    	<th width="35%">BucketName</th>
                    	<td width="65%" colspan="3">
                    		<form:select path="bucketName">
                    			<form:options items="${bucketNameList}" />
                    		</form:select>
                    	</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td colspan="2"><form:button>Search</form:button></td>
                    </tr>
                </table>
            </form:form>
        </p>
        
        <table id="result">
        	<tr>
        		<th>objectKey</th>
        		<th>bucketName</th>
        		<th>fileName</th>
        		<th>size</th>
        		<th>uploadUser</th>
        		<th>uploadDate</th>
        		<th>sequencer</th>
        		<th>version</th>
        	</tr>
        <c:forEach items="${resultList}" var="result">
        	<tr>
        		<td>${f:h(result.objectKey)}</td>
        		<td>${f:h(result.bucketName)}</td>
        		<td>${f:h(result.fileName)}</td>
        		<td>${f:h(result.size)}</td>
        		<td>${f:h(result.uploadUser)}</td>
        		<td>${f:h(result.uploadDate)}</td>
        		<td>${f:h(result.sequencer)}</td>
        		<td>${f:h(result.version)}</td>
        	</tr>
        </c:forEach>
        </table>
    </div>
</body>
</html>
