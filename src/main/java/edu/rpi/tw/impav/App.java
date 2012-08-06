package edu.rpi.tw.impav;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.prefs.BackingStoreException;

import javax.swing.JFrame;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import twitter4j.TwitterException;

/**
 * This application is now called skitter.
 * 
 */
public class App  {
    
    public static String fileOrUri;
//    private Graph graph = new Graph(this);
//    private TweetQueue queue;
    private Thread thread;
    public static boolean fullscreen = false;
    
//    OptionsPanel optionsPanel = null;

    public App() throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, BackingStoreException {
        
//		-- Flu Ontology -------
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
		
		String url = sparqlService + "?" + httpQueryString;
	
		
    	TweetQueue queue = new TweetQueue(url);
        System.out.println("queue created");
        System.out.println("queue will be started");
        queue.start();
        
        
        GraphBuilder builder = new GraphBuilder(queue, this);
        System.out.println("GraphBuilder builder created");
        thread = new Thread(builder);
        System.out.println("thread will be started");
        thread.start();
    }

//    @Override
//    public void setup() {
//        smooth();
//        frameRate( 24 );
//        ellipseMode( CENTER );
//        optionsPanel = new OptionsPanel(this);
//        optionsPanel.setup();
//    }



//    @Override
//    public synchronized void draw() {
//        this.setLocation(0,0);
//        if (fullscreen)
//            this.setSize(this.getParent().getSize());
//        graph.setMinTfidf(optionsPanel.getMinTfidf());
//        graph.lifetime = optionsPanel.getLifetime();
//        graph.height = height;
//        graph.width = width-Tweet.totalWidth;
//        graph.tick(g);
//
//        translate( width/2 , height/2 );
//        scale( (float) graph.getCentroid().z() );
//        translate( -graph.getCentroid().x(), -graph.getCentroid().y() );
//
//        background( 0 );
//        graph.draw(g);
//        this.resetMatrix();
//        float position = 0;
//        for (Tweet tweet : graph.getAllTweets()) {
//            position = tweet.draw(g, position, graph);
//        }
//        translate( -width/2 , -height/2 );
//    }

    public static void main(String[] args) {
    	System.out.println("main method @ App.java");
//        fileOrUri = args[0];
//        if (args.length > 1 && args[1].equals("--present")) {
//            fullscreen = true;
//            PApplet.main(new String[] { "--present", "edu.rpi.tw.impav.App" });
//        } else {
//            JFrame frame = new JFrame();
//            frame.getRootPane().setLayout(new BorderLayout());
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
            	
                App app = new App();
                
//                frame.getRootPane().add(app, BorderLayout.CENTER);
                //frame.getRootPane().add(app.optionsPanel, BorderLayout.EAST);
//                frame.pack();
//                frame.setSize(1024,768);
//                frame.setLocation(200, 200);
                //frame.pack();
//                app.init();
//                while (app.defaultSize&&!app.finished)
//                    try {Thread.sleep(5);} catch (Exception e) {}
//                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
        
        //PApplet.main(new String[] {"edu.rpi.tw.impav.App" });
    }
}
