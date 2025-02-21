package ch.so.agi.sodata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

public class ThemaPublikationRowMapper implements RowMapper<ThemaPublikation> {

    @Override
    public ThemaPublikation mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return new ThemaPublikation(
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
                );        
    }
}
