@import java.util.List
@import ch.so.agi.sodata.ThemaPublikation

@param List<ThemaPublikation> themaPublikationList

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Daten - Kanton Solothurn</title>
    
    <script src="https://unpkg.com/htmx.org@2.0.4"></script>
</head>
<body>
    <h2>ThemaPublikation List</h2>
    
<input class="form-control" type="search"
       name="search" placeholder="Begin Typing To Search Users..."
       hx-post="/search"
       hx-trigger="input changed delay:500ms, keyup[key=='Enter'], load"
       hx-target="#search-results">

    <table border="1">
        <thead>
        <tr>
            <th>Name</th>
            <th>Age</th>
        </tr>
        </thead>
        <tbody id="search-results">
            @template.search-results(themaPublikationList = themaPublikationList)
        </tbody>
    </table>
    

    
  <button hx-post="/clicked" hx-swap="outerHTML">
    Click Me
  </button>

</body>
</html>
