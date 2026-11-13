<%@ page isErrorPage="true" %>
<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Something went wrong"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="auth">
  <h1>Something went wrong</h1>
  <c:choose>
    <c:when test="${pageContext.errorData.statusCode == 403}"><p class="muted">You don't have access to this page.</p></c:when>
    <c:when test="${pageContext.errorData.statusCode == 404}"><p class="muted">We couldn't find that page.</p></c:when>
    <c:otherwise><p class="muted">The request failed. Check that Oracle is running, that the Oracle JDBC jar is in <code>WEB-INF/lib</code>, and that <code>db.properties</code> is correct.</p></c:otherwise>
  </c:choose>
  <c:if test="${applicationScope.showErrors and not empty pageContext.errorData.throwable}">
    <p class="muted"><strong>Technical details</strong> (hidden when <code>show.errors=false</code>):</p>
    <pre class="trace"><c:out value="${pageContext.errorData.throwable}"/><c:if test="${not empty pageContext.errorData.throwable.cause}">

Caused by: <c:out value="${pageContext.errorData.throwable.cause}"/></c:if></pre>
  </c:if>
  <a class="btn" href="${ctx}/home">Back to home</a>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
