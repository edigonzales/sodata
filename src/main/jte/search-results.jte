@import java.util.List
@import java.time.LocalDate
@import ch.so.agi.sodata.ThemaPublikation

@param List<ThemaPublikation> themaPublikationList
@param String baseUrl = "https://sos-ch-dk-2.exo.io/ch.so.agi.fubar1/"


@for (var themaPublikation : themaPublikationList) 
    <tr>
        <td>${themaPublikation.titel()}</td>
        <td>${themaPublikation.publiziertAm().toString()}</td>
        <td><a class="black-link" href='${"/themapublikation/" + themaPublikation.id()}' target="_blank"><i class="material-icons">info_outline</i></a></td>
        <td>
            <div class="valign-wrapper">
                <a href='${baseUrl + themaPublikation.id() + "/" + themaPublikation.id() + ".parquet"}'><span class="badge-file">Parquet</span></a>
                &nbsp;&nbsp;
                <a href='${baseUrl + themaPublikation.id() + "/" + themaPublikation.id() + ".csv"}'><span class="badge-file">CSV</span></a>
                &nbsp;&nbsp;
                <a href='${baseUrl + themaPublikation.id() + "/" + themaPublikation.id() + ".xlsx"}'><span class="badge-file">Excel</span></a>
            </div>
        </td>
    </tr>
@endfor
