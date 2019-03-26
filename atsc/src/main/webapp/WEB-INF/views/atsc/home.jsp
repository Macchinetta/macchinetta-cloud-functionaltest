<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>ATSC</title>
<link rel="stylesheet"
    href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<body>
    <div id="wrapper">
        <h1>Listen alarm notification.</h1>
        <p>
            <form:form action="${pageContext.request.contextPath}/listen">
                <button id="listen" type="submit">listen</button>
            </form:form>
        </p>
        <div>newState:<span id="newState">${notification.message.newStateValue}</span></div>
        <div>newStateReason:<span id="newStateReason">${notification.message.newStateReason}</span></div>
        <div>metricName:<span id="metricName">${notification.message.trigger.metricName}</span></div>
        <div>namespace:<span id="namespace">${notification.message.trigger.namespace}</span></div>
        <div>dimensions:<span id="dimensions">${fn:join(notification.message.trigger.dimensions, ',')}</span></div>
    </div>
</body>
</html>
