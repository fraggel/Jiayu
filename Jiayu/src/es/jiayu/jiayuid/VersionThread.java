package es.jiayu.jiayuid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.os.AsyncTask;

public class VersionThread extends AsyncTask<String, Void, String> {
	public AsyncResponse delegate=null;
	String urlActualizacion="";
	String version="";
	@Override
	protected String doInBackground(String... params) {
		String result = "";
		InputStreamReader isr=null;
		BufferedReader in =null;
	    try{
	        URL jsonUrl = new URL("http://www.jiayu.es/jiayuapkversion.txt");
	        in=new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
	        result=in.readLine();
	        urlActualizacion=in.readLine();
	        if(in!=null){
	        	in.close();
	        }
	    }catch(Exception ex){
	        result = "TIMEOUT";
	        urlActualizacion="";
	        try {
	        	if(in!=null){
		        	in.close();
		        }
		        if(isr!=null){
		        	isr.close();
		        }
	        } catch (Exception e) {
				// TODO: handle exception
			}
	    }
	    result=result.replaceAll("\t", "").trim();
	    if(params[0].equals(result)){
	    	urlActualizacion="";
	    }
		return result+"----"+urlActualizacion.trim();
	}
	@Override
	protected void onPostExecute(String result) {
		delegate.processFinish(result);
	}
}
