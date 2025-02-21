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
    
    // TODO row mapper für ThemaPublikation
    
    public List<ThemaPublikation> findByFilter(String filter) {        
        String stmt = """
SELECT 
    t.id, t.titel, t.beschreibung, t.modell, t.publiziertam, t.tags,
    a.aname, a.abkuerzung, a.abteilung, a.amtimweb, a.zeile1, a.zeile2,
    a.strasse, a.hausnr, a.plz, a.ort 
FROM
    themapublikation_themapublikation AS t
    LEFT JOIN amt_ AS a
    ON a.thempblktn_thmpblktion_datenherr = t.T_Id
WHERE 
    lower(t.titel) LIKE :filter           
ORDER BY 
    t.id
; 
                """;
        return jdbcClient.sql(stmt)
                .param("filter", "%"+filter+"%")
                .query(new ThemaPublikationRowMapper())
                .list();        
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
                .query(new ThemaPublikationRowMapper())
                .list();
    }
}
