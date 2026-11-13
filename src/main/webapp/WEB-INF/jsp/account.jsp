<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My account"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>My account</h1>
<p class="muted"><c:out value="${sessionScope.user.name}"/> · <c:out value="${sessionScope.user.email}"/></p>

<form method="post" action="${ctx}/account" class="panel narrow stack">
  <input type="hidden" name="csrf" value="${sessionScope.csrf}">
  <h2>Change password</h2>
  <label>Current password
    <input type="password" name="current" required maxlength="128" autocomplete="current-password">
  </label>
  <label>New password
    <input type="password" name="newPassword" required minlength="8" maxlength="128" autocomplete="new-password">
    <small>At least 8 characters, with a letter and a number.</small>
  </label>
  <label>Confirm new password
    <input type="password" name="confirm" required minlength="8" maxlength="128" autocomplete="new-password">
  </label>
  <div class="actions"><button class="btn">Update password</button></div>
</form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
