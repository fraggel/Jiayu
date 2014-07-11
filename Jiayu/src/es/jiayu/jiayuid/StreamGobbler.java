package es.jiayu.jiayuid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread
{
	

    public StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
        this.line="";
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            line = br.readLine();
            /*while( br.readLine()!=null){
                this.line=line;
                line = br.readLine();
            }*/
            /*for(String line = null; (line = br.readLine()) != null;)
                this.line=line;*/

        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    public String getLine(){
    	return line;
    }
    InputStream is;
    String type;
    String line;
}