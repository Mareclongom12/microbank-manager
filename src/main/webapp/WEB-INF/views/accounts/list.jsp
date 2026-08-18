<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Comptes - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Gestion des comptes</h2>
        <a href="${pageContext.request.contextPath}/accounts/create" class="btn btn-primary">
            + Ouvrir un compte
        </a>
    </div>

    <table class="table table-striped table-hover align-middle">
        <thead class="table-dark">
        <tr>
            <th>Numéro</th>
            <th>Titulaire</th>
            <th>Type</th>
            <th>Solde</th>
            <th>Statut</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="account" items="${accounts}">
            <tr>
                <td>${account.numeroCompte}</td>
                <td>${account.client.prenom} ${account.client.nom}</td>
                <td>${account.type}</td>
                <td>${account.solde} FCFA</td>
                <td>
                    <span class="badge ${account.statut == 'ACTIF' ? 'bg-success' : 'bg-secondary'}">
                            ${account.statut}
                    </span>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/accounts/details?id=${account.id}"
                       class="btn btn-sm btn-outline-primary">Voir</a>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty accounts}">
            <tr>
                <td colspan="6" class="text-center text-muted">Aucun compte ouvert</td>
            </tr>
        </c:if>
        </tbody>
    </table>

</div>

</body>
</html>