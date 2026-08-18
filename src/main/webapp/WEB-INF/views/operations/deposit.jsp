<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Dépôt - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4" style="max-width: 500px;">

    <h2 class="text-success">Dépôt</h2>
    <p class="text-muted">Compte n° ${account.numeroCompte} — Solde actuel : <strong>${account.solde} FCFA</strong></p>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/operations/deposit" method="post">
        <input type="hidden" name="accountId" value="${account.id}">

        <div class="mb-3">
            <label class="form-label">Montant à déposer (FCFA)</label>
            <input type="number" name="montant" class="form-control" min="1" step="1" required autofocus>
        </div>

        <div class="mb-3">
            <label class="form-label">Description (optionnel)</label>
            <input type="text" name="description" class="form-control" placeholder="Ex : dépôt espèces">
        </div>

        <button type="submit" class="btn btn-success">Valider le dépôt</button>
        <a href="${pageContext.request.contextPath}/accounts/details?id=${account.id}"
           class="btn btn-outline-secondary">Annuler</a>
    </form>

</div>

</body>
</html>