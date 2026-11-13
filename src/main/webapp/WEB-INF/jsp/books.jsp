<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Catalogue"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<section class="hero">
  <h1>Find your next read</h1>
  <p>Search the whole catalogue by title, author or ISBN.</p>
  <form class="hero-search" method="get" action="${ctx}/books">
    <input type="search" name="q" maxlength="100" placeholder="Try &quot;Clean Code&quot; or &quot;Kalam&quot;" value="<c:out value='${q}'/>">
    <select name="category" data-autosubmit aria-label="Category">
      <option value="">All categories</option>
      <c:forEach var="cat" items="${categories}">
        <option value="<c:out value='${cat}'/>" ${cat == category ? 'selected' : ''}><c:out value="${cat}"/></option>
      </c:forEach>
    </select>
    <button class="btn">Search</button>
  </form>
</section>

<c:choose>
  <c:when test="${empty books}">
    <p class="muted">No books match your search. Try a different title or author.</p>
  </c:when>
  <c:otherwise>
    <p class="muted count">${fn:length(books)} book<c:if test="${fn:length(books) != 1}">s</c:if></p>
    <div class="grid">
      <c:forEach var="b" items="${books}">
        <article class="book">
          <div class="cover c${b.id % 6}">
            <span class="cat"><c:out value="${b.category}"/></span>
            <span class="initial"><c:out value="${fn:toUpperCase(fn:substring(b.title, 0, 1))}"/></span>
          </div>
          <div class="book-body">
            <h3><c:out value="${b.title}"/></h3>
            <p class="muted"><c:out value="${b.author}"/></p>
            <div class="book-foot">
              <c:choose>
                <c:when test="${b.availableCopies > 0}">
                  <span class="pill green">${b.availableCopies} of ${b.totalCopies} available</span>
                </c:when>
                <c:otherwise><span class="pill red">All copies on loan</span></c:otherwise>
              </c:choose>
              <c:if test="${not sessionScope.user.admin}">
                <c:choose>
                  <c:when test="${myBookIds.contains(b.id)}"><span class="pill gray">You have this</span></c:when>
                  <c:when test="${b.availableCopies > 0}">
                    <form method="post" action="${ctx}/books">
                      <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                      <input type="hidden" name="bookId" value="${b.id}">
                      <button class="btn sm">Borrow</button>
                    </form>
                  </c:when>
                </c:choose>
              </c:if>
            </div>
          </div>
        </article>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>