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
    <h1>Hello world!</h1>
    <p>The time on the server is ${serverTime}.</p>
    <p>Welcome !!</p>
    <p>
    </p>
  </div>
  <c:if test="${!empty reservation}">
    <table>
      <tr>
        <th>予約番号</th>
        <th>予約日時</th>
        <th>合計金額</th>
      </tr>
      <tr>
        <td id="reserveNo">${f:h(reservation.reserveNo)}</td>
        <td id="reserveDate"><joda:format value="${reservation.reserveDate}" pattern="yyyy/MM/dd"/></td>
        <td id="totalFare">${reservation.totalFare}</td>
      </tr>
    </table>
  </c:if>
  <c:if test="${!empty reservation.repMember}">
    <table>
      <tr>
        <th>代表予約者会員番号</th>
        <th>代表予約者名</th>
        <th>代表予約者名(ふりがな)</th>
      </tr>
      <tr>
        <td id="repCustomerNo">${f:h(reservation.repMember.customerNo)}</td>
        <td id="repName">${f:h(reservation.repMember.name)}</td>
        <td id="repFuriName">${f:h(reservation.repMember.furiName)}</td>
      </tr>
    </table>
  </c:if>
  <c:if test="${!empty member}">
    <table>
      <tr>
        <th>会員番号</th>
        <th>会員氏名</th>
        <th>会員氏名(ふりがな)</th>
      </tr>
      <tr>
        <td id="customerNo">${f:h(member.customerNo)}</td>
        <td id="name">${f:h(member.name)}</td>
        <td id="furiName">${f:h(member.furiName)}</td>
      </tr>
    </table>
  </c:if>
</body>
</html>
