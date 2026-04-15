# Gestion de Ticket IT

Application console Java pour enregistrer, suivre et mettre a jour des tickets IT.

## Prerequis

- Java 21 ou version superieure

## Structure du projet

- `src/main/java` : code applicatif
- `src/test/java` : tests unitaires
- `data/tickets.csv` : fichier de persistence local cree a l'execution
- `data/tickets.example.csv` : exemple de structure CSV

## Commandes Maven

Sous Windows :

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

Sous macOS ou Linux :

```bash
./mvnw clean test
./mvnw clean package
```

## Execution

Apres le packaging :

```powershell
java -jar target/gestion-ticket-it-1.0.0-SNAPSHOT.jar
```

Le chemin du fichier CSV peut etre surcharge :

```powershell
java -Dticket.data.path=chemin/vers/tickets.csv -jar target/gestion-ticket-it-1.0.0-SNAPSHOT.jar
```

Ou via la variable d'environnement `TICKET_DATA_PATH`.

## GitHub Actions

Le workflow [`.github/workflows/maven.yml`](.github/workflows/maven.yml) execute automatiquement `test` a chaque `push` et `pull request`.
