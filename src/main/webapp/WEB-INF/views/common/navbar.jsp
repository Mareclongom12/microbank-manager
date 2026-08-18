<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark px-3">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">MicroBank Manager</a>
    <div class="collapse navbar-collapse">
        <ul class="navbar-nav me-auto">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Tableau de bord</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/clients">Clients</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/accounts">Comptes</a>
            </li>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/users">Utilisateurs</a>
                </li>
            </c:if>
        </ul>
    </div>
    <div class="text-white">
        <strong>${sessionScope.user.nomComplet}</strong> (${sessionScope.user.role})
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light ms-3">Déconnexion</a>
    </div>
</nav>