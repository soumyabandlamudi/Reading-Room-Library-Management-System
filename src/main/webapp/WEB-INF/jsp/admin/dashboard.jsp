<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Dashboard"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>Dashboard</h1>
<p class="muted">Welcome back, <c:out value="${sessionScope.user.name}"/>. Here is how the library looks today.</p>

<div class="stats">
  <div class="stat"><span class="ico">&#128218;</span><span class="num">${stats.titles}</span><strong>Titles</strong><span class="muted">${stats.copies} copies in total</span></div>
  <div class="stat"><span class="ico">&#128101;</span><span class="num">${stats.members}</span><strong>Members</strong><span class="muted">registered</span></div>
  <div class="stat"><span class="ico">&#128214;</span><span class="num">${stats.onLoan}</span><strong>On loan</strong><span class="muted">books currently out</span></div>
  <div class="stat ${stats.overdue > 0 ? 'alert-stat' : ''}"><span class="ico">&#9200;</span><span class="num">${stats.overdue}</span><strong>Overdue</strong><span class="muted">need follow-up</span></div>
</div>

<div class="row-between">
  <h2>Overdue books</h2>
  <a class="btn ghost sm" href="${ctx}/admin/loans">Open loans</a>
</div>
<c:choose>
  <c:when test="${empty overdueList}"><p class="muted">Nothing is overdue today.</p></c:when>
  <c:otherwise>
    <div class="table-wrap"><table>
      <thead><tr><th>Member</th><th>Book</th><th>Due</th><th>Fine so far</th></tr></thead>
      <tbody>
      <c:forEach var="i" items="${overdueList}">
        <tr>
          <td><c:out value="${i.userName}"/></td>
          <td><c:out value="${i.bookTitle}"/></td>
          <td><fmt:formatDate value="${i.dueDate}" pattern="dd MMM yyyy"/> <span class="pill red">Overdue</span></td>
          <td>&#8377;${i.currentFine}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table></div>
  </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>