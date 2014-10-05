package se.helsingborg.oppna.solarie.index.visitors;

import se.helsingborg.oppna.solarie.domain.Arende;
import se.helsingborg.oppna.solarie.domain.Atgard;
import se.helsingborg.oppna.solarie.domain.Dokument;
import se.helsingborg.oppna.solarie.domain.IndexableVisitor;

/**
 * @author kalle
 * @since 2014-10-05 13:27
 */
public class GetInkom extends IndexableVisitor<Long> {

  private static GetInkom instance = new GetInkom();

  public static GetInkom getInstance() {
    return instance;
  }

  private GetInkom() {
  }

  @Override
  public Long visit(Arende ärende) {
    return null;
  }

  @Override
  public Long visit(Atgard åtgärd) {
    return åtgärd.getInkom();
  }

  @Override
  public Long visit(Dokument dokument) {
    return null;
  }
}
