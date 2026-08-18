<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Détails client - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>${client.prenom} ${client.nom}</h2>
        <div>
            <a href="${pageContext.request.contextPath}/clients/update?id=${client.id}"
               class="btn btn-outline-secondary">Modifier</a>
            <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-dark">Retour à la liste</a>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title">Informations personnelles</h5>
            <table class="table table-borderless mb-0">
                <tr>
                    <th style="width: 200px;">Date de naissance</th>
                    <td>${client.dateNaissance}</td>
                </tr>
                <tr>
                    <th>Numéro de pièce</th>
                    <td>${client.numeroPiece}</td>
                </tr>
                <tr>
                    <th>Téléphone</th>
                    <td>${client.telephone}</td>
                </tr>
                <tr>
                    <th>Email</th>
                    <td>${client.email}</td>
                </tr>
                <tr>
                    <th>Adresse</th>
                    <td>${client.adresse}</td>
                </tr>
                <tr>
                    <th>Statut</th>
                    <td>
                        <span class="badge ${client.statut == 'ACTIF' ? 'bg-success' : 'bg-secondary'}">
                            ${client.statut}
                        </span>
                    </td>
                </tr>
                <tr>
                    <th>Client depuis</th>
                    <td>${client.dateCreation}</td>
                </tr>
            </table>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="card-title mb-0">Comptes associés</h5>
                <a href="${pageContext.request.contextPath}/accounts/create?clientId=${client.id}"
                   class="btn btn-sm btn-primary">+ Ouvrir un compte</a>
            </div>

            <c:choose>
                <c:when test="${empty client.accounts}">
                    <p class="text-muted mb-0">Aucun compte ouvert pour ce client.</p>
                </c:when>
                <c:otherwise>
                    <table class="table table-sm">
                        <thead>
                        <tr>
                            <th>Numéro</th>
                            <th>Type</th>
                            <th>Solde</th>
                            <th>Statut</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="account" items="${client.accounts}">
                            <tr>
                                <td>${account.numeroCompte}</td>
                                <td>${account.type}</td>
                                <td>${account.solde} FCFA</td>
                                <td>${account.statut}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/accounts/details?id=${account.id}"
                                       class="btn btn-sm btn-outline-primary">Voir</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</div>

</body>
</html>