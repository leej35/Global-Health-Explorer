
package edu.rpi.tw.impav;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

import com.hp.hpl.jena.ontology.Individual;

//import edu.rpi.tw.impav.TweetQueue.Tweet;

public class GraphBuilder implements Runnable {

    private TweetQueue queue;
    
//    private Graph graph;
    
    private App app;
    
    private boolean run = true;
    
    public synchronized void pleaseStop() {
        run = false;
    }
    
    public GraphBuilder(TweetQueue queue, App app) {
        this.queue = queue;
//        this.graph = graph;
        this.app = app;
    }

    @Override
    public void run() {
    	System.out.println("run method called");
    	
        while (true) {
            synchronized (this) {
                if (!run) return;
            }
            
            System.out.println("Can you see!!!?");
            List<Tweet> tweets = new LinkedList<Tweet>();
            BlockingQueue<Tweet> q = queue.getTweets();
            
            System.out.println("q @ GraphBuilder.java: " + q.toString());
            
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
//            processTweets(tweets);
            
            // Build up the graph nodes.
            synchronized(app) {
                processTweets(tweets);
//                graph.runExpiration();
            }
            
            // Run the draw method in a separate thread when the
            // event queue is ready to.
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    app.draw();
//                }
//            });
        }
    }

    private void processTweets(List<Tweet> tweets) {
    	System.out.println("processTweets@GraphBuilder.java called");

        for (Tweet t : tweets) {
        	
        	System.out.println("tweet@tweets in GraphBuilder: " + t);
//        	System.out.println("status @ GraphBuilder:" + t.text.toString());
//            System.out.println("label:" + t.labels.toString());
//            System.out.println("tweet:" + t.text);
//            System.out.println("time:" + t.created);
//            System.out.println("location: " + t.location + "\n");

        	
        	
//            List<Node> nodes = new ArrayList<Node>();
//            graph.seedPosition();
//            for (Individual i: t.termVector) {
//                Node node = graph.getNode(i);
//                node.getTweets().add(t);
//                nodes.add(node);
//            }
//            for (int i=0; i<nodes.size(); ++i) {
//                for (int j=i+1; j < nodes.size(); ++j) {
//                    Node u = nodes.get(i);
//                    Node v = nodes.get(j);
//                    Edge e = graph.getEdge(u, v);
//                    e.getTweets().add(t);
//                }
//            }
        }
    }

}
