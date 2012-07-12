package edu.rpi.tw.impav;

import java.awt.BorderLayout;
import java.io.IOException;
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
    private TweetQueue queue;
    private Thread thread;
    public static boolean fullscreen = false;
    
//    OptionsPanel optionsPanel = null;

    public App() throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, BackingStoreException {
        queue = new TweetQueue(fileOrUri);
        queue.start();
//        GraphBuilder builder = new GraphBuilder(queue, graph, this);
//        thread = new Thread(builder);
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
        fileOrUri = args[0];
        if (args.length > 1 && args[1].equals("--present")) {
            fullscreen = true;
//            PApplet.main(new String[] { "--present", "edu.rpi.tw.impav.App" });
        } else {
            JFrame frame = new JFrame();
            frame.getRootPane().setLayout(new BorderLayout());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            try {
//                App app = new App();
////                frame.getRootPane().add(app, BorderLayout.CENTER);
//                //frame.getRootPane().add(app.optionsPanel, BorderLayout.EAST);
//                frame.pack();
//                frame.setSize(1024,768);
//                frame.setLocation(200, 200);
//                //frame.pack();
//                app.init();
//                while (app.defaultSize&&!app.finished)
//                    try {Thread.sleep(5);} catch (Exception e) {}
//                frame.setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        
        //PApplet.main(new String[] {"edu.rpi.tw.impav.App" });
    }
}
