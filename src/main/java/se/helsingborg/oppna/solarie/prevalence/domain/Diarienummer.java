package se.helsingborg.oppna.solarie.prevalence.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-09-17 11:31
 */
public class Diarienummer implements Serializable {

  private static final long serialVersionUID = 1l;


  private String år;
  private String löpnummer;

  @Override
  public String toString() {
    return år + ":" + löpnummer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Diarienummer that = (Diarienummer) o;

    if (!löpnummer.equals(that.löpnummer)) return false;
    if (!år.equals(that.år)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = år.hashCode();
    result = 31 * result + löpnummer.hashCode();
    return result;
  }

  public String getÅr() {
    return år;
  }

  public void setÅr(String år) {
    this.år = år;
  }

  public String getLöpnummer() {
    return löpnummer;
  }

  public void setLöpnummer(String löpnummer) {
    this.löpnummer = löpnummer;
  }
}
