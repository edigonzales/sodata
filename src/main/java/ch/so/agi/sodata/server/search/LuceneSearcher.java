package ch.so.agi.sodata.server.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.so.agi.sodata.server.AppConfig;
import ch.so.agi.sodata.shared.Dataset;

@Repository("LuceneSearcher")
public class LuceneSearcher {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppConfig config;

    Directory memoryIndex;
    StandardAnalyzer analyzer;
    private QueryParser queryParser;

    @PostConstruct
    public void init() throws IOException {
        log.info("Building index in memory...");
        
        Path tempDirWithPrefix = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "sodata_idx");
        log.info("Index folder: " + tempDirWithPrefix);
        
        memoryIndex = new MMapDirectory(tempDirWithPrefix);
        analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
        
        List<Dataset> datasets = config.getDatasets();
        for (Dataset dataset : datasets) {
            Document document = new Document();
            document.add(new TextField("id", dataset.getId(), Store.YES));
            document.add(new TextField("title", dataset.getTitle(), Store.YES));
            document.add(new TextField("shortdescription", dataset.getShortDescription(), Store.YES));
            document.add(new TextField("keywords", dataset.getKeywords(), Store.YES));
            writter.addDocument(document);
        }
        writter.close();
    }

    @PreDestroy
    private void close() {
        try {
            memoryIndex.close();
            log.info("Lucene Index closed");
        } catch (IOException e) {
            log.warn("Issue closing Lucene Index: " + e.getMessage());
        }
    }
    
    /**
     * Search Lucene Index for records matching querystring
     * @param querystring - human written query string from e.g. a search form
     * @param numRecords - number of requested records 
     * @param showAvailable - check for number of matching available records 
     * @return Top Lucene query results as a Result object
     * @throws LuceneSearcherException 
     * @throws InvalidLuceneQueryException 
     */
    public Result searchIndex(String queryString, int numRecords, boolean showAvailable)
            throws LuceneSearcherException, InvalidLuceneQueryException {
        IndexReader reader = null;
        IndexSearcher indexSearcher = null;
        Query query;
        TopDocs documents;
        TotalHitCountCollector collector = null;
        try {
            reader = DirectoryReader.open(memoryIndex);
            indexSearcher = new IndexSearcher(reader);
            queryParser = new QueryParser("title", analyzer); // 'title' is default field if we don't prefix search string
            
            String luceneQueryString = "";
            String[] splitedQuery = queryString.split("\\s+");
            for (int i=0; i<splitedQuery.length; i++) {
                String token = splitedQuery[i];
                // Das Feld, welches bestimmend sein soll (also in der Suche zuoberst gelistet), bekommt
                // einen sehr hohen Boost.
                luceneQueryString += "(id:" + token + "*^100 OR title:" + token + "*^10 OR shortdescription:" + token + "*)";
                if (i<splitedQuery.length-1) {
                    luceneQueryString += " AND ";
                }
            }
            
            //luceneQueryString = "(id:kant*^100 OR title:kant*) AND (id:natur*^100 OR title:natur*)";
            
            query = queryParser.parse(luceneQueryString);
            log.info("'" + luceneQueryString + "' ==> '" + query.toString() + "'");
            
            if (showAvailable) {
                collector = new TotalHitCountCollector();
                indexSearcher.search(query, collector);
            }
            documents = indexSearcher.search(query, numRecords);
            List<Map<String, String>> mapList = new LinkedList<Map<String, String>>();
            for (ScoreDoc scoreDoc : documents.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                Map<String, String> docMap = new HashMap<String, String>();
                List<IndexableField> fields = document.getFields();
                for (IndexableField field : fields) {
                    docMap.put(field.name(), field.stringValue());
                }
                mapList.add(docMap);
            }
            Result result = new Result(mapList, mapList.size(),
                    collector == null ? (mapList.size() < numRecords ? mapList.size() : -1) : collector.getTotalHits());
            return result;
        } catch (ParseException e) {
            e.printStackTrace();            
            throw new InvalidLuceneQueryException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuceneSearcherException(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
                log.warn("Could not close IndexReader: " + ioe.getMessage());
            }
        }
    }
}
