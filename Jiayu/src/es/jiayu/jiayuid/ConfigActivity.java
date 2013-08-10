package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by Fraggel on 10/08/13.
 */
public class ConfigActivity extends Activity implements CompoundButton.OnCheckedChangeListener{
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
    boolean notificaciones=true;
    CheckBox notificacionesChk = null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = this.getResources();
        setContentView(R.layout.activity_config);
        ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);

        notificaciones=ajustes.getBoolean("notificaciones",true);
        notificacionesChk = (CheckBox) findViewById(R.id.notificacionChk);
        notificacionesChk.setOnCheckedChangeListener(this);
        if(notificaciones){
            notificacionesChk.setChecked(true);
        }else{
            notificacionesChk.setChecked(false);
        }
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