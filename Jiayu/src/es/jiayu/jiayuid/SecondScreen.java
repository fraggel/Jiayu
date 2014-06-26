package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.List;

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

        herram.setOnClickListener(this);
        herramBack.setOnClickListener(this);
        herramDisp.setOnClickListener(this);
        apks.setOnClickListener(this);
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
