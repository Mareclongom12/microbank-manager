<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Utilisateurs - MicroBank Manager</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Gestion des utilisateurs</h2>
    <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary">
      + Nouvel utilisateur
    </a>
  </div>

  <table class="table table-striped table-hover align-middle">
    <thead class="table-dark">
    <tr>
      <th>Nom</th>
      <th>Prénom</th>
      <th>Login</th>
      <th>Rôle</th>
      <th>Statut</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="u" items="${users}">
      <tr>
        <td>${u.nom}</td>
        <td>${u.prenom}</td>
        <td>${u.login}</td>
        <td>
          <span class="badge ${u.role == 'ADMIN' ? 'bg-dark' : 'bg-secondary'}">${u.role}</span>
        </td>
        <td>
          <span class="badge ${u.statut == 'ACTIF' ? 'bg-success' : 'bg-danger'}">${u.statut}</span>
        </td>
        <td>
          <a href="${pageContext.request.contextPath}/users/toggle?id=${u.id}"
             class="btn btn-sm ${u.statut == 'ACTIF' ? 'btn-outline-danger' : 'btn-outline-success'}"
             onclick="return confirm('Changer le statut de cet utilisateur ?');">
              ${u.statut == 'ACTIF' ? 'Désactiver' : 'Activer'}
          </a>
        </td>
      </tr>
    </c:forEach>

    <c:if test="${empty users}">
      <tr>
        <td colspan="6" class="text-center text-muted">Aucun utilisateur trouvé</td>
      </tr>
    </c:if>
    </tbody>
  </table>

</div>

</body>
</html>