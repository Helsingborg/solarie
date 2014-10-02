package se.helsingborg.oppna.solarie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.helsingborg.oppna.solarie.prevalence.domain.*;
import se.helsingborg.oppna.solarie.prevalence.queries.GetAnvandareBySignatur;
import se.helsingborg.oppna.solarie.prevalence.queries.GetArendeByDiarienummer;
import se.helsingborg.oppna.solarie.prevalence.queries.GetAtgardByDiarienummerAndAtgardsnummer;
import se.helsingborg.oppna.solarie.prevalence.queries.GetEnhetByKod;
import se.helsingborg.oppna.solarie.prevalence.transactions.anvandare.CreateAnvandare;
import se.helsingborg.oppna.solarie.prevalence.transactions.arende.*;
import se.helsingborg.oppna.solarie.prevalence.transactions.atgard.*;
import se.helsingborg.oppna.solarie.prevalence.transactions.diarium.SetDiariumSenasteSynkronisering;
import se.helsingborg.oppna.solarie.prevalence.transactions.enhet.CreateEnhet;
import se.helsingborg.oppna.solarie.prevalence.transactions.enhet.SetEnhetAnsvarig;
import se.helsingborg.oppna.solarie.prevalence.transactions.enhet.SetEnhetModifierad;
import se.helsingborg.oppna.solarie.prevalence.transactions.enhet.SetEnhetNamn;
import se.helsingborg.oppna.solarie.util.Equals;

import java.sql.*;

/**
 * @author kalle
 * @since 2014-10-01 22:06
 */
public class DiariumSyncronizer {

  private static final Logger log = LoggerFactory.getLogger(DiariumSyncronizer.class);

  private Diarium diarium;

  public DiariumSyncronizer(Diarium diarium) {
    this.diarium = diarium;
  }



  public void synchronize() throws Exception {

    long started = System.currentTimeMillis();
    long since = diarium.getSenasteSynkronisering() == null ? 0 : diarium.getSenasteSynkronisering();

    // todo läs alltid data från en dag tillbaka
    // todo problemet är att mod_dat ibland är formatterat som dag
    // todo och då kan vi missa allt som skett senare samma dag som vi senast synkroniserade.

    Connection connection = DriverManager.getConnection(diarium.getJdbcURL());
    try {

      synchronizeÄrenden(since, connection);
      synchronizeÅtgärder(since, connection);
      synchronizeEnheter(since, connection);

    } finally {
      connection.close();
    }

    Solarie.getInstance().getPrevayler().execute(new SetDiariumSenasteSynkronisering(diarium, started));
  }

  private void synchronizeEnheter(long since, Connection connection) throws Exception {
    StringBuilder sql = new StringBuilder(4096)
        .append("SELECT ")
        .append(" enhet_kod")

        .append(",")
        .append(convertModifieringsDatumSQLFactory())
        .append(" AS mod_dat")

        .append(",ansv_usrsign")
        .append(",enhet_namn")
        .append(",aktiv")

        .append(" FROM enhet ")

        .append(" WHERE ")

        .append(convertModifieringsDatumSQLFactory())
        .append(" >= ?");
    PreparedStatement ps = connection.prepareStatement(sql.toString());
    ps.setLong(1, since);

    try {
      ResultSet rs = ps.executeQuery();
      try {

        while (rs.next()) {

          String enhetskod = rs.getString("enhet_kod");

          log.debug("Läser in enhet med kod " + enhetskod);

          Enhet enhet = getOrCreateEnhet(enhetskod);

          Long datumModifierad = getTimestamp(rs, "mod_dat");
          if (!Equals.equals(enhet.getModifierad(), datumModifierad)) {
            Solarie.getInstance().getPrevayler().execute(new SetEnhetModifierad(enhet, datumModifierad));
          }

          Anvandare ansvarig = getOrCreateAnvändare(rs.getString("ansv_usrsign"));
          if (!Equals.equals(ansvarig, enhet.getAnsvarig())) {
            Solarie.getInstance().getPrevayler().execute(new SetEnhetAnsvarig(enhet, ansvarig));
          }

          String namn = rs.getString("enhet_namn");
          if (!Equals.equals(namn, enhet.getNamn())) {
            Solarie.getInstance().getPrevayler().execute(new SetEnhetNamn(enhet, namn));
          }

        }

      } finally {
        rs.close();
      }
    } finally {
      ps.close();
    }
  }

