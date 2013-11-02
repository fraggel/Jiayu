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


public class RomScreen extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

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
        modelo = getIntent().getExtras().getString("modelo");
        ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        firmarChk=ajustes.getBoolean("firmarChk",false);
        if (controlRoot()) {
            isRoot = true;
        }
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });

        romSpn = (Spinner) findViewById(R.id.romSpn);
        romBtn = (Button) findViewById(R.id.romBtn);
        chkCWM = (CheckBox) findViewById(R.id.cwmChk);
        dataCacheDalvikBtn= (Button) findViewById(R.id.dataCacheDalvikBtn);
        zipSpn = (Spinner) findViewById(R.id.zipSpn);
        zipBtn = (Button) findViewById(R.id.zipBtn);


        if (!isRoot) {
            chkCWM.setVisibility(View.INVISIBLE);
            //zipSpn.setVisibility(View.INVISIBLE);
            dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
            if (chkCWM.isChecked()) {
                romSpn.setVisibility(View.INVISIBLE);
                romBtn.setVisibility(View.INVISIBLE);
                findViewById(R.id.romTxt).setVisibility(View.INVISIBLE);
                dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
            }
        }
        //zipBtn.setVisibility(View.INVISIBLE);
        //zipSpn.setVisibility(View.INVISIBLE);
        //findViewById(R.id.zipTxt).setVisibility(View.INVISIBLE);
        dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
        zipBtn.setEnabled(false);
        romBtn.setEnabled(false);
        romSpn.setOnItemSelectedListener(this);
        romBtn.setOnClickListener(this);
        chkCWM.setOnCheckedChangeListener(this);
        zipSpn.setOnItemSelectedListener(this);
        zipBtn.setOnClickListener(this);
        dataCacheDalvikBtn.setOnClickListener(this);
        refreshCombos();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.romSpn) {
            if (listaRomsUrl != null && listaRomsUrl.size() > 0) {

                String romselec = listaRomsUrl.get(i);
                if (!"".equals(romselec.trim())) {
                    romBtn.setEnabled(true);
                    this.romseleccionada = romselec;
                    zipSpn.setSelection(0);
                    zipBtn.setEnabled(false);
                    this.zipseleccionada="";
                } else {
                    romSpn.setSelection(0);
                    romBtn.setEnabled(false);
                    this.romseleccionada = "";
                }


            }
        }else if(spinner.getId() == R.id.zipSpn){
            if(listaZipsUrl!=null && listaZipsUrl.size()>0){

                String zipselec=listaZipsUrl.get(i);
                if(!"".equals(zipselec.trim())){
                    zipBtn.setEnabled(true);
                    this.zipseleccionada=zipselec;
                    romBtn.setEnabled(false);
                    romSpn.setSelection(0);
                    this.romseleccionada="";
                }else{
                    zipSpn.setSelection(0);
                    zipBtn.setEnabled(false);
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
               boolean mimodelo=false;
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
               }else{
                   aceptadoNoModelo=true;
                   CheckBox chkCWM = (CheckBox) findViewById(R.id.cwmChk);
                   if (chkCWM.isChecked()) {
                        flashZip();
                   }else{
                       flashZipRecoveryOficial();
                   }
               }

           }catch(Exception e){
               Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 153", Toast.LENGTH_SHORT).show();
           }

       }else if(button.getId()==R.id.dataCacheDalvikBtn){
           try {
               Runtime rt = Runtime.getRuntime();
               java.lang.Process p = rt.exec("su");
               BufferedOutputStream bos = new BufferedOutputStream(
                       p.getOutputStream());

               bos.write(("rm /cache/recovery/extendedcommand\n")
                       .getBytes());
               bos.write(("echo 'ui_print(\"Wipe data/cache iniciando...\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
               bos.write(("echo 'format(\"/data\");\n' >> /cache/recovery/extendedcommand").getBytes());
               bos.write(("echo 'format(\"/cache\");\n' >> /cache/recovery/extendedcommand").getBytes());
               bos.write(("echo 'ui_print(\"Wipe data/cache completado...\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
               bos.flush();
               bos.close();
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

                    if (controlRoot()) {
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
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIngenieroNoExiste), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                            }
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
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIngenieroNoExiste), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (ActivityNotFoundException e) {
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
        String fileCWM = "";
        if("G4A".equals(modelo) || "S1".equals(modelo) || "G5A".equals(modelo)){
            fileCWM = this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/emmc");
            bos.write(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
            bos.write(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
        }else{
            fileCWM =this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
            bos.write(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
            bos.write(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
        }
        bos.write(("echo 'install_zip(\"" + fileCWM + "\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                                    /*String fileCWM2=this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard2");
                                    bos.write(("echo 'install_zip(\""+ fileCWM2 +"\");' >> /cache/recovery/extendedcommand\n").getBytes());*/
        bos.flush();
        bos.close();
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
                        bos.write(("rm /cache/recovery/extendedcommand\n")
                                .getBytes());
                        String fileCWM="";
                        if("G4A".equals(modelo) || "S1".equals(modelo)||"G5A".equals(modelo)){
                            fileCWM = this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/emmc");
                            bos.write(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                            bos.write(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        }else{
                            fileCWM =this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
                            bos.write(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                            bos.write(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        }

                        bos.write(("echo 'install_zip(\""+ fileCWM +"\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                                /*String fileCWM2=this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard2");
                                bos.write(("echo 'install_zip(\""+ fileCWM2 +"\");' >> /cache/recovery/extendedcommand\n").getBytes());*/
                        bos.flush();
                        bos.close();
                        rebootRecoveryQuestionFlashear();
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                    }
                }else{
                    Runtime rt = Runtime.getRuntime();
                    java.lang.Process p = rt.exec("su");
                    BufferedOutputStream bos = new BufferedOutputStream(
                            p.getOutputStream());
                    bos.write(("rm /cache/recovery/extendedcommand\n")
                            .getBytes());
                    String fileCWM="";
                    if("G4A".equals(modelo) || "S1".equals(modelo)||"G5A".equals(modelo)){
                        fileCWM = this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/emmc");
                        bos.write(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        bos.write(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n").getBytes());
                    }else{
                        fileCWM =this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
                        bos.write(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                        bos.write(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n").getBytes());
                    }

                    bos.write(("echo 'install_zip(\""+ fileCWM +"\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                                /*String fileCWM2=this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard2");
                                bos.write(("echo 'install_zip(\""+ fileCWM2 +"\");' >> /cache/recovery/extendedcommand\n").getBytes());*/
                    bos.flush();
                    bos.close();
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIngenieroNoExiste), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                    }
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
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIngenieroNoExiste), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 157", Toast.LENGTH_SHORT).show();
        }

    }
    private void rebootRecoveryQuestionFlashear() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgRebootRecoveryQF));
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
                            //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 158", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 159", Toast.LENGTH_SHORT).show();
            }
        }
        return rootB;
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
                romSpn.setVisibility(View.INVISIBLE);
                romBtn.setVisibility(View.INVISIBLE);
                dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
                zipSpn.setVisibility(View.INVISIBLE);
                zipBtn.setVisibility(View.INVISIBLE);
                findViewById(R.id.zipTxt).setVisibility(View.INVISIBLE);
            }else{
                romSpn.setVisibility(View.VISIBLE);
                romBtn.setVisibility(View.VISIBLE);
                dataCacheDalvikBtn.setVisibility(View.VISIBLE);
                zipBtn.setVisibility(View.VISIBLE);
                zipSpn.setVisibility(View.VISIBLE);
                findViewById(R.id.zipTxt).setVisibility(View.VISIBLE);
            }

        }else{
            romSpn.setVisibility(View.VISIBLE);
            romBtn.setVisibility(View.VISIBLE);
            dataCacheDalvikBtn.setVisibility(View.INVISIBLE);
            zipBtn.setVisibility(View.INVISIBLE);
            findViewById(R.id.zipTxt).setVisibility(View.INVISIBLE);
            zipSpn.setVisibility(View.INVISIBLE);
        }
    }
}