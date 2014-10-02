package se.helsingborg.oppna.solarie;

import se.helsingborg.oppna.solarie.prevalence.domain.Diarium;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;
import se.helsingborg.oppna.solarie.prevalence.transactions.diarium.CreateDiarium;

/**
 * @author kalle
 * @since 2014-10-01 22:27
 */
public class Main {

  public static void main(String[] args) throws Exception {

    Solarie.getInstance().open();
    try {

      for (Diarium diarium : Solarie.getInstance().getPrevayler().prevalentSystem().getDiariumByIdentity().values()) {
        new DiariumSyncronizer(diarium).synchronize();
        System.currentTimeMillis();
      }

    } finally {
      Solarie.getInstance().close();
    }

  }

}
