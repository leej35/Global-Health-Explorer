package edu.rpi.tw.impav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;
//import twitter4j.http.AccessToken;
//import twitter4j.http.RequestToken;

import com.hp.hpl.jena.ontology.Individual;

public class TweetQueue extends StatusAdapter {

    private ConceptMap concepts = null;
       
    private TwitterStream twitter = new TwitterStreamFactory().getInstance();

    private BlockingQueue<Tweet> tweets = new LinkedBlockingQueue<Tweet>();

    private String[] keywords = null;
    
    public TweetQueue(String fileOrURI) throws Exception {
        System.out.println("Loading Concepts...");
        concepts = new ConceptMap(fileOrURI);
        System.out.println("Done.");
    	twitter.addListener(this);
    }

    public TweetQueue(String[] keywords) throws Exception {
    	twitter.addListener(this);
        this.keywords = keywords;
    }

    public BlockingQueue<Tweet> getTweets() {
        return tweets;
    }

    public void start() throws IOException, TwitterException {
        if (keywords != null) {
            FilterQuery q = new FilterQuery();
            //q.count(100);
            q.track(keywords);
            twitter.filter(q);
        } else {
            twitter.sample();
        }
    }
    
    
    public void setConcepts(ConceptMap concepts) {
        this.concepts = concepts;
    }

    public ConceptMap getConcepts() {
        return concepts;
    }
    
    private String getGeocoord(String status){	
    	int latAt = status.indexOf("latitude");
    	if(latAt == -1){
    		return "null";
    	} 
    	latAt = latAt+9;
    	int latEnd = status.indexOf(",",latAt);
    	int lonAt = status.indexOf("longitude") + 10;
    	int lonEnd = status.indexOf("}", lonAt);
    	String latitude = status.substring(latAt, latEnd);
    	String longitude = status.substring(lonAt, lonEnd);
        return "geo:lat \"" + latitude + "\"^^xsd:double; \n geo:long \"" + longitude +"\"^^xsd:double .";
    }
    
    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0) {
    }

    @Override
    public void onException(Exception arg0) {
    	System.out.println(arg0);
    }

    @Override
    public void onStatus(Status status) {
        List<Individual> individuals = null;
        if (concepts != null)
            individuals = concepts.getConcepts(status);
    	
    	if (concepts == null || individuals.size() > 0) {
    		ArrayList<String> labels = new ArrayList<String>(); 		
    		Tweet tweet = new Tweet();
                tweet.status = status;
    		tweet.termVector = individuals;
    		tweet.id = status.getId();
    		tweet.text = status.getText();
    		tweet.created = status.getCreatedAt();
    		tweet.creator = status.getUser();
    		tweet.location = getGeocoord(status.toString());
    		tweet.added = new Date();
    		tweet.originalText = status.toString();
                if (individuals != null) {
                    for(Individual i : individuals){
    			String label = i.getPropertyValue(concepts.getSkosPrefLabel()).asLiteral().getString();
    			labels.add(label);
                    }
                    tweet.labels = labels;    		
                }
    		tweets.add(tweet);
        }
    }	
    
    @Override
    public void onTrackLimitationNotice(int arg0) {
    	System.out.println("On Track Limitation Notice:\t"+arg0);
    }

    @Override
    public void onScrubGeo(long arg0, long arg1) {
    	// TODO Auto-generated method stub
    	
    }
    

    public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, BackingStoreException {		

    }

}
