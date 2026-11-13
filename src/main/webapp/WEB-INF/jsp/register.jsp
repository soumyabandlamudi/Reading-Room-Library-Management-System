<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Create account"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="auth-split">
  <section class="auth-hero">
    <h2>Join the<br>Reading Room.</h2>
    <p>Create a free member account to browse the catalogue and follow your loans.</p>
    <ul>
      <li>Takes less than a minute</li>
      <li>Borrow up to 5 books at a time</li>
      <li>Never miss a due date</li>
    </ul>
  </section>
  <section class="auth-card">
    <h1>Create your account</h1>
    <p class="muted">Members can browse books and see their due dates and fines.</p>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <form method="post" action="${ctx}/register" class="stack">
      <label>Full name
        <input name="name" required maxlength="100" autocomplete="name" value="<c:out value='${param.name}'/>">
      </label>
      <label>Email
        <input type="email" name="email" required maxlength="120" autocomplete="username" value="<c:out value='${param.email}'/>">
      </label>
      <label>Password
        <input type="password" name="password" required minlength="8" maxlength="128" autocomplete="new-password">
        <small>At least 8 characters, with a letter and a number.</small>
      </label>
      <button class="btn block">Create account</button>
    </form>
    <p class="muted">Already registered? <a href="${ctx}/login">Log in</a></p>
  </section>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
