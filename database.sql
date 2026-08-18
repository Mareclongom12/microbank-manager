-- =====================================================
-- MicroBank Manager - Script de création de la base de données
-- ISI - Licence 3 IAGE - Projet JEE
-- =====================================================

-- Suppression des tables existantes (dans l'ordre inverse des dépendances)
DROP TABLE IF EXISTS operations CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- Table : users (agents et administrateurs)
-- =====================================================
CREATE TABLE users (
                       id              BIGSERIAL PRIMARY KEY,
                       nom             VARCHAR(255) NOT NULL,
                       prenom          VARCHAR(255) NOT NULL,
                       login           VARCHAR(255) NOT NULL UNIQUE,
                       mot_de_passe    VARCHAR(255) NOT NULL,
                       role            VARCHAR(20)  NOT NULL CHECK (role IN ('AGENT', 'ADMIN')),
                       statut          VARCHAR(20)  NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'INACTIF'))
);

-- =====================================================
-- Table : clients
-- =====================================================
CREATE TABLE clients (
                         id              BIGSERIAL PRIMARY KEY,
                         nom             VARCHAR(255) NOT NULL,
                         prenom          VARCHAR(255) NOT NULL,
                         date_naissance  DATE,
                         telephone       VARCHAR(50)  NOT NULL,
                         email           VARCHAR(255),
                         adresse         TEXT,
                         numero_piece    VARCHAR(100) UNIQUE,
                         date_creation   DATE,
                         statut          VARCHAR(20)  NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'INACTIF'))
);

-- =====================================================
-- Table : accounts (comptes bancaires)
-- =====================================================
CREATE TABLE accounts (
                          id              BIGSERIAL PRIMARY KEY,
                          numero_compte   VARCHAR(50)     NOT NULL UNIQUE,
                          type            VARCHAR(20)     NOT NULL CHECK (type IN ('COURANT', 'EPARGNE')),
                          solde           NUMERIC(15,2)   NOT NULL DEFAULT 0,
                          date_ouverture  DATE,
                          statut          VARCHAR(20)     NOT NULL DEFAULT 'ACTIF' CHECK (statut IN ('ACTIF', 'BLOQUE', 'CLOTURE')),
                          client_id       BIGINT          NOT NULL REFERENCES clients(id) ON DELETE CASCADE
);

-- =====================================================
-- Table : operations (dépôts, retraits, virements)
-- =====================================================
CREATE TABLE operations (
                            id                      BIGSERIAL PRIMARY KEY,
                            reference               VARCHAR(100)    NOT NULL UNIQUE,
                            type                    VARCHAR(20)     NOT NULL CHECK (type IN ('DEPOT', 'RETRAIT', 'VIREMENT')),
                            montant                 NUMERIC(15,2)   NOT NULL,
                            date_operation          TIMESTAMP       NOT NULL,
                            description             TEXT,
                            account_id              BIGINT          NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
                            compte_destination_id   BIGINT          REFERENCES accounts(id),
                            agent_id                BIGINT          NOT NULL REFERENCES users(id)
);

-- =====================================================
-- Index utiles pour les performances (recherche, filtres, pagination)
-- =====================================================
CREATE INDEX idx_clients_nom_prenom ON clients(nom, prenom);
CREATE INDEX idx_accounts_client ON accounts(client_id);
CREATE INDEX idx_operations_account ON operations(account_id);
CREATE INDEX idx_operations_date ON operations(date_operation);

-- =====================================================
-- Compte administrateur par défaut
-- Login : admin / Mot de passe : admin123 (haché en SHA-256)
-- =====================================================
INSERT INTO users (nom, prenom, login, mot_de_passe, role, statut)
VALUES ('Admin', 'Système', 'admin',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'ADMIN', 'ACTIF');