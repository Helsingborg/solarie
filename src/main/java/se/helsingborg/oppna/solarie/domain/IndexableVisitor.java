package se.helsingborg.oppna.solarie.domain;

/**
 * @author kalle
 * @since 2014-10-03 00:45
 */
public abstract class IndexableVisitor<R> implements IdentifiableVisitor<R> {


  @Override
  public R visit(Diarium diarium) {
    throw new UnsupportedOperationException();
  }

  @Override
  public R visit(Enhet enhet) {
    throw new UnsupportedOperationException();
  }

  @Override
  public R visit(Anvandare användare) {
    throw new UnsupportedOperationException();
  }

  public abstract R visit(Arende ärende);
  public abstract R visit(Atgard åtgärd);
  public abstract R visit(Dokument dokument);

}
