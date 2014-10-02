package se.helsingborg.oppna.solarie.prevalence.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kalle
 * @since 2014-09-16 22:41
 */
public class Root implements Serializable {

  private static final long serialVersionUID = 1l;

  private AtomicLong identityFactory = new AtomicLong(0);

  private Map<Long, Object> identifiables = new HashMap<>(10000);

  private Map<Diarienummer, Arende> ärendeByDiarienummer = new HashMap<>(10000);
  private Map<Long, Arende> ärendeByIdentity = new HashMap<>(10000);

  private Map<Long, Atgard> åtgärdByIdentity = new HashMap<>(10000);

  private Map<String, Anvandare> användareBySignatur = new HashMap<>(250);
  private Map<Long, Anvandare> användareByIdentity = new HashMap<>(250);

  private Map<String, Enhet> enhetByKod = new HashMap<>(250);
  private Map<Long, Enhet> enhetByIdentity = new HashMap<>(250);

  // getters & setters


  public Map<Long, Atgard> getÅtgärdByIdentity() {
    return åtgärdByIdentity;
  }

  public void setÅtgärdByIdentity(Map<Long, Atgard> åtgärdByIdentity) {
    this.åtgärdByIdentity = åtgärdByIdentity;
  }

  public Map<String, Enhet> getEnhetByKod() {
    return enhetByKod;
  }

  public void setEnhetByKod(Map<String, Enhet> enhetByKod) {
    this.enhetByKod = enhetByKod;
  }

  public Map<Long, Enhet> getEnhetByIdentity() {
    return enhetByIdentity;
  }

  public void setEnhetByIdentity(Map<Long, Enhet> enhetByIdentity) {
    this.enhetByIdentity = enhetByIdentity;
  }

  public Map<String, Anvandare> getAnvändareBySignatur() {
    return användareBySignatur;
  }

  public void setAnvändareBySignatur(Map<String, Anvandare> användareBySignatur) {
    this.användareBySignatur = användareBySignatur;
  }

  public Map<Long, Anvandare> getAnvändareByIdentity() {
    return användareByIdentity;
  }

  public void setAnvändareByIdentity(Map<Long, Anvandare> användareByIdentity) {
    this.användareByIdentity = användareByIdentity;
  }

  public AtomicLong getIdentityFactory() {
    return identityFactory;
  }

  public void setIdentityFactory(AtomicLong identityFactory) {
    this.identityFactory = identityFactory;
  }

  public Map<Diarienummer, Arende> getÄrendeByDiarienummer() {
    return ärendeByDiarienummer;
  }

  public void setÄrendeByDiarienummer(Map<Diarienummer, Arende> ärendeByDiarienummer) {
    this.ärendeByDiarienummer = ärendeByDiarienummer;
  }

  public Map<Long, Arende> getÄrendeByIdentity() {
    return ärendeByIdentity;
  }

  public void setÄrendeByIdentity(Map<Long, Arende> ärendeByIdentity) {
    this.ärendeByIdentity = ärendeByIdentity;
  }

  public Map<Long, Object> getIdentifiables() {
    return identifiables;
  }

  public void setIdentifiables(Map<Long, Object> identifiables) {
    this.identifiables = identifiables;
  }
}
