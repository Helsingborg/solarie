package se.helsingborg.oppna.solarie;

import java.sql.Connection;

/**
 * @author kalle
 * @since 2014-10-01 22:27
 */
public class Main {

  public static void main(String[] args) throws Exception {

    Solarie.getInstance().open();
    try {


      Solarie.getInstance().getDatabase().importNewEntriesSince(0l);

    } finally {
      Solarie.getInstance().close();
    }

  }

}
