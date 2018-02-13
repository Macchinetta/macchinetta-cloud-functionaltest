<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Maintenance</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<body>
    <div id="wrapper">
        <h1>S3 Upload</h1>
        <p>
            <form:form action="${pageContext.request.contextPath}/upload" method="post"
                    modelAttribute="maintenanceForm" enctype="multipart/form-data">
                <table>
                    <tr>
                    	<th width="35%">UploadUser</th>
                    	<td width="65%">
                    	    <form:input path="uUploadUser" />
                    	    <form:errors path="uUploadUser" />
                    	</td>
                    </tr>
                    <tr>
                    	<th width="35%">BucketName</th>
                    	<td width="65%">
                    		<form:select path="uBucketName">
                    			<form:options items="${bucketNameList}" />
                    		</form:select>
                    	</td>
                    </tr>
                    <tr>
                    	<th width="35%">File to upload</th>
                    	<td width="65%">
                    	    <form:input type="file" path="uFile" />
                    	    <form:errors path="uFile" />
                    	</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td><form:button>Upload</form:button></td>
                    </tr>
                </table>
            </form:form>
        </p>
        <br>
        <h1>S3 Delete</h1>
        <p>
            <form:form action="${pageContext.request.contextPath}/delete" method="post"
                    modelAttribute="maintenanceForm">
                <table>
                    <tr>
                    	<th width="35%">BucketName and ObjectKey</th>
                    	<td width="65%">
                    		BucketName
                    		<form:select path="dBucketName">
                    			<form:options items="${bucketNameList}" />
                    		</form:select>
                    	    &nbsp;&nbsp;
                    	    ObjectKey
                    	    <form:input path="dObjectKey" />
                    	    <form:errors path="dObjectKey" />
                    	</td>
                    </tr>
                    <tr>
                    	<th width="35%">UploadUser</th>
                    	<td width="65%" colspan="3">
                    	    <form:input path="dUploadUser" />
                    	    <form:errors path="dUploadUser" />
                    	</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td colspan="3"><form:button>Delete</form:button></td>
                    </tr>
                </table>
            </form:form>
        </p>
        <br>
        <p>
            <form:form action="${pageContext.request.contextPath}/" method="post"
                    modelAttribute="maintenanceForm">
                <form:button>back</form:button>
            </form:form>
        </p>
    </div>
</body>
</html>
