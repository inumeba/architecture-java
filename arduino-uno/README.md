# Projet Arduino Uno

Ce dossier contient un projet de base pour **Arduino Uno**.

## Contenu

- `arduino-uno.ino` : sketch principal (blink de la LED integree)
- `.gitignore` : exclusions usuelles pour Arduino

## Prerequis

- Arduino IDE 2.x (ou VS Code + extension Arduino)
- Carte: **Arduino Uno**
- Port serie detecte

## Utilisation (Arduino IDE)

1. Ouvrir le dossier `arduino-uno`.
2. Ouvrir le fichier `arduino-uno.ino`.
3. Selectionner `Board: Arduino Uno`.
4. Selectionner le port serie.
5. Cliquer sur **Upload**.

## Personnalisation rapide

- Modifier `BLINK_DELAY_MS` pour accelerer ou ralentir le clignotement.
- Utiliser `Serial.println(...)` pour debug.
