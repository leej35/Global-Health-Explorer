package edu.rpi.tw.impav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
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
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
//import twitter4j.http.AccessToken;
//import twitter4j.http.RequestToken;

import com.hp.hpl.jena.ontology.Individual;

public class TweetQueue implements StatusListener {

    private ConceptMap concepts = null;
    private TwitterStream twitter = new TwitterStreamFactory().getInstance();

    
    private BlockingQueue<Tweet> tweets = new LinkedBlockingQueue<Tweet>();
    
    public TweetQueue(String fileOrURI) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, BackingStoreException {
        System.out.println("Loading Concepts...");
        concepts = new ConceptMap(fileOrURI);
        System.out.println("Done.");
    	twitter.addListener(this);
    }

    public BlockingQueue<Tweet> getTweets() {
        return tweets;
    }

    public void start() throws IOException, TwitterException {
        twitter.sample();
    }
    

//    private AccessToken getAccessToken() throws TwitterException, IOException, BackingStoreException {
//        Preferences prefs = Preferences.userNodeForPackage(getClass());
//        String token = prefs.get("twitter.access.token", null);
//        String tokenSecret = prefs.get("twitter.access.token.secret", null);
//        AccessToken result = null;
//        if (token == null) {
//            result = authenticate();
//            token = result.getToken();
//            tokenSecret = result.getTokenSecret();
//            prefs.put("twitter.access.token", token);
//            prefs.put("twitter.access.token.secret", tokenSecret);
//            prefs.flush();
//        } else {
//            result = new AccessToken(token, tokenSecret);
//        }
//        return result;
//    }
    
//    private AccessToken authenticate() throws TwitterException, IOException {
//        TwitterStream t = factory.getOAuthAuthorizedInstance(consumerKey, consumerSecret);
//        RequestToken requestToken = t.getOAuthRequestToken();
//        AccessToken accessToken = null;
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        while (null == accessToken) {
//          System.out.println("Open the following URL and grant access to your account:");
//          System.out.println(requestToken.getAuthorizationURL());
//          System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
//          String pin = br.readLine();
//          try{
//             if(pin.length() > 0){
//               accessToken = t.getOAuthAccessToken(requestToken, pin);
//             }else{
//               accessToken = t.getOAuthAccessToken();
//             }
//          } catch (TwitterException te) {
//            if(401 == te.getStatusCode()){
//              System.out.println("Unable to get the access token.");
//            }else{
//              te.printStackTrace();
//            }
//          }
//        }
//        return accessToken;
//    }
    
    
    public void setConcepts(ConceptMap concepts) {
        this.concepts = concepts;
    }

    public ConceptMap getConcepts() {
        return concepts;
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
    	List<Individual> individuals = concepts.getConcepts(status.getText());
    	if (individuals.size() > 1) {
    		Tweet tweet = new Tweet();
    		tweet.termVector = individuals;
    		tweet.text = status.getText();
    		tweet.created = status.getCreatedAt();
    		tweet.creator = status.getUser();
    		tweet.added = new Date();
    		tweets.add(tweet);
    		//System.out.println(status.getText()+"\t"+individuals);
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
		String queryText = 	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"PREFIX bfo: <http://www.ifomis.org/bfo/1.1#Entity>" +
				"PREFIX obo: <http://purl.obolibrary.org/obo/>" +
				"PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>" +
				"CONSTRUCT {"+
				"?s skos:prefLabel ?symptomName."+
				"?s a skos:Concept." +
				"}WHERE{" +
				"GRAPH <http://bioportal.bioontology.org/ontologies/FLU>{" +
				"?s ?p <http://purl.obolibrary.org/obo/OGMS_0000020>." +
				"?s <http://bioportal.bioontology.org/metadata/def/prefLabel> ?symptomName. "+
				"}}";


		
		String sparqlService = "http://sparql.bioontology.org/sparql";
		String apikey = "b2aa80e5-8af9-4cc8-9226-55547c5faa65";

		
		String httpQueryString = String.format("query=%s&apikey=%s", 
			     URLEncoder.encode(queryText, "UTF-8"), 
			     apikey);
//			     URLEncoder.encode(apikey, "UTF-8"));
		
		String url = sparqlService + "?" + httpQueryString;
		System.out.println(url);
    	TweetQueue queue = new TweetQueue(url);//args[0]   endpoint+"?query="+URLEncoder.encode(sparqlConstructQuery)
        queue.start();
        System.out.println("Back?");
    }
}
