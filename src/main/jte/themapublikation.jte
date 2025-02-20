@import java.util.List
@import ch.so.agi.sodata.ThemaPublikation

@param List<ThemaPublikation> themaPublikationList

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Daten - Kanton Solothurn</title>
</head>
<body>
    <h2>ThemaPublikation List</h2>
    <table border="1">
        <tr>
            <th>Name</th>
            <th>Age</th>
        </tr>
        @for (ThemaPublikation themaPublikation : themaPublikationList) 
            <tr>
                <td>${themaPublikation.id()}</td>
                <td>${themaPublikation.titel()}</td>
            </tr>
        @endfor
    </table>
</body>
</html>
