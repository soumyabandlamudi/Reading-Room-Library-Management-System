<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Loans"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>Loans</h1>
<p class="muted">Loan period: ${applicationScope.loanDays} days. Late fee: <strong>&#8377;${applicationScope.finePerDay} per day</strong>, collected at the desk. Members borrow and return their own books from the catalogue and My books pages.</p>


<div class="tabs">
  <a class="tab ${status == 'active' ? 'on' : ''}"   href="${ctx}/admin/loans?status=active">Active</a>
  <a class="tab ${status == 'overdue' ? 'on' : ''}"  href="${ctx}/admin/loans?status=overdue">Overdue</a>
  <a class="tab ${status == 'returned' ? 'on' : ''}" href="${ctx}/admin/loans?status=returned">Returned</a>
  <a class="tab ${status == 'all' ? 'on' : ''}"      href="${ctx}/admin/loans?status=all">All</a>
</div>

<div class="table-wrap"><table>
  <thead><tr><th>Member</th><th>Book</th><th>Issued</th><th>Due</th><th>Returned</th><th>Fine</th></tr></thead>
  <tbody>
  <c:if test="${empty loans}"><tr><td colspan="6" class="muted">No loans in this view.</td></tr></c:if>
  <c:forEach var="i" items="${loans}">
    <tr>
      <td><c:out value="${i.userName}"/></td>
      <td><c:out value="${i.bookTitle}"/></td>
      <td><fmt:formatDate value="${i.issueDate}" pattern="dd MMM yyyy"/></td>
      <td><fmt:formatDate value="${i.dueDate}" pattern="dd MMM yyyy"/><c:if test="${i.overdue}"> <span class="pill red">Overdue</span></c:if></td>
      <td><c:choose><c:when test="${i.returned}"><fmt:formatDate value="${i.returnDate}" pattern="dd MMM yyyy"/> <span class="pill green">Returned</span></c:when><c:otherwise><span class="pill amber">On loan</span></c:otherwise></c:choose></td>
      <td><c:choose><c:when test="${i.currentFine > 0}">&#8377;${i.currentFine}</c:when><c:otherwise>&ndash;</c:otherwise></c:choose></td>
    </tr>
  </c:forEach>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>