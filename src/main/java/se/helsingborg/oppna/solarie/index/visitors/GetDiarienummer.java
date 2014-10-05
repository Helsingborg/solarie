package se.helsingborg.oppna.solarie.index.visitors;

import se.helsingborg.oppna.solarie.domain.*;

/**
 * @author kalle
 * @since 2014-10-04 08:44
 */
public class GetDiarienummer extends IndexableVisitor<Diarienummer> {

  private static GetDiarienummer instance = new GetDiarienummer();

  public static GetDiarienummer getInstance() {
    return instance;
  }

  @Override
  public Diarienummer visit(Arende ärende) {
    return ärende.getDiarienummer();
  }

  @Override
  public Diarienummer visit(Atgard åtgärd) {
    return visit(åtgärd.getÄrende());
  }

  @Override
  public Diarienummer visit(Dokument dokument) {
    if (dokument.getÅtgärd() != null) {
      return visit(dokument.getÅtgärd());
    }
    return null;
  }


}
