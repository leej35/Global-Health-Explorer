/**
 * 
 */
package edu.rpi.tw.impav;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//import processing.core.PGraphics;
import twitter4j.User;

import com.hp.hpl.jena.ontology.Individual;

public class Tweet {
    public User creator;
    public Date created;
    public Date added;
    public List<Individual> termVector;
    public String text;
    public String location;
    public String originalText;
    public List<String> labels;
        
    public List<String> lines = null;
    
    public static int width = 250;
    public static float textSize = 16;
    public static float padding = 5;
    public static float margin = 2;
    public static float totalWidth = width + 2*(padding+margin);
    
}