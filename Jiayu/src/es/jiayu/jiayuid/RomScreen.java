package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static es.jiayu.jiayuid.Utilidades.comprobarRecovery;


public class RomScreen extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    Spinner romSpn = null;
    Button romBtn = null;
    Spinner zipSpn=null;
    ImageButton imageButton = null;
    Button zipBtn=null;
    String zipseleccionada=null;
    String romseleccionada = null;
    ArrayList<String> listaRomsUrl = new ArrayList<String>();
    ArrayList<String> listaZipsUrl=new ArrayList<String>();
    public static boolean aceptadoNoModelo=false;
    List listaZip=new ArrayList();
    List listaRo = new ArrayList();
    String modelo=null;
    CheckBox chkCWM = null;
    Button dataCacheDalvikBtn = null;
    SharedPreferences ajustes=null;
    boolean firmarChk=false;
    boolean isRoot = false;
    String path = "";
    boolean detectRecovery=false;
    String recoveryDetectado="ori";
    @Override
    protected void onResume() {
        super.onResume();

        romSpn = (Spinner) findViewById(R.id.romSpn);
        zipSpn = (Spinner) findViewById(R.id.zipSpn);

        refreshCombos();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_romscreen);
        TextView scText=(TextView) findViewById(R.id.herramientasROMTxt);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 86);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 360, 0, 176);
        }
        scText.setLayoutParams((llp));
        modelo = getIntent().getExtras().getString("modelo");
        ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        firmarChk=ajustes.getBoolean("firmarChk",false);
        if(firmarChk){
            File f1 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/CHECKSUM.md5");
            if(!f1.exists()){
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setMessage(getResources().getString(R.string.msgNoMD5File));
                dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        getResources().getString(R.string.aceptarBtn),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int witch) {
                                try {
                                    Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 285", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
            }
        }
        if (Utilidades.controlRootSinExec(getApplicationContext(),getResources(),"Rom1")) {
            isRoot = true;
        }
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/

        romSpn = (Spinner) findViewById(R.id.romSpn);
        romBtn = (Button) findViewById(R.id.romBtn);
        chkCWM = (CheckBox) findViewById(R.id.cwmChk);
        dataCacheDalvikBtn= (Button) findViewById(R.id.dataCacheDalvikBtn);
        zipSpn = (Spinner) findViewById(R.id.zipSpn);
        zipBtn = (Button) findViewById(R.id.zipBtn);


        if (!isRoot) {
            chkCWM.setEnabled(false);
            chkCWM.setTextColor(Color.GRAY);
            //chkCWM.setVisibility(View.INVISIBLE);
            dataCacheDalvikBtn.setEnabled(false);
            dataCacheDalvikBtn.setTextColor(Color.parseColor("#BDBDBD"));
            //dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
            if (chkCWM.isChecked()) {
                romSpn.setEnabled(false);
                //romSpn.setVisibility(View.INVISIBLE);
                romBtn.setEnabled(false);
                romBtn.setTextColor(Color.parseColor("#BDBDBD"));
                //romBtn.setVisibility(View.INVISIBLE);
                findViewById(R.id.romTxt).setEnabled(false);

                //findViewById(R.id.romTxt).setVisibility(View.INVISIBLE);
                dataCacheDalvikBtn.setEnabled(false);
                dataCacheDalvikBtn.setTextColor(Color.parseColor("#BDBDBD"));
                //dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
            }
        }
        //zipBtn.setVisibility(View.INVISIBLE);
        //zipSpn.setVisibility(View.INVISIBLE);
        //findViewById(R.id.zipTxt).setVisibility(View.INVISIBLE);
        dataCacheDalvikBtn.setEnabled(false);
        dataCacheDalvikBtn.setTextColor(Color.parseColor("#BDBDBD"));
        //dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
        zipBtn.setEnabled(false);
        zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
        romBtn.setEnabled(false);
        romBtn.setTextColor(Color.parseColor("#BDBDBD"));
        romSpn.setOnItemSelectedListener(this);
        romBtn.setOnClickListener(this);
        chkCWM.setOnCheckedChangeListener(this);
        zipSpn.setOnItemSelectedListener(this);
        zipBtn.setOnClickListener(this);
        dataCacheDalvikBtn.setOnClickListener(this);
        refreshCombos();
        String externalStorageState = Environment.getExternalStorageState();
        if(!"mounted".equals(externalStorageState.toLowerCase())){
            romSpn.setEnabled(false);
            romBtn.setEnabled(false);
            romBtn.setTextColor(Color.parseColor("#BDBDBD"));
            zipBtn.setEnabled(false);
            zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
            zipSpn.setEnabled(false);
            findViewById(R.id.zipTxt).setEnabled(false);
        }
        if(isRoot){
            if(ajustes.getBoolean("recoveryChk",false)){
                detectRecovery=true;
                recoveryDetectado=comprobarRecovery(getApplicationContext(),getResources(),"RomScreen");
            }else{
                detectRecovery=false;
                chkCWM.setEnabled(true);
                chkCWM.setVisibility(View.VISIBLE);
            }
            if(detectRecovery){
                if("cwm".equals(recoveryDetectado)){
                    chkCWM.setChecked(true);
                    chkCWM.setEnabled(false);
                    chkCWM.setTextColor(Color.BLUE);
                    chkCWM.setText(getResources().getString(R.string.msgRecoveryDetectado)+" CWM RECOVERY");
                    //chkCWM.setVisibility(View.INVISIBLE);
                }else if("ori".equals(recoveryDetectado)){
                    chkCWM.setChecked(false);
                    chkCWM.setEnabled(false);
                    chkCWM.setTextColor(Color.BLUE);
                    chkCWM.setText(getResources().getString(R.string.msgRecoveryDetectado)+" ORIGINAL RECOVERY");
                    //chkCWM.setVisibility(View.INVISIBLE);
                }else{
                    chkCWM.setChecked(false);
                    chkCWM.setEnabled(true);
                    chkCWM.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.romSpn) {
            if (listaRomsUrl != null && listaRomsUrl.size() > 0) {

                String romselec = listaRomsUrl.get(i);
                if (!"".equals(romselec.trim())) {
                    romBtn.setEnabled(true);
                    romBtn.setTextColor(Color.BLACK);
                    this.romseleccionada = romselec;
                    zipSpn.setSelection(0);
                    zipBtn.setEnabled(false);
                    zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
                    this.zipseleccionada="";
                } else {
                    romSpn.setSelection(0);
                    romBtn.setEnabled(false);
                    romBtn.setTextColor(Color.parseColor("#BDBDBD"));
                    this.romseleccionada = "";
                }


            }
        }else if(spinner.getId() == R.id.zipSpn){
            if(listaZipsUrl!=null && listaZipsUrl.size()>0){

                String zipselec=listaZipsUrl.get(i);
                if(!"".equals(zipselec.trim())){
                    zipBtn.setEnabled(true);
                    zipBtn.setTextColor(Color.BLACK);
                    this.zipseleccionada=zipselec;
                    romBtn.setEnabled(false);
                    romBtn.setTextColor(Color.parseColor("#BDBDBD"));
                    romSpn.setSelection(0);
                    this.romseleccionada="";
                }else{
                    zipSpn.setSelection(0);
                    zipBtn.setEnabled(false);
                    zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
                    this.zipseleccionada="";
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
       if (button.getId() == R.id.romBtn) {
            try {
                boolean mimodelo=false;
                if(this.romseleccionada.indexOf(modelo)!=-1){
                    mimodelo=true;
                }else{
                    mimodelo=false;
                }
                if(!mimodelo){
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setMessage(getResources().getString(R.string.msgModeloNoIgualFichero));
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                            getResources().getString(R.string.cancelarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    RomScreen.aceptadoNoModelo = false;
                                }
                            });
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                            getResources().getString(R.string.aceptarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    try {
                                        RomScreen.aceptadoNoModelo = true;
                                        flashRom();
                                        //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 150", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.show();
                }else{
                    aceptadoNoModelo=true;
                    flashRom();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgErrorRom) + new File(this.romseleccionada).getName()+" 151", Toast.LENGTH_SHORT).show();
            }



        }else if(button.getId()==R.id.zipBtn){
           try {
               /*boolean mimodelo=false;
               if(this.zipseleccionada.indexOf(modelo)!=-1){
                   mimodelo=true;
               }else{
                   mimodelo=false;
               }
               if(!mimodelo){
                   AlertDialog dialog = new AlertDialog.Builder(this).create();
                   dialog.setMessage(getResources().getString(R.string.msgModeloNoIgualFichero));
                   dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                           getResources().getString(R.string.cancelarBtn),
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int witch) {
                                   RomScreen.aceptadoNoModelo = false;
                               }
                           });
                   dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                           getResources().getString(R.string.aceptarBtn),
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int witch) {
                                   try {
                                       RomScreen.aceptadoNoModelo = true;
                                       CheckBox chkCWM = (CheckBox) findViewById(R.id.cwmChk);
                                       if (chkCWM.isChecked()) {
                                           flashZip();
                                       }else{
                                           flashZipRecoveryOficial();
                                       }
                                       //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                                   } catch (Exception e) {
                                       Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 152", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                   dialog.show();
               }else{*/
                   aceptadoNoModelo=true;
                   CheckBox chkCWM = (CheckBox) findViewById(R.id.cwmChk);
                   if (chkCWM.isChecked()) {
                        flashZip();
                   }else{
                       flashZipRecoveryOficial();
                   }
              /* }*/

           }catch(Exception e){
               Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 153", Toast.LENGTH_SHORT).show();
           }

       }else if(button.getId()==R.id.dataCacheDalvikBtn){
           try {
               Runtime rt = Runtime.getRuntime();
               java.lang.Process p = rt.exec("su");
               BufferedOutputStream bos = new BufferedOutputStream(
                       p.getOutputStream());
               bos.write(("rm /cache/recovery/command\n")
                       .getBytes());
               bos.write(("rm /cache/recovery/extendedcommand\n")
                       .getBytes());
               bos.write(("echo 'ui_print(\"Wipe data/cache iniciando...\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
               bos.write(("echo 'format(\"/data\");\n' >> /cache/recovery/extendedcommand").getBytes());
               bos.write(("echo 'format(\"/cache\");\n' >> /cache/recovery/extendedcommand").getBytes());
               bos.write(("echo 'ui_print(\"Wipe data/cache completado...\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
               bos.write(("exit").getBytes());
               bos.flush();
               bos.close();
               p.waitFor();
               rebootRecoveryQuestionFlashear();

           }catch(Exception e){
               Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 154", Toast.LENGTH_SHORT).show();
           }
       }
    }
    public void flashRom(){
        try {
            if(aceptadoNoModelo){
                CheckBox chkCWM = (CheckBox) findViewById(R.id.cwmChk);
                if (chkCWM.isChecked()) {

                    if (Utilidades.controlRootSinExec(getApplicationContext(),getResources(),"Rom Flash")) {
                        if(firmarChk){
                            if(Utilidades.checkFileMD5(new File(this.romseleccionada))){
                                writeCWMInstall();
                            }else{
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                            }
                        }else{
                            writeCWMInstall();
                        }
                    }
                } else {
                    String application_name = "";
                    try {
                        if(firmarChk){
                            if(Utilidades.checkFileMD5(new File(this.romseleccionada))){
                                if(isRoot){

                                    writeORIInstall(this.romseleccionada);
                                }else{
                                    application_name = "com.mediatek.updatesystem.UpdateSystem";
                                    Intent intent = new Intent("android.intent.action.MAIN");
                                    List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                                    boolean existe = false;
                                    for (ResolveInfo info : resolveinfo_list) {
                                        if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.updatesystem")) {
                                            if (info.activityInfo.name.equalsIgnoreCase(application_name)) {
                                                File f = new File(this.romseleccionada);
                                                if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                                    new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                                }

                                                    f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
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
                                        if(isRoot){
                                            writeORIInstall(this.romseleccionada);
                                        }else{
                                            File f = new File(this.romseleccionada);
                                            if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                                new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                            }
                                            f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgManualReboot), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                            }
                        }else{
                            if(isRoot){
                                writeORIInstall(this.romseleccionada);
                            }else{
                                application_name = "com.mediatek.updatesystem.UpdateSystem";
                                Intent intent = new Intent("android.intent.action.MAIN");
                                List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                                boolean existe = false;
                                for (ResolveInfo info : resolveinfo_list) {
                                    if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.updatesystem")) {
                                        if (info.activityInfo.name.equalsIgnoreCase(application_name)) {
                                            File f = new File(this.romseleccionada);
                                            if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                                new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                            }

                                            f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
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
                                    if(isRoot){
                                        writeORIInstall(this.romseleccionada);
                                    }else{
                                        File f = new File(this.romseleccionada);
                                        if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                            new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                        }
                                        f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgManualReboot), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError) + application_name+" 155", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 156", Toast.LENGTH_SHORT).show();
        }

    }
    public void writeCWMInstall() throws Exception{
        Runtime rt = Runtime.getRuntime();
        java.lang.Process p = rt.exec("su");
        BufferedOutputStream bos = new BufferedOutputStream(
                p.getOutputStream());
        bos.write(("rm /cache/recovery/extendedcommand\n")
                .getBytes());
        bos.write(("rm /cache/recovery/command\n")
                .getBytes());
        String fileCWM = "";
        if("G4A".equals(modelo) || "S1".equals(modelo)||"G5A".equals(modelo)|| "S2A".equals(modelo)|| "G4S".equals(modelo) || "G6A".equals(modelo)){
            fileCWM = this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/emmc");
            bos.write(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
            bos.write(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
        }else{
            fileCWM =this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
            bos.write(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
            bos.write(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
        }
        bos.write(("echo 'install_zip(\"" + fileCWM + "\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
        bos.write(("exit").getBytes());
        bos.flush();
        bos.close();
        p.waitFor();
        rebootRecoveryQuestionFlashear();
    }
    public void writeORIInstall(String fichero) throws Exception{
        Runtime rt = Runtime.getRuntime();
        java.lang.Process p = rt.exec("su");
        BufferedOutputStream bos = new BufferedOutputStream(
                p.getOutputStream());
        bos.write(("rm /cache/recovery/command\n")
                .getBytes());
        bos.write(("rm /cache/recovery/extendedcommand\n")
                .getBytes());
        String fileCWM = "";
        if("G4A".equals(modelo) || "S1".equals(modelo) || "G5A".equals(modelo)){
            bos.write(("echo '--wipe_data' >> /cache/recovery/command\n").getBytes());
            bos.write(("echo '--update_package="+fichero.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard")+"' >> /cache/recovery/command\n").getBytes());
            bos.write(("echo '--locale=es_ES' >> /cache/recovery/command\n").getBytes());

        }else{
            bos.write(("echo '--wipe_data' >> /cache/recovery/command\n").getBytes());
            bos.write(("echo '--update_package="+fichero.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard2")+"' >> /cache/recovery/command\n").getBytes());
            bos.write(("echo '--locale=es_ES' >> /cache/recovery/command\n").getBytes());
        }
        bos.write(("exit").getBytes());
        bos.flush();
        bos.close();
        p.waitFor();
        rebootRecoveryQuestionFlashear();
    }
    public void flashZip(){
        try {
            if(aceptadoNoModelo){
                if(firmarChk){
                    if(Utilidades.checkFileMD5(new File(this.zipseleccionada))){
                        Runtime rt = Runtime.getRuntime();
                        java.lang.Process p = rt.exec("su");
                        BufferedOutputStream bos = new BufferedOutputStream(
                                p.getOutputStream());
                        bos.write(("rm /cache/recovery/command\n")
                                .getBytes());
                        bos.write(("rm /cache/recovery/extendedcommand\n")
                                .getBytes());
                        String fileCWM="";
                        if("G4A".equals(modelo) || "S1".equals(modelo)||"G5A".equals(modelo)|| "S2A".equals(modelo)|| "G4S".equals(modelo)|| "G6A".equals(modelo)){
                            fileCWM = this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/emmc");
                            bos.write(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                            bos.write(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        }else{
                            fileCWM =this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
                            bos.write(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                            bos.write(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        }

                        bos.write(("echo 'install_zip(\""+ fileCWM +"\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                        bos.write(("exit").getBytes());
                        bos.flush();
                        bos.close();
                        p.waitFor();
                        rebootRecoveryQuestionFlashear();
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                    }
                }else{
                    Runtime rt = Runtime.getRuntime();
                    java.lang.Process p = rt.exec("su");
                    BufferedOutputStream bos = new BufferedOutputStream(
                            p.getOutputStream());
                    bos.write(("rm /cache/recovery/command\n")
                            .getBytes());
                    bos.write(("rm /cache/recovery/extendedcommand\n")
                            .getBytes());
                    String fileCWM="";
                    if("G4A".equals(modelo) || "S1".equals(modelo)||"G5A".equals(modelo)|| "S2A".equals(modelo)|| "G4S".equals(modelo) || "G6A".equals(modelo)){
                        fileCWM = this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/emmc");
                        bos.write(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        bos.write(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                    }else{
                        fileCWM =this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
                        bos.write(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        bos.write(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                    }

                    bos.write(("echo 'install_zip(\""+ fileCWM +"\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                    bos.write(("exit").getBytes());
                    bos.flush();
                    bos.close();
                    p.waitFor();
                    rebootRecoveryQuestionFlashear();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 157", Toast.LENGTH_SHORT).show();
        }

    }
    public void flashZipRecoveryOficial(){
        try {
            if(aceptadoNoModelo){
                if(firmarChk){
                    if(Utilidades.checkFileMD5(new File(this.zipseleccionada))){
                        if(isRoot){
                            writeORIInstall(this.zipseleccionada);
                        }else{
                            String application_name = "";
                            application_name = "com.mediatek.updatesystem.UpdateSystem";
                            Intent intent = new Intent("android.intent.action.MAIN");
                            List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                            boolean existe = false;
                            for (ResolveInfo info : resolveinfo_list) {
                                if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.updatesystem")) {
                                    if (info.activityInfo.name.equalsIgnoreCase(application_name)) {
                                        File f = new File(this.zipseleccionada);
                                        if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                            new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                        }
                                        f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
                                        Intent launch_intent = new Intent("android.intent.action.MAIN");
                                        launch_intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                                        launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        this.startActivity(launch_intent);
                                        existe = true;
                                        break;
                                    }
                                }
                            }

                            if(isRoot){
                                writeORIInstall(this.zipseleccionada);
                            }else{
                                File f = new File(this.zipseleccionada);
                                if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                    new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                }
                                f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgManualReboot), Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(isRoot){
                        writeORIInstall(this.zipseleccionada);
                    }else{
                        String application_name = "";
                        application_name = "com.mediatek.updatesystem.UpdateSystem";
                        Intent intent = new Intent("android.intent.action.MAIN");
                        List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                        boolean existe = false;
                        for (ResolveInfo info : resolveinfo_list) {
                            if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.updatesystem")) {
                                if (info.activityInfo.name.equalsIgnoreCase(application_name)) {
                                    File f = new File(this.zipseleccionada);
                                    if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                        new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                    }
                                    f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
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
                            if(isRoot){
                                writeORIInstall(this.zipseleccionada);
                            }else{
                                File f = new File(this.zipseleccionada);
                                if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                                    new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                                }
                                f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgManualReboot), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 157", Toast.LENGTH_SHORT).show();
        }

    }
    private void rebootRecoveryQuestionFlashear() {
        /*AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgRebootRecoveryQF));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        if(isRoot){
                            try{
                                Runtime rt = Runtime.getRuntime();
                                java.lang.Process p = rt.exec("su");
                                BufferedOutputStream bos = new BufferedOutputStream(
                                        p.getOutputStream());
                                bos.write(("rm /cache/recovery/command\n")
                                        .getBytes());
                                bos.write(("rm /cache/recovery/extendedcommand\n")
                                        .getBytes());
                                bos.flush();
                                bos.close();
                            }catch(Exception e){}
                        }
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.aceptarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {*/
                        try {
                            Runtime rt = Runtime.getRuntime();
                            java.lang.Process p = rt.exec("su");
                            BufferedOutputStream bos = new BufferedOutputStream(
                                    p.getOutputStream());
                            bos.write(("reboot recovery\n").getBytes());
                            bos.write(("exit").getBytes());
                            bos.flush();
                            bos.close();
                            p.waitFor();
                            //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 158", Toast.LENGTH_SHORT).show();
                        }/*
                    }
                });
        dialog.show();*/
    }

    public void refreshCombos() {
        listaRo.clear();
        listaZip.clear();
        listaRomsUrl.clear();
        listaZipsUrl.clear();

        listaRo.add(getResources().getString(R.string.seleccionaValue));
        listaZip.add(getResources().getString(R.string.seleccionaValue));

        listaRomsUrl.add("");
        listaZipsUrl.add("");


        File f3 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/ROMS/");
        File f4=new File(Environment.getExternalStorageDirectory()+"/JIAYUES/DOWNLOADS/");
        if (f3.exists()) {
            if (f3.listFiles().length > 0) {
                for (int x = 0; x < f3.listFiles().length; x++) {
                    File fx = (File) f3.listFiles()[x];
                    if (!fx.isDirectory() && fx.isFile()) {
                        listaRo.add(fx.getName());
                        listaRomsUrl.add(fx.getAbsolutePath());
                    }
                }
            }
        }
        if(f4.exists()){
            if(f4.listFiles().length>0){
                for (int x =0;x<f4.listFiles().length;x++)
                {
                    File fx=(File)f4.listFiles()[x];
                    if(!fx.isDirectory() && fx.isFile()){
                        listaZip.add(fx.getName());
                        listaZipsUrl.add(fx.getAbsolutePath());
                    }
                }
            }
        }



        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaRo);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        romSpn.setAdapter(dataAdapter3);

        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listaZip);
        dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zipSpn.setAdapter(dataAdapter4);
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isChecked()){
            if(!isRoot){
                romSpn.setEnabled(true);

                //romSpn.setVisibility(View.INVISIBLE);
                if(romSpn.getSelectedItemPosition()==0){
                    romBtn.setEnabled(false);
                    romBtn.setTextColor(Color.parseColor("#BDBDBD"));
                }else{
                    romBtn.setEnabled(true);
                    romBtn.setTextColor(Color.BLACK);
                }
                //romBtn.setVisibility(View.INVISIBLE);
                dataCacheDalvikBtn.setEnabled(false);
                dataCacheDalvikBtn.setTextColor(Color.parseColor("#BDBDBD"));
                //dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
                if(zipSpn.getSelectedItemPosition()==0){
                    zipBtn.setEnabled(false);
                    zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
                }else{
                    zipBtn.setEnabled(true);
                    zipBtn.setTextColor(Color.BLACK);
                }
                //zipSpn.setVisibility(View.INVISIBLE);

                //zipBtn.setVisibility(View.INVISIBLE);
            }else{
                romSpn.setEnabled(true);
                //romSpn.setVisibility(View.INVISIBLE);
                if(romSpn.getSelectedItemPosition()==0){
                    romBtn.setEnabled(false);
                    romBtn.setTextColor(Color.parseColor("#BDBDBD"));
                }else{
                    romBtn.setEnabled(true);
                    romBtn.setTextColor(Color.BLACK);
                }
                //romBtn.setVisibility(View.INVISIBLE);
                dataCacheDalvikBtn.setEnabled(true);
                dataCacheDalvikBtn.setTextColor(Color.BLACK);
                //dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
                zipSpn.setEnabled(true);
                //zipSpn.setVisibility(View.INVISIBLE);
                if(zipSpn.getSelectedItemPosition()==0){
                    zipBtn.setEnabled(false);
                    zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
                }else{
                    zipBtn.setEnabled(true);
                    zipBtn.setTextColor(Color.BLACK);
                }
                //zipBtn.setVisibility(View.INVISIBLE);
            }

        }else{
            romSpn.setEnabled(true);
            //romSpn.setVisibility(View.INVISIBLE);
            if(romSpn.getSelectedItemPosition()==0){
                romBtn.setEnabled(false);
                romBtn.setTextColor(Color.parseColor("#BDBDBD"));
            }else{
                romBtn.setEnabled(true);
                romBtn.setTextColor(Color.BLACK);
            }
            //romBtn.setVisibility(View.INVISIBLE);
            dataCacheDalvikBtn.setEnabled(false);
            dataCacheDalvikBtn.setTextColor(Color.parseColor("#BDBDBD"));
            //dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
            zipSpn.setEnabled(true);
            //zipSpn.setVisibility(View.INVISIBLE);
            if(zipSpn.getSelectedItemPosition()==0){
                zipBtn.setEnabled(false);
                zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
            }else{
                zipBtn.setEnabled(true);
                zipBtn.setTextColor(Color.BLACK);
            }
            //zipBtn.setVisibility(View.INVISIBLE);

            //findViewById(R.id.zipTxt).setVisibility(View.INVISIBLE);
        }
        String externalStorageState = Environment.getExternalStorageState();
        if(!"mounted".equals(externalStorageState.toLowerCase())){
            romSpn.setEnabled(false);
            romBtn.setEnabled(false);
            romBtn.setTextColor(Color.parseColor("#BDBDBD"));
            zipBtn.setEnabled(false);
            zipBtn.setTextColor(Color.parseColor("#BDBDBD"));
            zipSpn.setEnabled(false);
            findViewById(R.id.zipTxt).setEnabled(false);
        }
    }
}