  private void synchronizeÅtgärder(long since, Connection connection) throws Exception {
    // åtgärder

    StringBuilder sql = new StringBuilder(4096);
    sql.append("SELECT ")

        .append(" diarienr")

        .append(",")
        .append(convertModifieringsDatumSQLFactory())
        .append(" AS mod_dat")

        .append(",atgardsnr")
        .append(",atgard_text")
        .append(",enhet_kod")
        .append(",agare_usrsign")
        .append(",reg_dat")


        .append(" FROM atgard ")

        .append(" WHERE ")

        .append(convertModifieringsDatumSQLFactory())
        .append(" >= ?")
    ;
    PreparedStatement ps = connection.prepareStatement(sql.toString());
    ps.setTimestamp(1, new Timestamp(since));
    try {

      ResultSet rs = ps.executeQuery();
      try {
        while (rs.next()) {
          Diarienummer diarienummer = diarienummerFactory(rs.getString("diarienr"));
          short åtgärdsnummer = rs.getShort("atgardsnr");

          log.debug("Läser in åtgärd #" + åtgärdsnummer + " i ärende med diarienummer " + diarienummer.toString());


          // Ärende must be created in case it was added in database after we iterated them above.
          Arende ärende = Solarie.getInstance().getPrevayler().execute(new GetArendeByDiarienummer(diarium, diarienummer));
          if (ärende == null) {
            ärende = Solarie.getInstance().getPrevayler().execute(new CreateArende(diarium, diarienummer));
          }

          Atgard åtgärd = Solarie.getInstance().getPrevayler().execute(new GetAtgardByDiarienummerAndAtgardsnummer(diarium, diarienummer, åtgärdsnummer));
          if (åtgärd == null) {
            log.info("Skapar åtgärd #" + åtgärdsnummer + " i ärende med diarienummer " + diarienummer.toString());
            åtgärd = Solarie.getInstance().getPrevayler().execute(new CreateAtgard(ärende, åtgärdsnummer));
          }

          // update delta


          String text = rs.getString("atgard_text");
          if (!Equals.equals(text, åtgärd.getText())) {
            Solarie.getInstance().getPrevayler().execute(new SetAtgardText(åtgärd, text));
          }

          Enhet enhet = getOrCreateEnhet(rs.getString("enhet_kod"));
          if (!Equals.equals(enhet, åtgärd.getEnhet())) {
            Solarie.getInstance().getPrevayler().execute(new SetAtgardEnhet(åtgärd, enhet));
          }

          Anvandare ägare = getOrCreateAnvändare(rs.getString("agare_usrsign"));
          if (!Equals.equals(ägare, åtgärd.getÄgare())) {
            Solarie.getInstance().getPrevayler().execute(new SetAtgardAgare(åtgärd, ägare));
          }

          Long datumRegistrerad = getTimestamp(rs, "reg_dat");
          if (!Equals.equals(åtgärd.getRegistrerad(), datumRegistrerad)) {
            Solarie.getInstance().getPrevayler().execute(new SetAtgardRegistrerad(åtgärd, datumRegistrerad));
          }


          System.currentTimeMillis();


        }
      } finally {
        rs.close();
      }
    } finally {
      ps.close();
    }
  }

