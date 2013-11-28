package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fraggel on 28/11/13.
 */
public class SendInformeActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enviarEmailErrores();

}
    public void enviarEmailErrores() {
        Resources res = getResources();
        String texto=obtenerTextoMail();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[] { res.getString(R.string.emailContacto) });
        i.putExtra(Intent.EXTRA_SUBJECT,
                res.getString(R.string.envioErrores));
        i.putExtra(Intent.EXTRA_TEXT, texto);
        try {
            startActivity(Intent.createChooser(i,
                    res.getString(R.string.envioErrores)));
        } catch (android.content.ActivityNotFoundException ex) {

        }
        this.finish();

    }
    private String obtenerTextoMail(){
        String textoTMP="";

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;

        textoTMP+="CPU:\n"+"*"+getInfoCPU()+"*\n";
        textoTMP+="CPUFREQ:\n"+"*"+getCPUFreq()+"*\n";
        textoTMP+="RAM:\n"+"*"+(Integer.parseInt(getTotalRAM()) / 1000)+"*\n";
        textoTMP+="DIMENSIONES PANTALLA:\n"+"*"+height+"x"+width+"*\n";
        String modelo = Build.MODEL;
        String disp = Build.DISPLAY;
        android.hardware.Camera cam = android.hardware.Camera.open(1);
        List<Camera.Size> supportedPictureSizes = cam.getParameters().getSupportedPictureSizes();
        int result = -1;
        for (Iterator iterator = supportedPictureSizes
                .iterator(); iterator
                     .hasNext(); ) {
            Camera.Size sizes = (Camera.Size) iterator.next();
            result = sizes.width;

        }
        cam.release();
        textoTMP+="CAMARA:\n"+"*"+result+"*\n";
        textoTMP+="MODEL:\n"+"*"+modelo+"*\n";
        textoTMP+="DISPLAY:\n"+"*"+disp+"*\n";



        return textoTMP;
    }
    public static String getTotalRAM(){
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            load = load.replaceAll(" ", "");
            int indexOf = load.indexOf(":");
            int indexOf2 = load.toLowerCase().indexOf("kb");
            load = load.substring(indexOf + 1, indexOf2);
            load = load.trim();
        } catch (IOException ex) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }catch (Exception e){}

            }
        }
        return load;
    }

    public static String getInfoCPU(){
        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/proc/cpuinfo", "r");
            while (load.toLowerCase().indexOf("hardware") == -1) {
                load = reader.readLine();
            }

            load = load.replaceAll(" ", "");
            load = load.replaceAll("\t", "");
            load = load.toLowerCase();
            int indexOf = load.indexOf(":");
            int indexOf2 = load.length();
            load = load.substring(indexOf + 1, indexOf2);
        } catch (IOException ex) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }catch (Exception e){}
            }
        }
        return load.trim();
    }

    public static String getCPUFreq(){
        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            load = reader.readLine();
            int cpufreq = Integer.parseInt(load.trim());
            load=String.valueOf(cpufreq);
        } catch (IOException ex) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }catch (Exception e){}
            }
        }
        return load.trim();
    }

}