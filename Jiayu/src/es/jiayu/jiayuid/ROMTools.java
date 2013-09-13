package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by u028952 on 24/07/13.
 */
public class ROMTools extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner apkSpn = null;
    Button apkBtn = null;
    ImageButton imageButton = null;
    Button recoveryBtn = null;
    Button imeiBtn = null;
    Button romBtn = null;
    Button ingenieroBtn = null;
    Button abrirExploradorBtn = null;
    Button rebootRecoveryBtn = null;
    Button bootAnimationBtn=null;
    String modelo=null;
    String apkseleccionada = null;
    ArrayList<String> listaAppsUrl = new ArrayList<String>();
    List listaApps = new ArrayList();

    boolean isRoot = false;
    protected void onResume() {
        super.onResume();
        apkSpn = (Spinner) findViewById(R.id.apkSpn);
        refreshCombos();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_romtools);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");
        deleteDirectories();
        if (controlRoot()) {
            isRoot = true;
            if (!controlBusybox()) {
                //instalarBusyBox();
            }
        }
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });
        apkSpn = (Spinner) findViewById(R.id.apkSpn);
        apkBtn = (Button) findViewById(R.id.apkBtn);
        recoveryBtn = (Button) findViewById(R.id.recoveryBtn);
        imeiBtn = (Button) findViewById(R.id.imeiBtn);
        romBtn = (Button) findViewById(R.id.romBtn);
        ingenieroBtn = (Button) findViewById(R.id.ingenieroBtn);
        abrirExploradorBtn = (Button) findViewById(R.id.filesBtn);
        rebootRecoveryBtn = (Button) findViewById(R.id.rebootRecoveryBtn);
        bootAnimationBtn=(Button) findViewById(R.id.bootAnimationBtn);

        if (!isRoot) {
            recoveryBtn.setVisibility(View.INVISIBLE);
            rebootRecoveryBtn.setVisibility(View.INVISIBLE);
            imeiBtn.setVisibility(View.INVISIBLE);
            bootAnimationBtn.setVisibility(View.INVISIBLE);
        }

        apkBtn.setEnabled(false);
        apkSpn.setOnItemSelectedListener(this);

        recoveryBtn.setOnClickListener(this);
        romBtn.setOnClickListener(this);
        imeiBtn.setOnClickListener(this);
        ingenieroBtn.setOnClickListener(this);
        apkBtn.setOnClickListener(this);
        abrirExploradorBtn.setOnClickListener(this);
        rebootRecoveryBtn.setOnClickListener(this);
        bootAnimationBtn.setOnClickListener(this);
        //bootAnimationBtn.setEnabled(false);
        refreshCombos();
    }

    private void deleteDirectories() {
        try {
            File f1 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/RECOVERY/");
            File[] files = f1.listFiles();
            for (int x=0;x<files.length;x++){
                if(((File)files[x]).isDirectory()){
                    File[] files2 = ((File)files[x]).listFiles();
                    for (int y=0;y<files2.length;y++){
                        ((File)files2[y]).delete();
                    }
                    ((File)files[x]).delete();
                }

            }
        }catch(Exception e ){

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.apkSpn) {
            if (listaAppsUrl != null && listaAppsUrl.size() > 0) {
                String apkselec = listaAppsUrl.get(i);
                if (!"".equals(apkselec.trim())) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgSeleccionado) + " " + new File(apkselec).getName(), Toast.LENGTH_SHORT).show();
                    apkBtn.setEnabled(true);
                    this.apkseleccionada = apkselec;
                } else {
                    apkBtn.setEnabled(false);
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

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.apkBtn) {
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.fromFile(new File(apkseleccionada)), "application/vnd.android.package-archive");
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            getBaseContext().startActivity(intent2);
            this.apkseleccionada = "";
            this.apkSpn.setSelection(0);
            apkBtn.setEnabled(false);
        } else if (button.getId() == R.id.imeiBtn) {
            try {
                Intent intent = new Intent(this, ImeiScreen.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.recoveryBtn) {
            try {
                Intent intent = new Intent(this, RecoveryScreen.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
            }
        }else if (button.getId()==R.id.rebootRecoveryBtn){
            {
                try {
                    rebootRecoveryQuestion();
                } catch (Exception e) {

                }
            }

        } else if (button.getId() == R.id.filesBtn) {
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
                    //Toast.makeText(getBaseContext(), getResources().getString(R.string.msgIngenieroNoExiste), Toast.LENGTH_SHORT).show();
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError) + application_name, Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.romBtn) {
            try {
                Intent intent = new Intent(this, RomScreen.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
            }

        }else if(button.getId()==R.id.ingenieroBtn){
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
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgIngenieroNoExiste),Toast.LENGTH_SHORT).show();
                }
            }catch (ActivityNotFoundException e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError)+application_name,Toast.LENGTH_SHORT).show();
            }
        }else if(button.getId()==R.id.bootAnimationBtn){
            try {
                Intent intent = new Intent(this, BootAnimation.class);
                intent.putExtra("modelo",modelo);
                intent.putExtra("tipo","bootanimation");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void rebootRecoveryQuestion() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgRebootRecoveryQ));

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {

                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.aceptarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        try {
                            Runtime rt = Runtime.getRuntime();
                            java.lang.Process p = rt.exec("su");
                            BufferedOutputStream bos = new BufferedOutputStream(
                                    p.getOutputStream());
                            bos.write(("reboot recovery\n").getBytes());
                            bos.flush();
                            bos.close();
                        } catch (Exception e) {

                        }
                    }
                });
        dialog.show();
    }
    private boolean controlRoot() {
        boolean rootB = false;
        File f = new File("/system/bin/su");
        if (!f.exists()) {
            f = new File("/system/xbin/su");
            if (f.exists()) {
                rootB = true;
            }
        } else {
            rootB = true;
        }
        if (rootB) {
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("su");
            } catch (Exception e) {
            }
        }
        return rootB;
    }


    private boolean controlBusybox() {
        boolean busybox = true;
        File f = new File("/system/bin/busybox");
        if (!f.exists()) {
            f = new File("/system/xbin/busybox");
            if (!f.exists()) {
                busybox = false;
            } else {
                busybox = true;
            }
        } else {
            busybox = true;
        }
        return busybox;
    }

    public void refreshCombos() {
        listaApps.clear();
        listaAppsUrl.clear();
        listaApps.add(getResources().getString(R.string.seleccionaValue));
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
}