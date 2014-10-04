package se.helsingborg.oppna.solarie.index.visitors;

import se.helsingborg.oppna.solarie.domain.*;

/**
 * @author kalle
 * @since 2014-10-04 08:44
 */
public class GetDiarium implements IndexableVisitor<Diarium> {

  private static GetDiarium instance = new GetDiarium();

  public static GetDiarium getInstance() {
    return instance;
  }

  @Override
  public Diarium visit(Arende ärende) {
    return ärende.getDiarium();
  }

  @Override
  public Diarium visit(Atgard åtgärd) {
    return visit(åtgärd.getÄrende());
  }

  @Override
  public Diarium visit(Dokument dokument) {
    return dokument.getDiarium();
  }
}
