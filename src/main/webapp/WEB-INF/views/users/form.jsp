<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Nouvel utilisateur - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4" style="max-width: 500px;">

    <h2>Nouvel utilisateur</h2>

    <c:if test="${not empty erreur}">
        <div class="alert alert-danger">${erreur}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/users/create" method="post" class="mt-3">

        <div class="mb-3">
            <label class="form-label">Nom</label>
            <input type="text" name="nom" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Prénom</label>
            <input type="text" name="prenom" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Login</label>
            <input type="text" name="login" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Mot de passe</label>
            <input type="password" name="motDePasse" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Rôle</label>
            <select name="role" class="form-select" required>
                <option value="AGENT">Agent</option>
                <option value="ADMIN">Administrateur</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Créer</button>
        <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">Annuler</a>

    </form>

</div>

</body>
</html>