package es.jiayu.jiayuid;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MD5Thread extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    String urlActualizacion = "";

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String updateContenido="";
        InputStreamReader isr = null;
        BufferedReader in = null;
        FileOutputStream fos=null;
        String inicio="firmanok";
        try {
            File f1 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/CHECKSUM.md5");
            boolean deleted=f1.delete();
            fos=new FileOutputStream(f1);
            URL jsonUrl = new URL("http://www.jiayu.es/desarrollo/CHECKSUM.md5");
            in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
            result = in.readLine();
            while(result!=null){
                fos.write((result+"\n").getBytes());
                result = in.readLine();
            }
            fos.flush();
            fos.close();
            if (isr != null) {
                isr.close();
            }
            inicio="firmaok";
        } catch (Exception ex) {
            urlActualizacion = "";
            try {
                if (isr != null) {
                    isr.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if(fos!=null){
                    fos.close();
                }
            } catch (Exception e) {
                if (isr != null) {
                    try {
                        isr.close();
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            inicio="firmanok";
        }
        return inicio;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
