[![Build Status](https://travis-ci.org/crosart/oc-myerp.svg?branch=master)](https://travis-ci.org/crosart/oc-myerp)
[![codecov](https://codecov.io/gh/crosart/oc-myerp/branch/master/graph/badge.svg)](https://codecov.io/gh/crosart/oc-myerp)

### Projet OpenClassrooms - Parcours Développeur d'application Java
# Test course - My ERP


##### Objectif : testez vos développements Java.

<hr>

## CONTEXTE
Votre équipe est en train de réaliser un système de facturation et de comptabilité pour un client. Le développement a débuté depuis quelques temps et vous devez commencer à vérifier que l'application fonctionne correctement, qu'elle répond bien aux règles de gestion et les respecte.

#### Contraintes fonctionnelles :
Votre travail consiste à réaliser 2 types de tests :
- des **tests unitaires** : leurs objectifs sont de valider les règles de gestion unitaires de chaque "composant" de l'application
- des **tests d'intégration** : leurs objectifs sont de valider la bonne interaction entre les différents composants de l'application

À vous de définir vos tests et la stratégie que vous allez mettre en place pour les tests d'intégration.

Vous implémenterez et automatiserez ces tests à l'aide de JUnit, Mockito, Maven et Travis CI / GitLab CI / Jenkins.

Les tests seront lancés via le build Maven.

Les tests d'intégration font l'objet de profils Maven spécifiques (cf. le fichier pom.xml du projet parent fourni).

Cet environnement sera construit (à partir des éléments disponibles dans le dépôt Git du projet) et monté à la volée par votre système d'intégration continue.

<hr>

## REFERENTIEL
- Réaliser l'audit d'un système
    - Les 4 erreurs de développements dans le projet fourni sont identifiées et résolues.
    - Le développement a été complété en suivant les TODO.
- Mettre en place une démarche qualité et sa méthodologie
    - Les tests unitaires ont été réalisés à l’aide de JUnit.
    - Les tests d’intégration ont été réalisés à l’aide des “profiles” Maven.
    - L'ensemble des modules a un code coverage de 75% minimum.
- Gérer l’évolutivité et l’adaptabilité d'un système
    - Un serveur d'intégration est installé et configuré (Jenkins / Travis CI / GitLab CI au choix).
    - Un rapport d'exécution des tests est automatiquement généré à chaque commit.
    - Un logiciel de versioning a été correctement utilisé.
    
<p align="center">
<img src="https://upload.wikimedia.org/wikipedia/fr/0/0d/Logo_OpenClassrooms.png" alt="OC logo">
</p>
