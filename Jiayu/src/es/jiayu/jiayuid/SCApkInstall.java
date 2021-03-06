package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fraggel on 26/06/2014.
 */
public class SCApkInstall extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String modelo;
    SharedPreferences ajustes;
    Spinner apkSpn = null;
    Button apkBtn = null;
    ImageButton imageButton = null;
    String apkseleccionada = null;
    ArrayList<String> listaAppsUrl = new ArrayList<String>();
    List listaApps = new ArrayList();
    protected void onResume() {
        super.onResume();
        String listaIdiomas[]=getResources().getStringArray(R.array.languages_values);
        SharedPreferences ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
        int i=ajustes.getInt("language",0);
        Locale locale =null;
        if(i==0){
            locale=getResources().getConfiguration().locale;
        }else{
            locale = new Locale(listaIdiomas[i]);
        }


        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config,
                getApplicationContext().getResources().getDisplayMetrics());
        onCreate(null);
        apkSpn=(Spinner) findViewById(R.id.apkSpn);
        refreshCombos();
        modificarMargins();

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_apks);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");
        ajustes = getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/

        apkSpn = (Spinner) findViewById(R.id.apkSpn);
        apkBtn = (Button) findViewById(R.id.apkBtn);
        String externalStorageState = Environment.getExternalStorageState();
        if(!"mounted".equals(externalStorageState.toLowerCase())){
            apkSpn.setEnabled(false);
            apkBtn.setEnabled(false);
            apkBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }

        apkSpn.setOnItemSelectedListener(this);
        apkBtn.setOnClickListener(this);
        refreshCombos();
        modificarMargins();
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.apkBtn) {
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.fromFile(new File(apkseleccionada)), "application/vnd.android.package-archive");
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            getApplicationContext().startActivity(intent2);
            this.apkseleccionada = "";
            this.apkSpn.setSelection(0);
            apkBtn.setEnabled(false);
            apkBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }
    }
    public void refreshCombos() {
        listaApps.clear();
        listaAppsUrl.clear();
        listaApps.add(getResources().getString(R.string.downloadedAPKTxt));
        listaAppsUrl.add("");
        File f1 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/APP/");

        if (f1.exists()) {
            if (f1.listFiles().length > 0) {
                for (int x = 0; x < f1.listFiles().length; x++) {
                    File fx = (File) f1.listFiles()[x];
                    if (!fx.isDirectory() && fx.isFile()) {
                        listaApps.add(fx.getName());
                        listaAppsUrl.add(fx.getAbsolutePath());
                    }
                }
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaApps);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apkSpn.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.apkSpn) {
            if (listaAppsUrl != null && listaAppsUrl.size() > 0) {
                String apkselec = listaAppsUrl.get(i);
                if (!"".equals(apkselec.trim())) {
                    apkBtn.setEnabled(true);
                    apkBtn.setTextColor(Color.BLACK);
                    this.apkseleccionada = apkselec;
                } else {
                    apkBtn.setEnabled(false);
                    apkBtn.setTextColor(Color.parseColor("#BDBDBD"));
                    this.apkseleccionada = "";
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.apkSpn) {
            this.apkseleccionada = null;
        }

    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi>=160 && dpi<240){
        }else if(dpi>=240 && dpi<320) {
            llp.setMargins(40, 0, 0, 94);
        }else if(dpi>=320 && dpi<480) {
            llp.setMargins(50, 0, 0, 130);
        }else if(dpi>=480 && dpi<680) {
            llp.setMargins(80, 0, 0, 190);
        }
        scText.setLayoutParams((llp));


        Button b1=(Button) findViewById(R.id.apkBtn);
        ImageButton b2=(ImageButton) findViewById(R.id.imageButton1);

        int orientation = getResources().getConfiguration().orientation;
        if(dpi>=160 && dpi<240){
        }else if(dpi>=240 && dpi<320) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);

            }
        }else if(dpi>=320 && dpi<480) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);

            }
        }else if(dpi>=480 && dpi<680) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);

            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);

            }
        }

    }
}