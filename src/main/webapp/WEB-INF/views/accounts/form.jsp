<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Ouvrir un compte - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4" style="max-width: 600px;">

    <h2>Ouvrir un compte</h2>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/accounts/create" method="post" class="mt-3">

        <div class="mb-3">
            <label class="form-label">Client</label>

            <c:choose>
                <c:when test="${not empty client}">
                    <input type="text" class="form-control" disabled
                           value="${client.prenom} ${client.nom} (${client.numeroPiece})">
                    <input type="hidden" name="clientId" value="${client.id}">
                </c:when>
                <c:otherwise>
                    <select name="clientId" class="form-select" required>
                        <option value="" disabled selected>-- Choisir un client --</option>
                        <c:forEach var="c" items="${clientsList}">
                            <option value="${c.id}">${c.prenom} ${c.nom} (${c.numeroPiece})</option>
                        </c:forEach>
                    </select>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="mb-3">
            <label class="form-label">Type de compte</label>
            <div class="form-check">
                <input class="form-check-input" type="radio" name="type" id="typeCourant"
                       value="COURANT" checked>
                <label class="form-check-label" for="typeCourant">Compte courant</label>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="radio" name="type" id="typeEpargne"
                       value="EPARGNE">
                <label class="form-check-label" for="typeEpargne">Compte épargne</label>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Dépôt initial (FCFA)</label>
            <input type="number" name="depotInitial" class="form-control"
                   min="0" step="1" value="0" required>
        </div>

        <button type="submit" class="btn btn-primary">Créer le compte</button>
        <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary">Annuler</a>

    </form>

</div>

</body>
</html>