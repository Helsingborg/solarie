package se.helsingborg.oppna.solarie.prevalence.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-17 11:30
 */
public class Arende implements Identitfiable {

  private static final long serialVersionUID = 1l;

  /** Internal solarie identity */
  private Long identity;

  private Map<Short, Atgard> åtgärderByNummer = new HashMap<>(5);

  private Diarienummer diarienummer;

  private Enhet enhet;

  private Anvandare registrator;
  private Anvandare handläggare;
  private Anvandare senasteModifierare;
  private Anvandare ägare;

  // tidsstämplar av olika slag

  private Long modifierad;
  private Long ankommen;
  private Long registrerad;
  private Long beslutad;
  private Long planeratBeslut;
  private Long avslutad;
  private Long bekräftad;
  private Long makulerad;
  private Long bevakning;
  private Long exspirerar;

  // text etc

  private String mening;


  // getters & setters


  public Anvandare getÄgare() {
    return ägare;
  }

  public void setÄgare(Anvandare ägare) {
    this.ägare = ägare;
  }

  public Enhet getEnhet() {
    return enhet;
  }

  public void setEnhet(Enhet enhet) {
    this.enhet = enhet;
  }

  public Anvandare getRegistrator() {
    return registrator;
  }

  public void setRegistrator(Anvandare registrator) {
    this.registrator = registrator;
  }

  public Anvandare getHandläggare() {
    return handläggare;
  }

  public void setHandläggare(Anvandare handläggare) {
    this.handläggare = handläggare;
  }

  public Anvandare getSenasteModifierare() {
    return senasteModifierare;
  }

  public void setSenasteModifierare(Anvandare senasteModifierare) {
    this.senasteModifierare = senasteModifierare;
  }

  @Override
  public Long getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Long identity) {
    this.identity = identity;
  }


  public Diarienummer getDiarienummer() {
    return diarienummer;
  }

  public void setDiarienummer(Diarienummer diarienummer) {
    this.diarienummer = diarienummer;
  }

  public Long getModifierad() {
    return modifierad;
  }

  public void setModifierad(Long modifierad) {
    this.modifierad = modifierad;
  }

  public Long getAnkommen() {
    return ankommen;
  }

  public void setAnkommen(Long ankommen) {
    this.ankommen = ankommen;
  }

  public Long getRegistrerad() {
    return registrerad;
  }

  public void setRegistrerad(Long registrerad) {
    this.registrerad = registrerad;
  }

  public Long getBeslutad() {
    return beslutad;
  }

  public void setBeslutad(Long beslutad) {
    this.beslutad = beslutad;
  }

  public Long getPlaneratBeslut() {
    return planeratBeslut;
  }

  public void setPlaneratBeslut(Long planeratBeslut) {
    this.planeratBeslut = planeratBeslut;
  }

  public Long getAvslutad() {
    return avslutad;
  }

  public void setAvslutad(Long avslutad) {
    this.avslutad = avslutad;
  }

  public Long getBekräftad() {
    return bekräftad;
  }

  public void setBekräftad(Long bekräftad) {
    this.bekräftad = bekräftad;
  }

  public Long getMakulerad() {
    return makulerad;
  }

  public void setMakulerad(Long makulerad) {
    this.makulerad = makulerad;
  }

  public String getMening() {
    return mening;
  }

  public void setMening(String mening) {
    this.mening = mening;
  }

  public Long getBevakning() {
    return bevakning;
  }

  public void setBevakning(Long bevakning) {
    this.bevakning = bevakning;
  }

  public Long getExspirerar() {
    return exspirerar;
  }

  public void setExspirerar(Long exspirerar) {
    this.exspirerar = exspirerar;
  }

  public Map<Short, Atgard> getÅtgärderByNummer() {
    return åtgärderByNummer;
  }

  public void setÅtgärderByNummer(Map<Short, Atgard> åtgärderByNummer) {
    this.åtgärderByNummer = åtgärderByNummer;
  }
}
