package edu.rpi.tw.impav;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.prefs.BackingStoreException;

import javax.swing.JFrame;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import twitter4j.TwitterException;
import java.util.Arrays;

/**
 * This application is now called skitter.
 * 
 */
public class App  {
    
    public static String fileOrUri;
    private Thread thread;
    public static boolean fullscreen = false;
    public String endpoint = "http://localhost:3030/db/update";
    public String graph = "http://purl.org/twc/skitter/ncit";
    
    public App(String endpoint, String graph, String[] keywords) throws Exception {
    	//TweetQueue queue = new TweetQueue(url);
        this.endpoint = endpoint;
        this.graph = graph;
    	TweetQueue queue = new TweetQueue(keywords);
        queue.start(); 
        GraphBuilder builder = new GraphBuilder(queue, this);
        thread = new Thread(builder);
        thread.start();
    }

    public static void main(String[] args) {
            try {
                App app = new App(args[0], args[1], Arrays.copyOfRange(args,2,args.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
