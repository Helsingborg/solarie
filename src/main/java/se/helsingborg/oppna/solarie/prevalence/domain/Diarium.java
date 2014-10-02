package se.helsingborg.oppna.solarie.prevalence.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-10-02 21:30
 */
public class Diarium implements Serializable {

  private static final long serialVersionUID = 1l;

  private Map<Diarienummer, Arende> ärendeByDiarienummer = new HashMap<>(10000);
  private Map<String, Anvandare> användareBySignatur = new HashMap<>(250);
  private Map<String, Enhet> enhetByKod = new HashMap<>(250);


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
}
