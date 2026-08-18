<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>${empty client.id ? 'Nouveau client' : 'Modifier client'} - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4" style="max-width: 700px;">

    <h2>${empty client.id ? 'Nouveau client' : 'Modifier le client'}</h2>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/clients/${empty client.id ? 'create' : 'update'}"
          method="post" class="mt-3">

        <c:if test="${not empty client.id}">
            <input type="hidden" name="id" value="${client.id}">
        </c:if>

        <div class="row mb-3">
            <div class="col">
                <label class="form-label">Nom *</label>
                <input type="text" name="nom" class="form-control" required value="${client.nom}">
            </div>
            <div class="col">
                <label class="form-label">Prénom *</label>
                <input type="text" name="prenom" class="form-control" required value="${client.prenom}">
            </div>
        </div>

        <div class="row mb-3">
            <div class="col">
                <label class="form-label">Date de naissance</label>
                <input type="date" name="dateNaissance" class="form-control" value="${client.dateNaissance}">
            </div>
            <div class="col">
                <label class="form-label">Numéro de pièce *</label>
                <input type="text" name="numeroPiece" class="form-control" required value="${client.numeroPiece}">
            </div>
        </div>

        <div class="row mb-3">
            <div class="col">
                <label class="form-label">Téléphone *</label>
                <input type="text" name="telephone" class="form-control" required value="${client.telephone}">
            </div>
            <div class="col">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control" value="${client.email}">
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Adresse</label>
            <textarea name="adresse" class="form-control" rows="2">${client.adresse}</textarea>
        </div>

        <button type="submit" class="btn btn-primary">Enregistrer</button>
        <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary">Annuler</a>

    </form>

</div>

</body>
</html>