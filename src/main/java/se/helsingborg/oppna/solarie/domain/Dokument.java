package se.helsingborg.oppna.solarie.domain;

/**
 * @author kalle
 * @since 2014-10-01 22:48
 */
public class Dokument implements Identitfiable, DiariumBound, Indexable {

  private static final long serialVersionUID = 1l;

  @Override
  public <R> R accept(IdentifiableVisitor<R> visitor) {
    return visitor.visit(this);
  }

  /** Internal solarie identity */
  private Long identity;

  private Diarium diarium;

  /** Possible null. */
  private Atgard åtgärd;


  // getters & setters

  @Override
  public Long getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  @Override
  public Diarium getDiarium() {
    return diarium;
  }

  @Override
  public void setDiarium(Diarium diarium) {
    this.diarium = diarium;
  }


  public Atgard getÅtgärd() {
    return åtgärd;
  }

  public void setÅtgärd(Atgard åtgärd) {
    this.åtgärd = åtgärd;
  }
}
