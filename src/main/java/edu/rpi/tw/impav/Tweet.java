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
    public List<String> labels;
    
    public List<String> lines = null;
    
    public static int width = 250;
    public static float textSize = 16;
    public static float padding = 5;
    public static float margin = 2;
    public static float totalWidth = width + 2*(padding+margin);
    
//    public float draw(PGraphics graphics, float y, Graph g) {
//        float x = g.getWidth() + margin; //- 1 - width - (2*padding+margin);
//        
//        long age = new Date().getTime() - added.getTime();
//        double highlightFade = Math.pow(2, -(double)age/500f);
//
//        
//        // Flash red and then fade to a washed-out gray/blue/purple, 
//        // and decay the alpha to 0. 
//        graphics.fill((float)(56f*highlightFade + 200), 
//                      (float)((150-150*highlightFade)+50), 
//                      (float)((200-200*highlightFade)+50), 
//                      (float)128f);
//        graphics.strokeWeight(1);
//        graphics.textSize(textSize);
//        graphics.stroke((float)(56f*highlightFade + 200), 
//                (float)((150-150*highlightFade)+50), 
//                (float)((200-200*highlightFade)+50), 
//                (float)200f);
//
//        String username = creator.getScreenName();
//        String renderString = username + " " + text;
//        if (lines == null)
//            lines = wordWrap(renderString, width, graphics);
//        y += margin;
//        graphics.rect(x, y, width+2*padding, textSize*lines.size()+2*padding);
//        //graphics.rect(x, y, width+2*padding, textSize+2*padding);
//
//        // Pad for text.
//        x += padding;
//        y += padding;
//                
//        float firstLine = y;
//
//        graphics.fill(255, 255, 255, 150f);
//        //graphics.text(renderString,x, y+textSize*.75f);
//        //y += textSize;
//        for (String line : lines) {
//            graphics.text(line,x, y+textSize*.75f);
//            y += textSize;
//        }
//
//        // highlight the username.
//        graphics.fill(255, 255, 255, 255);
//        graphics.text(username,x, firstLine+textSize*0.75f);
//        
//        y += padding + margin;
//        return y;
//    }
    
    /** 
     * A simple greedy algorithm for wrapping text, using a regex to split on whitespace.
     * 
     * @param s String to wrap.
     * @param maxWidth Maximum width of the rendered text.
     * @param graphics The graphics object to compute the text width against.
     * @return List A list of lines for the wrapped text.
     */
//    List<String> wordWrap(String s, float maxWidth, PGraphics graphics) {
//        List<String> result = new LinkedList<String>();
//        StringBuilder currentLine = new StringBuilder();
//        for (String word : s.split("\\s+")) {
//            String lineText = currentLine.toString();
//            if (currentLine.length() > 0) 
//                currentLine.append(" ");
//            currentLine.append(word);
//            float size = graphics.textWidth(currentLine.toString());
//            if (size > maxWidth) {
//                result.add(lineText);
//                currentLine = new StringBuilder();
//                currentLine.append(word);
//            }
//        }
//        if (currentLine.length() > 0) result.add(currentLine.toString());
//        return result;
//    }
}