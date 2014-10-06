package se.helsingborg.oppna.solarie.webapp;

import se.helsingborg.oppna.solarie.Solarie;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author kalle
 * @since 2014-09-16 22:46
 */
public class ServiceStarter implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Solarie.getInstance().open();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    try {
      Solarie.getInstance().close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
