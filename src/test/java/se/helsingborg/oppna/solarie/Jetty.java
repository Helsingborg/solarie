package se.helsingborg.oppna.solarie;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;


/**
 * @author kalle
 * @since 2014-10-01 22:27
 */
public class Jetty {

  public static void main(String[] args) throws Exception {
    Jetty jetty = new Jetty();
    try {
      jetty.start();
      while (true) {
        Thread.sleep(1000);
      }
    } finally {
      jetty.stop();
    }
  }

  private Server server;

  private int port = 8080;

  public void start() throws  Exception {
    System.setProperty("DEBUG", "true");

    server = new Server(port);

    WebAppContext webappcontext = new WebAppContext();
    webappcontext.setContextPath("/");

    File warPath = new File(new File("."), "src/main/webapp");
    webappcontext.setWar(warPath.getAbsolutePath());

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { webappcontext, new DefaultHandler() });
    server.setHandler(handlers);

    server.start();

  }

  public void stop() throws Exception{
    server.stop();
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}

