<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Order Form</title>
<link rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/app/css/style.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
  <div id="wrapper">
    <h1>Order Form</h1>
    <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
    <p>
      <form:form action="${pageContext.request.contextPath}/logout">
        <button type="submit" id="logout">Logout</button>
      </form:form>
    </p>
  </div>
  <div>
    <table>
      <tr>
        <th></th>
        <th>ID</th>
        <th>Name</th>
        <th>Price</th>
      </tr>
      <c:forEach var="product" items="${products}" varStatus="status">
        <tr>
          <form:form method="post"
            action="${pageContext.request.contextPath}/cart/add">
            <input type="hidden" id="id" name="id" value="${f:h(product.id)}"/>
            <td><button type="submit" id="add${status.index}" name="addCart">カートに追加</button></td>
          </form:form>
          <td>${f:h(product.id)}</td>
          <td>${f:h(product.name)}</td>
          <td><fmt:formatNumber
                value="${f:h(product.price)}" type="CURRENCY"
                currencySymbol="&yen;" maxFractionDigits="0" /></td>
        </tr>
      </c:forEach>
    </table>
  </div>
  <div>
    <form:form method="post" action="${pageContext.request.contextPath}/cart">
        <button type="submit" id="viewCart">カートの内容表示</button>
    </form:form>
  </div>
  <div>
    <a id="viewHome" href="${pageContext.request.contextPath}/">Home画面へ戻る</a>
  </div>
</body>
</html>
