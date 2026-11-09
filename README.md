# WeGrow - API REST de Gestion des Départements et Employés

**WeGrow** est une API Spring MVC permettant à l'équipe RH de gérer les départements de l'entreprise et leurs collaborateurs.

---

## 🛠️ Technologies Utilisées

- **Java** : 17+ (testé avec OpenJDK 25)
- **Spring Core & Spring MVC** : 6.1.4
- **Spring Data JPA & Spring ORM** : 3.2.3
- **Hibernate ORM** : 6.4.4.Final
- **Base de données relationnelle** : PostgreSQL
- **Serveur d'application** : Tomcat 10.1+ (Support Jakarta EE 10 / Servlet 6.0)
- **Format de packaging** : WAR
- **Build & Tests** : Maven, JUnit 5, Mockito, Spring Test

---

## 🏗️ Architecture en Couches

Le projet respecte une architecture en couches stricte, avec injection par constructeur et dépendance vers des interfaces :

```
src/main/java/ma/wegrow/
├── config/
│   └── DataInitializer.java        # Initialisation des données au démarrage (phase init)
├── controller/
│   ├── DepartementController.java  # Endpoints REST pour les départements
│   └── EmployeController.java      # Endpoints REST pour les employés
├── dto/
│   ├── DepartementRequestDTO.java
│   ├── DepartementResponseDTO.java
│   ├── EmployeRequestDTO.java
│   ├── EmployeResponseDTO.java
│   └── ErrorResponseDTO.java       # Format d'erreur uniforme (status, error, message, timestamp)
├── entity/
│   ├── Contrat.java                # Enum : CDI, CDD, STAGE
│   ├── Departement.java            # Entité Departement (id, nom unique, description)
│   └── Employe.java                # Entité Employe (id, matricule unique, nom, prenom, poste, contrat, departement)
├── exception/
│   ├── DepartmentNotEmptyException.java # Exception pour 409 (département contenant des employés)
│   ├── DuplicateResourceException.java  # Exception pour 409 (doublon nom ou matricule)
│   ├── ResourceNotFoundException.java   # Exception pour 404 (département introuvable)
│   └── GlobalExceptionHandler.java      # @RestControllerAdvice pour capturer les exceptions
├── repository/
│   ├── DepartementRepository.java  # Interface Spring Data JPA
│   └── EmployeRepository.java      # Interface Spring Data JPA
└── service/
    ├── DepartementService.java     # Interface de service
    ├── EmployeService.java         # Interface de service
    └── impl/
        ├── DepartementServiceImpl.java # Implémentation du service département
        └── EmployeServiceImpl.java     # Implémentation du service employé
```

---

## ⚙️ Configuration Unique en XML

