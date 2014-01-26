package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Fraggel on 10/08/13.
 */
public class ConfigActivity extends Activity implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, AsyncResponse{
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
    boolean notificacionesNews=true;
    boolean notificacionesUpd=true;
    boolean firmaChk=false;
    CheckBox notificacionesChkNews = null;
    CheckBox notificacionesChkUpd = null;
    CheckBox firmarChk= null;
    ImageButton imageButton;
    Spinner languageSpn=null;
    String listaIdiomas[]=null;
    Button md5Btn=null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = this.getResources();
        setContentView(R.layout.activity_config);
        ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        imageButton = (ImageButton) findViewById(R.id.imageButton1);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });
        md5Btn=(Button)findViewById(R.id.downloadMD5Btn);
        md5Btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                descargarFirmas();
            }

        });
        notificacionesNews=ajustes.getBoolean("notificacionesNews",true);
        notificacionesChkNews = (CheckBox) findViewById(R.id.notificacionChkNews);
        notificacionesChkNews.setOnCheckedChangeListener(this);
        if(notificacionesNews){
            notificacionesChkNews.setChecked(true);
        }else{
            notificacionesChkNews.setChecked(false);
        }
        notificacionesUpd=ajustes.getBoolean("notificacionesUpd",true);
        notificacionesChkUpd = (CheckBox) findViewById(R.id.notificacionChkUpd);
        notificacionesChkUpd.setOnCheckedChangeListener(this);
        if(notificacionesUpd){
            notificacionesChkUpd.setChecked(true);
        }else{
            notificacionesChkUpd.setChecked(false);
        }
        languageSpn =(Spinner) findViewById(R.id.languageSpn);
        listaIdiomas=getResources().getStringArray(R.array.languages_values);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpn.setAdapter(adapter);
        languageSpn.setOnItemSelectedListener(this);
        firmaChk=ajustes.getBoolean("firmarChk",false);
        firmarChk = (CheckBox) findViewById(R.id.firmarChk);
        firmarChk.setOnCheckedChangeListener(this);
        if(firmaChk){
            firmarChk.setChecked(true);
            md5Btn.setEnabled(true);
        }else{
            firmarChk.setChecked(false);
            md5Btn.setEnabled(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editorAjustes=ajustes.edit();
        if(buttonView.getId()==R.id.notificacionChkNews){
            if(buttonView.isChecked()){
                editorAjustes.putBoolean("notificacionesNews",true);
            }else{
                editorAjustes.putBoolean("notificacionesNews",false);
            }
        }else if(buttonView.getId()==R.id.notificacionChkUpd){
            if(buttonView.isChecked()){
                editorAjustes.putBoolean("notificacionesUpd",true);
            }else{
                editorAjustes.putBoolean("notificacionesUpd",false);
            }
        }else if(buttonView.getId()==R.id.firmarChk){
            if(buttonView.isChecked()){
                editorAjustes.putBoolean("firmarChk",true);
                md5Btn.setEnabled(true);
                Toast.makeText(getApplicationContext(),R.string.msgMD5Jiayu,Toast.LENGTH_LONG).show();
            }else{
                editorAjustes.putBoolean("firmarChk",false);
                md5Btn.setEnabled(false);
            }
        }
        editorAjustes.commit();
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       Spinner spinner = (Spinner) adapterView;
        editorAjustes=ajustes.edit();
        if(!"".equals(listaIdiomas[i].trim())){
            Locale locale = new Locale(listaIdiomas[i]);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            editorAjustes.putInt("language", i);
            editorAjustes.commit();
            getApplicationContext().getResources().updateConfiguration(config,
                    getApplicationContext().getResources().getDisplayMetrics());
            /*Intent intent = new Intent(getApplicationContext(), App.class);
            startActivity(intent);*/
            onCreate(null);

        }
    }
    private void descargarFirmas(){
        try {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIniciandoDescarga), Toast.LENGTH_SHORT).show();
            MD5Thread asyncTask = new MD5Thread();
            asyncTask.delegate = this;
            asyncTask.execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 103", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void processFinish(String output) {

        if("firmaok".equals(output)){
            Toast.makeText(getApplicationContext(),R.string.msgMmd5Updated, Toast.LENGTH_SHORT).show();
        }

    }
}