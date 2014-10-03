package se.helsingborg.oppna.solarie.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-10-02 21:30
 */
public class Diarium implements Identitfiable {

  private static final long serialVersionUID = 1l;

  private Long identity;

  private String namn;
  private String jdbcURL;

  /** Datum då vi senast sökte i databasen efter ny information */
  private Long senasteSynkronisering;

  private Map<Diarienummer, Arende> ärendeByDiarienummer = new HashMap<>(10000);
  private Map<String, Anvandare> användareBySignatur = new HashMap<>(250);
  private Map<String, Enhet> enhetByKod = new HashMap<>(250);

  public String getNamn() {
    return namn;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }

  public String getJdbcURL() {
    return jdbcURL;
  }

  public void setJdbcURL(String jdbcURL) {
    this.jdbcURL = jdbcURL;
  }

  public Map<Diarienummer, Arende> getÄrendeByDiarienummer() {
    return ärendeByDiarienummer;
  }

  public void setÄrendeByDiarienummer(Map<Diarienummer, Arende> ärendeByDiarienummer) {
    this.ärendeByDiarienummer = ärendeByDiarienummer;
  }

  public Map<String, Anvandare> getAnvändareBySignatur() {
    return användareBySignatur;
  }

  public void setAnvändareBySignatur(Map<String, Anvandare> användareBySignatur) {
    this.användareBySignatur = användareBySignatur;
  }

  public Map<String, Enhet> getEnhetByKod() {
    return enhetByKod;
  }

  public void setEnhetByKod(Map<String, Enhet> enhetByKod) {
    this.enhetByKod = enhetByKod;
  }

  @Override
  public Long getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public Long getSenasteSynkronisering() {
    return senasteSynkronisering;
  }

  public void setSenasteSynkronisering(Long senasteSynkronisering) {
    this.senasteSynkronisering = senasteSynkronisering;
  }
}
