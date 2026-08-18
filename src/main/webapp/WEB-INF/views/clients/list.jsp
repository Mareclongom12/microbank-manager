<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Clients - MicroBank Manager</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Gestion des clients</h2>
    <a href="${pageContext.request.contextPath}/clients/create" class="btn btn-primary">
      + Nouveau client
    </a>
  </div>

  <c:if test="${not empty param.success}">
    <div class="alert alert-success">${param.success}</div>
  </c:if>

  <form action="${pageContext.request.contextPath}/clients" method="get" class="row g-2 mb-4">
    <div class="col-auto flex-grow-1">
      <input type="text" name="search" class="form-control"
             placeholder="Rechercher par nom, prénom, téléphone ou numéro de pièce"
             value="${search}">
    </div>
    <div class="col-auto">
      <button type="submit" class="btn btn-outline-secondary">Rechercher</button>
    </div>
    <c:if test="${not empty search}">
      <div class="col-auto">
        <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-danger">Réinitialiser</a>
      </div>
    </c:if>
  </form>

  <table class="table table-striped table-hover align-middle">
    <thead class="table-dark">
    <tr>
      <th>Nom</th>
      <th>Prénom</th>
      <th>Téléphone</th>
      <th>Email</th>
      <th>Statut</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="client" items="${clients}">
      <tr>
        <td>${client.nom}</td>
        <td>${client.prenom}</td>
        <td>${client.telephone}</td>
        <td>${client.email}</td>
        <td>
                    <span class="badge ${client.statut == 'ACTIF' ? 'bg-success' : 'bg-secondary'}">
                        ${client.statut}
                    </span>
        </td>
        <td>
          <a href="${pageContext.request.contextPath}/clients/details?id=${client.id}"
             class="btn btn-sm btn-outline-primary">Voir</a>
          <a href="${pageContext.request.contextPath}/clients/update?id=${client.id}"
             class="btn btn-sm btn-outline-secondary">Modifier</a>
          <a href="${pageContext.request.contextPath}/clients/delete?id=${client.id}"
             class="btn btn-sm btn-outline-danger"
             onclick="return confirm('Supprimer ce client ?');">Supprimer</a>
        </td>
      </tr>
    </c:forEach>

    <c:if test="${empty clients}">
      <tr>
        <td colspan="6" class="text-center text-muted">Aucun client trouvé</td>
      </tr>
    </c:if>
    </tbody>
  </table>

  <c:if test="${totalPages > 1}">
    <nav>
      <ul class="pagination">
        <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
          <a class="page-link"
             href="${pageContext.request.contextPath}/clients?page=${currentPage - 1}&search=${search}">
            &laquo; Précédent
          </a>
        </li>

        <c:forEach begin="0" end="${totalPages - 1}" var="i">
          <li class="page-item ${i == currentPage ? 'active' : ''}">
            <a class="page-link"
               href="${pageContext.request.contextPath}/clients?page=${i}&search=${search}">
                ${i + 1}
            </a>
          </li>
        </c:forEach>

        <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
          <a class="page-link"
             href="${pageContext.request.contextPath}/clients?page=${currentPage + 1}&search=${search}">
            Suivant &raquo;
          </a>
        </li>
      </ul>
    </nav>
  </c:if>

</div>

</body>
</html>