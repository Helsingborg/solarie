package se.helsingborg.oppna.solarie.prevalence.transactions.atgard;

import org.prevayler.Transaction;
import se.helsingborg.oppna.solarie.prevalence.domain.Arende;
import se.helsingborg.oppna.solarie.prevalence.domain.Atgard;
import se.helsingborg.oppna.solarie.prevalence.domain.Root;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-10-02 02:15
 */
public class SetAtgardText implements Transaction<Root> {

  private static final long serialVersionUID = 1l;

  private Long åtgärdIdentity;
  private String text;

  public SetAtgardText() {
  }

  public SetAtgardText(Atgard åtgärd, String text) {
    this.åtgärdIdentity = åtgärd.getIdentity();
    this.text = text;
  }

  public SetAtgardText(Long åtgärdIdentity, String text) {
    this.åtgärdIdentity = åtgärdIdentity;
    this.text = text;
  }

  @Override
  public void executeOn(Root root, Date executionTime) {
    root.getÅtgärdByIdentity().get(åtgärdIdentity).setText(text);
  }

  public Long getÅtgärdIdentity() {
    return åtgärdIdentity;
  }

  public void setÅtgärdIdentity(Long åtgärdIdentity) {
    this.åtgärdIdentity = åtgärdIdentity;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
