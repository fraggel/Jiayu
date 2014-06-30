package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
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
import java.util.List;

import static es.jiayu.jiayuid.Utilidades.controlRoot;

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
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 86);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 120);
        }else if(dpi==480) {
            llp.setMargins(80, 350, 0, 176);
        }
        scText.setLayoutParams((llp));

        herram.setOnClickListener(this);
        herramBack.setOnClickListener(this);
        herramDisp.setOnClickListener(this);
        apks.setOnClickListener(this);
        if (controlRoot(getApplicationContext(),getResources(),"SCHerramientasUsu")) {
            isRoot = true;
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgOptDisabled),Toast.LENGTH_LONG).show();
        }
        if(!isRoot){
            herramBack.setEnabled(false);
            herramBack.setTextColor(Color.parseColor("#BDBDBD"));
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.herramientasUsuTxt) {
            try {
                Intent intent = new Intent(getApplicationContext(), SCHerramientasUsu.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.copiasSegTxt) {
            try {
                Intent intent = new Intent(getApplicationContext(), SCCopiaSeg.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.herramientasROMTxt) {
            try {
                Intent intent = new Intent(getApplicationContext(), SCHerramientasRom.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        }else if (button.getId()==R.id.apkTexto){

            try {
                Intent intent = new Intent(getApplicationContext(), SCApkInstall.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        }else if(button.getId()==R.id.imageButton){
            try {
                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
