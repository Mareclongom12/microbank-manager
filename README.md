# MicroBank Manager

Application web de gestion d'une institution de microfinance, développée en Jakarta EE (Servlet/JSP/JSTL, JPA/Hibernate, PostgreSQL, Bootstrap).

Projet réalisé dans le cadre du module Développement Web Java / Jakarta EE — Licence 3 IAGE, ISI.

## Prérequis

- **JDK 21**
- **Apache Tomcat 10.1.x** (Jakarta EE 10 — namespace `jakarta.*`)
- **PostgreSQL** (testé avec PostgreSQL 15+)
- **Maven** (ou le wrapper `mvnw` fourni avec le projet)
- **IntelliJ IDEA** (recommandé) ou tout IDE supportant Maven + déploiement Tomcat

## 1. Création de la base de données

1. Ouvrir pgAdmin (ou tout client PostgreSQL)
2. Créer une base nommée `microbank_db` :
```sql
   CREATE DATABASE microbank_db;
```
3. Ouvrir le `Query Tool` sur cette base et exécuter le script `database.sql` fourni à la racine du projet. Ce script crée les 4 tables (`users`, `clients`, `accounts`, `operations`) ainsi qu'un compte administrateur par défaut.

## 2. Configuration de `persistence.xml`

Le fichier `src/main/resources/META-INF/persistence.xml` contient la configuration de connexion à PostgreSQL :

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/microbank_db"/>
<property name="jakarta.persistence.jdbc.user" value="postgres"/>
<property name="jakarta.persistence.jdbc.password" value="Marcelo12"/>
```

Adapter l'utilisateur et le mot de passe selon votre installation PostgreSQL locale.

La propriété `hibernate.hbm2ddl.auto` est réglée sur `update` : Hibernate crée/actualise automatiquement les tables à partir des entités si elles n'existent pas déjà (utile si vous ne souhaitez pas exécuter `database.sql` manuellement).

## 3. Lancement de l'application

**Avec IntelliJ IDEA :**
1. Ouvrir le projet (`File` → `Open`)
2. Laisser Maven télécharger les dépendances (JPA/Hibernate, driver PostgreSQL, JSTL, iText, Servlet API)
3. Configurer un serveur Tomcat local (`Run` → `Edit Configurations` → `Add` → `Tomcat Server` → `Local`)
4. Déployer l'artifact `microbank-manager:war` (exploded ou non)
5. Lancer le serveur

**En ligne de commande (avec Maven + Tomcat installé séparément) :**
```bash
mvn clean package
# Copier le fichier target/microbank-manager.war dans le dossier webapps/ de Tomcat
# Démarrer Tomcat (bin/startup.sh ou bin/startup.bat)
```

## 4. Accès à l'application

Une fois Tomcat démarré, l'application est accessible à l'adresse :

Le port dépend de votre configuration Tomcat (par défaut 8080, ou tout autre port configuré).

## 5. Compte de test

Un compte administrateur est créé automatiquement par le script `database.sql` :

| Champ | Valeur |
|---|---|
| Login | `admin` |
| Mot de passe | `admin123` |
| Rôle | ADMIN |

Ce compte permet d'accéder à toutes les fonctionnalités, y compris la gestion des utilisateurs (réservée aux administrateurs), et de créer de nouveaux comptes agents depuis l'interface (`Utilisateurs` → `+ Nouvel utilisateur`).

## 6. Fonctionnalités principales

- Authentification par formulaire avec `HttpSession`, protection des pages via `Filter`
- Gestion des clients : création, modification, suppression, recherche, pagination
- Gestion des comptes bancaires : ouverture (compte courant/épargne), consultation
- Opérations bancaires : dépôt, retrait, virement — chacune exécutée dans une transaction JPA garantissant la cohérence (rollback automatique en cas d'erreur)
- Historique des opérations avec filtres (type, période, montant) et pagination
- Génération d'un relevé de compte au format PDF (bibliothèque iText)
- Export de l'historique au format CSV
- Tableau de bord avec statistiques globales (nombre de clients, de comptes, solde total, opérations du jour)
- Gestion des utilisateurs (réservée aux administrateurs) : création d'agents, activation/désactivation

## 7. Architecture du projet
src/main/java/sn/iage/isi/microbank/
├── controller/ Servlets (LoginServlet, ClientServlet, AccountServlet, OperationServlet, UserServlet...)
├── model/ Entités JPA (User, Client, Account, Operation) et enums
├── dao/ Accès aux données via EntityManager (CRUD, requêtes JPQL, transactions)
├── service/ Logique métier et validations
├── filter/ AuthFilter (protection des pages nécessitant une authentification)
└── util/ JPAUtil (EntityManagerFactory), PdfGenerator

src/main/webapp/
├── login.jsp Page de connexion (accessible sans authentification)
└── WEB-INF/
├── web.xml
└── views/ Toutes les JSP protégées (accessibles uniquement via forward de Servlet)
├── common/ Fragments réutilisables (navbar)
├── clients/
├── accounts/
├── operations/
└── users/

## 8. Notes techniques

- Les mots de passe sont hachés en SHA-256 avant stockage (jamais en clair, jamais affichés dans les JSP).
- La pagination est réalisée côté base de données via `setFirstResult`/`setMaxResults` en JPQL, jamais en récupérant l'intégralité des données puis en filtrant côté Java.
- Les opérations bancaires (dépôt, retrait, virement) sont chacune encapsulées dans une transaction unique (`EntityManager.getTransaction()`), avec `rollback()` automatique en cas d'exception à n'importe quelle étape.