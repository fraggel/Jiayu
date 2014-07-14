package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static es.jiayu.jiayuid.Utilidades.controlRoot;
import static es.jiayu.jiayuid.Utilidades.controlRootConExec;

/**
 * Created by Fraggel on 26/06/2014.
 */
public class SecondScreen extends Activity implements View.OnClickListener {
    String modelo;
    SharedPreferences ajustes;
    ImageButton imageButton = null;
    Button herram;
    Button herramBack;
    Button herramDisp;
    Button apks;
    boolean isRoot=false;

    protected void onResume() {
        super.onResume();
        String listaIdiomas[]=getResources().getStringArray(R.array.languages_values);
        ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
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
        modificarMargins();

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_romtools);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");
        ajustes = getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        herram=(Button)findViewById(R.id.herramientasUsuTxt);
        herramBack=(Button)findViewById(R.id.copiasSegTxt);
        herramDisp=(Button)findViewById(R.id.herramientasROMTxt);
        apks=(Button)findViewById(R.id.apkTexto);
        isRoot=intent.getExtras().getBoolean("root");
        modificarMargins();

        herram.setOnClickListener(this);
        herramBack.setOnClickListener(this);
        herramDisp.setOnClickListener(this);
        apks.setOnClickListener(this);
        if(!isRoot){
            herramBack.setEnabled(false);
            herramBack.setTextColor(Color.parseColor("#BDBDBD"));
        }else{
            herramBack.setEnabled(true);
            herramBack.setTextColor(Color.BLACK);
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.herramientasUsuTxt) {
            try {
                Intent intent = new Intent(getApplicationContext(), SCHerramientasUsu.class);
                intent.putExtra("modelo",modelo);
                intent.putExtra("root",isRoot);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.copiasSegTxt) {
            try {
                Intent intent = new Intent(getApplicationContext(), SCCopiaSeg.class);
                intent.putExtra("modelo",modelo);
                intent.putExtra("root",isRoot);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.herramientasROMTxt) {
            try {
                Intent intent = new Intent(getApplicationContext(), SCHerramientasRom.class);
                intent.putExtra("modelo",modelo);
                intent.putExtra("root",isRoot);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        }else if (button.getId()==R.id.apkTexto){

            try {
                Intent intent = new Intent(getApplicationContext(), SCApkInstall.class);
                intent.putExtra("modelo",modelo);
                intent.putExtra("root",isRoot);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        }/*else if(button.getId()==R.id.imageButton){
            try {
                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        }*/
    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 84);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 350, 0, 190);
        }
        scText.setLayoutParams((llp));

        Button b1=(Button) findViewById(R.id.herramientasUsuTxt);
        Button b2=(Button) findViewById(R.id.copiasSegTxt);
        Button b3=(Button) findViewById(R.id.herramientasROMTxt);
        Button b4=(Button) findViewById(R.id.apkTexto);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);
                b2.setPadding(250, 0, 0, 0);
                b3.setPadding(250, 0, 0, 0);
                b4.setPadding(250, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);
                b2.setPadding(140, 0, 0, 0);
                b3.setPadding(140, 0, 0, 0);
                b4.setPadding(140, 0, 0, 0);
            }
        }else if(dpi==320) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(400, 0, 0, 0);
                b2.setPadding(400, 0, 0, 0);
                b3.setPadding(400, 0, 0, 0);
                b4.setPadding(400, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);
                b2.setPadding(250, 0, 0, 0);
                b3.setPadding(250, 0, 0, 0);
                b4.setPadding(250, 0, 0, 0);
            }
        }else if(dpi==480) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);
                b2.setPadding(570, 0, 0, 0);
                b3.setPadding(570, 0, 0, 0);
                b4.setPadding(570, 0, 0, 0);
            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);
                b3.setPadding(350, 0, 0, 0);
                b4.setPadding(350, 0, 0, 0);
            }
        }

    }
}
