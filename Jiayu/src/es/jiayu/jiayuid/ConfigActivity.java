package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

/**
 * Created by Fraggel on 10/08/13.
 */
public class ConfigActivity extends Activity implements CompoundButton.OnCheckedChangeListener{
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
    boolean notificaciones=true;
    CheckBox notificacionesChk = null;
    ImageButton imageButton;
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

        notificaciones=ajustes.getBoolean("notificaciones",true);
        notificacionesChk = (CheckBox) findViewById(R.id.notificacionChk);
        notificacionesChk.setOnCheckedChangeListener(this);
        if(notificaciones){
            notificacionesChk.setChecked(true);
        }else{
            notificacionesChk.setChecked(false);
        }
        notificacionesChk.setChecked(false);
        notificacionesChk.setEnabled(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editorAjustes=ajustes.edit();
        if(buttonView.isChecked()){
            editorAjustes.putBoolean("notificaciones",true);
        }else{
            editorAjustes.putBoolean("notificaciones",false);
        }
        editorAjustes.commit();
    }
}