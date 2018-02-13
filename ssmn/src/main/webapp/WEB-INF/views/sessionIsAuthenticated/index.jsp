<h2>SessionEnforcerFilter Functional Test(Is Authenticated)</h2>
<table>
  <!-- SessionEnforcerFilter get test scenario -->
  <tr>
    <td>
      <form:form action="${pageContext.request.contextPath}/session/isAuthenticated" method="get">
          <button type="submit" id="sessionGet" name="getTest">GET Method</button>
      </form:form>
    </td>
  </tr>
  <!-- SessionEnforcerFilter post test scenario -->
  <tr>
    <td>
      <form:form action="${pageContext.request.contextPath}/session/isAuthenticated" method="post">
          <button type="submit" id="sessionPost" name="postTest">POST Method</button>
      </form:form>
  </tr>
</table>