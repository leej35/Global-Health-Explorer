
package edu.rpi.tw.impav;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

import org.apache.jena.fuseki.http.UpdateRemote;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import twitter4j.Status;
import twitter4j.HashtagEntity;

public class GraphBuilder implements Runnable {

    private TweetQueue queue;
    
    private int count = 0;

    private App app;
    
    private boolean run = true;

    private String PREFIX = "http://purl.org/twc/twitter/";

    private String PREFIXES = "prefix dc: <http://purl.org/dc/terms/> \n"+
        "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"+
        "prefix prov: <http://www.w3.org/ns/prov#>  \n"+
        "prefix ogc: <http://www.opengis.net/rdf#>  \n"+
        "prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>  \n"+
        "prefix foaf: <http://xmlns.com/foaf/0.1/>  \n"+
        "prefix owl: <http://www.w3.org/2002/07/owl#>  \n"+
        "prefix xsd: <http://www.w3.org/2001/XMLSchema#>  \n"+
        "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  \n"+
        "prefix twitter: <http://purl.org/twc/twitter/ns#>  \n"+
        "prefix skos: <http://www.w3.org/2004/02/skos/core#> \n"+
        "\n";
    
    public synchronized void pleaseStop() {
        run = false;
    }
    
    public GraphBuilder(TweetQueue queue, App app) {
        this.queue = queue;
        this.app = app;
    }

    @Override
    public void run() {	
        while (true) {
            synchronized (this) {
                if (!run) return;
            }
            List<Tweet> tweets = new LinkedList<Tweet>();
            BlockingQueue<Tweet> q = queue.getTweets();
                        
            // First attempt to get a single tweet, 
            // blocking until there is at least one.
            try {
                Tweet t = q.take();
                tweets.add(t);
            } catch (InterruptedException e) {
            }
            // Once we've established that there's at least one,
            // drain the rest to the list for processing.
            q.drainTo(tweets);
            
            // Build up the graph nodes.
            synchronized(app) {
                processTweets(tweets);
            }
            
        }
    }

    private String makeURI(String x) {
        return "<"+x+">";
    }

    private String makeStatusRDF(Tweet t) {
        String tweetURI = PREFIX + "user/"+ t.creator.getScreenName() + "/status/" + t.id;
        String result =  makeURI(tweetURI) +" prov:value '''" + t.text + "'''; \n"+
            "      a twitter:Status; \n" +
            "      rdfs:seeAlso <http://twitter.com/" + t.creator.getScreenName() + "/status/" + t.id + ">; \n"+
            "      prov:wasAttributedTo <http://twitter.com/" + t.creator.getScreenName() + ">; \n"+
            "      dc:date \"" + t.getCreated() + "\"^^xsd:dateTime . \n" +
            "<http://twitter.com/" + t.creator.getScreenName() + "> a twitter:User. \n";
        if (t.termVector != null) for (Individual concept : t.termVector) {
            result += makeURI(tweetURI)+" dc:subject <"+concept.getURI()+">. \n";
        }
        if(t.location.compareTo("null") < 0){
            result +=  makeURI(tweetURI +"/location")+" a geo:Point; \n"+
                "     " + t.location + "\n"+
                makeURI(tweetURI)+ " prov:atLocation "+ makeURI(tweetURI +"/location") + ".\n";
        }
        if (t.status.isRetweet()) {
            Status retweetedFrom = t.status.getRetweetedStatus();
            
            result += makeURI(tweetURI) + " prov:wasQuotedFrom " +
                makeURI(PREFIX +"user/"+
                        retweetedFrom.getUser().getScreenName() +
                        "/status/" + retweetedFrom.getId()) + " .\n";
        }
        for (HashtagEntity hashtag : t.status.getHashtagEntities()) {
            String tagURI = PREFIX+"hashtag/"+hashtag.getText();
            result += makeURI(tweetURI) + " dc:subject "+makeURI(tagURI) + " .\n";
            result += makeURI(tagURI) + " skos:prefLabel \"" + hashtag.getText() + "\" .\n";
        }
        return result;
    }

    private void processTweets(List<Tweet> tweets) {
        String query = PREFIXES + "INSERT DATA { GRAPH <"+app.graph+"> {\n";
        UpdateRequest request = UpdateFactory.create();
        try {
            for (Tweet t : tweets) {
                //System.out.println("label:" + t.labels.toString());
                System.out.println("tweet:" + t.text);
                System.out.println("time:" + t.created);
                //System.out.println("location: " + t.location + "\n");           
                //            System.out.println("origianl: " + t.originalText + "\n");
                
                
                query += makeStatusRDF(t);
                
            }
            query = query + "} }";
            //System.out.println(query);
            
            request.add(query);
            count++;
            UpdateRemote.execute(request, app.endpoint); //http://doppio.med.yale.edu:3030
            
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
