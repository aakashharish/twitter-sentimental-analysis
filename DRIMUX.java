/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drimux;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 *
 * @author Elcot
 */
public class DRIMUX {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        System.out.println("\t\t\t****************");
        System.out.println("\t\t\t    DRIMUX");
        System.out.println("\t\t\t****************");                           
        
        /****************************************************************************************************************/
        System.out.println("===================================");
        System.out.println("\t1) Extract Tweets");
        System.out.println("===================================");
        
        ArrayList alltweets=new ArrayList();
        
        String qry=JOptionPane.showInputDialog(new JFrame(),"Enter the Keyword: ");
        
        try
        {                       
            String str="";
            Twitter twitter1 = new TwitterFactory().getInstance();
            Query query = new Query(qry);
            query.setCount(200);
            QueryResult result = twitter1.search(query);
            for (Status status : result.getTweets())
            {                                        
                String sg=status.getText().trim();                    
                if(!alltweets.contains(sg.trim()))
                {
                    String time=status.getCreatedAt().toString().trim();
                    System.out.println(sg.trim());
                    str=str+ time.trim() + " --> " +"@"+  status.getUser().getScreenName() + ":" + status.getText()+"\n\n";
                    alltweets.add(status.getText().trim());                        
                }
            }
            System.out.println("=====================================================");
            System.out.println("\t Extracted Tweets of Keyword - "+qry.trim());
            System.out.println("=====================================================");
            System.out.println(str.trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        /****************************************************************************************************************/
        
        System.out.println();
        System.out.println("=========================================");
        System.out.println("\t2) Extract Hashtags from Tweets");
        System.out.println("=========================================");
        
        ArrayList allHashtags=new ArrayList();
        for(int i=0;i<alltweets.size();i++)
        {
            String s=alltweets.get(i).toString().trim();
            String sp[]=s.trim().replaceAll("\n"," ").split(" ");
            for(int j=0;j<sp.length;j++)
            {
                if(sp[j].trim().startsWith("#"))
                {
                    if(!(allHashtags.contains(sp[j].trim())))
                    {
                        allHashtags.add(sp[j].trim());
                        System.out.println(sp[j].trim());
                    }
                }
            }
        }
        
        System.out.println();
        System.out.println("=====================================");
        System.out.println("\t3) Retweet Extraction");
        System.out.println("=====================================");
        
        ArrayList allretweets=new ArrayList();
        for(int i=0;i<alltweets.size();i++)
        {
            String tweet=alltweets.get(i).toString().trim();
            if(tweet.trim().contains("RT"))
            {
                allretweets.add(tweet.trim());
                System.out.println(tweet.trim());
            }
        }
        
        System.out.println();
        System.out.println("===================================================");
        System.out.println("\t4) Extract Hashtags from Retweet");
        System.out.println("===================================================");
        
        ArrayList allHashtagsaftRe=new ArrayList();
        
        for(int i=0;i<allretweets.size();i++)
        {
            String s=allretweets.get(i).toString().trim();
            String sp[]=s.trim().split(" ");
            for(int j=0;j<sp.length;j++)
            {
                if(sp[j].trim().startsWith("#"))
                {
                    String hashtag=sp[j].trim();
                    if(!(hashtag.trim().equals("")))
                    {
                        if(!(allHashtagsaftRe.contains(hashtag.trim())))
                        {
                            allHashtagsaftRe.add(hashtag.trim());
                            System.out.println(hashtag.trim());
                        }
                    }
                }
            }
        }
        
        System.out.println();
        System.out.println("===================================================");
        System.out.println("\t5) Calcuate Hastags Influence");
        System.out.println("===================================================");
        
        ArrayList allWords=new ArrayList();
        for(int i=0;i<alltweets.size();i++)
        {
            String s=alltweets.get(i).toString().trim();
            String sp[]=s.trim().split(" ");
            for(int j=0;j<sp.length;j++)
            {
                String word=sp[j].trim().replaceAll("[^\\w\\s]", "");
                allWords.add(word.toLowerCase().trim());
            }
        }
        
        ArrayList allHashtagsinfluence=new ArrayList();
        System.out.println("Hashtag"+"\t-->\t"+"Influence");
        for(int i=0;i<allHashtagsaftRe.size();i++)
        {
            String hashtag=allHashtagsaftRe.get(i).toString().trim();
            String topic=hashtag.trim().replaceAll("#","").replaceAll("[^\\w\\s]", "");
            int influence=Collections.frequency(allWords,topic.toLowerCase().trim());
            allHashtagsinfluence.add(influence);
            System.out.println(hashtag.trim()+"\t-->\t"+influence);
        }
        
        System.out.println();
        System.out.println("=================================================================================");
        System.out.println("\t6) Greedy & Dynamic Blocking Algorithms (for Detect & Block Rumours)");
        System.out.println("=================================================================================");
          
        long start=System.currentTimeMillis();
        int Threshold=1;
        
        ArrayList secureTweets=new ArrayList();     // VB
        
        for(int i=0;i<alltweets.size();i++)     // Initial Edge Matrix A0
        {
            String tweet=alltweets.get(i).toString().trim().replaceAll("\n", " ");
            //System.out.println("tweet: "+tweet);
            
            ArrayList availableHashtags=new ArrayList();
            String sp[]=tweet.trim().split(" ");
            for(int j=0;j<sp.length;j++)
            {
                if(sp[j].trim().contains("#"))
                {
                    if(!(availableHashtags.contains(sp[j].trim())))
                    {
                        availableHashtags.add(sp[j].trim());
                    }
                }
            }
            
            double val=0;
            int sz=0;
            for(int j=0;j<availableHashtags.size();j++)
            {
                String hash=availableHashtags.get(j).toString().trim();
                int index=allHashtagsaftRe.indexOf(hash.trim());
                if(index>=0)
                {
                    String influ=allHashtagsinfluence.get(index).toString().trim();
                    double inf=Double.parseDouble(influ.trim());
                    if(inf>Threshold)
                    {
                        val=val+inf;
                        sz++;
                    }
                }
            }
            
            //System.out.println("val: "+val);
            //System.out.println("sz: "+sz);
            //double totinfluence=val/(double)sz;
            //System.out.println("totinfluence: "+totinfluence);
            String mainResult="Secure";
            if(val==0)
            {
                if(!(tweet.trim().contains("#")))
                {
                    mainResult="Secure";
                }
                else
                {
                    mainResult="Rumour";
                }
            }           
            //System.out.println("maniResult: "+maniResult);         
            System.out.println(tweet.trim()+" --> "+mainResult.trim());
            if(mainResult.trim().equals("Secure"))
            {
                secureTweets.add(tweet.trim());
            }
        }
        
        System.out.println();
        System.out.println("===================================================");
        System.out.println("\t7) Tweets after Blocking Rumours");
        System.out.println("===================================================");
        
        for(int i=0;i<secureTweets.size();i++)
        {
            String s=secureTweets.get(i).toString().trim();
            System.out.println(s.trim());
        }
        
        long stop=System.currentTimeMillis();
        long rumourBlockingtime=stop-start;
        System.out.println();
        System.out.println();
        System.out.println("Rumour Blocking Time: "+rumourBlockingtime+" ms");
        
        int rumourTweetsSize=alltweets.size()-secureTweets.size();
        double infectionRatio=(double)((double)rumourTweetsSize/(double)alltweets.size())*100;        
        System.out.println("Infection Ratio: "+infectionRatio+" %");  
        
        System.out.println();
        System.out.println("===================================================");
        System.out.println("\t8) Sentiment Classification");
        System.out.println("===================================================");
        
        ArrayList posWd=new ArrayList();
        ArrayList negWd=new ArrayList();
        ArrayList slang1=new ArrayList();
        ArrayList slang2=new ArrayList();
        ArrayList stop1=new ArrayList();
        
        try
        {
             //// Read Posivie words
             
            File fe1=new File("Positive.txt");
            FileInputStream fis1=new FileInputStream(fe1);
            byte data1[]=new byte[fis1.available()];
            fis1.read(data1);
            fis1.close();
            
            String sg1[]=new String(data1).split("\n");
               
            for(int i=0;i<sg1.length;i++)
                posWd.add(sg1[i].trim());
             
             ///// Read negative word
             
            File fe2=new File("Negative.txt");
            FileInputStream fis2=new FileInputStream(fe2);
            byte data2[]=new byte[fis2.available()];
            fis2.read(data2);
            fis2.close();
            
            String sg2[]=new String(data2).split("\n");
               
            for(int i=0;i<sg2.length;i++)
                negWd.add(sg2[i].trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            File fe=new File("Slang.txt");
            FileInputStream fis=new FileInputStream(fe);
            byte data[]=new byte[fis.available()];
            fis.read(data);
            fis.close();
              
            String s1[]=new String(data).split("\n");            
            
            for(int i=0;i<s1.length;i++)
            {
                String g1[]=s1[i].trim().split("#");
                slang1.add(g1[0].trim());
                slang2.add(g1[1].trim());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            File fe2=new File("stopwords1.txt");
            FileInputStream fis2=new FileInputStream(fe2);
            byte data2[]=new byte[fis2.available()];
            fis2.read(data2);
            fis2.close();
                
            String sg2[]=new String(data2).split("\n");
               
            for(int i=0;i<sg2.length;i++)
                stop1.add(sg2[i].trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        for(int i=0;i<secureTweets.size();i++)
        {
            String s=secureTweets.get(i).toString().trim();
            String sentence=s.trim().toLowerCase().trim().replaceAll("[^\\w\\s]", " ");
            String status="Positive";
            int pos=0; int neg=0;
            String sen[]=sentence.trim().split(" ");
            for(int j=0;j<sen.length;j++)
            {
                if(!(stop1.contains(sen[j].trim())))          // stopwords removal
                {
                    if(slang1.contains(sen[j].trim()))        // slang word removal
                    {
                        int ind1=slang1.indexOf(sen[j].trim());
                        sen[j]=slang2.get(ind1).toString().trim();                    
                    }
                    
                    if(posWd.contains(sen[j].trim()))
                    {
                        pos++;
                    }
                    if(negWd.contains(sen[j].trim()))
                    {
                        neg++;
                    }
                }
            }
            if(pos>neg)
            {
                status="Positive";                    
            }
            else
            {
                status="Negative";                    
            }
            System.out.println(s.trim()+" --> "+status.trim());
        }
        System.out.println("===================================================");
    }    
}
