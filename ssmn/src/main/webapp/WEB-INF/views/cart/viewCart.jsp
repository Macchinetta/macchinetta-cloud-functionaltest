<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>View Cart</title>
<link rel="stylesheet"
  href="${pageContext.request.contextPath}/resources/app/css/styles.css">
</head>
<sec:authentication property="principal.account" var="account" />
<body>
  <div id="wrapper">
    <h1>View Cart</h1>
    <p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)} !!</p>
    <p>
      <form:form action="${pageContext.request.contextPath}/logout">
        <button type="submit" id="logout">Logout</button>
      </form:form>
    </p>
  </div>
  <br>
  <br>
  <div>
    <spring:eval var="cart" expression="@cart" />
    <div>
     合計金額:<fmt:formatNumber value="${f:h(cart.totalAmount)}" type="CURRENCY"
        currencySymbol="&yen;" maxFractionDigits="0" />
    </div>
    <table id="cart">
      <tr>
        <th>Name</th>
        <th>Price</th>
        <th>Quantity</th>
      </tr>
      <c:forEach items="${cart.cartItems}" var="cartItem"
        varStatus="status">
        <tr>
          <td id="itemName${status.index}">${f:h(cartItem.product.name)}</td>
          <td id="itemPrice${status.index}"><fmt:formatNumber
                value="${cartItem.product.price}" type="CURRENCY"
                currencySymbol="&yen;" maxFractionDigits="0" /></td>
          <td>
            <form:form modelAttribute="cartForm" action="${pageContext.request.contextPath}/cart/change">
              <form:input path="quantity" id="itemQuantity${status.index}" value="${f:h(cartItem.quantity)}" /><form:errors path="quantity" />
              <form:input type="hidden" path="id" value="${f:h(cartItem.product.id)}" /><form:errors path="id" />
              <button type="submit" id="changeQuantity${status.index}">個数変更</button>
            </form:form>
            <form:form modelAttribute="cartForm" action="${pageContext.request.contextPath}/cart/delete">
              <form:input type="hidden" path="id" value="${f:h(cartItem.product.id)}" /><form:errors path="id" />
              <button type="submit" id="deleteItem${status.index}">カートから削除</button>
            </form:form>
          </td>
        </tr>
      </c:forEach>
    </table>
  </div>
  <div>SessionID</div>
  <div id="sessionId">${sessionId}</div>
  <div>
    <form:form method="post" action="${pageContext.request.contextPath}/order/confirm">
        <button type="submit" id="viewOrderConfirm">注文内容確認</button>
    </form:form>
  </div>
  <div>
    <form:form method="post" action="${pageContext.request.contextPath}/order/form">
        <button type="submit" id="viewOrderForm">注文画面に戻る</button>
    </form:form>
  </div>
  <div>
    <a id="viewHome" href="${pageContext.request.contextPath}/">Home画面へ戻る</a>
  </div>
</body>
</html>