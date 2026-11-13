<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Log in"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="auth-split">
  <section class="auth-hero">
    <h2>Your library,<br>always open.</h2>
    <p>Find a book in seconds, see when it is due, and keep track of every loan.</p>
    <ul>
      <li>Search by title, author or ISBN</li>
      <li>Due dates and fines at a glance</li>
      <li>Your full borrowing history</li>
    </ul>
  </section>
  <section class="auth-card">
    <h1>Welcome back</h1>
    <p class="muted">Log in to continue.</p>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <form method="post" action="${ctx}/login" class="stack">
      <label>Email
        <input type="email" name="email" required maxlength="120" autocomplete="username" value="<c:out value='${param.email}'/>">
      </label>
      <label>Password
        <input type="password" name="password" required maxlength="128" autocomplete="current-password">
      </label>
      <button class="btn block">Log in</button>
    </form>
    <p class="muted">New here? <a href="${ctx}/register">Create an account</a></p>
  </section>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
