# Backend API - MDD 

## Prérequis
- Java 11 ou supérieur
- MySQL 8.0 ou supérieur
- Maven 3.6 ou supérieur

## Configuration de la base de données

1. Installez MySQL sur votre machine
2. La base de données `mdd_db` sera créée automatiquement au premier lancement
3. Modifiez les identifiants dans `src/main/resources/application.properties` si nécessaire :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mdd_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

## Installation et démarrage

### Sous Windows
```bash
# Installation des dépendances et compilation
mvnw.cmd clean install

# Démarrage de l'application
mvnw.cmd spring-boot:run
```

### Sous Linux/Mac
```bash
# Installation des dépendances et compilation
./mvnw clean install

# Démarrage de l'application
./mvnw spring-boot:run
```

L'API sera accessible sur `http://localhost:8080`

## Documentation Swagger

- Lancez l'application avec `mvnw.cmd spring-boot:run`
- Rendez-vous sur `http://localhost:8080/swagger-ui/index.html` pour explorer les endpoints en Swagger UI
- Swagger expose les définitions OpenAPI (`/v3/api-docs`) et utilise un schéma `bearerAuth` préconfiguré
- Les requêtes protégées nécessitent toujours un header `Authorization: Bearer {token}` ; cliquez sur "Authorize" dans Swagger et collez le token retourné par `/api/auth/login`

## Authentification JWT

L'authentification est basée sur JWT (JSON Web Token). Après connexion ou inscription, vous recevez un token à inclure dans toutes les requêtes protégées.

### Utilisation du token
Ajoutez le header suivant à vos requêtes :
```
Authorization: Bearer {votre_token_jwt}
```

### Durée de validité
Le token est valide pendant 24 heures (configurable dans `application.properties`)

## Validation des données

### Email
- Format email valide requis
- Doit être unique

### Username
- Minimum 3 caractères
- Maximum 50 caractères
- Doit être unique

### Password
- Minimum 6 caractères
- Stocké de manière sécurisée avec BCrypt

## Gestion des erreurs

L'API retourne des codes HTTP standard :
- `200 OK` : Succès
- `400 Bad Request` : Données invalides ou erreur de validation
- `401 Unauthorized` : Non authentifié ou token invalide
- `404 Not Found` : Ressource non trouvée
- `500 Internal Server Error` : Erreur serveur

Format des erreurs :
```json
{
  "message": "Description de l'erreur"
}
```

## Sécurité

- Mots de passe hashés avec BCrypt
- Authentification stateless avec JWT
- Protection CSRF désactivée (utilisation de JWT)
- CORS configuré pour autoriser les requêtes depuis `http://localhost:4200`
- Validation des entrées utilisateur

## Structure du projet

```
src/
├── main/
│   ├── java/com/openclassrooms/mddapi/
│   │   ├── config/          # Configuration Spring Security
│   │   ├── controller/      # Contrôleurs REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Gestion des exceptions
│   │   ├── model/           # Entités JPA
│   │   ├── repository/      # Repositories JPA
│   │   ├── security/        # JWT et sécurité
│   │   └── service/         # Logique métier
│   └── resources/
│       └── application.properties  # Configuration
```

## Technologies utilisées

- **Spring Boot 2.7.3** : Framework principal
- **Spring Security** : Authentification et autorisation
- **Spring Data JPA** : Accès aux données
- **MySQL** : Base de données
- **JWT (jjwt 0.11.5)** : Gestion des tokens
- **Lombok** : Réduction du code boilerplate
- **Hibernate Validator** : Validation des données

## Tests

Pour exécuter les tests :
```bash
mvnw.cmd test
```

## Licence

© 2026 David Cardigos

