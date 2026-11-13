<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Members"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>Members</h1>
<div class="table-wrap"><table>
  <thead><tr><th>Name</th><th>Email</th><th>Joined</th><th></th></tr></thead>
  <tbody>
  <c:if test="${empty members}"><tr><td colspan="4" class="muted">No members yet. They appear here after they register.</td></tr></c:if>
  <c:forEach var="m" items="${members}">
    <tr>
      <td><strong><c:out value="${m.name}"/></strong></td>
      <td><c:out value="${m.email}"/></td>
      <td><fmt:formatDate value="${m.createdAt}" pattern="dd MMM yyyy"/></td>
      <td class="actions">
        <form method="post" action="${ctx}/admin/members" data-confirm="Remove this member?">
          <input type="hidden" name="csrf" value="${sessionScope.csrf}">
          <input type="hidden" name="id" value="${m.id}">
          <button class="btn danger sm">Remove</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
