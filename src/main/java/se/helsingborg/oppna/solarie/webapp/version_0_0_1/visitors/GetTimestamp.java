package se.helsingborg.oppna.solarie.webapp.version_0_0_1.visitors;

import se.helsingborg.oppna.solarie.domain.*;

/**
 * @author kalle
 * @since 2014-10-06 21:13
 */
public class GetTimestamp extends IndexableVisitor<Long> {

  private static final GetTimestamp instance = new GetTimestamp();

  public static GetTimestamp getInstance() {
    return instance;
  }

  @Override
    public Long visit(Arende ärende) {
      return ärende.getRegistrerad();
    }

    @Override
    public Long visit(Atgard åtgärd) {
      return åtgärd.getRegistrerad();
    }

    @Override
    public Long visit(Dokument dokument) {
      throw new UnsupportedOperationException();
    }

}
