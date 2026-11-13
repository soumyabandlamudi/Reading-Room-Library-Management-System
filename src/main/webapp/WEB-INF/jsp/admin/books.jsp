<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Manage books"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>Manage books</h1>

<form method="post" action="${ctx}/admin/books" class="panel form-grid">
  <input type="hidden" name="csrf" value="${sessionScope.csrf}">
  <input type="hidden" name="id" value="${empty editBook ? 0 : editBook.id}">
  <h2 class="span"><c:out value="${empty editBook ? 'Add a book' : 'Edit book'}"/></h2>
  <label>Title<input name="title" required maxlength="200" value="<c:out value='${editBook.title}'/>"></label>
  <label>Author<input name="author" required maxlength="150" value="<c:out value='${editBook.author}'/>"></label>
  <label>Category<input name="category" maxlength="80" placeholder="e.g. Fiction" value="<c:out value='${editBook.category}'/>"></label>
  <label>ISBN<input name="isbn" maxlength="20" value="<c:out value='${editBook.isbn}'/>"></label>
  <label>Total copies<input type="number" name="totalCopies" min="0" max="9999" required value="${empty editBook ? 1 : editBook.totalCopies}"></label>
  <div class="actions span">
    <button class="btn">${empty editBook ? 'Add book' : 'Save changes'}</button>
    <c:if test="${not empty editBook}"><a class="btn ghost" href="${ctx}/admin/books">Cancel</a></c:if>
  </div>
</form>

<form class="toolbar" method="get" action="${ctx}/admin/books">
  <input class="grow" type="search" name="q" placeholder="Search books" value="<c:out value='${q}'/>">
  <button class="btn ghost">Search</button>
</form>

<div class="table-wrap"><table>
  <thead><tr><th>Title</th><th>Author</th><th>Category</th><th>Available</th><th></th></tr></thead>
  <tbody>
  <c:if test="${empty books}"><tr><td colspan="5" class="muted">No books yet. Add your first book above.</td></tr></c:if>
  <c:forEach var="b" items="${books}">
    <tr>
      <td><strong><c:out value="${b.title}"/></strong></td>
      <td><c:out value="${b.author}"/></td>
      <td><c:out value="${b.category}"/></td>
      <td><span class="pill ${b.availableCopies > 0 ? 'green' : 'red'}">${b.availableCopies} / ${b.totalCopies}</span></td>
      <td class="actions">
        <a class="btn ghost sm" href="${ctx}/admin/books?edit=${b.id}">Edit</a>
        <form method="post" action="${ctx}/admin/books" data-confirm="Delete this book?">
          <input type="hidden" name="csrf" value="${sessionScope.csrf}">
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="id" value="${b.id}">
          <button class="btn danger sm">Delete</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
