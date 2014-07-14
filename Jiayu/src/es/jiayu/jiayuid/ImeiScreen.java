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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
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
    protected void onResume() {
        super.onResume();
        String listaIdiomas[]=getResources().getStringArray(R.array.languages_values);
        SharedPreferences ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        int i=ajustes.getInt("language",0);
        Locale locale =null;
        if(i==0){
            locale=getResources().getConfiguration().locale;
        }else{
            locale = new Locale(listaIdiomas[i]);
        }


        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config,
                getApplicationContext().getResources().getDisplayMetrics());
        onCreate(null);
        modificarMargins();

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imeiscreen);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");

        if (Utilidades.controlRoot(getApplicationContext(),getResources(),"Imei")) {
            isRoot = true;
            if (!controlBusybox()) {
                instalarBusyBox();
            }
        }else{
            isRoot=false;
        }
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/
        imeiBBtn = (Button) findViewById(R.id.imeiBBtn);
        imeiRBtn = (Button) findViewById(R.id.imeiRBtn);
        imeiSEBtn = (Button) findViewById(R.id.imeiSEBtn);
        if (!isRoot) {
            imeiBBtn.setEnabled(false);
            imeiBBtn.setTextColor(Color.parseColor("#BDBDBD"));
            imeiRBtn.setEnabled(false);
            imeiRBtn.setTextColor(Color.parseColor("#BDBDBD"));
            imeiSEBtn.setEnabled(false);
            imeiSEBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }else{
            imeiBBtn.setEnabled(true);
            imeiBBtn.setTextColor(Color.BLACK);
            imeiRBtn.setEnabled(true);
            imeiRBtn.setTextColor(Color.BLACK);
            File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
            if(ff.exists()){
                imeiSEBtn.setEnabled(true);
                imeiSEBtn.setTextColor(Color.BLACK);
            }else{
                imeiSEBtn.setEnabled(false);
                imeiSEBtn.setTextColor(Color.parseColor("#BDBDBD"));
            }
        }

        imeiBBtn.setOnClickListener(this);
        imeiRBtn.setOnClickListener(this);
        imeiSEBtn.setOnClickListener(this);
        modificarMargins();
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
        /*File ff=new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak");
        if(ff.exists()){
            imeiSEBtn.setEnabled(true);
        }else{
            imeiSEBtn.setEnabled(false);
        }*/
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 136", Toast.LENGTH_SHORT).show();
        }
    }

    private void rebootQuestion() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgRebootQ));
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
                            java.lang.Process p = rt.exec("su -c 'reboot'\n");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 137", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();
    }


    private boolean controlBusybox() {
        boolean busybox = false;
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
                                    bos.write(("busybox cp /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001 "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak\n").getBytes());
                                    //calcular md5 de la copia, comparar y si es correcto mostrar aviso de ok, en caso contrario decir que no OK y eliminar el IMEI.bak
                                    //Poner md5 en la cabecera del fichero si se puede
                                    bos.write(("exit").getBytes());
                                    bos.flush();
                                    bos.close();
                                    p.waitFor();
                                    Toast.makeText(getApplicationContext(), "IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
                                    imeiSEBtn.setEnabled(true);
                                    imeiSEBtn.setTextColor(Color.BLACK);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 139", Toast.LENGTH_SHORT).show();
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
                                        bos.write(("busybox cp /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001 "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak\n").getBytes());
                                        //calcular md5 de la copia, comparar y si es correcto mostrar aviso de ok, en caso contrario decir que no OK y eliminar el IMEI.bak
                                        //Poner md5 en la cabecera del fichero si se puede
                                        bos.write(("exit").getBytes());
                                        bos.flush();
                                        bos.close();
                                        p.waitFor();
                                        Toast.makeText(getApplicationContext(),  "IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeihecho), Toast.LENGTH_SHORT).show();
                                        imeiSEBtn.setEnabled(true);
                                        imeiSEBtn.setTextColor(Color.BLACK);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 140", Toast.LENGTH_SHORT).show();
                                    }
                  /*              }
                            });
                    dialog.show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();

                }*/
            }
            refresh();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 141", Toast.LENGTH_SHORT).show();
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
                                bos.write(("busybox cp "+Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/IMEI"+modelo+".bak /data/nvram/md/NVRAM/NVD_IMEI/MP0B_001\n").getBytes());

                                bos.write(("exit").getBytes());
                                bos.flush();
                                bos.close();
                                p.waitFor();
                                Toast.makeText(getApplicationContext(),  "IMEI"+modelo+".bak "+getResources().getString(R.string.msgImeirestored), Toast.LENGTH_SHORT).show();
                                rebootQuestion();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 142", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            dialog.show();
            refresh();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 143", Toast.LENGTH_SHORT).show();

        }
    }
    private void instalarBusyBox() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgNoBusybox));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        finish();
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.aceptarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri
                                    .parse("market://details?id=com.jrummy.busybox.installer"));
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 144", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();

    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
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
         Button b1=(Button) findViewById(R.id.imeiBBtn);
        Button b2=(Button) findViewById(R.id.imeiRBtn);
        Button b3=(Button) findViewById(R.id.imeiSEBtn);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);
                b2.setPadding(250, 0, 0, 0);
                b3.setPadding(250, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);
                b2.setPadding(140, 0, 0, 0);
                b3.setPadding(140, 0, 0, 0);
            }
        }else if(dpi==320) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);
                b3.setPadding(350, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);
                b2.setPadding(200, 0, 0, 0);
                b3.setPadding(200, 0, 0, 0);
            }
        }else if(dpi==480) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);
                b2.setPadding(570, 0, 0, 0);
                b3.setPadding(570, 0, 0, 0);
            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);
                b3.setPadding(350, 0, 0, 0);
            }
        }

    }
}