package edu.rpi.tw.impav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.fuseki.http.UpdateRemote;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import twitter4j.GeoLocation;
import twitter4j.Status;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.io.*;

public class ConceptMap {
	
    public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    public static final String RDFs = "http://www.w3.org/2000/01/rdf-schema#";
    
    private OntClass skosConcept;
    private DatatypeProperty skosPrefLabel;

    public OntModel model;
    private RAMDirectory index;

    private IndexSearcher searcher;

    private QueryParser parser;

    private HashSet<String> stopwords = loadStopwords();
    
    
    GraphStore graphStore = GraphStoreFactory.create() ;

    public ConceptMap(String fileOrURI) throws Exception {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        FileManager.get().readModel(model, fileOrURI);
        model.loadImports();     
        skosConcept = model.createClass(SKOS + "Concept");
        
        skosPrefLabel = model.createDatatypeProperty(SKOS + "prefLabel");
        
        index = new RAMDirectory();
        loadConcepts();
        searcher = new IndexSearcher(index);
        parser = new QueryParser(Version.LUCENE_30, "label",
                new StandardAnalyzer(Version.LUCENE_30));
        parser.setDefaultOperator(Operator.OR);
    }
    
    private HashSet<String> loadStopwords() {
        BufferedReader reader 
            = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/stopwords.txt")));
        HashSet<String> result = new HashSet<String>();
        try {
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().length() > 0)
                    result.add(line.trim());
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void loadConcepts() throws Exception {
        IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(Version.LUCENE_30), MaxFieldLength.LIMITED);
        int i=0;
        for (Individual concept : (List<Individual>)skosConcept.listInstances().toList()) {
            String uri = concept.getURI();
            
            Document doc = new Document();
            doc.add(new Field("uri", uri, Store.YES, Index.NOT_ANALYZED_NO_NORMS));
            for (Statement stmt : (List<Statement>)concept.listProperties(skosPrefLabel).toList()) {
                if (stmt.getObject().isLiteral()){
                	
                    doc.add(new Field("label",stmt.getString().toLowerCase(),Store.YES,Index.NOT_ANALYZED));
                    i++;
                	// translated word might be added here.
                    ArrayList<String> otherLangs = new ArrayList<String>();
                    otherLangs = translateTerm(stmt.getString().toLowerCase());
                    for(String label:otherLangs){
                        doc.add(new Field("labelOtherLang",label.toLowerCase(),Store.YES,Index.NOT_ANALYZED));
                        System.out.println("["+ i + "] TERM: " +stmt.getString().toLowerCase() + " Translated: " + label.toLowerCase()+ " URI:" + uri);
                        i++;
                    }
                }
            }   
            writer.addDocument(doc);
        }
        writer.commit();
        writer.optimize();
        writer.close();
        System.out.println("Overall "+i+" vocabularies added");
    }
    
    private ArrayList<String> translateTerm (String term) throws Exception{
    	ArrayList<String> termList = new ArrayList<String>();

    	// Microsoft Translator API: https://github.com/boatmeme/microsoft-translator-java-api
    	Translate.setClientId("leej35");
        Translate.setClientSecret("IydKdGG7l0b3dH7kth0m5Af5DtpfQXf2Bp+v3GkAjqU=");

        // 36 languages..
        termList.add(term);
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.ARABIC));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.BULGARIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.CHINESE_SIMPLIFIED));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.CHINESE_TRADITIONAL));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.CZECH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.DANISH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.DUTCH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.ESTONIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.FINNISH));
        termList.add(Translate.execute(term, Language.ENGLISH, Language.FRENCH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.GERMAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.GREEK));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.HAITIAN_CREOLE));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.HEBREW));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.KOREAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.HINDI));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.HMONG_DAW));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.HUNGARIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.INDONESIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.ITALIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.JAPANESE));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.LATVIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.LITHUANIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.NORWEGIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.POLISH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.PORTUGUESE));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.ROMANIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.RUSSIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.SLOVAK));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.SLOVENIAN));
        termList.add(Translate.execute(term, Language.ENGLISH, Language.SPANISH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.SWEDISH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.THAI));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.TURKISH));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.UKRAINIAN));
//        termList.add(Translate.execute(term, Language.ENGLISH, Language.VIETNAMESE));
   	
    	return termList;
    }
    
    public List<Individual> getConcepts(Status status) {
    	String tweet = status.getText();
    	
        List<Individual> result = new LinkedList<Individual>();
        try {
        	
            Query query = parser.parse(tweet.toLowerCase().replaceAll("[\\;\\'\\{\\}\"\\[\\]\\~\\*\\?\\:\\(\\)\\!\\@\\#\\$\\%\\^\\&\\*\\-\\+\\=\\_]+", " "));
            TopDocs results = searcher.search(query, 10);
            
            Set<String> labels = new HashSet<String>();
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                Individual i = model.getIndividual(doc.get("uri"));
                String label = doc.get("label");
                
                if (!labels.contains(label) 
                        && label.length() > 1 
                        && !stopwords.contains(label)) {
                    result.add(i);                                       
                    labels.add(label);                   
                }

            }
        } catch (ParseException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
	
    public Property getSkosPrefLabel(){
    	return skosPrefLabel;
    }

}
