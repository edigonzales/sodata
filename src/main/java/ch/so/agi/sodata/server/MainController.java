package ch.so.agi.sodata.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.document.Field.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import ch.interlis.ili2c.Ili2c;
import ch.interlis.ili2c.Ili2cException;
import ch.interlis.ili2c.config.Configuration;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iom_j.xtf.XtfWriter;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxWriter;
import ch.so.agi.sodata.server.search.InvalidLuceneQueryException;
import ch.so.agi.sodata.server.search.LuceneSearcher;
import ch.so.agi.sodata.server.search.LuceneSearcherException;
import ch.so.agi.sodata.server.search.Result;
import ch.so.agi.sodata.shared.Dataset;


@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppConfig config;
    
    @Autowired
    private LuceneSearcher indexSearcher;
    
    @Value("${lucene.query.default.records:10}")
    private Integer QUERY_DEFAULT_RECORDS;

    @Value("${lucene.query.max.records:50}")
    private Integer QUERY_MAX_RECORDS;   
    
    private Map<String, Dataset> datasetMap;
    
    @PostConstruct
    public void init() {
        datasetMap = new HashMap<String, Dataset>();
        for (Dataset dataset : config.getDatasets()) {

            log.info("******" + dataset.getTables());

            datasetMap.put(dataset.getId(), dataset);
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() throws ParseException, IOException, LuceneSearcherException, InvalidLuceneQueryException {
        String query = "ch.so.agi kant natur";
        Result results = indexSearcher.searchIndex(query, QUERY_DEFAULT_RECORDS, false);
        log.info("Search for '" + query +"' found " + results.getAvailable() + " and retrieved " + results.getRetrieved() + " records");

        return new ResponseEntity<String>("sodata", HttpStatus.OK);
    }
        
    @GetMapping("/datasets")
    public List<Dataset> searchDatasets(@RequestParam(value="query", required=false) String queryString) {
        if (queryString == null) {
            return config.getDatasets();

        } else {
            Result results = null;
            try {
                results = indexSearcher.searchIndex(queryString, QUERY_DEFAULT_RECORDS, false);
                log.info("Search for '" + queryString +"' found " + results.getAvailable() + " and retrieved " + results.getRetrieved() + " records");            
            } catch (LuceneSearcherException | InvalidLuceneQueryException e) {
                throw new IllegalStateException(e);
            }

            List<Map<String, String>> records = results.getRecords();
            List<Dataset> resultList = records.stream()
                    //.distinct()
                    .map(r -> {
                        log.info(r.get("id"));
                        return datasetMap.get(r.get("id"));
                    })
                    .collect(Collectors.toList());
            return resultList;
        }
    }
    
    @GetMapping("/dataset/{fileName}") 
    public RedirectView datset(@PathVariable String fileName) {
        RedirectView redirectView = new RedirectView();
        // TODO: Im wahren Leben steckt die URL in der Konfig, da sie unterschiedlich sein kann.
        redirectView.setUrl("https://s3.eu-central-1.amazonaws.com/ch.so.agi.geodata/" + fileName);
        return redirectView;
    }
    
    // TODO: PostConstruct
    @GetMapping("/ilidata.xml") 
    public ResponseEntity<?> ilidata() throws IOException, IoxException {
        String ILI_TOPIC="IliRepository09.RepositoryIndex";
        String BID="DatasetIdx16.DataIndex";
        
        TransferDescription td = null;
        try {
            td = getTransferDescriptionFromModelName("DatasetIdx16");
        } catch (Ili2cException e) {
            throw new IllegalStateException(e);
        }
        
        Path tempDirWithPrefix = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "sodata_ilidata");
        File outputFile = Paths.get(tempDirWithPrefix.toFile().getAbsolutePath(), "ilidata.xml").toFile();
        IoxWriter ioxWriter = new XtfWriter(outputFile, td);
        ioxWriter.write(new ch.interlis.iox_j.StartTransferEvent("SOGIS-20200906", "", null));
        ioxWriter.write(new ch.interlis.iox_j.StartBasketEvent(ILI_TOPIC,BID));
        
        Iom_jObject iomRootObj = new Iom_jObject("DatasetIdx16.DataIndex.Metadata", String.valueOf(1));
        iomRootObj.setattrvalue("id", "ch.so.agi.geodata.repository");
        iomRootObj.setattrvalue("version", "2020-09-06");
        iomRootObj.setattrvalue("owner", "2020-09-06");
        iomRootObj.setattrvalue("technicalContact", "mailto:agi@bd.so.ch");        
        Iom_jObject restrictions = new Iom_jObject("DatasetIdx16.Code_", null);
        restrictions.setattrvalue("value",  "https://www.gl.ch/public/upload/assets/5053/ktgl-ogd-geo-20180601.pdf");
        iomRootObj.addattrobj("restrictions", restrictions);
        ioxWriter.write(new ch.interlis.iox_j.ObjectEvent(iomRootObj));   

        for (int i=0; i<config.getDatasets().size(); i++) {
            Dataset dataset = config.getDatasets().get(i);
            Iom_jObject iomObj = new Iom_jObject("DatasetIdx16.DataIndex.DatasetMetadata", String.valueOf(i+2));
            iomObj.setattrvalue("id", dataset.getId());
            iomObj.setattrvalue("originalId", dataset.getId());
            iomObj.setattrvalue("version", "current");
            iomObj.setattrvalue("owner", dataset.getOwner());
            iomObj.setattrvalue("original", ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
            
            Iom_jObject model = new Iom_jObject("DatasetIdx16.ModelLink", null);
            model.setattrvalue("name", dataset.getModel());
            model.setattrvalue("locationHint", "http://geo.so.ch/models");
            iomObj.addattrobj("model", model);
            
            iomObj.setattrvalue("epsgCode", dataset.getEpsgCode());
            iomObj.setattrvalue("resolutionScope", dataset.getResolutionScope());
            iomObj.setattrvalue("publishingDate", dataset.getPublishingDate());
            iomObj.setattrvalue("lastEditingDate", dataset.getLastEditingDate());
            
            Iom_jObject boundary = new Iom_jObject("DatasetIdx16.BoundingBox", null);
            boundary.setattrvalue("westlimit", "7.7748367478");
            boundary.setattrvalue("southlimit", "47.3107510994");
            boundary.setattrvalue("eastlimit", "7.8194827337");
            boundary.setattrvalue("northlimit", "47.3407206208");
            iomObj.addattrobj("boundary", boundary);
            
            Iom_jObject title = new Iom_jObject("DatasetIdx16.MultilingualText", null);
            Iom_jObject titleLocalisedText = new Iom_jObject("DatasetIdx16.LocalisedText", null);
            titleLocalisedText.setattrvalue("Language", "de");
            titleLocalisedText.setattrvalue("Text", dataset.getTitle());
            title.addattrobj("LocalisedText", titleLocalisedText);
            iomObj.addattrobj("title", title);
            
            Iom_jObject shortDescription = new Iom_jObject("DatasetIdx16.MultilingualMText", null);
            Iom_jObject localisedMTextshortDescription = new Iom_jObject("DatasetIdx16.LocalisedMText", null);
            localisedMTextshortDescription.setattrvalue("Language", "de");
            localisedMTextshortDescription.setattrvalue("Text", dataset.getShortDescription());
            shortDescription.addattrobj("LocalisedText", localisedMTextshortDescription);
            iomObj.addattrobj("shortDescription", shortDescription);
            
            iomObj.setattrvalue("keywords", dataset.getKeywords());
            iomObj.setattrvalue("servicer", dataset.getServicer());
            iomObj.setattrvalue("technicalContact", dataset.getTechnicalContact());
            iomObj.setattrvalue("furtherInformation", dataset.getFurtherInformation());
            iomObj.setattrvalue("furtherMetadata", dataset.getFurtherMetadata());
            
            Iom_jObject knownWMS = new Iom_jObject("DatasetIdx16.WebService_", null);
            knownWMS.setattrvalue("value", dataset.getKnownWMS());
            iomObj.addattrobj("knownWMS", knownWMS);
            
            for (String fileStr : dataset.getFiles()) {
                Iom_jObject files = new Iom_jObject("DatasetIdx16.DataFile", null);
                String fileFormat = "unknown";
                if (fileStr.equalsIgnoreCase("xtf")) {
                    fileFormat = "application/interlis+xml;version=2.3";
                } else if (fileStr.equalsIgnoreCase("gpkg")) {
                    fileFormat = "application/geopackage+vnd.sqlite3";
                } else if (fileStr.equalsIgnoreCase("shp")) {
                    fileFormat = "x-gis/x-shapefile";
                } else if (fileStr.equalsIgnoreCase("dxf")) {
                    fileFormat = "application/dxf";
                } else if (fileStr.equalsIgnoreCase("gtiff")) {
                    fileFormat = "image/geo+tiff";
                } else if (fileStr.equalsIgnoreCase("laz")) {
                    fileFormat = "application/octet-stream";
                }
                files.setattrvalue("fileFormat", fileFormat);
                
                Iom_jObject file = new Iom_jObject("DatasetIdx16.File", null);
                file.setattrvalue("path", "/dataset/" + dataset.getId() + "_" + fileStr + ".zip");
                files.addattrobj("file", file);
                iomObj.addattrobj("files", files);                
            } 
            ioxWriter.write(new ch.interlis.iox_j.ObjectEvent(iomObj));
        }
        ioxWriter.write(new ch.interlis.iox_j.EndBasketEvent());
        ioxWriter.write(new ch.interlis.iox_j.EndTransferEvent());
        ioxWriter.flush();
        ioxWriter.close();

        InputStream is = new FileInputStream(outputFile);
        return ResponseEntity.ok().header("Content-Type", "application/xml; charset=utf-8")
                .contentLength(outputFile.length())
                .body(new InputStreamResource(is));
    }
    
    private TransferDescription getTransferDescriptionFromModelName(String iliModelName) throws Ili2cException {
        IliManager manager = new IliManager();
        String repositories[] = new String[] { "http://models.interlis.ch/", "http://models.kkgeo.ch/", "http://models.geo.admin.ch/", "http://geo.so.ch/models" };
        manager.setRepositories(repositories);
        ArrayList<String> modelNames = new ArrayList<String>();
        modelNames.add(iliModelName);
        Configuration config = manager.getConfig(modelNames, 2.3);
        ch.interlis.ili2c.metamodel.TransferDescription td = Ili2c.runCompiler(config);

        if (td == null) {
            throw new IllegalArgumentException("INTERLIS compiler failed"); 
        }
        
        return td;
    }
}
