package se.helsingborg.oppna.solarie;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.helsingborg.oppna.solarie.domain.Diarium;
import se.helsingborg.oppna.solarie.domain.Root;
import se.helsingborg.oppna.solarie.index.SolarieIndex;
import se.helsingborg.oppna.solarie.prevalence.transactions.IdentityFactory;
import se.helsingborg.oppna.solarie.prevalence.transactions.diarium.CreateDiarium;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * Singleton service root.
 *
 * @author kalle
 * @since 2014-09-16 22:39
 */
public class Solarie {

  private static Logger log = LoggerFactory.getLogger(Solarie.class);

  private static Solarie instance = new Solarie();

  public static Solarie getInstance() {
    return instance;
  }

  private Solarie() {
  }

  private File dataPath;

  private Prevayler<Root> prevayler;
  private SolarieIndex index;

  public void open() throws Exception {

    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

    if (dataPath == null) {
      dataPath = new File("data");
    }
    if (!dataPath.exists() && !dataPath.mkdirs()) {
      throw new IOException("Could not mkdirs dataPath " + dataPath.getAbsolutePath());
    }

    // prevayler

    File prevaylerPath = new File(dataPath, "prevayler");
    if (!prevaylerPath.exists() && !prevaylerPath.mkdirs()) {
      throw new IOException("Could not mkdirs prevalence data path " + prevaylerPath.getAbsolutePath());
    }

    PrevaylerFactory<Root> prevaylerFactory = new PrevaylerFactory<>();
    prevaylerFactory.configurePrevalentSystem(new Root());
    prevaylerFactory.configurePrevalenceDirectory(prevaylerPath.getAbsolutePath());
    prevayler = prevaylerFactory.create();

    // index

    boolean doReconstructIndex = false;
    index = new SolarieIndex();
    File indexPath = new File(dataPath, "index");
    if (!indexPath.exists()) {
      doReconstructIndex = true;
    }
    index.open(indexPath);


    // initialize from resource if no diarier in root
    if (prevayler.prevalentSystem().getDiariumByIdentity().isEmpty()) {

      log.info("Creating initial diarier from default JSON.");

      JSONArray diarierJSON;
      String localJSONResource = "/diarier." + InetAddress.getLocalHost().getHostName() + ".json";
      try {
        diarierJSON = new JSONArray(new JSONTokener(new InputStreamReader(getClass().getResourceAsStream(localJSONResource), "UTF8")));
      } catch (Exception e) {
        log.warn("No resource file named " + localJSONResource + ", defaulting to /diarier.json", e);
        diarierJSON = new JSONArray(new JSONTokener(new InputStreamReader(getClass().getResourceAsStream("/diarier.json"), "UTF8")));
      }
      for (int i = 0; i < diarierJSON.length(); i++) {
        JSONObject diariumJSON = diarierJSON.getJSONObject(i);
        CreateDiarium createDiarium = new CreateDiarium();
        createDiarium.setIdentity(prevayler.execute(new IdentityFactory()));
        createDiarium.setNamn(diariumJSON.getString("namn"));
        createDiarium.setJdbcURL(diariumJSON.getString("jdbcURL"));
        Diarium diarium = prevayler.execute(createDiarium);
        log.info("Created diarium with identity " + diarium.getIdentity() + " from " + diariumJSON.toString());

      }

    } else {
      doReconstructIndex = true;
    }

    if (doReconstructIndex) {
      index.reconstruct();
    }


  }

  public File getDataPath() {
    return dataPath;
  }

  public void setDataPath(File dataPath) {
    this.dataPath = dataPath;
  }

  public void close() throws Exception {
    index.close();
    prevayler.close();
  }

  public Prevayler<Root> getPrevayler() {
    return prevayler;
  }

  public SolarieIndex getIndex() {
    return index;
  }
}
