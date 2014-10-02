package se.helsingborg.oppna.solarie.prevalence.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-10-01 22:48
 */
public class Dokument implements Identitfiable {

  private static final long serialVersionUID = 1l;


  /** Internal solarie identity */
  private Long identity;

  /** Possible null. */
  private Atgard åtgärd;

  @Override
  public Long getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public Atgard getÅtgärd() {
    return åtgärd;
  }

  public void setÅtgärd(Atgard åtgärd) {
    this.åtgärd = åtgärd;
  }
}
