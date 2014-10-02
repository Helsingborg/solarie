package se.helsingborg.oppna.solarie.prevalence.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2014-10-02 04:16
 */
public class Enhet implements Identitfiable, DiariumBound {

  private static final long serialVersionUID = 1l;

  private Long identity;

  private Diarium diarium;

  private Long modifierad;

  private String kod;
  private String namn;
  private boolean aktiv;
  private Anvandare ansvarig;

  private List<Arende> ärenden = new ArrayList<>(1000);
  private List<Atgard> åtgärder = new ArrayList<>(1000);

  // getters and setters

  @Override
  public Diarium getDiarium() {
    return diarium;
  }

  @Override
  public void setDiarium(Diarium diarium) {
    this.diarium = diarium;
  }


  public List<Arende> getÄrenden() {
    return ärenden;
  }

  public void setÄrenden(List<Arende> ärenden) {
    this.ärenden = ärenden;
  }

  @Override
  public Long getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getKod() {
    return kod;
  }

  public void setKod(String kod) {
    this.kod = kod;
  }

  public String getNamn() {
    return namn;
  }

  public void setNamn(String namn) {
    this.namn = namn;
  }

  public boolean isAktiv() {
    return aktiv;
  }

  public void setAktiv(boolean aktiv) {
    this.aktiv = aktiv;
  }

  public Anvandare getAnsvarig() {
    return ansvarig;
  }

  public void setAnsvarig(Anvandare ansvarig) {
    this.ansvarig = ansvarig;
  }

  public Long getModifierad() {
    return modifierad;
  }

  public void setModifierad(Long modifierad) {
    this.modifierad = modifierad;
  }

  public List<Atgard> getÅtgärder() {
    return åtgärder;
  }

  public void setÅtgärder(List<Atgard> åtgärder) {
    this.åtgärder = åtgärder;
  }
}
