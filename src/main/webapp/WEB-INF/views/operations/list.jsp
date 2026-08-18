<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Historique - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Historique — Compte n° ${account.numeroCompte}</h2>
        <div>
            <a href="${pageContext.request.contextPath}/operations/export?accountId=${account.id}&type=${type}&dateDebut=${dateDebut}&dateFin=${dateFin}&montantMin=${montantMin}&montantMax=${montantMax}"
               class="btn btn-outline-success">Exporter CSV</a>
            <a href="${pageContext.request.contextPath}/accounts/details?id=${account.id}"
               class="btn btn-outline-secondary">Retour au compte</a>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/operations" method="get" class="row g-2 mb-4 align-items-end">
        <input type="hidden" name="accountId" value="${account.id}">

        <div class="col-auto">
            <label class="form-label">Type</label>
            <select name="type" class="form-select">
                <option value="">Tous</option>
                <option value="DEPOT" ${type == 'DEPOT' ? 'selected' : ''}>Dépôt</option>
                <option value="RETRAIT" ${type == 'RETRAIT' ? 'selected' : ''}>Retrait</option>
                <option value="VIREMENT" ${type == 'VIREMENT' ? 'selected' : ''}>Virement</option>
            </select>
        </div>

        <div class="col-auto">
            <label class="form-label">Du</label>
            <input type="date" name="dateDebut" class="form-control" value="${dateDebut}">
        </div>

        <div class="col-auto">
            <label class="form-label">Au</label>
            <input type="date" name="dateFin" class="form-control" value="${dateFin}">
        </div>

        <div class="col-auto">
            <label class="form-label">Montant min</label>
            <input type="number" name="montantMin" class="form-control" value="${montantMin}" style="width: 120px;">
        </div>

        <div class="col-auto">
            <label class="form-label">Montant max</label>
            <input type="number" name="montantMax" class="form-control" value="${montantMax}" style="width: 120px;">
        </div>

        <div class="col-auto">
            <button type="submit" class="btn btn-outline-secondary">Filtrer</button>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/operations?accountId=${account.id}"
               class="btn btn-outline-danger">Réinitialiser</a>
        </div>
    </form>

    <table class="table table-striped table-hover align-middle">
        <thead class="table-dark">
        <tr>
            <th>Date</th>
            <th>Référence</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Description</th>
            <th>Agent</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="op" items="${operations}">
            <tr>
                <td>${op.dateOperation}</td>
                <td>${op.reference}</td>
                <td>
                    <span class="badge
                        ${op.type == 'DEPOT' ? 'bg-success' : op.type == 'RETRAIT' ? 'bg-warning' : 'bg-info'}">
                            ${op.type}
                    </span>
                </td>
                <td class="${op.type == 'RETRAIT' ? 'text-danger' : 'text-success'}">
                        ${op.type == 'RETRAIT' ? '-' : '+'}${op.montant} FCFA
                </td>
                <td>${op.description}</td>
                <td>${op.agent.nomComplet}</td>
            </tr>
        </c:forEach>

        <c:if test="${empty operations}">
            <tr>
                <td colspan="6" class="text-center text-muted">Aucune opération trouvée</td>
            </tr>
        </c:if>
        </tbody>
    </table>

    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination">
                <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/operations?accountId=${account.id}&page=${currentPage - 1}&type=${type}&dateDebut=${dateDebut}&dateFin=${dateFin}">
                        &laquo; Précédent
                    </a>
                </li>

                <c:forEach begin="0" end="${totalPages - 1}" var="i">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}/operations?accountId=${account.id}&page=${i}&type=${type}&dateDebut=${dateDebut}&dateFin=${dateFin}">
                                ${i + 1}
                        </a>
                    </li>
                </c:forEach>

                <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/operations?accountId=${account.id}&page=${currentPage + 1}&type=${type}&dateDebut=${dateDebut}&dateFin=${dateFin}">
                        Suivant &raquo;
                    </a>
                </li>
            </ul>
        </nav>
    </c:if>

</div>

</body>
</html>