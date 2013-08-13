package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
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
public class ImeiScreen extends Activity implements View.OnClickListener {
    Button imeiBBtn = null;
    Button imeiRBtn = null;
    Button imeiSEBtn = null;
    boolean isRoot = false;
    String modelo=null;
    String path = "";
    ImageButton imageButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imeiscreen);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");

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
        imeiBBtn = (Button) findViewById(R.id.imeiBBtn);
        imeiRBtn = (Button) findViewById(R.id.imeiRBtn);
        imeiSEBtn = (Button) findViewById(R.id.imeiSEBtn);
        if (!isRoot) {
            imeiBBtn.setEnabled(false);
            imeiRBtn.setEnabled(false);
            imeiSEBtn.setEnabled(false);
        }else{
            imeiBBtn.setEnabled(true);
            imeiRBtn.setEnabled(true);
            File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
            if(ff.exists()){
                imeiSEBtn.setEnabled(true);
            }else{
                imeiSEBtn.setEnabled(false);
            }
        }

        imeiBBtn.setOnClickListener(this);
        imeiRBtn.setOnClickListener(this);
        imeiSEBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.imeiBBtn) {
            backupImeis();
        } else if (button.getId() == R.id.imeiRBtn) {
           restoreImeis();
        }
        else if (button.getId() == R.id.imeiSEBtn) {
            sendImei();
        }
    }
    public void refresh(){
        File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
        if(ff.exists()){
            imeiSEBtn.setEnabled(true);
        }else{
            imeiSEBtn.setEnabled(false);
        }
    }
    private void sendImei() {
        File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
        Resources res2 = getResources();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        Uri uri = Uri.parse("file://" + ff);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.putExtra(Intent.EXTRA_SUBJECT, "Jiayu.es IMEI"+modelo+".bak");
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {

            startActivity(Intent.createChooser(i,
                    res2.getString(R.string.enviarEmailBtn)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
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

    public void backupImeis(){
        try {
            File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
            if(ff.exists()){
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setMessage(getResources().getString(R.string.msgImeiExiste1)+" IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeiExiste2));
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
                                    File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
                                    ff.delete();
                                    Runtime rt = Runtime.getRuntime();
                                    java.lang.Process p = rt.exec("su");
                                    BufferedOutputStream bos = new BufferedOutputStream(
                                            p.getOutputStream());
                                    //TODO
                                    //Calcular Md5 del original,
                                    bos.write(("cp /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001 "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak\n").getBytes());
                                    //calcular md5 de la copia, comparar y si es correcto mostrar aviso de ok, en caso contrario decir que no OK y eliminar el IMEI.bak
                                    //Poner md5 en la cabecera del fichero si se puede
                                    bos.flush();
                                    bos.close();
                                    Toast.makeText(getBaseContext(), "IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();

            }else{
               /* try {
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setMessage(getResources().getString(R.string.msgImeiRestoreQ));
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                            getResources().getString(R.string.cancelarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {

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
                                        //TODO
                                        //Calcular Md5 del original,
                                        bos.write(("cp /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001 "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak\n").getBytes());
                                        //calcular md5 de la copia, comparar y si es correcto mostrar aviso de ok, en caso contrario decir que no OK y eliminar el IMEI.bak
                                        //Poner md5 en la cabecera del fichero si se puede
                                        bos.flush();
                                        bos.close();
                                        Toast.makeText(getBaseContext(),  "IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
                                    }
                  /*              }
                            });
                    dialog.show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();

                }*/
            }
            refresh();
        } catch (Exception e) {

        }
    }
    public void restoreImeis(){
        try {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(getResources().getString(R.string.msgImeiRestoreQ1)+" "+"IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeiRestoreQ2));
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
                                //TODO
                                //calcular Md5, comparar con el de la cabecera del fichero si es correcto copiar
                                //Si no lo es no copiar....
                                bos.write(("cp "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001\n").getBytes());

                                bos.flush();
                                bos.close();
                                Toast.makeText(getBaseContext(),  "IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            dialog.show();
            refresh();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();

        }
    }
}