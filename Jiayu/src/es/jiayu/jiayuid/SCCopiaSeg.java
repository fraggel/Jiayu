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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static es.jiayu.jiayuid.Utilidades.comprobarRecovery;

/**
 * Created by Fraggel on 26/06/2014.
 */
public class SCCopiaSeg extends Activity implements View.OnClickListener {
    String modelo;
    SharedPreferences ajustes;
    ImageButton imageButton = null;
    Button backupBtn=null;
    Button imeiBtn = null;
    boolean isRoot = false;
    boolean detectRecovery=false;
    String recoveryDetectado="ori";
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
        modificarMargins();

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_backup);
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

        if (Utilidades.controlRoot(getApplicationContext(),getResources(),"RomTools")) {
            isRoot = true;
        }else{
            isRoot=false;
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgOptDisabled),Toast.LENGTH_LONG).show();
        }
        backupBtn=(Button) findViewById(R.id.backupBtn);
        imeiBtn = (Button) findViewById(R.id.imeiBtn);
        String externalStorageState = Environment.getExternalStorageState();
        if(!"mounted".equals(externalStorageState.toLowerCase())){
            backupBtn.setEnabled(false);
            backupBtn.setTextColor(Color.parseColor("#BDBDBD"));
            imeiBtn.setEnabled(false);
            imeiBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }else{
            backupBtn.setEnabled(true);
            backupBtn.setTextColor(Color.BLACK);
            imeiBtn.setEnabled(true);
            imeiBtn.setTextColor(Color.BLACK);
        }

        if (!isRoot) {
            backupBtn.setEnabled(false);
            backupBtn.setTextColor(Color.parseColor("#BDBDBD"));
            imeiBtn.setEnabled(false);
            imeiBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }else{
            backupBtn.setEnabled(true);
            backupBtn.setTextColor(Color.BLACK);
            imeiBtn.setEnabled(true);
            imeiBtn.setTextColor(Color.BLACK);
        }
        backupBtn.setOnClickListener(this);
        imeiBtn.setOnClickListener(this);
        if(isRoot){
            if("mounted".equals(externalStorageState.toLowerCase())) {
                if (ajustes.getBoolean("recoveryChk", false)) {
                    detectRecovery = true;
                    recoveryDetectado = comprobarRecovery(getApplicationContext(), getResources(), "RomTools");

                } else {
                    detectRecovery = false;
                }
                if (detectRecovery) {
                    if ("cwm".equals(recoveryDetectado)) {
                        backupBtn.setEnabled(true);
                        backupBtn.setTextColor(Color.parseColor("#BDBDBD"));
                        //chkCWM.setVisibility(View.INVISIBLE);
                    } else if ("crl".equals(recoveryDetectado)) {
                        backupBtn.setEnabled(true);
                        backupBtn.setTextColor(Color.parseColor("#BDBDBD"));
                        //chkCWM.setVisibility(View.INVISIBLE);
                    } else if ("ori".equals(recoveryDetectado)) {
                        backupBtn.setEnabled(false);
                        backupBtn.setTextColor(Color.BLACK);
                        //chkCWM.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        modificarMargins();
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if(button.getId()==R.id.backupBtn){
            try {
                Intent intent = new Intent(this, BackupRestore.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" FRAGGEL", Toast.LENGTH_SHORT).show();
            }

        }else if (button.getId() == R.id.imeiBtn) {
            try {
                Intent intent = new Intent(this, ImeiScreen.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 161", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 86);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 350, 0, 176);
        }
        scText.setLayoutParams((llp));


        Button b1=(Button) findViewById(R.id.imeiBtn);
        Button b2=(Button) findViewById(R.id.backupBtn);

        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);
                b2.setPadding(250, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);
                b2.setPadding(140, 0, 0, 0);

            }
        }else if(dpi==320) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);
                b2.setPadding(200, 0, 0, 0);

            }
        }else if(dpi==480) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);
                b2.setPadding(570, 0, 0, 0);

            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);

            }
        }

    }
}