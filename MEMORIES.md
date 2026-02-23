# MIMOSA X ANDROID (MEMORIES)

## 📌 Rôle et Statut Actuel
- **Nom du composant :** Mimosa (Anciennement Element X Android).
- **Statut :** Code source `element-x` cloné, re-déclaré, et customisé (Février 2026).
- **Objectif métier :** Représenter l'outil de collaboration instantanée "Zero-Touch" et ultrasur (métaphore de la plante réactive "Mimosa") du système d'information gouvernemental.

## 🛠️ Modifications Zéro-Touch Appliquées
L'application originale a été nettoyée de toute dépendance publique complexe pour devenir un outil souverain unique.
- **Fichier impacté (1) :** `appconfig/src/main/kotlin/io/element/android/appconfig/AuthenticationConfig.kt`
  - *Modification :* Remplacement de `MATRIX_ORG_URL` par l'URL institutionnelle codée en dur (Hardcoded) : `https://ma3x.ztn0.net`.
- **Fichier impacté (2) :** `plugins/src/main/kotlin/config/BuildTimeConfig.kt`
  - *Modification :* Application ID figé sur `net.ztn0.mimosa.x`. L'Application Name affiché à la compilation est devenu "Mimosa".
  - *Désactivation GAFAM :* `PUSH_CONFIG_INCLUDE_FIREBASE = false`. Les Services de Push Google sont retirés temporairement pour empêcher le plantage du compilateur par absence de clé Google Play, préservant la souveraineté. L'app utilisera les websockets `unifiedpush`.

## 🚀 Guide de Compilation (Android Studio)
1. Ouvrir le dossier `mimosa-x-android` dans Android Studio.
2. Laisser Gradle télécharger les paquets natifs (Kotlin/Rust).
3. **Logos :** Utiliser l'outil "Image Asset" sur le sous-dossier Android (`appicon`) ou `app` pour uploader le logo "Mimosa vert" en tant que Foreground.
4. Compiler : Menu `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
5. Distribuer et procéder aux démos via le SSO Authentik.

## 📦 Backlog Technique & Sécurité
- Créer si besoin une page d'atterrissage souveraine pour héberger l'APK `Mimosa.apk` afin que les agents le téléchargent directement sans passer par le store public Google.
- À terme, envisager le rapatriement des services de Notifications *UnifiedPush* (`ntfy`) sur un VPS interne si le client veut du 100% On-Premise.
