package se.helsingborg.oppna.solarie.domain;

/**
 * @author kalle
 * @since 2014-10-03 00:45
 */
public interface IdentifiableVisitor<R> {

  public abstract R visit(Diarium diarium);
  public abstract R visit(Enhet enhet);
  public abstract R visit(Anvandare användare);

  public abstract R visit(Arende ärende);
  public abstract R visit(Atgard åtgärd);
  public abstract R visit(Dokument dokument);

}
