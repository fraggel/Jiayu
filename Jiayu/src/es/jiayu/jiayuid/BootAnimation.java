package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * Created by Fraggel on 25/08/13.
 */
public class BootAnimation extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Spinner bootSpn = null;
    Button bootBtn = null;
    Button bootDescargaBtn = null;
    ImageButton imageButton = null;
    String bootSeleccionada=null;
    ArrayList<String> listaBootsUrl = new ArrayList<String>();
    List listaBoots=new ArrayList();
    String modelo=null;
    String tipo=null;


    boolean isRoot = false;
    boolean isBusy=false;
    String path = "";
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
        bootSpn = (Spinner) findViewById(R.id.bootSpn);
        refreshCombos();
        modificarMargins();

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootanimation);
        modelo = getIntent().getExtras().getString("modelo");
        tipo = getIntent().getExtras().getString("tipo");
        isRoot=getIntent().getExtras().getBoolean("root");
            if (!controlBusybox()) {
                isBusy = false;
                instalarBusyBox();
            }else{
                isBusy = true;
            }
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/

        /*CheckBox chk= (CheckBox) findViewById(R.id.ajustaBootChk);
        chk.setVisibility(View.INVISIBLE);*/
        bootDescargaBtn=(Button) findViewById(R.id.bootDescargaBtn);
        bootBtn=(Button) findViewById(R.id.bootInstallBtn);
        bootSpn = (Spinner) findViewById(R.id.bootSpn);
        bootBtn.setEnabled(false);
        bootBtn.setTextColor(Color.parseColor("#BDBDBD"));
        bootBtn.setOnClickListener(this);
        bootDescargaBtn.setOnClickListener(this);
        bootSpn.setOnItemSelectedListener(this);
        modificarMargins();
    }

    public void refreshCombos() {
        listaBoots.clear();
        listaBootsUrl.clear();


        listaBoots.add(getResources().getString(R.string.downloadedBootTxt));
        listaBootsUrl.add("");


        File f3 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/BOOTANIMATION/");
        if (f3.exists()) {
            if (f3.listFiles().length > 0) {
                for (int x = 0; x < f3.listFiles().length; x++) {
                    File fx = (File) f3.listFiles()[x];
                    if (!fx.isDirectory() && fx.isFile() && (fx.getName().lastIndexOf("zip")!=-1)) {
                        listaBoots.add(fx.getName());
                        listaBootsUrl.add(fx.getAbsolutePath());
                    }
                }
            }
        }

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaBoots);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bootSpn.setAdapter(dataAdapter3);

    }
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.bootDescargaBtn) {
            try {
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("modelo", modelo);
                intent.putExtra("tipo", tipo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 126", Toast.LENGTH_SHORT).show();
            }
        }else if (button.getId() == R.id.bootInstallBtn) {
            try {

                String bootAnimSelec=null;
                /*CheckBox chk= (CheckBox) findViewById(R.id.ajustaBootChk);*/
                /*if(chk.isChecked()){
                    bootAnimSelec=reconstruirZipBootAnimation(this.bootSeleccionada);
                }else{*/
                    bootAnimSelec=this.bootSeleccionada;
               /* }*/
                if(bootAnimSelec!=null && !"".equals(bootAnimSelec)){
                    Runtime rt = Runtime.getRuntime();
                    java.lang.Process p = rt.exec("su");
                    BufferedOutputStream bos = new BufferedOutputStream(
                            p.getOutputStream());
                    bos.write(("mount -o rw,remount /dev/block/mmcblk0p3 /system\n").getBytes());
                    bos.write(("mv /system/media/bootanimation.zip /system/media/bootanimationORI.zip\n").getBytes());
                    bos.write(("busybox cp "+bootAnimSelec+" /system/media/bootanimation.zip\n").getBytes());
                    bos.write(("chmod 777 /system/media/bootanimation.zip\n").getBytes());
                    bos.write(("exit").getBytes());
                    bos.flush();
                    bos.close();
                    p.waitFor();
                }
                /*if(chk.isChecked()){
                    File ff=new File(bootSeleccionada.substring(0, bootSeleccionada.length() - 4)+"2.zip");
                    ff.delete();
                }*/
                bootSpn.setSelection(0);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgInstallBootC), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 127", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String reconstruirZipBootAnimation(String bootSeleccionada) {
        String selec=null;
        try {
            unZip(bootSeleccionada);
            changeDesc(this.bootSeleccionada.substring(0, this.bootSeleccionada.length() - 4));
            File ff=new File(this.bootSeleccionada+"2");
            ff.delete();
            ZipOutputStream zos=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(bootSeleccionada+"2")));
            zos.setLevel(0);
            zipIt(this.bootSeleccionada.substring(0, this.bootSeleccionada.length() - 4), zos,new File(this.bootSeleccionada.substring(0, this.bootSeleccionada.length() - 4)).getName());
            selec=this.bootSeleccionada+"2";
            zos.flush();
            zos.close();
            zos.finish();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 128", Toast.LENGTH_SHORT).show();
        }

        return selec;
    }

    private void changeDesc(String carpeta) {
        try {
            File f=new File(carpeta+"/desc.txt");
            File f2=new File(carpeta+"/desc2.txt");
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width = dm.widthPixels;
            f.renameTo(f2);
            //OutputStreamWriter bos=new OutputStreamWriter(new FileOutputStream(f),"ISO-8859-1");
            OutputStreamWriter bos=new OutputStreamWriter(new FileOutputStream(f));
            BufferedReader br=new BufferedReader(new FileReader(f2));
            String linea=br.readLine();
            String linea1=width + " " + height + " "+linea.split(" ")[2]+"\n";
            bos.write(linea1);
            //bos.write(linea+"\n");
            linea=br.readLine();
            while(linea!=null){
                bos.write(linea+"\n");
                linea=br.readLine();
            }
            br.close();
            bos.flush();
            bos.close();
            f2.delete();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 129", Toast.LENGTH_SHORT).show();
        }
    }
    AnimationDrawable frameAnimation=null;
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {

            Spinner spinner = (Spinner) adapterView;
            if (spinner.getId() == R.id.bootSpn) {
                if (listaBootsUrl != null && listaBootsUrl.size() > 0) {
                    String bootselect = listaBootsUrl.get(i);
                    if (!"".equals(bootselect.trim())) {
                        if(isBusy){
                            bootBtn.setEnabled(true);
                            bootBtn.setTextColor(Color.BLACK);
                            this.bootSeleccionada = bootselect;
                        }else{
                            bootBtn.setEnabled(false);
                            bootBtn.setTextColor(Color.parseColor("#BDBDBD"));
                            this.bootSeleccionada = null;
                        }
                        //unZip(bootSeleccionada);
                        /*ImageView showedImage = (ImageView) findViewById(R.id.gifAnimadoImg);
                        showedImage.setBackgroundResource(R.drawable.ic_launcher);
                        frameAnimation = (AnimationDrawable) showedImage.getBackground();
                        addPicturesOnExternalStorageIfExist();*/
                        WebView myWebView = (WebView) findViewById(R.id.gifAnimadoImg);
                        myWebView.setVisibility(View.VISIBLE);
                        myWebView.loadUrl("file:///"+bootselect.substring(0,bootselect.length()-4)+".gif");
                    } else {
                        bootBtn.setEnabled(false);
                        bootBtn.setTextColor(Color.parseColor("#BDBDBD"));
                        this.bootSeleccionada = "";
                        WebView myWebView = (WebView) findViewById(R.id.gifAnimadoImg);
                        myWebView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 130", Toast.LENGTH_SHORT).show();
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
        Spinner spinner = (Spinner) adapterView;
        if (spinner.getId() == R.id.bootSpn) {
            this.bootSeleccionada = null;
        }

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
    public static void zipIt(String dir2zip, ZipOutputStream zos,String parent) {
        try {

            File zipDir = new File(dir2zip);
            // lista del contenido del directorio
            String[] dirList = zipDir.list();
            // System.out.println(dirList[1]);
            //byte[] readBuffer = new byte[2048];
            //int bytesIn = 0;

            System.out.println(dirList.length);
            // recorro el directorio y añado los archivos al zip
            for (int i = 0; i < dirList.length; i++) {
                File f = new File(zipDir, dirList[i]);
                if (f.isDirectory()) {

                    String filePath = f.getPath();
                    zipIt(filePath, zos,parent);

                    System.out.println(filePath);
                    continue;
                }

                FileInputStream fis = new FileInputStream(f);
                ZipEntry anEntry=null;
                if(parent.equals(f.getParentFile().getName())){
                    anEntry = new ZipEntry(f.getName());
                }else{
                    anEntry = new ZipEntry(f.getParentFile().getName()+"/"+f.getName());
                }


                zos.putNextEntry(anEntry);
                int ii;
                while ((ii=fis.read()) != -1) {
                    zos.write(ii);
                    ii=fis.read();
                }
                zos.closeEntry();
                fis.close();
            }
        }
        catch (Exception e) {
        }
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
            llp.setMargins(40, 0, 0, 94);
        }else if(dpi==320) {
            llp.setMargins(50, 0, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 0, 0, 190);
        }
        scText.setLayoutParams((llp));


        Button b1=(Button) findViewById(R.id.bootDescargaBtn);
        Button b2=(Button) findViewById(R.id.bootInstallBtn);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);
                b2.setPadding(250, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);
                b2.setPadding(140, 0, 0, 0);
            }
        }else if(dpi==320) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);
                b2.setPadding(200, 0, 0, 0);
            }
        }else if(dpi==480) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);
                b2.setPadding(570, 0, 0, 0);
            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);
            }
        }

    }
}