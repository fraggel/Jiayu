package es.jiayu.jiayuid;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class DataApp extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String updateContenido="";
        InputStreamReader isr = null;
        BufferedReader in = null;
        try {
            System.out.println(params[0]);
            URL jsonUrl = new URL(params[0]);
            in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
            result = in.readLine();
            while(in.readLine()!=null){
                result = result+in.readLine();
            }

            System.out.println(result);
        } catch (Exception ex) {

        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
