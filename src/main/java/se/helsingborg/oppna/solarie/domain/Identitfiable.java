package se.helsingborg.oppna.solarie.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-10-02 04:42
 */
public interface Identitfiable extends Serializable {

  public abstract <R> R accept(IdentifiableVisitor<R> visitor);

  public abstract Long getIdentity();
  public abstract void setIdentity(Long identity);


}
