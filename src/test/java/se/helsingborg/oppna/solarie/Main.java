package se.helsingborg.oppna.solarie;

import se.helsingborg.oppna.solarie.domain.Diarium;

/**
 * @author kalle
 * @since 2014-10-01 22:27
 */
public class Main {

  public static void main(String[] args) throws Exception {

    Solarie.getInstance().open();
    try {

      boolean running = true;
      while(running) {

        Thread.sleep(10000);

      }



    } finally {
      Solarie.getInstance().close();
    }

  }

}
