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
        <h1>Mail Sending</h1>
        <p>
            <form:form action="${pageContext.request.contextPath}/send">
                <p>to address:<input type="text" name="to" id="to" size="30"></p>
                <p><label><input type="radio" name="kind" value="simple" checked>simple</label> &nbsp;<label><input type="radio" name="kind" value="mime">MIME</label></p>
                <p>body:<br><textarea name="body" id="body" cols="30" rows="10"></textarea></p>
                <button type="submit" id="sendMail">Send</button>
            </form:form>
        </p>
        <p>
            messageId:<span id="messageId">${f:h(result.messageId)}</span>
        </p>
        <p>
            topicArn:<span id="topicArn">${f:h(result.topicArn)}</span>
        </p>
        <p>
            notificationType:<span id="notificationType">${f:h(result.notificationType)}</span>
        </p>
        <p>
            headers:<span id="headers">${f:h(result.headers)}</span>
        </p>
    </div>
</body>
</html>
