package ch.so.agi.sodata;

import java.time.LocalDate;

public record ThemaPublikation(String id, String titel, String beschreibung, String modell, LocalDate publiziertAm, String[] tags, Amt datenherr) {

}