Conformément au brief, **toute la configuration de Spring** (beans, MVC, persistance JPA, transactions) est centralisée dans un seul fichier XML :
- [web.xml](file:///home/vito/Documents/yc-projects/Sprint_3/weGrow/src/main/webapp/WEB-INF/web.xml) : Déclaration du `DispatcherServlet`.
- [applicationContext.xml](file:///home/vito/Documents/yc-projects/Sprint_3/weGrow/src/main/webapp/WEB-INF/applicationContext.xml) :
  - `<mvc:annotation-driven />` pour Spring MVC et Jackson JSON.
  - Déclaration de la DataSource PostgreSQL (`DriverManagerDataSource`).
  - Configuration de l'`EntityManagerFactory` JPA (Hibernate).
  - Gestionnaire de transactions (`JpaTransactionManager`) et `<tx:annotation-driven />`.
  - Scan des repositories Spring Data JPA (`<jpa:repositories>`).
  - Déclaration explicite des beans de service et des contrôleurs avec injection de dépendances par constructeur (`<constructor-arg>`).
  - Déclaration du bean `DataInitializer` avec sa méthode de cycle de vie `init-method="init"`.

---

## 📋 Endpoints de l'API

### 1. Départements

| Méthode | URL | Codes HTTP | Description |
|---|---|---|---|
| `POST` | `/api/departements` | `201 Created` / `409 Conflict` | Créer un nouveau département |
| `GET` | `/api/departements` | `200 OK` | Lister les départements avec `nombreEmployes` |
| `DELETE` | `/api/departements/{id}` | `204 No Content` / `404 Not Found` / `409 Conflict` | Supprimer un département vide |

#### Exemple `POST /api/departements`
**Requête :**
```json
{
  "nom": "Ressources humaines",
  "description": "Gestion du personnel"
}
```
**Réponse (201 Created) :**
```json
{
  "id": 1,
  "nom": "Ressources humaines",
  "description": "Gestion du personnel",
  "nombreEmployes": 0
}
```

#### Exemple `GET /api/departements`
**Réponse (200 OK) :**
```json
[
  {
    "id": 1,
    "nom": "Ressources humaines",
    "description": "Gestion du personnel",
    "nombreEmployes": 2
  },
  {
    "id": 2,
    "nom": "Informatique",
    "description": "Développement",
    "nombreEmployes": 1
  }
]
```

---

### 2. Employés

| Méthode | URL | Codes HTTP | Description |
|---|---|---|---|
| `POST` | `/api/departements/{id}/employes` | `201 Created` / `404 Not Found` / `409 Conflict` | Ajouter un employé dans un département |
| `GET` | `/api/departements/{id}/employes` | `200 OK` / `404 Not Found` | Lister les employés d'un département |

#### Exemple `POST /api/departements/1/employes`
**Requête :**
```json
{
  "matricule": "EMP001",
  "nom": "Alami",
  "prenom": "Sara",
  "poste": "Responsable RH",
  "contrat": "CDI"
}
```
**Réponse (201 Created) :**
```json
{
  "id": 1,
  "matricule": "EMP001",
  "nom": "Alami",
  "prenom": "Sara",
  "poste": "Responsable RH",
  "contrat": "CDI"
}
```

#### Exemple `GET /api/departements/1/employes`
**Réponse (200 OK) :**
```json
[
  {
    "id": 1,
    "matricule": "EMP001",
    "nom": "Alami",
    "prenom": "Sara",
    "poste": "Responsable RH",
    "contrat": "CDI"
  },
  {
    "id": 2,
    "matricule": "EMP002",
    "nom": "Bennani",
    "prenom": "Youssef",
    "poste": "Assistant RH",
    "contrat": "CDD"
  }
]
```

---

## 🔒 Règles de Gestion et Concurrence

1. **Unicité du nom de département** : Contrainte d'unicité `unique = true` en base de données et vérification dans `DepartementServiceImpl`. En cas de conflit (ou requête simultanée), le code HTTP `409 Conflict` est renvoyé.
2. **Unicité du matricule employé** : Contrainte d'unicité `unique = true` en base de données et vérification dans `EmployeServiceImpl`. En cas de conflit, le code HTTP `409 Conflict` est renvoyé.
3. **Appartenance à un département existant** : L'existence du département est validée lors de l'ajout ou de la consultation des employés ; renvoie `404 Not Found` si inexistant.
4. **Suppression protégée** : Un département contenant au moins un employé ne peut pas être supprimé ; renvoie `409 Conflict`.
5. **Fiabilité lors de requêtes simultanées** :
   - Transactions gérées avec `@Transactional`.
   - Contraintes uniques au niveau du SGBD.
   - `GlobalExceptionHandler` intercepte `DataIntegrityViolationException` pour garantir qu'aucune donnée n'est corrompue et que les requêtes simultanées reçoivent un code 409 propre.

---

## 🚀 Compilation et Exécution des Tests

### Lancer la suite de tests (26 tests unitaires et d'intégration) :
```bash
mvn clean test
```

### Générer le fichier WAR pour Tomcat :
```bash
mvn clean package
```
Le fichier WAR est généré dans `target/wegrow.war`.

### Déploiement Tomcat 10.1+ :
Copier `target/wegrow.war` dans le dossier `webapps` de votre serveur Tomcat.
L'API sera disponible sur :
```
http://localhost:8080/wegrow/api/departements
```
