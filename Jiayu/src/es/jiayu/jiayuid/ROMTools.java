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
public class ROMTools extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Spinner recoverySpn = null;
    Spinner romSpn = null;
    Spinner apkSpn = null;
    Spinner zipSpn = null;
    Button apkBtn = null;
    Button recoveryBtn = null;
    Button imeiBtn = null;
    Button romBtn = null;
    Button ingenieroBtn = null;
    Button abrirExploradorBtn = null;
    Button zipBtn = null;
    Button rebootRcoveryBtn = null;
    String recoveryseleccionado = null;
    String romseleccionada = null;
    String apkseleccionada = null;
    String zipseleccionada = null;
    ArrayList<String> listaAppsUrl = new ArrayList<String>();
    ArrayList<String> listaRecoUrl = new ArrayList<String>();
    ArrayList<String> listaRomsUrl = new ArrayList<String>();
    ArrayList<String> listaZipsUrl = new ArrayList<String>();
    List listaApps = new ArrayList();
    List listaReco = new ArrayList();
    List listaRo = new ArrayList();
    List listaZip = new ArrayList();
    CheckBox chkCWM = null;
    boolean isRoot = false;
    String path = "";

    @Override
    protected void onResume() {
        super.onResume();
        apkSpn = (Spinner) findViewById(R.id.apkSpn);
        recoverySpn = (Spinner) findViewById(R.id.recoverySpn);
        romSpn = (Spinner) findViewById(R.id.romSpn);
        zipSpn = (Spinner) findViewById(R.id.zipSpn);
        refreshCombos();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_romtools);
        if (controlRoot()) {
            isRoot = true;
            if (!controlBusybox()) {
                //instalarBusyBox();
            }
        }

        apkSpn = (Spinner) findViewById(R.id.apkSpn);
        recoverySpn = (Spinner) findViewById(R.id.recoverySpn);
        romSpn = (Spinner) findViewById(R.id.romSpn);
        zipSpn = (Spinner) findViewById(R.id.zipSpn);

        recoveryBtn = (Button) findViewById(R.id.recoveryBoton);
        romBtn = (Button) findViewById(R.id.romBoton);
        imeiBtn = (Button) findViewById(R.id.imeiBoton);
        apkBtn = (Button) findViewById(R.id.apkBoton);
        ingenieroBtn = (Button) findViewById(R.id.ingenieroBoton);
        rebootRcoveryBtn = (Button) findViewById(R.id.recoverybtn);
        abrirExploradorBtn = (Button) findViewById(R.id.filesBtn);
        chkCWM = (CheckBox) findViewById(R.id.cwmChk);
        zipBtn = (Button) findViewById(R.id.zipBtn);

        if (!isRoot) {
            recoverySpn.setVisibility(View.INVISIBLE);
            recoveryBtn.setVisibility(View.INVISIBLE);
            findViewById(R.id.recoveryTexto).setVisibility(View.INVISIBLE);
            chkCWM.setVisibility(View.INVISIBLE);
            zipBtn.setVisibility(View.INVISIBLE);
            findViewById(R.id.textoZip).setVisibility(View.INVISIBLE);
            zipSpn.setVisibility(View.INVISIBLE);
            rebootRcoveryBtn.setVisibility(View.INVISIBLE);
            if (chkCWM.isChecked()) {
                romSpn.setVisibility(View.INVISIBLE);
                romBtn.setVisibility(View.INVISIBLE);
                imeiBtn.setVisibility(View.INVISIBLE);
                findViewById(R.id.romTexto).setVisibility(View.INVISIBLE);
            }
        }

        recoveryBtn.setEnabled(false);
        zipBtn.setEnabled(false);
        romBtn.setEnabled(false);
        apkBtn.setEnabled(false);

        apkSpn.setOnItemSelectedListener(this);
        recoverySpn.setOnItemSelectedListener(this);
        romSpn.setOnItemSelectedListener(this);
        zipSpn.setOnItemSelectedListener(this);

        apkBtn.setOnClickListener(this);
        recoveryBtn.setOnClickListener(this);
        romBtn.setOnClickListener(this);
        imeiBtn.setOnClickListener(this);
        ingenieroBtn.setOnClickListener(this);

        abrirExploradorBtn.setOnClickListener(this);
        chkCWM.setOnCheckedChangeListener(this);
        zipBtn.setOnClickListener(this);
        rebootRcoveryBtn.setOnClickListener(this);
        zipBtn.setVisibility(View.INVISIBLE);
        findViewById(R.id.textoZip).setVisibility(View.INVISIBLE);
        zipSpn.setVisibility(View.INVISIBLE);
        refreshCombos();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.apkSpn) {
            if (listaAppsUrl != null && listaAppsUrl.size() > 0) {
                String recoveryselec = null;
                String romselec = null;
                String apkselec = listaAppsUrl.get(i);
                if (!"".equals(apkselec.trim())) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgSeleccionado) + " " + new File(apkselec).getName(), Toast.LENGTH_SHORT).show();
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(true);
                    zipBtn.setEnabled(false);
                    this.apkseleccionada = apkselec;
                } else {
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(false);
                    this.apkseleccionada = "";
                }
            }
        } else if (spinner.getId() == R.id.recoverySpn) {
            if (listaRecoUrl != null && listaRecoUrl.size() > 0) {
                String recoveryselec = listaRecoUrl.get(i);
                if (!"".equals(recoveryselec.trim())) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgSeleccionado) + " " + new File(recoveryselec).getName(), Toast.LENGTH_SHORT).show();
                    recoveryBtn.setEnabled(true);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(false);
                    this.recoveryseleccionado = recoveryselec;
                } else {
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(false);
                    this.recoveryseleccionado = "";
                }
                String romselec = null;
                String apkselec = null;
            }
        } else if (spinner.getId() == R.id.romSpn) {
            if (listaRomsUrl != null && listaRomsUrl.size() > 0) {
                String recoveryselec = null;
                String romselec = listaRomsUrl.get(i);
                if (!"".equals(romselec.trim())) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgSeleccionado) + " " + new File(romselec).getName(), Toast.LENGTH_SHORT).show();
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(true);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(false);
                    this.romseleccionada = romselec;
                } else {
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(false);
                    this.romseleccionada = "";
                }
                String apkselec = null;
            }
        } else if (spinner.getId() == R.id.zipSpn) {
            if (listaZipsUrl != null && listaZipsUrl.size() > 0) {
                String recoveryselec = null;
                String zipselec = listaZipsUrl.get(i);
                if (!"".equals(zipselec.trim())) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgSeleccionado) + " " + new File(zipselec).getName(), Toast.LENGTH_SHORT).show();
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(true);
                    this.zipseleccionada = zipselec;
                } else {
                    recoveryBtn.setEnabled(false);
                    romBtn.setEnabled(false);
                    apkBtn.setEnabled(false);
                    zipBtn.setEnabled(false);
                    this.zipseleccionada = "";
                }
                String apkselec = null;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.apkSpn) {
            String recoveryselec = null;
            String romselec = null;
            String apkselec = null;
        } else if (spinner.getId() == R.id.recoverySpn) {
            String recoveryselec = null;
            String romselec = null;
            String apkselec = null;
        } else if (spinner.getId() == R.id.romSpn) {
            String recoveryselec = null;
            String romselec = null;
            String apkselec = null;
        } else if (spinner.getId() == R.id.zipSpn) {
            String recoveryselec = null;
            String romselec = null;
            String apkselec = null;
        }

    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.apkBoton) {
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.fromFile(new File(apkseleccionada)), "application/vnd.android.package-archive");
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            getBaseContext().startActivity(intent2);
            this.apkseleccionada = "";
            this.apkSpn.setSelection(0);
            recoveryBtn.setEnabled(false);
            romBtn.setEnabled(false);
            apkBtn.setEnabled(false);
        } else if (button.getId() == R.id.recoveryBoton) {
            boolean descomprimido = false;
            try {
                unZip(this.recoveryseleccionado);
                descomprimido = true;
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.errorUnzip) + new File(this.recoveryseleccionado).getName(), Toast.LENGTH_SHORT).show();
            }
            if (descomprimido) {
                try {

                    Runtime rt = Runtime.getRuntime();
                    java.lang.Process p = rt.exec("su");
                    BufferedOutputStream bos = new BufferedOutputStream(
                            p.getOutputStream());
                    String ficheroRecovery = "";
                    ficheroRecovery = this.recoveryseleccionado.substring(0, this.recoveryseleccionado.length() - 4);
                    File fReco = new File(ficheroRecovery);
                    File[] files = fReco.listFiles();
                    for (int x = 0; x < files.length; x++) {
                        if (files[x].getName().toLowerCase().lastIndexOf(".img") != -1) {
                            ficheroRecovery = files[x].getAbsolutePath();
                        }
                    }
                    bos.write(("dd if=" + ficheroRecovery + " of=/dev/recovery bs=6291456c count=1\n").getBytes());
                    bos.flush();
                    bos.close();
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setMessage(getResources().getString(R.string.recoveryFlasheado));
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                            getResources().getString(R.string.cancelar),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                }
                            });
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                            getResources().getString(R.string.aceptar),
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
                                        //((PowerManager) getSystemService(getBaseContext().POWER_SERVICE)).reboot("recovery");
                                    } catch (Exception e) {
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.errorRecovery) + new File(this.recoveryseleccionado).getName(), Toast.LENGTH_SHORT).show();
                }
            }
            this.recoveryseleccionado = "";
            this.recoverySpn.setSelection(0);
            recoveryBtn.setEnabled(false);
            romBtn.setEnabled(false);
            apkBtn.setEnabled(false);
        } else if (button.getId() == R.id.romBoton) {
            try {
                CheckBox chkCWM = (CheckBox) findViewById(R.id.cwmChk);
                if (chkCWM.isChecked()) {

                    if (controlRoot()) {
                        Runtime rt = Runtime.getRuntime();
                        java.lang.Process p = rt.exec("su");
                        BufferedOutputStream bos = new BufferedOutputStream(
                                p.getOutputStream());
                        bos.write(("rm /cache/recovery/extendedcommand\n")
                                .getBytes());
                        String fileCWM = this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
                        bos.write(("echo 'install_zip(\"" + fileCWM + "\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                        /*String fileCWM2=this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard2");
                        bos.write(("echo 'install_zip(\""+ fileCWM2 +"\");' >> /cache/recovery/extendedcommand\n").getBytes());*/
                        bos.flush();
                        bos.close();
                        rebootRecoveryQuestionFlashear();
                    }
                } else {
                    File f = new File(this.romseleccionada);
                    if (new File(Environment.getExternalStorageDirectory() + "/update.zip").exists()) {
                        new File(Environment.getExternalStorageDirectory() + "/update.zip").delete();

                    }
                    f.renameTo(new File(Environment.getExternalStorageDirectory() + "/update.zip"));
                    String application_name = "";
                    try {

                        application_name = "com.mediatek.updatesystem.UpdateSystem";
                        Intent intent = new Intent("android.intent.action.MAIN");
                        List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                        boolean existe = false;
                        for (ResolveInfo info : resolveinfo_list) {
                            if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.updatesystem")) {
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
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.ingenieroNoExiste), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError) + application_name, Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.errorRom) + new File(this.romseleccionada).getName(), Toast.LENGTH_SHORT).show();
            }


            this.romseleccionada = "";
            this.romSpn.setSelection(0);
            recoveryBtn.setEnabled(false);
            romBtn.setEnabled(false);
            apkBtn.setEnabled(false);
        } else if (button.getId() == R.id.ingenieroBoton) {
            String application_name = "";
            try {

                application_name = "com.mediatek.engineermode.EngineerMode";
                Intent intent = new Intent("android.intent.action.MAIN");
                List<ResolveInfo> resolveinfo_list = getPackageManager().queryIntentActivities(intent, 0);
                boolean existe = false;
                for (ResolveInfo info : resolveinfo_list) {
                    if (info.activityInfo.packageName.equalsIgnoreCase("com.mediatek.engineermode")) {
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
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.ingenieroNoExiste), Toast.LENGTH_SHORT).show();
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError) + application_name, Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.imeiBoton) {
            backupImeis();
        } else if (button.getId() == R.id.recoverybtn) {
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
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.ingenieroNoExiste), Toast.LENGTH_SHORT).show();
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError) + application_name, Toast.LENGTH_SHORT).show();
            }
        } else if (button.getId() == R.id.zipBtn) {
            try {
                Runtime rt = Runtime.getRuntime();
                java.lang.Process p = rt.exec("su");
                BufferedOutputStream bos = new BufferedOutputStream(
                        p.getOutputStream());
                bos.write(("rm /cache/recovery/extendedcommand\n")
                        .getBytes());
                String fileCWM = this.zipseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(), "/sdcard");
                bos.write(("echo 'install_zip(\"" + fileCWM + "\");\n' >> /cache/recovery/extendedcommand\n").getBytes());
                /*String fileCWM2=this.romseleccionada.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sdcard2");
                bos.write(("echo 'install_zip(\""+ fileCWM2 +"\");' >> /cache/recovery/extendedcommand\n").getBytes());*/
                bos.flush();
                bos.close();
                rebootRecoveryQuestionFlashear();
            } catch (Exception e) {

            }

        }
        refreshCombos();
    }

    private void rebootRecoveryQuestionFlashear() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.rebootRecoveryQF));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {

                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.aceptar),
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
                            //((PowerManager) getSystemService(getBaseContext().POWER_SERVICE)).reboot("recovery");
                        } catch (Exception e) {

                        }
                    }
                });
        dialog.show();
    }
    private void rebootRecoveryQuestion() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.rebootRecoveryQ));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {

                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.aceptar),
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


    private static void unZip(String strZipFile) throws Exception {

        File fSourceZip = new File(strZipFile);
        String zipPath = strZipFile.substring(0, strZipFile.length() - 4);
        File temp = new File(zipPath);
        temp.mkdir();

		/*
         * STEP 2 : Extract entries while creating required sub-directories
		 */
        ZipFile zipFile = new ZipFile(fSourceZip);
        Enumeration e = zipFile.entries();

        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            File destinationFilePath = new File(zipPath, entry.getName());

            // create directories if required.
            destinationFilePath.getParentFile().mkdirs();

            // if the entry is directory, leave it. Otherwise extract it.
            if (entry.isDirectory()) {
                continue;
            } else {
                System.out.println("Extracting " + destinationFilePath);

				/*
				 * Get the InputStream for current entry of the zip file using
				 *
				 * InputStream getInputStream(Entry entry) method.
				 */
                BufferedInputStream bis = new BufferedInputStream(
                        zipFile.getInputStream(entry));

                int b;
                byte buffer[] = new byte[1024];

				/*
				 * read the current entry from the zip file, extract it and
				 * write the extracted file.
				 */
                FileOutputStream fos = new FileOutputStream(destinationFilePath);
                BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

                while ((b = bis.read(buffer, 0, 1024)) != -1) {
                    bos.write(buffer, 0, b);
                }

                // flush the output stream and close it.
                bos.flush();
                bos.close();

                // close the input stream.
                bis.close();
            }
        }

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

    /*private void instalarBusyBox() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgNoBusybox));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                res.getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        finish();
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                res.getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri
                                    .parse("market://details?id=com.jrummy.busybox.installer"));
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            new REException(e);

                        }
                    }
                });
        dialog.show();

    }*/
    public void refreshCombos() {
        listaApps.clear();
        listaReco.clear();
        listaRo.clear();
        listaZip.clear();
        listaAppsUrl.clear();
        listaRecoUrl.clear();
        listaRomsUrl.clear();
        listaZipsUrl.clear();

        listaApps.add(getResources().getString(R.string.selecciona));
        listaReco.add(getResources().getString(R.string.selecciona));
        listaRo.add(getResources().getString(R.string.selecciona));
        listaZip.add(getResources().getString(R.string.selecciona));

        listaAppsUrl.add("");
        listaRecoUrl.add("");
        listaRomsUrl.add("");
        listaZipsUrl.add("");


        File f1 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/APP/");
        File f2 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/RECOVERY/");
        File f3 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/ROMS/");
        File f4 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/DOWNLOADS/");
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
        if (f2.exists()) {
            if (f2.listFiles().length > 0) {
                for (int x = 0; x < f2.listFiles().length; x++) {
                    File fx = (File) f2.listFiles()[x];
                    if (!fx.isDirectory() && fx.isFile()) {
                        listaReco.add(fx.getName());
                        listaRecoUrl.add(fx.getAbsolutePath());
                    }
                }
            }
        }
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
        if (f4.exists()) {
            if (f4.listFiles().length > 0) {
                for (int x = 0; x < f4.listFiles().length; x++) {
                    File fx = (File) f4.listFiles()[x];
                    if (!fx.isDirectory() && fx.isFile()) {
                        listaZip.add(fx.getName());
                        listaZipsUrl.add(fx.getAbsolutePath());
                    }
                }
            }
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaApps);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apkSpn.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaReco);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recoverySpn.setAdapter(dataAdapter2);

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaRo);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        romSpn.setAdapter(dataAdapter3);

        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaZip);
        dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zipSpn.setAdapter(dataAdapter4);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked()) {
            if (!isRoot) {
                romSpn.setVisibility(View.INVISIBLE);
                romBtn.setVisibility(View.INVISIBLE);
                findViewById(R.id.romTexto).setVisibility(View.INVISIBLE);
                zipSpn.setVisibility(View.INVISIBLE);
                zipBtn.setVisibility(View.INVISIBLE);
                findViewById(R.id.textoZip).setVisibility(View.INVISIBLE);
            } else {
                romSpn.setVisibility(View.VISIBLE);
                romBtn.setVisibility(View.VISIBLE);
                findViewById(R.id.romTexto).setVisibility(View.VISIBLE);
                zipBtn.setVisibility(View.VISIBLE);
                zipSpn.setVisibility(View.VISIBLE);
                findViewById(R.id.textoZip).setVisibility(View.VISIBLE);
            }

        } else {
            romSpn.setVisibility(View.VISIBLE);
            romBtn.setVisibility(View.VISIBLE);
            findViewById(R.id.romTexto).setVisibility(View.VISIBLE);
            zipBtn.setVisibility(View.INVISIBLE);
            findViewById(R.id.textoZip).setVisibility(View.INVISIBLE);
            zipSpn.setVisibility(View.INVISIBLE);
        }
    }
    public void backupImeis(){}
}