<h2>Session Management Functional Test(Is Authenticated)</h2>
<table>
  <!-- Session Management get test scenario -->
  <tr>
    <td>
      <form:form action="${pageContext.request.contextPath}/session/isAuthenticated" method="get">
          <button type="submit" id="sessionGet" name="getTest">GET Method</button>
      </form:form>
    </td>
  </tr>
  <!-- Session Management post test scenario -->
  <tr>
    <td>
      <form:form action="${pageContext.request.contextPath}/session/isAuthenticated" method="post">
          <button type="submit" id="sessionPost" name="postTest">POST Method</button>
      </form:form>
  </tr>
</table>