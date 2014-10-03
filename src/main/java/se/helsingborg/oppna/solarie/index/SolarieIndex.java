package se.helsingborg.oppna.solarie.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.helsingborg.oppna.solarie.Solarie;
import se.helsingborg.oppna.solarie.domain.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author kalle
 * @since 2014-10-03 00:38
 */
public class SolarieIndex {

  private static final Logger log = LoggerFactory.getLogger(SolarieIndex.class);

  private Directory directory;
  private IndexWriter indexWriter;
  private SearcherManager searcherManager;
  private Analyzer analyzer = SolarieAnalyzerFactory.factory();


  public void open(File path) throws Exception {

    directory = FSDirectory.open(path);
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);
    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    indexWriter = new IndexWriter(directory, indexWriterConfig);

    searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());


    Thread autocommitThread = new Thread(new Runnable() {
      @Override
      public void run() {

        // todo close on close

        while (true) {
          try {
            Thread.sleep(1000 * 60);
          } catch (InterruptedException e) {
            log.warn("Caught interruption in auto commit thread", e);
            break;
          }
          try {
            commit();
          } catch (Exception e) {
            log.error("Error auto committing", e);
          }
        }
      }
    });
    autocommitThread.setDaemon(true);
    autocommitThread.setName("Index auto committer");
    autocommitThread.start();

  }

  public void close() throws Exception {

    searcherManager.close();
    indexWriter.close();
    directory.close();
  }

  public void reconstruct() throws Exception {
    reconstruct(10);
  }

  public void commit() throws Exception {
    if (indexWriter.hasUncommittedChanges()) {
      log.info("Committing index...");
      indexWriter.commit();
      log.info("Committed!");
      searcherManager.maybeRefresh();
    }
  }

  public void reconstruct(int numberOfQueueUpdaterThreads) throws Exception {

    log.info("Reconstructing index using " + numberOfQueueUpdaterThreads + " threads.");

    final ConcurrentLinkedQueue<Indexable> queue = new ConcurrentLinkedQueue<>();
    queue.addAll(Solarie.getInstance().getPrevayler().prevalentSystem().getÄrendeByIdentity().values());
    queue.addAll(Solarie.getInstance().getPrevayler().prevalentSystem().getÅtgärdByIdentity().values());
    queue.addAll(Solarie.getInstance().getPrevayler().prevalentSystem().getDokumentByIdentity().values());

    Thread[] threads = new Thread[numberOfQueueUpdaterThreads];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          Indexable indexable;
          while ((indexable = queue.poll()) != null) {
            try {
              update(indexable);
            } catch (Exception e) {
              log.error("Exception while indexing " + indexable, e);
            }
          }
        }
      });
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join();
    }

    log.info("Index reconstructed!");


    commit();

  }

  public void updateAll(Collection<Indexable> indexables) throws Exception {
    // todo threads? does it make sense? probably only when creating the index the first time.
    // todo set up some timers and then compare to an executor service on concurrent queue
    for (Indexable indexable : indexables) {
      update(indexable);
    }
  }

  public void update(Indexable indexable) throws Exception {
    if(log.isDebugEnabled()) {
      log.debug("Updating " + indexable.getClass().getSimpleName() + " with id " + indexable.getIdentity());
    }
    indexWriter.updateDocument(new Term(SolarieFields.identity_indexed, String.valueOf(indexable.getIdentity())), documentFactory(indexable));
  }

  public Document documentFactory(Indexable indexable) {
    final Document document = new Document();
    document.add(new NumericDocValuesField(SolarieFields.identity_doc_value, indexable.getIdentity()));
    document.add(new LongField(SolarieFields.identity_indexed, indexable.getIdentity(), StoredField.Store.NO));
    document.add(new LongField(SolarieFields.diarium_identity, indexable.getDiarium().getIdentity(), StoredField.Store.NO));

    indexable.accept(new IndexableVisitor<Void>() {

      private void addDiarienummer(Diarienummer diarienummer) {
        document.add(new StringField(SolarieFields.diarienummer, diarienummer.getLöpnummer(), Field.Store.NO));
        document.add(new StringField(SolarieFields.diarienummer, diarienummer.getÅr(), Field.Store.NO));
        document.add(new StringField(SolarieFields.diarienummer, diarienummer.toString(), Field.Store.NO));
      }

      @Override
      public Void visit(Arende ärende) {
        document.add(new TextField(SolarieFields.ärende_mening, ärende.getMening(), Field.Store.NO));
        addTextFields(ärende.getMening());

        return null;
      }

      @Override
      public Void visit(Atgard åtgärd) {
        document.add(new TextField(SolarieFields.åtgärd_text, åtgärd.getText(), Field.Store.NO));
        addTextFields(åtgärd.getText());
        addTextFields(åtgärd.getÄrende().getMening());

        return null;
      }

      private void addTextFields(String text) {
        document.add(new TextField(SolarieFields.text, text, Field.Store.NO));
        document.add(new TextField(SolarieFields.text_singular, text, Field.Store.NO));
        document.add(new TextField(SolarieFields.text_särskivning, text, Field.Store.NO));
      }

      @Override
      public Void visit(Dokument dokument) {
        return null;
      }
    });

    return document;
  }

  public List<SearchResult> search(final Query query, final boolean score, final boolean explain) throws IOException {

    final Map<Integer, SearchResult> searchResultsByDocumentNumber = new HashMap<>(1000);
    final List<SearchResult> searchResults = new ArrayList<>(1000);

    IndexSearcher indexSearcher = getSearcherManager().acquire();
    try {

      indexSearcher.search(query, new Collector() {

        private Scorer scorer;
        private AtomicReaderContext context;
        private NumericDocValues identityDocValues;

        @Override
        public void setScorer(Scorer scorer) throws IOException {
          this.scorer = scorer;
        }

        @Override
        public void collect(int doc) throws IOException {
          if (identityDocValues == null) {
            identityDocValues = context.reader().getNumericDocValues(SolarieFields.identity_doc_value);
          }

          SearchResult searchResult = new SearchResult();

          Long identity = identityDocValues.get(doc);
          Indexable indexable = (Indexable) Solarie.getInstance().getPrevayler().prevalentSystem().getIdentifiables().get(identity);

          searchResult.setIndexable(indexable);

          if (score) {
            searchResult.setScore(scorer.score());
          }

          if (explain) {
            searchResultsByDocumentNumber.put(doc, searchResult);
          }

          searchResults.add(searchResult);

        }

        @Override
        public void setNextReader(AtomicReaderContext context) throws IOException {
          this.context = context;
        }

        @Override
        public boolean acceptsDocsOutOfOrder() {
          return true;
        }

      });

      // todo threaded? global thread pool then or what?
      for (Map.Entry<Integer, SearchResult> entry : searchResultsByDocumentNumber.entrySet()) {
        entry.getValue().setExplanation(indexSearcher.explain(query, entry.getKey()));
      }

      return searchResults;

    } finally {
      searcherManager.release(indexSearcher);
    }
  }

  public SearcherManager getSearcherManager() {
    return searcherManager;
  }
}
