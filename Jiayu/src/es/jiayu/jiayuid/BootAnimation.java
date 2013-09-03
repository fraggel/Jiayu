package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fraggel on 25/08/13.
 */
public class BootAnimation extends Activity {
    Spinner bootSpn = null;
    Button bootBtn = null;

    ImageButton imageButton = null;
    String bootSeleccionada=null;
    ArrayList<String> listaBootsUrl = new ArrayList<String>();
    List listaBootsUrlZip=new ArrayList();
    String modelo=null;
    String tipo=null;


    boolean isRoot = false;
    String path = "";
    protected void onResume() {
        super.onResume();

        bootSpn = (Spinner) findViewById(R.id.bootSpn);
        refreshCombos();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootanimation);
        modelo = getIntent().getExtras().getString("modelo");
        tipo = getIntent().getExtras().getString("tipo");
        if (controlRoot()) {
            isRoot = true;
        }
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });
    }
    private boolean controlRoot() {
        boolean rootB = false;
        File f = new File("/system/bin/su");
        if (!f.exists()) {
            f = new File("/system/xbin/su");
            if (f.exists()) {
                rootB = true;
            }
        } else {
            rootB = true;
        }
        if (rootB) {
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("su");
            } catch (Exception e) {
            }
        }
        return rootB;
    }
    public void refreshCombos() {
        listaBootsUrl.clear();
        listaBootsUrlZip.clear();


        listaBootsUrl.add(getResources().getString(R.string.seleccionaValue));
        listaBootsUrlZip.add("");


        File f3 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/BOOTANIMATION/");
        if (f3.exists()) {
            if (f3.listFiles().length > 0) {
                for (int x = 0; x < f3.listFiles().length; x++) {
                    File fx = (File) f3.listFiles()[x];
                    if (!fx.isDirectory() && fx.isFile()) {
                        listaBootsUrl.add(fx.getName());
                        listaBootsUrlZip.add(fx.getAbsolutePath());
                    }
                }
            }
        }

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaBootsUrl);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bootSpn.setAdapter(dataAdapter3);

    }
}