package se.helsingborg.oppna.solarie;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.io.File;
import java.io.IOException;

/**
 * Singleton service root.
 *
 * @author kalle
 * @since 2014-09-16 22:39
 */
public class Solarie {

  private static Solarie instance = new Solarie();

  public static Solarie getInstance() {
    return instance;
  }

  private Solarie() {
  }

  private File dataPath;

  private Prevayler<Root> prevayler;

  private Database database;

  public void open() throws Exception {

    if (dataPath == null) {
      dataPath = new File("data");
    }
    if (!dataPath.exists() && !dataPath.mkdirs()) {
      throw new IOException("Could not mkdirs dataPath " + dataPath.getAbsolutePath());
    }

    // prevayler

    File prevaylerPath = new File(dataPath, "prevayler");
    if (!prevaylerPath.exists() && !prevaylerPath.mkdirs()) {
      throw new IOException("Could not mkdirs prevaylerPath " + prevaylerPath.getAbsolutePath());
    }

    PrevaylerFactory<Root> prevaylerFactory = new PrevaylerFactory<>();
    prevaylerFactory.configurePrevalentSystem(new Root());
    prevaylerFactory.configurePrevalenceDirectory(prevaylerPath.getAbsolutePath());
    prevayler = prevaylerFactory.create();

    // index

    // database

    database = new Database();
    database.setup();

  }

  public void close() throws Exception {

    prevayler.close();

  }

  public Database getDatabase() {
    return database;
  }

  public Prevayler<Root> getPrevayler() {
    return prevayler;
  }
}
