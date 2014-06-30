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
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static es.jiayu.jiayuid.Utilidades.controlRoot;

/**
 * Created by Fraggel on 26/06/2014.
 */
public class SCHerramientasUsu extends Activity implements View.OnClickListener {
    String modelo;
    SharedPreferences ajustes;
    ImageButton imageButton = null;
    Button ingenieroBtn = null;
    Button abrirExploradorBtn = null;
    Button bootAnimationBtn=null;
    Button toolsAndroidBtn=null;
    boolean isRoot = false;

    protected void onResume() {
        super.onResume();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tools);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");
        ajustes = getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        if (!controlRoot(getApplicationContext(),getResources(),"SCHerramientasUsu")) {
            isRoot = true;
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgOptDisabled),Toast.LENGTH_LONG).show();
        }
        abrirExploradorBtn = (Button) findViewById(R.id.filesBtn);
        bootAnimationBtn=(Button) findViewById(R.id.bootAnimationBtn);
        String externalStorageState = Environment.getExternalStorageState();
        if(!"mounted".equals(externalStorageState.toLowerCase())){
            abrirExploradorBtn.setEnabled(false);
            abrirExploradorBtn.setTextColor(Color.parseColor("#BDBDBD"));
            bootAnimationBtn.setEnabled(false);
            bootAnimationBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }
        ingenieroBtn = (Button) findViewById(R.id.ingenieroBtn);
        toolsAndroidBtn=(Button)findViewById(R.id.toolsAndroidBtn);
        if (!isRoot) {
            bootAnimationBtn.setEnabled(false);
            bootAnimationBtn.setTextColor(Color.parseColor("#BDBDBD"));
            toolsAndroidBtn.setEnabled(false);
            toolsAndroidBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }
        ingenieroBtn.setOnClickListener(this);
        abrirExploradorBtn.setOnClickListener(this);
        bootAnimationBtn.setOnClickListener(this);
        toolsAndroidBtn.setOnClickListener(this);
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/
        TextView scText=(TextView) findViewById(R.id.herramientasUsuTxt);
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
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.filesBtn) {
            String application_name = "";
            try {

                application_name = "com.mediatek.filemanager.FileManagerOperationActivity";
                Intent intent = new Intent("android.intent.action.MAIN");
                List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                boolean existe = false;
                for (ResolveInfo info : resolveinfo_list) {
                    if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.filemanager")) {
                        if (info.activityInfo.name.equalsIgnoreCase(application_name)) {
                            Intent launch_intent = new Intent("android.intent.action.MAIN");
                            launch_intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                            launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.startActivity(launch_intent);
                            existe = true;
                            break;
                        }
                    }
                }
                if (!existe) {
                    //TODO
                    //abrir el selector de explorador
                    Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent2.setType("file/*");
                    this.startActivity(intent2);
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIngenieroNoExiste), Toast.LENGTH_SHORT).show();
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError) + application_name+" 164", Toast.LENGTH_SHORT).show();
            }
        } else if(button.getId()==R.id.ingenieroBtn){
            String application_name="";
            try{

                application_name="com.mediatek.engineermode.EngineerMode";
                Intent intent = new Intent("android.intent.action.MAIN");
                List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                boolean existe=false;
                for(ResolveInfo info:resolveinfo_list){
                    if(info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.engineermode")){
                        if(info.activityInfo.name.equalsIgnoreCase(application_name)){
                            Intent launch_intent = new Intent("android.intent.action.MAIN");
                            launch_intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                            launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            this.startActivity(launch_intent);
                            existe=true;
                            break;
                        }
                    }
                }
                if(!existe){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIngenieroNoExiste),Toast.LENGTH_SHORT).show();
                }
            }catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+application_name+" 166",Toast.LENGTH_SHORT).show();
            }
        }else if(button.getId()==R.id.bootAnimationBtn){
            try {
                Intent intent = new Intent(this, BootAnimation.class);
                intent.putExtra("modelo",modelo);
                intent.putExtra("tipo","bootanimation");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 167", Toast.LENGTH_SHORT).show();
            }
        }else if(button.getId()==R.id.toolsAndroidBtn){
            try {
                Intent intent = new Intent(this, ToolsAndroid.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" FRAGGEL", Toast.LENGTH_SHORT).show();
            }
        }
    }
}