  private void synchronizeÄrenden(long since, Connection connection) throws Exception {
    StringBuilder sql = new StringBuilder(4096);
    sql.append("SELECT")
        .append(" diarienr")

        .append(",")
        .append(convertModifieringsDatumSQLFactory())
        .append("AS mod_dat")

        .append(",ankomst_dat")
        .append(",reg_dat")
        .append(",beslut_dat")
        .append(",plan_beslut_dat")
        .append(",avslut_dat")
        .append(",bevak_dat")
        .append(",exp_dat")
        .append(",bekraft_dat")
        .append(",makulerat_datum")

        .append(",enhet_kod")

        .append(",usrsign_handl")
        .append(",usrsign_reg")
        .append(",usrsign_senast")
        .append(",agare_usrsign")

        .append(",arende_mening")

        .append(" FROM arende ")

        .append(" WHERE ")

        .append(convertModifieringsDatumSQLFactory())
        .append(" >= ?")
    ;
    PreparedStatement ps = connection.prepareStatement(sql.toString());
    ps.setTimestamp(1, new Timestamp(since));

    try {

      ResultSet rs = ps.executeQuery();
      try {
        while (rs.next()) {
          Diarienummer diarienummer = diarienummerFactory(rs.getString("diarienr"));

          log.debug("Läser in ärende med diarienummer " + diarienummer.toString());

          Arende ärende = Solarie.getInstance().getPrevayler().execute(new GetArendeByDiarienummer(diarium, diarienummer));
          if (ärende == null) {
            log.info("Skapar ärende med diarienummer " + diarienummer.toString());
            ärende = Solarie.getInstance().getPrevayler().execute(new CreateArende(diarium, diarienummer));
          }

          // update delta

          // datum

          Long datumModifierad = getTimestamp(rs, "mod_dat");
          if (!Equals.equals(ärende.getModifierad(), datumModifierad)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeModifierad(ärende, datumModifierad));
          }

          Long datumAnkommen = getTimestamp(rs, "ankomst_dat");
          if (!Equals.equals(ärende.getAnkommen(), datumAnkommen)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeAnkommen(ärende, datumAnkommen));
          }

          Long datumRegistrerad = getTimestamp(rs, "reg_dat");
          if (!Equals.equals(ärende.getRegistrerad(), datumRegistrerad)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeRegistrerad(ärende, datumRegistrerad));
          }

          Long datumBeslutad = getTimestamp(rs, "beslut_dat");
          if (!Equals.equals(ärende.getBeslutad(), datumBeslutad)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeBeslutad(ärende, datumBeslutad));
          }

