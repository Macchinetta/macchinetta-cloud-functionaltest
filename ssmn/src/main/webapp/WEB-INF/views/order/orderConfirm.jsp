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
    <h1>Order Confirm</h1>
    <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
    <p>
      <form:form action="${pageContext.request.contextPath}/logout">
        <button type="submit" id="logout">Logout</button>
      </form:form>
    </p>
  </div>
  <div>
    <div>
      <spring:eval var="cart" expression="@cart" />
      合計金額:
      <fmt:formatNumber value="${f:h(cart.totalAmount)}" type="CURRENCY"
        currencySymbol="&yen;" maxFractionDigits="0" />
    </div>
    <table id="confirm">
      <tr>
        <th>Name</th>
        <th>Price</th>
        <th>Quantity</th>
      </tr>
      <c:forEach var="cartItem" items="${cart.cartItems}"
        varStatus="status">
        <tr>
          <td id="itemName${status.index}">${f:h(cartItem.product.name)}</td>
          <td id="itemPrice${status.index}"><fmt:formatNumber
                value="${cartItem.product.price}" type="CURRENCY"
                currencySymbol="&yen;" maxFractionDigits="0" /></td>
          <td id="itemQuantity${status.index}">${f:h(cartItem.quantity)}</td>
        </tr>
      </c:forEach>
    </table>
  </div>
  <c:if test="${!empty cart.cartItems}">
    <div>
      <form:form method="post"
        action="${pageContext.request.contextPath}/order/finish">
        <button type="submit" id="viewOrderFinish">注文する</button>
      </form:form>
    </div>
  </c:if>
  <div>
    <form:form method="post"
      action="${pageContext.request.contextPath}/order/form">
      <button type="submit" id="viewOrderForm">注文画面に戻る</button>
    </form:form>
  </div>
  <div>
    <form:form method="post"
      action="${pageContext.request.contextPath}/cart">
      <button type="submit" id="viewCart">カートの内容表示</button>
    </form:form>
  </div>
  <div>
    <a id="viewHome"
      href="${pageContext.request.contextPath}/order/form">注文画面へ進む</a>
  </div>
</body>
</html>
