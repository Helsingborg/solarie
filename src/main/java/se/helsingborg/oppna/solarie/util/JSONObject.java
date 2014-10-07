package se.helsingborg.oppna.solarie.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.Map;

/**
 * @author kalle
 * @since 2014-10-03 05:52
 */
public class JSONObject extends org.json.JSONObject {

  public JSONObject() {
  }

  public JSONObject(org.json.JSONObject jsonObject, String[] strings) throws JSONException {
    super(jsonObject, strings);
  }

  public JSONObject(JSONTokener jsonTokener) throws JSONException {
    super(jsonTokener);
  }

  public JSONObject(Map map) {
    super(map);
  }

  public JSONObject(Map map, boolean b) {
    super(map, b);
  }

  public JSONObject(Object o) {
    super(o);
  }

  public JSONObject(Object o, boolean b) {
    super(o, b);
  }

  public JSONObject(Object o, String[] strings) {
    super(o, strings);
  }

  public JSONObject(String s) throws JSONException {
    super(s);
  }


  public Object get(String s, Object defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.get(s);
  }


  public boolean getBoolean(String s, boolean defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getBoolean(s);
  }

  public double getDouble(String s, double defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getDouble(s);
  }

  public int getInt(String s, int defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getInt(s);
  }


  public JSONArray getJSONArray(String s, JSONArray defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getJSONArray(s);
  }

  public org.json.JSONObject getJSONObject(String s, org.json.JSONObject defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getJSONObject(s);
  }

  public long getLong(String s, long defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getLong(s);
  }

  public String getString(String s, String defaultValue) throws JSONException {
    if (!has(s)) {
      return defaultValue;
    }
    return super.getString(s);
  }
}
