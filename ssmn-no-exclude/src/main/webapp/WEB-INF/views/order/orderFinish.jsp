<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Order Confirm</title>
<link rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/app/css/style.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
  <div id="wrapper">
    <h1>Order Finish</h1>
    <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
    <p>
      <form:form action="${pageContext.request.contextPath}/logout">
        <button type="submit" id="logout">Logout</button>
      </form:form>
    </p>
  </div>
  <div>
    <h2>注文完了しました。</h2>
  </div>
  <div>
    <a id="viewHome" href="${pageContext.request.contextPath}/">Home画面へ戻る</a>
  </div>
</body>
</html>
