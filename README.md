# 📜 Description du projet

Le projet consiste à développer une application Android en Kotlin simulant le fonctionnement d'un compteur de taxi. Cette application permettra de calculer en temps réel la distance parcourue, le temps de trajet, et le tarif de la course, en prenant en compte plusieurs paramètres : tarif de base, tarif par kilomètre, et tarif par minute. L'application devra intégrer plusieurs fonctionnalités comme la géolocalisation en temps réel, la gestion des permissions d'accès à la localisation, et l'affichage des informations du chauffeur.

---

# 🎯 Objectifs et Fonctionnalités

## 📍 Affichage en Temps Réel de la Localisation du Chauffeur
L'application doit afficher sur une carte Google Maps la position actuelle du chauffeur, mise à jour en temps réel.

## 💰 Calcul Dynamique du Tarif
Le tarif total est calculé en fonction de la distance parcourue et du temps écoulé depuis le début de la course.
- **Tarif de base** : Un prix fixe au début de la course.
- **Tarif par kilomètre** : Le prix par distance parcourue (par exemple, 1.5 DH/km).
- **Tarif par minute** : Le prix basé sur le temps écoulé (par exemple, 0.5 DH/min).

## 🔔 Notifications
Une notification sera envoyée à la fin de la course, incluant des informations sur la distance parcourue, le temps écoulé, et le tarif total.

## 🔒 Gestion des Permissions
L'application demandera l'autorisation d'accès à la localisation à l'exécution, en utilisant la bibliothèque EasyPermissions pour simplifier cette gestion.

## 🖥️ Interface Utilisateur
L'interface principale comprendra :
- Un **TextView** pour afficher la distance parcourue.
- Un **TextView** pour afficher le temps écoulé.
- Un **TextView** pour afficher le tarif total.
- Un **Button** pour démarrer la course.
- Une carte **Google Maps** pour afficher la position du chauffeur en temps réel.

## 📏 Calcul de la Distance et du Temps
Le calcul de la distance parcourue et du temps écoulé sera effectué en temps réel en utilisant les services de géolocalisation (FusedLocationProviderClient).

## 🌍 Traduction Multilingue
L'application sera disponible en trois langues : Arabe, Français et Anglais, pour atteindre un public plus large.

---

# 🛠️ Détails Techniques

## 🔑 Permissions et Manifest
- L'application demandera les permissions nécessaires pour accéder à la localisation dans le fichier `AndroidManifest.xml`.
- La gestion des permissions sera simplifiée en utilisant **EasyPermissions**.

## 🎨 UI et Composants
L'interface principale comprendra plusieurs éléments :
- Un **TextView** pour afficher la distance parcourue.
- Un **TextView** pour afficher le temps écoulé.
- Un **TextView** pour afficher le tarif total.
- Un **Button** pour démarrer la course.
- Une carte **Google Map** pour afficher la position du chauffeur en temps réel.

## 💵 Calcul du Tarif
- Une méthode sera implémentée pour calculer le tarif basé sur la distance parcourue et le temps écoulé.
- Le tarif sera mis à jour en temps réel à chaque changement de position.

## 🔔 Notifications
- Une notification sera créée pour signaler la fin de la course et afficher le tarif total, la distance et le temps écoulé.

---

# 📸 Captures d'écran

![photo_5884470860901893299_y](https://github.com/user-attachments/assets/26c1f838-5fae-467f-8005-d4c9bd168ce8)

