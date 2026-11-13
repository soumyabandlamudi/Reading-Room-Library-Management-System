<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="My books"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>My books</h1>
<p class="muted">Books you have borrowed, with due dates and fines. Late fee: <strong>&#8377;${applicationScope.finePerDay} per day</strong> after the due date.</p>

<c:if test="${totalFines > 0}">
  <div class="alert warn">Total fines: <strong>&#8377;${totalFines}</strong>. Please pay at the library desk when you return the book.</div>
</c:if>

<h2>On loan</h2>
<c:set var="hasCurrent" value="false"/>
<c:forEach var="i" items="${issues}"><c:if test="${!i.returned}"><c:set var="hasCurrent" value="true"/></c:if></c:forEach>
<c:choose>
  <c:when test="${!hasCurrent}">
    <p class="muted">You have no books on loan. Open the <a href="${ctx}/books">catalogue</a> and press Borrow on any available book.</p>
  </c:when>
  <c:otherwise>
    <div class="table-wrap"><table>
      <thead><tr><th>Book</th><th>Issued</th><th>Due</th><th>Fine so far</th><th></th></tr></thead>
      <tbody>
      <c:forEach var="i" items="${issues}">
        <c:if test="${!i.returned}">
          <tr>
            <td><strong><c:out value="${i.bookTitle}"/></strong><br><span class="muted"><c:out value="${i.bookAuthor}"/></span></td>
            <td><fmt:formatDate value="${i.issueDate}" pattern="dd MMM yyyy"/></td>
            <td><fmt:formatDate value="${i.dueDate}" pattern="dd MMM yyyy"/> <c:choose><c:when test="${i.overdue}"><span class="pill red">Overdue</span></c:when><c:otherwise><span class="pill gray">On time</span></c:otherwise></c:choose></td>
            <td><c:choose><c:when test="${i.currentFine > 0}">&#8377;${i.currentFine}</c:when><c:otherwise>&ndash;</c:otherwise></c:choose></td>
            <td class="actions">
              <form method="post" action="${ctx}/my-books" data-confirm="Return this book?">
                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                <input type="hidden" name="id" value="${i.id}">
                <button class="btn sm">Return</button>
              </form>
            </td>
          </tr>
        </c:if>
      </c:forEach>
      </tbody>
    </table></div>
  </c:otherwise>
</c:choose>

<h2>History</h2>
<c:set var="hasPast" value="false"/>
<c:forEach var="i" items="${issues}"><c:if test="${i.returned}"><c:set var="hasPast" value="true"/></c:if></c:forEach>
<c:choose>
  <c:when test="${!hasPast}"><p class="muted">Returned books will appear here.</p></c:when>
  <c:otherwise>
    <div class="table-wrap"><table>
      <thead><tr><th>Book</th><th>Issued</th><th>Returned</th><th>Fine</th></tr></thead>
      <tbody>
      <c:forEach var="i" items="${issues}">
        <c:if test="${i.returned}">
          <tr>
            <td><c:out value="${i.bookTitle}"/></td>
            <td><fmt:formatDate value="${i.issueDate}" pattern="dd MMM yyyy"/></td>
            <td><fmt:formatDate value="${i.returnDate}" pattern="dd MMM yyyy"/></td>
            <td><c:choose><c:when test="${i.fine > 0}">&#8377;${i.fine}</c:when><c:otherwise>&ndash;</c:otherwise></c:choose></td>
          </tr>
        </c:if>
      </c:forEach>
      </tbody>
    </table></div>
  </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>