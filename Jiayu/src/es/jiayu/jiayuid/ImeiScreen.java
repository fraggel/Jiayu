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
public class ImeiScreen extends Activity implements View.OnClickListener {
    Button imeiBBtn = null;
    Button imeiRBtn = null;
    boolean isRoot = false;
    String path = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imeiscreen);
        if (controlRoot()) {
            isRoot = true;
            if (!controlBusybox()) {
                //instalarBusyBox();
            }
        }

        imeiBBtn = (Button) findViewById(R.id.imeiBBtn);
        imeiRBtn = (Button) findViewById(R.id.imeiRBtn);

        if (!isRoot) {
            imeiBBtn.setEnabled(false);
            imeiRBtn.setEnabled(false);
        }else{
            imeiBBtn.setEnabled(true);
            imeiRBtn.setEnabled(true);
        }

        imeiBBtn.setOnClickListener(this);
        imeiRBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.imeiBBtn) {
            backupImeis();
        } else if (button.getId() == R.id.imeiRBtn) {
           restoreImeis();
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
            File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI.bak");
            if(ff.exists()){
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setMessage(getResources().getString(R.string.msgImeiExiste));
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
                                    File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI.bak");
                                    ff.delete();
                                    Runtime rt = Runtime.getRuntime();
                                    java.lang.Process p = rt.exec("su");
                                    BufferedOutputStream bos = new BufferedOutputStream(
                                            p.getOutputStream());
                                    //TODO
                                    //Calcular Md5 del original,
                                    bos.write(("cp /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001 "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI.bak\n").getBytes());
                                    //calcular md5 de la copia, comparar y si es correcto mostrar aviso de ok, en caso contrario decir que no OK y eliminar el IMEI.bak
                                    //Poner md5 en la cabecera del fichero si se puede
                                    bos.flush();
                                    bos.close();
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
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
                                        bos.write(("cp /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001 "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI.bak\n").getBytes());
                                        //calcular md5 de la copia, comparar y si es correcto mostrar aviso de ok, en caso contrario decir que no OK y eliminar el IMEI.bak
                                        //Poner md5 en la cabecera del fichero si se puede
                                        bos.flush();
                                        bos.close();
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {

        }
    }
    public void restoreImeis(){
        try {
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
                        public void onClick(DialogInterface dialog, int witch) {
                            try {
                                Runtime rt = Runtime.getRuntime();
                                java.lang.Process p = rt.exec("su");
                                BufferedOutputStream bos = new BufferedOutputStream(
                                        p.getOutputStream());
                                //TODO
                                //calcular Md5, comparar con el de la cabecera del fichero si es correcto copiar
                                //Si no lo es no copiar....
                                bos.write(("cp "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI.bak /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001\n").getBytes());

                                bos.flush();
                                bos.close();
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();

        }
    }
}