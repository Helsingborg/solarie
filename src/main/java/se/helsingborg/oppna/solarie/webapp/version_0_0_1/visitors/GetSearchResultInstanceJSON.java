package se.helsingborg.oppna.solarie.webapp.version_0_0_1.visitors;

import org.json.JSONException;
import se.helsingborg.oppna.solarie.domain.*;
import se.helsingborg.oppna.solarie.index.visitors.GetDiarienummer;
import se.helsingborg.oppna.solarie.index.visitors.GetDiarium;
import se.helsingborg.oppna.solarie.util.JSONObject;

import java.util.LinkedHashMap;

/**
 * @author kalle
 * @since 2014-10-06 20:33
 */
public class GetSearchResultInstanceJSON implements IdentifiableVisitor<JSONObject> {

  private static final GetSearchResultInstanceJSON instance = new GetSearchResultInstanceJSON();

  public static GetSearchResultInstanceJSON getInstance() {
    return instance;
  }

  @Override
  public JSONObject visit(Diarium diarium) {
    try {
      JSONObject json = new JSONObject(new LinkedHashMap(20));
      json.put("identity", diarium.getIdentity());
      json.put("namn", diarium.getNamn());
      return json;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }

  }

  @Override
  public JSONObject visit(Enhet enhet) {
    try {
      JSONObject json = new JSONObject(new LinkedHashMap(20));
      json.put("diarium", enhet.getDiarium().getIdentity());
      json.put("identity", enhet.getIdentity());
      json.put("namn", enhet.getNamn());
      json.put("kod", enhet.getKod());
      return json;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }

  @Override
  public JSONObject visit(Anvandare användare) {
    try {
      JSONObject json = new JSONObject(new LinkedHashMap(20));
      json.put("diarium", användare.getDiarium().getIdentity());
      json.put("identity", användare.getIdentity());
      json.put("namn", användare.getNamn());
      json.put("signatur", användare.getSignatur());
      return json;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }

  private JSONObject factory(Indexable indexable) throws JSONException {
    JSONObject json = new JSONObject(new LinkedHashMap(20));
    json.put("identity", indexable.getIdentity());

    json.put("diarium", indexable.accept(GetDiarium.getInstance()).getIdentity());
    json.put("diarienummer", indexable.accept(GetDiarienummer.getInstance()).toString());

    return json;
  }


  @Override
  public JSONObject visit(Arende ärende) {
    try {
      JSONObject json = factory(ärende);

      json.put("mening", ärende.getMening());

      if (ärende.getEnhet() != null) {
        json.put("enhet", ärende.getEnhet().getIdentity());
      }

      return json;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }


  @Override
  public JSONObject visit(Atgard åtgärd) {
    try {
      JSONObject json = factory(åtgärd);

      json.put("ärende", åtgärd.getÄrende().getIdentity());

      if (åtgärd.getEnhet() != null) {
        json.put("enhet", åtgärd.getEnhet().getIdentity());
      }

      json.put("text", åtgärd.getText());

      json.put("inkom", åtgärd.getInkom());
      json.put("utgick", åtgärd.getUtgick());

      return json;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }

  @Override
  public JSONObject visit(Dokument dokument) {
    try {
      JSONObject json = factory(dokument);

      if (dokument.getÅtgärd() != null) {
        json.put("åtgärd", dokument.getÅtgärd().getIdentity());
        if (dokument.getÅtgärd().getEnhet() != null) {
          json.put("enhet", dokument.getÅtgärd().getEnhet().getIdentity());
        }
      }

      return json;
    } catch (JSONException je) {
      throw new RuntimeException(je);
    }
  }
}
