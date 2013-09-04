package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Movie;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
    String path = "";
    protected void onResume() {
        super.onResume();

        bootSpn = (Spinner) findViewById(R.id.bootSpn);
        refreshCombos();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootanimation);
        modelo = getIntent().getExtras().getString("modelo");
        tipo = getIntent().getExtras().getString("tipo");
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
        CheckBox chk= (CheckBox) findViewById(R.id.ajustaBootChk);
        chk.setVisibility(View.INVISIBLE);
        bootDescargaBtn=(Button) findViewById(R.id.bootDescargaBtn);
        bootBtn=(Button) findViewById(R.id.bootInstallBtn);
        bootSpn = (Spinner) findViewById(R.id.bootSpn);
        bootBtn.setEnabled(false);
        bootBtn.setOnClickListener(this);
        bootDescargaBtn.setOnClickListener(this);
        bootSpn.setOnItemSelectedListener(this);
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
    public void refreshCombos() {
        listaBoots.clear();
        listaBootsUrl.clear();


        listaBoots.add(getResources().getString(R.string.seleccionaValue));
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
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
            }
        }else if (button.getId() == R.id.bootInstallBtn) {
            try {

                String bootAnimSelec=null;
                CheckBox chk= (CheckBox) findViewById(R.id.ajustaBootChk);
                if(chk.isChecked()){
                    bootAnimSelec=reconstruirZipBootAnimation(this.bootSeleccionada);
                }else{
                    bootAnimSelec=this.bootSeleccionada;
                }
                if(bootAnimSelec!=null && !"".equals(bootAnimSelec)){
                    Runtime rt = Runtime.getRuntime();
                    java.lang.Process p = rt.exec("su");
                    BufferedOutputStream bos = new BufferedOutputStream(
                            p.getOutputStream());
                    bos.write(("mount -o rw,remount /dev/block/mmcblk0p3 /system\n").getBytes());
                    bos.write(("rm /system/media/bootanimation.zip\n").getBytes());
                    bos.write(("cp "+bootAnimSelec+" /system/media/bootanimation.zip\n").getBytes());
                    bos.write(("chmod 777 /system/media/bootanimation.zip\n").getBytes());
                    bos.flush();
                    bos.close();
                }
                if(chk.isChecked()){
                    File ff=new File(bootSeleccionada+"2");
                    ff.delete();
                }
                bootSpn.setSelection(0);
                Toast.makeText(getBaseContext(), getResources().getString(R.string.msgInstallBootC), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }

        }
    }

    private String reconstruirZipBootAnimation(String bootSeleccionada) {
        String selec=null;
        try {
            unZip(bootSeleccionada);
            changeDesc(this.bootSeleccionada.substring(0, this.bootSeleccionada.length() - 4));
            File ff=new File(bootSeleccionada+"2");
            ff.delete();
            ZipOutputStream zos=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(bootSeleccionada+"2")));
            zos.setLevel(0);
            zipIt(this.bootSeleccionada.substring(0, this.bootSeleccionada.length() - 4), zos);
            selec=bootSeleccionada+"2";
            zos.flush();
            zos.close();
        }catch(Exception e){
            e.printStackTrace();
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
            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(f2));

            byte[] b= new byte[(int)f2.length()];
            bis.read(b);
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(f));
            bos.write(b);
            bos.flush();
            bos.close();
            f2.delete();
        }catch(Exception e){

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
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.msgSeleccionado) + " " + new File(bootselect).getName(), Toast.LENGTH_SHORT).show();
                        bootBtn.setEnabled(true);
                        this.bootSeleccionada = bootselect;
                        unZip(bootSeleccionada);
                        ImageView showedImage = (ImageView) findViewById(R.id.gifAnimadoImg);
                        showedImage.setBackgroundResource(R.drawable.ic_launcher);
                        frameAnimation=new AnimationDrawable();
                        //frameAnimation = (AnimationDrawable) showedImage.getBackground();
                        addPicturesOnExternalStorageIfExist();
                        frameAnimation.start();
                        /*WebView myWebView = (WebView) findViewById(R.id.gifAnimadoImg);
                        myWebView.setVisibility(View.VISIBLE);
                        myWebView.loadUrl("file:///"+bootselect.substring(0,bootselect.length()-4)+".gif");*/
                    } else {
                        bootBtn.setEnabled(false);
                        this.bootSeleccionada = "";
                        /*WebView myWebView = (WebView) findViewById(R.id.gifAnimadoImg);
                        myWebView.setVisibility(View.INVISIBLE);*/
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
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
    public static void zipIt(String dir2zip, ZipOutputStream zos) {
            try {

                File zipDir = new File(dir2zip);
                // lista del contenido del directorio
                String[] dirList = zipDir.list();
                // System.out.println(dirList[1]);
                byte[] readBuffer = new byte[2156];
                int bytesIn = 0;

                System.out.println(dirList.length);
                // recorro el directorio y añado los archivos al zip
                for (int i = 0; i < dirList.length; i++) {
                    File f = new File(zipDir, dirList[i]);
                    if (f.isDirectory()) {

                        String filePath = f.getPath();
                        zipIt(filePath, zos);

                        System.out.println(filePath);
                        continue;
                    }

                    FileInputStream fis = new FileInputStream(f);

                    ZipEntry anEntry = new ZipEntry(f.getPath());

                    zos.putNextEntry(anEntry);

                    while ((bytesIn = fis.read(readBuffer)) != -1) {
                        zos.write(readBuffer, 0, bytesIn);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }
    private void addPicturesOnExternalStorageIfExist() {
        // check if external storage
        String state = Environment.getExternalStorageState();
        if ( !(Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) ) {
            return;
        }
        // 'happyShow' is the name of directory
        File pictureDirectory = new File(Environment.getExternalStorageDirectory()+ "/JIAYUES/BOOTANIMATION/bootanimationCirulo/part0/");
        if ( !pictureDirectory.exists() ) {
            return;
        }

        // check if there is any picture
        //create a FilenameFilter and override its accept-method
        FilenameFilter filefilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpeg") ||
                        name.endsWith(".jpg") ||
                        name.endsWith(".png") );
            }
        };

        String[] sNamelist = pictureDirectory.list(filefilter);
        if (sNamelist.length == 0) {
            return;
    }

        for (String filename : sNamelist) {
            frameAnimation.addFrame(
                    Drawable.createFromPath(pictureDirectory.getPath() + '/' + filename),
                    1000);
        }

        return;
    }
}