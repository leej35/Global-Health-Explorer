package edu.rpi.tw.impav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;

public class ConceptMap {

    public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    public static final String RDFs = "http://www.w3.org/2000/01/rdf-schema#";
    
    private OntClass skosConcept;
    private DatatypeProperty skosPrefLabel;

    private OntModel model;
    private RAMDirectory index;

    private IndexSearcher searcher;

    private QueryParser parser;

    private HashSet<String> stopwords = loadStopwords();
    
    public ConceptMap(String fileOrURI) throws CorruptIndexException,
            LockObtainFailedException, IOException {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        FileManager.get().readModel(model, fileOrURI);
        model.loadImports();
        System.out.println(model.toString());
        
        System.out.println("tic");
//        skosConcept = model.createClass();
        skosConcept = model.getOntClass(SKOS + "Concept");
//        skosConcept = model.getOntClass(SKOS + "Concept");//SKOS + "Concept"
        System.out.println(skosConcept.listInstances().toString() + "\n\n");
        
        System.out.println("toc");
        
        BufferedWriter out = new BufferedWriter(new FileWriter("./out.txt"));
        out.write(model.toString()); out.newLine();

        out.close();

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

    private void loadConcepts() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(Version.LUCENE_30), MaxFieldLength.LIMITED);
        for (Individual concept : (List<Individual>)skosConcept.listInstances().toList()) {
            String uri = concept.getURI();
            Document doc = new Document();
            doc.add(new Field("uri", uri, Store.YES, Index.NOT_ANALYZED_NO_NORMS));
            for (Statement stmt : (List<Statement>)concept.listProperties(skosPrefLabel).toList()) {
                if (stmt.getObject().isLiteral())
                    doc.add(new Field("label",stmt.getString().toLowerCase(),Store.YES,Index.NOT_ANALYZED));
            }
            writer.addDocument(doc);
        }
        writer.commit();
        writer.optimize();
        writer.close();
    }
    
    public List<Individual> getConcepts(String tweet) {
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
    
}