package se.helsingborg.oppna.solarie.domain;

import java.util.List;

/**
 * @author kalle
 * @since 2014-10-01 22:48
 */
public class Atgard implements Identitfiable, Indexable {

  private static final long serialVersionUID = 1l;

  /** Internal solarie identity */
  private Long identity;

  private Arende ärende;

  /** Unikt per ärende */
  private Short nummer;

  private Long registrerad;

  private Long inkom;
  private Long utgick;

  private String text;

  private Enhet enhet;

  private Anvandare ägare;

  private List<Dokument> dokument;


  @Override
  public <R> R accept(IdentifiableVisitor<R> visitor) {
    return visitor.visit(this);
  }


  // getters & setters


  @Override
  public Long getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public Arende getÄrende() {
    return ärende;
  }

  public void setÄrende(Arende ärende) {
    this.ärende = ärende;
  }

  public Short getNummer() {
    return nummer;
  }

  public void setNummer(Short nummer) {
    this.nummer = nummer;
  }

  public List<Dokument> getDokument() {
    return dokument;
  }

  public void setDokument(List<Dokument> dokument) {
    this.dokument = dokument;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Enhet getEnhet() {
    return enhet;
  }

  public void setEnhet(Enhet enhet) {
    this.enhet = enhet;
  }

  public Anvandare getÄgare() {
    return ägare;
  }

  public void setÄgare(Anvandare ägare) {
    this.ägare = ägare;
  }

  public Long getRegistrerad() {
    return registrerad;
  }

  public void setRegistrerad(Long registrerad) {
    this.registrerad = registrerad;
  }

  public Long getInkom() {
    return inkom;
  }

  public void setInkom(Long inkom) {
    this.inkom = inkom;
  }

  public Long getUtgick() {
    return utgick;
  }

  public void setUtgick(Long utgick) {
    this.utgick = utgick;
  }
}
