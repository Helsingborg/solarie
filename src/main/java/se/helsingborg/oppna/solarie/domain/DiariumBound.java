package se.helsingborg.oppna.solarie.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-10-02 22:13
 */
public interface DiariumBound extends Serializable {

  public abstract void setDiarium(Diarium diarium);
  public abstract Diarium getDiarium();

}
