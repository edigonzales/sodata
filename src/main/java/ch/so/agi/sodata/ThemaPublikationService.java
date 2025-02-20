package ch.so.agi.sodata;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class ThemaPublikationService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final JdbcClient jdbcClient;

    public ThemaPublikationService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }
    
    public List<ThemaPublikation> findAll() {
        String stmt = """
SELECT 
    t.id, t.titel, t.beschreibung, t.modell, t.publiziertam, t.tags,
    a.aname, a.abkuerzung, a.abteilung, a.amtimweb, a.zeile1, a.zeile2,
    a.strasse, a.hausnr, a.plz, a.ort 
FROM
    themapublikation_themapublikation AS t
    LEFT JOIN amt_ AS a
    ON a.thempblktn_thmpblktion_datenherr = t.T_Id
ORDER BY 
    t.id
; 
                """;

        return jdbcClient.sql(stmt)
                .query((rs, rowNum) -> new ThemaPublikation(
                      rs.getString("id"),
                      rs.getString("titel"),
                      rs.getString("beschreibung"),
                      rs.getString("modell"),
                      rs.getString("publiziertam") != null ? LocalDate.parse(rs.getString("publiziertam")) : null,
                      rs.getString("tags") != null ? rs.getString("tags").split(",") : new String[0],
                      new Amt(
                              rs.getString("aname"),
                              rs.getString("abkuerzung"),
                              rs.getString("abteilung"),
                              rs.getString("amtimweb"),
                              rs.getString("zeile1"),
                              rs.getString("zeile2"),
                              rs.getString("strasse"),
                              rs.getString("hausnr"),
                              rs.getString("plz"),
                              rs.getString("ort")
                              )
                        ))
                .list();
    }
}
