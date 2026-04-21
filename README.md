# Gestion de Ticket IT

Application console Java pour enregistrer, suivre et mettre a jour des tickets IT.

## Roles metier

- `Employer` : soumet une alerte en renseignant le demandeur et le service.
- `Administrateur` : consulte l'ordre de priorite et valide l'assignation des tickets non critiques.
- `Technicien` : prend en charge les tickets assignes, les met en attente, les resout et les cloture.

## Regle d'assignation

- Les tickets `LOW`, `MEDIUM` et `HIGH` doivent etre valides par un administrateur avant leur assignation.
- Les tickets `CRITICAL` constituent l'exception metier : ils peuvent etre assignes directement a un technicien sans attendre la validation admin.
- Si un ticket non critique est assigne trop tot, l'application leve `ValidationAdministrateurRequiseException`.

## Prerequis

- Java 21 ou version superieure

## Structure du projet

- `src/main/java` : code applicatif
- `src/test/java` : tests unitaires
- `data/tickets.csv` : fichier de persistence local cree a l'execution
- `data/tickets.example.csv` : exemple de structure CSV

## Flux conseille

1. `Employer` cree un ticket.
2. `Administrateur` visualise l'ordre de priorite et valide l'assignation si necessaire.
3. `Technicien` prend en charge le ticket et suit son cycle de vie.

## Exemple de scenario

1. Un `Employer` signale une panne reseau avec une priorite `HIGH`.
2. L'`Administrateur` consulte la liste des tickets tries par priorite.
3. L'`Administrateur` valide le ticket pour autoriser son assignation.
4. Le `Technicien` assigne le ticket a son nom, le met en attente si besoin, puis le resout et le cloture.
5. Si la priorite est `CRITICAL`, le `Technicien` peut l'assigner directement sans attendre l'etape 3.

## Donnees persistees

Le fichier CSV conserve maintenant, en plus des informations existantes, les colonnes suivantes :

- `adminValidated` : indique si l'assignation a ete validee par un administrateur.
- `validatedByAdmin` : nom de l'administrateur ayant valide l'assignation.
- `validatedAt` : date et heure de validation.

Le lecteur CSV reste compatible avec les anciennes lignes qui ne contiennent pas encore ces colonnes.

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
