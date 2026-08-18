<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Compte ${account.numeroCompte} - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Compte n° ${account.numeroCompte}</h2>
        <div>
            <a href="${pageContext.request.contextPath}/clients/details?id=${account.client.id}"
               class="btn btn-outline-dark">Voir le client</a>
            <a href="${pageContext.request.contextPath}/accounts" class="btn btn-outline-secondary">Retour à la liste</a>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <h5 class="card-title">Informations</h5>
                    <table class="table table-borderless mb-0">
                        <tr>
                            <th style="width: 160px;">Titulaire</th>
                            <td>${account.client.prenom} ${account.client.nom}</td>
                        </tr>
                        <tr>
                            <th>Type</th>
                            <td>${account.type}</td>
                        </tr>
                        <tr>
                            <th>Statut</th>
                            <td>
                                <span class="badge ${account.statut == 'ACTIF' ? 'bg-success' : 'bg-secondary'}">
                                    ${account.statut}
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <th>Date d'ouverture</th>
                            <td>${account.dateOuverture}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-md-6 text-center border-start">
                    <p class="text-muted mb-1">Solde actuel</p>
                    <h1 class="text-primary">${account.solde} FCFA</h1>
                </div>
            </div>
        </div>
    </div>

    <div class="d-flex gap-2 mb-4 flex-wrap">
        <a href="${pageContext.request.contextPath}/operations/deposit?accountId=${account.id}"
           class="btn btn-success">+ Dépôt</a>
        <a href="${pageContext.request.contextPath}/operations/withdraw?accountId=${account.id}"
           class="btn btn-warning">- Retrait</a>
        <a href="${pageContext.request.contextPath}/operations/transfer?accountId=${account.id}"
           class="btn btn-info">Virement</a>
        <a href="${pageContext.request.contextPath}/operations?accountId=${account.id}"
           class="btn btn-outline-dark">Historique complet</a>
        <a href="${pageContext.request.contextPath}/accounts/statement?id=${account.id}"
           class="btn btn-outline-primary" target="_blank">Télécharger le relevé PDF</a>
    </div>

</div>

</body>
</html>