package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by u028952 on 6/03/14.
 */
public class NoInternet extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nointernet);
    }
    protected void onResume() {
        super.onResume();
        String listaIdiomas[]=getResources().getStringArray(R.array.languages_values);
        SharedPreferences ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
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
    public void volver(View view) {
        super.onBackPressed();
    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.textView1);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
                /*if(dpi==240) {
                    llp.setMargins(0, 180, 0, 2);
                }else if(dpi==320) {
                    llp.setMargins(0, 250, 0, 2);
                }else if(dpi==480) {
                    llp.setMargins(0, 350, 0, 2);
                }*/
        if(dpi==240) {
            llp.setMargins(0, 175, 0, 2);
        }else if(dpi==320) {
            llp.setMargins(0, 230, 0, 2);
        }else if(dpi==480) {
            llp.setMargins(0, 350, 0, 2);
        }

        scText.setLayoutParams((llp));
        Button b1=(Button) findViewById(R.id.button1);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                b1.setPadding(240, 0, 0, 0);
            }else{
                b1.setPadding(120, 0, 0, 0);
            }
        }else if(dpi==320) {
            if(orientation==2) {
                b1.setPadding(270, 0, 0, 0);
            }else{
                b1.setPadding(200, 0, 0, 0);
            }
        }else if(dpi==480) {
            if(orientation==2) {
                b1.setPadding(550, 0, 0, 0);
            }else{
                b1.setPadding(290, 0, 0, 0);
            }
        }

    }
}