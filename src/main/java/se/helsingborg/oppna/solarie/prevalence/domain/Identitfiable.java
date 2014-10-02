package se.helsingborg.oppna.solarie.prevalence.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-10-02 04:42
 */
public interface Identitfiable extends Serializable {

  public abstract Long getIdentity();
  public abstract void setIdentity(Long identity);


}
