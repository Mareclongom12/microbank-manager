<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Tableau de bord - MicroBank Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container mt-4">

    <h2 class="mb-4">Tableau de bord</h2>

    <div class="row g-4">
        <div class="col-md-3">
            <div class="card text-center shadow-sm">
                <div class="card-body">
                    <p class="text-muted mb-1">Clients</p>
                    <h2 class="text-primary">${nombreClients}</h2>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-center shadow-sm">
                <div class="card-body">
                    <p class="text-muted mb-1">Comptes</p>
                    <h2 class="text-primary">${nombreComptes}</h2>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-center shadow-sm">
                <div class="card-body">
                    <p class="text-muted mb-1">Solde total</p>
                    <h2 class="text-success">${soldeTotal} FCFA</h2>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card text-center shadow-sm">
                <div class="card-body">
                    <p class="text-muted mb-1">Opérations du jour</p>
                    <h2 class="text-info">${operationsDuJour}</h2>
                </div>
            </div>
        </div>
    </div>

    <div class="mt-5">
        <h5>Accès rapide</h5>
        <div class="d-flex gap-2 mt-2">
            <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-primary">Gérer les clients</a>
            <a href="${pageContext.request.contextPath}/accounts" class="btn btn-outline-primary">Gérer les comptes</a>
            <a href="${pageContext.request.contextPath}/clients/create" class="btn btn-primary">+ Nouveau client</a>
        </div>
    </div>

</div>

</body>
</html>