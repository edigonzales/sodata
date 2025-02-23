package ch.so.agi.sodata;

import java.util.List;
import java.util.Optional;

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

    public Optional<ThemaPublikation> findById(String id) {
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
    id = :id           
; 
                """;
        return jdbcClient.sql(stmt)
                .param("id", id)
                .query(new ThemaPublikationRowMapper())
                .optional();
    }
    
    
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
    t.titel
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
    t.titel
; 
                """;

        return jdbcClient.sql(stmt)
                .query(new ThemaPublikationRowMapper())
                .list();
    }
}
