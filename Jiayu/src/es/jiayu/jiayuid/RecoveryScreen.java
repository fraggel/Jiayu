package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
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
public class RecoveryScreen extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner recoverySpn = null;
    boolean firmarChk=false;

    Button recoveryBtn = null;
    ImageButton imageButton = null;
    Button rebootRcoveryBtn = null;
    public static String recoveryseleccionado = null;
    public static boolean descomprimido = false;
    String modelo=null;
    ArrayList<String> listaRecoUrl = new ArrayList<String>();
    SharedPreferences ajustes=null;
    List listaReco = new ArrayList();

    boolean isRoot = false;

    @Override
    protected void onResume() {
        super.onResume();
        String listaIdiomas[]=getResources().getStringArray(R.array.languages_values);
        SharedPreferences ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
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
        recoverySpn = (Spinner) findViewById(R.id.recoverySpn);
        refreshCombos();
        modificarMargins();

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoveryscreen);
        modificarMargins();
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
        modelo = getIntent().getExtras().getString("modelo");
        isRoot=getIntent().getExtras().getBoolean("root");
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/
        recoverySpn = (Spinner) findViewById(R.id.recoverySpn);

        recoveryBtn = (Button) findViewById(R.id.recoveryBtn);
        rebootRcoveryBtn = (Button) findViewById(R.id.recoveryBtn);

        if (!isRoot) {
            recoverySpn.setVisibility(View.INVISIBLE);
            recoveryBtn.setVisibility(View.INVISIBLE);
            rebootRcoveryBtn.setVisibility(View.INVISIBLE);
        }

        recoveryBtn.setEnabled(false);
        recoveryBtn.setTextColor(Color.parseColor("#BDBDBD"));
        recoverySpn.setOnItemSelectedListener(this);
        recoveryBtn.setOnClickListener(this);
        rebootRcoveryBtn.setOnClickListener(this);
        refreshCombos();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.recoverySpn) {
            if (listaRecoUrl != null && listaRecoUrl.size() > 0) {
                String recoveryselec = listaRecoUrl.get(i);
                if (!"".equals(recoveryselec.trim())) {
                    recoveryBtn.setEnabled(true);
                    recoveryBtn.setTextColor(Color.BLACK);
                    this.recoveryseleccionado = recoveryselec;
                } else {
                    recoveryBtn.setEnabled(false);
                    recoveryBtn.setTextColor(Color.parseColor("#BDBDBD"));
                    this.recoveryseleccionado = "";
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.recoverySpn) {
            String recoveryselec = null;
        }

    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.recoveryBtn) {

            try {
                boolean mimodelo=false;
                if(this.recoveryseleccionado.indexOf(modelo)!=-1){
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
                                    RecoveryScreen.descomprimido = false;
                                }
                            });
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                            getResources().getString(R.string.aceptarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    try {
                                        if(firmarChk){
                                            if(Utilidades.checkFileMD5(new File(RecoveryScreen.recoveryseleccionado))){
                                                unZip(RecoveryScreen.recoveryseleccionado);
                                                RecoveryScreen.descomprimido = true;
                                                flashRecovery();

                                            }else{
                                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            unZip(RecoveryScreen.recoveryseleccionado);
                                            RecoveryScreen.descomprimido = true;
                                            flashRecovery();
                                        }
                                        //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 145", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.show();
                }else{
                    if(firmarChk){
                        if(Utilidades.checkFileMD5(new File(this.recoveryseleccionado))){
                            unZip(this.recoveryseleccionado);
                            this.descomprimido = true;
                            flashRecovery();
                        }else{
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgErrorMD5),Toast.LENGTH_LONG).show();
                        }
                    }else{
                        unZip(this.recoveryseleccionado);
                        this.descomprimido = true;
                        flashRecovery();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgErrorUnzip) + new File(this.recoveryseleccionado).getName()+" 146", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void flashRecovery(){
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
                bos.write(("dd if=" + ficheroRecovery + " of=/dev/recovery\n").getBytes());
                bos.write(("exit").getBytes());
                bos.flush();
                bos.close();
                p.waitFor();
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                String recoveryInstall="";
                if(ajustes.getBoolean("recoveryChk",false)){
                    recoveryInstall=getResources().getString(R.string.msgRecoveryDetectadoReboot);
                    dialog.setMessage(recoveryInstall+getResources().getString(R.string.msgRecoveryFlasheado));
                }else{
                    dialog.setMessage(getResources().getString(R.string.msgRecoveryFlasheado));
                }
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
                                    bos.write(("exit").getBytes());
                                    bos.flush();
                                    bos.close();
                                    p.waitFor();
                                    //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 146", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgErrorRecovery) + new File(this.recoveryseleccionado).getName()+" 147", Toast.LENGTH_SHORT).show();
            }
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
                            bos.write(("exit").getBytes());
                            bos.flush();
                            bos.close();
                            p.waitFor();
                            //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 148", Toast.LENGTH_SHORT).show();
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

    public void refreshCombos() {
        listaReco.clear();
        listaRecoUrl.clear();
        listaReco.add(getResources().getString(R.string.seleccionaValue));
        listaRecoUrl.add("");
        File f2 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/RECOVERY/");

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

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaReco);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recoverySpn.setAdapter(dataAdapter2);

    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 94);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 350, 0, 190);
        }
        scText.setLayoutParams((llp));


        Button b1=(Button) findViewById(R.id.recoveryBtn);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);

            }
        }else if(dpi==320) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);

            }
        }else if(dpi==480) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);

            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);

            }
        }

    }
}