@import java.util.List
@import ch.so.agi.sodata.ThemaPublikation

@param ThemaPublikation themaPublikation

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=0" />
    <meta name="mobile-web-app-capable" content="yes" />

    <title>Datenportal • Kanton Solothurn: ${themaPublikation.titel()}</title>
    
    <script src="https://unpkg.com/htmx.org@2.0.4"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@materializecss/materialize@2.2.1/dist/css/materialize.min.css">
    <script src="https://cdn.jsdelivr.net/npm/@materializecss/materialize@2.2.1/dist/js/materialize.min.js"></script>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" />

    <link rel="stylesheet" href="/fonts.css">
    <link rel="stylesheet" href="/sodata.css">
</head>

<body>
    <div class="container">
        <div class="right-align" style="margin-top:10px;">
            <img src="/Logo.png" alt="Logo Kanton Solothurn">
        </div>

        <div class="left-align" style="margin-top:120px;">
            <h4>${themaPublikation.titel()}</h4>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Beschreibung</h6>
            <p>${themaPublikation.beschreibung()}</p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Schlagwörter</h6>
            <p>
                @for (var tag : themaPublikation.tags())
                    <span class="badge-file">${tag}</span>
                @endfor
            </p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Datum der letzten Publikation</h6>
            <p>${themaPublikation.publiziertAm().format(java.time.format.DateTimeFormatter.ofPattern("dd. MMMM yyyy", java.util.Locale.GERMAN))}</p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Datenmodell</h6>
            <p><a href='${"https://sos-ch-dk-2.exo.io/ch.so.agi.fubar1/" + themaPublikation.modell() + ".ili"}' class="red-link" target="_blank">${themaPublikation.modell()}</a></p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Periodizität</h6>
            <p>todo</p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Zeitraum von/bis</h6>
            <p>todo</p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Lizenz</h6>
            <p><a href='https://files.geo.so.ch/nutzungsbedingungen.html' class="red-link" target="_blank">https://files.geo.so.ch/nutzungsbedingungen.html</a></p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Kontakt (fachlich)</h6>
            <p><a href="${themaPublikation.datenherr().amtImWeb()}" class="red-link" target="_blank">${themaPublikation.datenherr().name()}</a>
            </p>
        </div>

        <div class="left-align" style="margin-top:40px;">
            <h6>Kontakt (technisch)</h6>
            <p><a href="https://agi.so.ch" class="red-link" target="_blank">Amt für Geoinformation</a>
            </p>
        </div>    
    </div>
</body>
</html>