          Long datumPlaneratBeslut = getTimestamp(rs, "plan_beslut_dat");
          if (!Equals.equals(ärende.getPlaneratBeslut(), datumPlaneratBeslut)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendePlaneratBeslut(ärende, datumPlaneratBeslut));
          }

          Long datumAvslutad = getTimestamp(rs, "avslut_dat");
          if (!Equals.equals(ärende.getAvslutad(), datumAvslutad)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeAvslutad(ärende, datumAvslutad));
          }

          Long datumBevakning = getTimestamp(rs, "bevak_dat");
          if (!Equals.equals(ärende.getBevakning(), datumBevakning)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeBevakning(ärende, datumBevakning));
          }

          Long datumExspirerar = getTimestamp(rs, "exp_dat");
          if (!Equals.equals(ärende.getExspirerar(), datumExspirerar)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeMakulerad(ärende, datumExspirerar));
          }

          Long datumBekräftad = getTimestamp(rs, "bekraft_dat");
          if (!Equals.equals(ärende.getBekräftad(), datumBekräftad)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeMakulerad(ärende, datumBekräftad));
          }

          Long datumMakulerad = getTimestamp(rs, "makulerat_datum");
          if (!Equals.equals(ärende.getMakulerad(), datumMakulerad)) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeMakulerad(ärende, datumMakulerad));
          }


          // text

          if (!Equals.equals(ärende.getMening(), rs.getString("arende_mening"))) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeMening(ärende, rs.getString("arende_mening")));
          }

          // grupper och kategorier

          Enhet enhet = getOrCreateEnhet(rs.getString("enhet_kod"));
          if (!Equals.equals(enhet, ärende.getEnhet())) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeEnhet(ärende, enhet));
          }


          // användare

          Anvandare handläggare = getOrCreateAnvändare(rs.getString("usrsign_handl"));
          if (!Equals.equals(handläggare, ärende.getHandläggare())) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeHandlaggare(ärende, handläggare));
          }

          Anvandare registrator = getOrCreateAnvändare(rs.getString("usrsign_reg"));
          if (!Equals.equals(registrator, ärende.getRegistrator())) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeRegistrator(ärende, registrator));
          }

          Anvandare senasteModifierare = getOrCreateAnvändare(rs.getString("usrsign_reg"));
          if (!Equals.equals(senasteModifierare, ärende.getSenasteModifierare())) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeSenasteModifierare(ärende, senasteModifierare));
          }

          Anvandare ägare = getOrCreateAnvändare(rs.getString("agare_usrsign"));
          if (!Equals.equals(ägare, ärende.getÄgare())) {
            Solarie.getInstance().getPrevayler().execute(new SetArendeAgare(ärende, ägare));
          }


        }
      } finally {
        rs.close();
      }
    } finally {
      ps.close();
    }
  }


  public static Diarienummer diarienummerFactory(String columnValue) {
    Diarienummer diarienummer = new Diarienummer();
    diarienummer.setÅr(columnValue.substring(0, 4));
    diarienummer.setLöpnummer(columnValue.substring(4));
    return diarienummer;
  }

  public Anvandare getOrCreateAnvändare(String signatur) throws Exception {
    if (signatur == null) {
      return null;
    }
    Anvandare användare = Solarie.getInstance().getPrevayler().execute(new GetAnvandareBySignatur(diarium, signatur));
    if (användare != null) {
      return användare;
    }

    log.info("Skapar användare med signatur " + signatur);

    användare = Solarie.getInstance().getPrevayler().execute(new CreateAnvandare(diarium, signatur));

    return användare;
  }

  public Enhet getOrCreateEnhet(String kod) throws Exception {
    if (kod == null) {
      return null;
    }
    Enhet enhet = Solarie.getInstance().getPrevayler().execute(new GetEnhetByKod(diarium, kod));
    if (enhet != null) {
      return enhet;
    }

    log.info("Skapar enhet med kod " + kod);
    enhet = Solarie.getInstance().getPrevayler().execute(new CreateEnhet(diarium, kod));

    return enhet;
  }


  private Long getTimestamp(ResultSet rs, String column) throws SQLException {
    Timestamp timestamp = rs.getTimestamp(column);
    return timestamp == null ? null : timestamp.getTime();
  }

  private String convertModifieringsDatumSQLFactory() {
    // terrible solution to handle funky formatted varchar mod_date

    return new StringBuilder(1024)
        .append("CONVERT(datetime, ")
            // yyyy-MM-dd HH:mm:ss
        .append("CASE ")
        .append("  WHEN LEN(mod_dat) >= 20 THEN ")
        .append("( SUBSTRING(mod_dat, 1, 4) + '-' + SUBSTRING(mod_dat, 6, 2) + '-' + SUBSTRING(mod_dat, 9, 2)")
        .append(" + ' ' + SUBSTRING(mod_dat, 12, 2) + ':' + SUBSTRING(mod_dat, 15, 2) + ':' + SUBSTRING(mod_dat, 18, 2) + '.000' )")
            // yyyy-MM-dd HH:mm
        .append("  WHEN LEN(mod_dat) >= 17 THEN ")
        .append("( SUBSTRING(mod_dat, 1, 4) + '-' + SUBSTRING(mod_dat, 6, 2) + '-' + SUBSTRING(mod_dat, 9, 2)")
        .append(" + ' ' + SUBSTRING(mod_dat, 12, 2) + ':' + SUBSTRING(mod_dat, 15, 2) + '00:.000')")
            // yyyy-MM-dd HH
        .append("  WHEN LEN(mod_dat) >= 14 THEN ")
        .append("( SUBSTRING(mod_dat, 1, 4) + '-' + SUBSTRING(mod_dat, 6, 2) + '-' + SUBSTRING(mod_dat, 9, 2)")
        .append(" + ' ' + SUBSTRING(mod_dat, 12, 2) + ':00:00.000' )")
            // yyyy-MM-dd
        .append("  ELSE ")
        .append("( SUBSTRING(mod_dat, 1, 4) + '-' + SUBSTRING(mod_dat, 6, 2) + '-' + SUBSTRING(mod_dat, 9, 2) + ' 00:00:00.000' )")
        .append("END ")
        .append(", 120)")
        .toString();
  }
}
