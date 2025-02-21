@import java.util.List
@import ch.so.agi.sodata.ThemaPublikation

@param List<ThemaPublikation> themaPublikationList

@for (var themaPublikation : themaPublikationList) 
    <tr>
        <td>${themaPublikation.id()}</td>
        <td>${themaPublikation.titel()}</td>
    </tr>
@endfor
