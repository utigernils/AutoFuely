# Datenschutzerklärung für AutoFuely

**Stand:** März 2026

Der Schutz Ihrer persönlichen Daten ist uns ein wichtiges Anliegen. Nachfolgend informieren wir Sie transparent darüber, welche Daten bei der Nutzung der Android-App **AutoFuely** verarbeitet werden.

---

## 1. Überblick & Grundsatz

**AutoFuely** ist eine Open-Source-Anwendung für Android und Android Auto zur Anzeige von aktuellen Tankstellenpreisen in der Schweiz. 

* **Keine Registrierung:** Für die Nutzung der App ist kein Benutzerkonto erforderlich.
* **Kein Tracking oder Analysen:** Wir setzen keinerlei Analyse-Tools, Tracking-Dienste (wie Google Analytics oder Firebase Analytics), Crash-Reporter (wie Crashlytics) oder Werbenetzwerke ein.
* **Keine Speicherung personenbezogener Daten:** Es werden von uns keine personenbezogenen Daten auf eigenen Servern erfasst, gespeichert oder verarbeitet.

---

## 2. Erhebung und Verarbeitung von Standortdaten (GPS)

Damit AutoFuely nahegelegene Tankstellen auf der Karte anzeigen, Entfernungen berechnen und die Fahrzeugposition auf dem Display anzeigen kann, benötigt die App Zugriff auf den Standort Ihres Geräts.

* **Verwendete Berechtigungen:**
  * `android.permission.ACCESS_FINE_LOCATION` (Genauer Standort)
  * `android.permission.ACCESS_COARSE_LOCATION` (Ungefährer Standort)
* **Zweck der Verarbeitung:** Aus Ihrem aktuellen Standort wird lokal auf Ihrem Gerät ein geografischer Suchbereich (Bounding Box, z. B. mit einem Umkreis von 3 bis 20 km) berechnet.
* **Übermittlung:** Dieser berechnete Suchbereich wird an die Server der TCS Benzinpreis-API übermittelt, um passende Tankstellen abzurufen.
* **Speicherung:** Ihre Standortdaten werden weder dauerhaft gespeichert noch zur Erstellung von Bewegungsprofilen genutzt. Die Standortberechtigung kann jederzeit in den Android-Systemeinstellungen widerrufen werden.

---

## 3. Drittanbieter & externe Netzwerkanfragen

Zur Bereitstellung der Funktionen stellt AutoFuely Verbindungen zu folgenden externen Diensten her:

### a) TCS Benzinpreis-API (Touring Club Schweiz / Google Cloud Functions)
* **Endpunkt:** `https://europe-west6-tcs-digitalbackend.cloudfunctions.net/`
* **Zweck:** Abruf von aktuellen Tankstellenstandorten, Kraftstoffpreisen und Aktualisierungszeitpunkten.
* **Übertragene Daten:** Geografische Koordinaten des Suchbereichs (Bounding Box), ausgewählter Treibstofftyp (`DIESEL`, `SP95`, `SP98`). Beim Abruf werden technisch bedingt Standard-Netzwerkdaten (wie Ihre IP-Adresse und HTTP-Header) an den Server übermittelt.

### b) TCS Marken-Logos (Content Delivery Network)
* **Endpunkt:** `https://benzin.tcs.ch/images/brands/icons/`
* **Zweck:** Herunterladen der Bilddateien von Tankstellen-Markenlogos (z. B. Agrola, Shell, BP, Avia) zur Anzeige auf der Karte und in der Liste.

### c) Externe Navigations-Apps (z. B. Google Maps, Waze)
* Wenn Sie in der App auf **"Navigation starten"** tippen, wird ein Android-Intent an eine auf Ihrem Gerät installierte Navigations-App gesendet. Dabei werden die Zielkoordinaten und die Adresse der gewählten Tankstelle übergeben. Es gelten die Datenschutzbestimmungen der jeweiligen Navigationsanwendung.

### d) GitHub
* Die App enthält einen Link zum Quellcode auf GitHub (`https://github.com/utigernils/AutoFuely`). Wenn Sie diesen Link antippen, wird die Webseite im Browser Ihres Geräts geöffnet.

---

## 4. Lokale Speicherung auf Ihrem Gerät

AutoFuely speichert minimale Anwendungseinstellungen lokal auf Ihrem Smartphone (`SharedPreferences`):

* Gewählte Kraftstoffart (z. B. Diesel, Bleifrei 95, Bleifrei 98)
* Bevorzugter Suchradius / Bounding-Box-Größe (z. B. 15 km)
* Sortierfunktion (nach Preis oder Entfernung)
* Filteroptionen (z. B. Ausblenden von Tankstellen ohne Preisangabe)

Diese Daten verbleiben **ausschließlich lokal** auf Ihrem Gerät. Sie werden weder an uns noch an Dritte übertragen und können jederzeit durch das Löschen der App-Daten oder die Deinstallation der App entfernt werden.

---

## 5. Ihre Rechte

Sie haben gemäß den geltenden Datenschutzgesetzen (DSGVO / Schweizer DSG) das Recht:
* Die erteilte Standortberechtigung jederzeit in den Android-Einstellungen zu widerrufen.
* Sämtliche lokal gespeicherte Einstellungen durch Löschen des App-Speichers oder Deinstallation der App zu entfernen.

---

## 6. Kontakt & Verantwortlicher

Bei Fragen zu dieser Datenschutzerklärung oder der App wenden Sie sich bitte an:

**Nils Utiger**  
GitHub: [github.com/utigernils/AutoFuely](https://github.com/utigernils/AutoFuely)
