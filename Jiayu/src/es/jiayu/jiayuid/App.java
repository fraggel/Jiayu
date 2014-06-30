package es.jiayu.jiayuid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class App extends Activity implements AsyncResponse{

    static long downloadREF = -1;
    static HashMap<String, String> listaDescargas = new HashMap<String, String>();
    String nversion = "";
    String version = "";
    ImageButton imageButton;
    Button descargas;
    Button foro;
    Button about;
    Button videotutoriales;
    Button driversherramientas;
    Button herramientasROM;
    Button config;
    TextView t3;
    //Button envioNoExisteBtn;
    Button btnInfo;
    String modelo = "";
    String model = "";
    String urlActualizacion = "";
    String fabricante = "";
    String compilacion = "";
    String modelBuild="";
    String newversion = "";
    String chip = "";
    String listaIdiomas[]=null;


    public static boolean noInternet=false;
    static NotificationManager mNotificationManagerUpdate=null;
    static NotificationManager mNotificationManagerNews=null;
    private int SIMPLE_NOTFICATION_UPDATE=8888;
    private int SIMPLE_NOTFICATION_NEWS=8889;
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
    public static boolean updatemostrado=false;
    @Override
    protected void onResume() {
        super.onResume();
        listaIdiomas=getResources().getStringArray(R.array.languages_values);
        ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
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

    }
    private boolean comprobarConexion() {
        boolean nohayinternet=false;
        ConnectivityManager cn=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        if(nf != null && nf.isConnected()==true )
        {
            nohayinternet=false;

        }
        else
        {
            nohayinternet=true;
        }
        return nohayinternet;
    }
    protected void onCreate(Bundle savedInstanceState) {
            try {
                //Intent intent = getIntent();
                //String ini = intent.getExtras().getString("ini");

                super.onCreate(savedInstanceState);

                Resources res = this.getResources();

                setContentView(R.layout.activity_app);
                TextView scText=(TextView) findViewById(R.id.textView3);
                TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int dpi=getResources().getDisplayMetrics().densityDpi;
                /*if(dpi==240) {
                    llp.setMargins(0, 180, 0, 2);
                }else if(dpi==320) {
                    llp.setMargins(0, 250, 0, 2);
                }else if(dpi==480) {
                    llp.setMargins(0, 350, 0, 2);
                }*/
                if(dpi==240) {
                    llp.setMargins(0, 175, 0, 2);
                }else if(dpi==320) {
                    llp.setMargins(0, 230, 0, 2);
                }else if(dpi==480) {
                    llp.setMargins(0, 350, 0, 2);
                }

                scText.setLayoutParams((llp));
                /*WebView wvv=(WebView) findViewById(R.id.webView);
                TableLayout.LayoutParams llp2 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int dpi2=getResources().getDisplayMetrics().densityDpi;
                if(dpi2==240) {
                    llp2.height=70;
                }else if(dpi2==320) {
                    llp2.height=90;
                }else if(dpi2==480) {
                    llp2.height=100;
                }
                wvv.setLayoutParams((llp2));*/
                //if("ini".equals(ini)){
                    //    intent.putExtra("ini","");
                        nversion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                        mNotificationManagerUpdate = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManagerUpdate.cancel(SIMPLE_NOTFICATION_UPDATE);
                        ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
                        editorAjustes=ajustes.edit();
                        boolean notificacionesNews=ajustes.getBoolean("notificacionesNews",true);
                        boolean notificacionesUpd=ajustes.getBoolean("notificacionesUpd",true);
                        editorAjustes.putBoolean("notificacionesNews",notificacionesNews);
                        editorAjustes.putBoolean("notificacionesUpd",notificacionesUpd);

                        String tmpFecha="";
                        tmpFecha=ajustes.getString("fechaUltimoAccesoDescargas", "");
                        if("".equals(tmpFecha)){
                            editorAjustes.putString("fechaUltimoAccesoDescargas", asignaFecha());
                            editorAjustes.commit();
                        }
                        version = "Jiayu.es ";
                        version = version + nversion;
                        App.noInternet=comprobarConexion();
                        comprobarVersionInicio(version);
                    //}
                        File f1 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/APP/");
                        if (!f1.exists()) {
                            f1.mkdirs();
                        }
                        File f2 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/ROMS/");
                        if (!f2.exists()) {
                            f2.mkdirs();
                        }
                        File f3 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/RECOVERY/");
                        if (!f3.exists()) {
                            f3.mkdirs();
                        }
                        File f4 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/DOWNLOADS/");
                        if (!f4.exists()) {
                            f4.mkdirs();
                        }
                        File f5 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/IMEI/");
                        if (!f5.exists()) {
                            f5.mkdirs();
                        }
                        File f6 = new File(Environment.getExternalStorageDirectory() + "/JIAYUES/BOOTANIMATION/");
                        if (!f6.exists()) {
                            f6.mkdirs();
                        }
                        /*File f7 = new File(Environment.getExternalStorageDirectory() + "/update.zip");
                        if (f7.exists()) {
                            f7.delete();
                        }*/



                        descargas = (Button) findViewById(R.id.button1);
                        about = (Button) findViewById(R.id.button2);
                        config = (Button) findViewById(R.id.button5);
                        videotutoriales = (Button) findViewById(R.id.button3);
                        foro = (Button) findViewById(R.id.button4);
                        driversherramientas = (Button) findViewById(R.id.button9);
                        herramientasROM = (Button) findViewById(R.id.button10);
                        //envioNoExisteBtn=(Button)findViewById(R.id.envioNoExisteBtn);
                        //btnInfo=(Button)findViewById(R.id.btnInfo);
                        //envioNoExisteBtn.setVisibility(View.INVISIBLE);
                        descargas.setEnabled(false);
                        descargas.setTextColor(Color.parseColor("#BDBDBD"));
                        //accesorios.setEnabled(false);
                        videotutoriales.setEnabled(false);
                        videotutoriales.setTextColor(Color.parseColor("#BDBDBD"));
                        driversherramientas.setEnabled(false);
                        driversherramientas.setTextColor(Color.parseColor("#BDBDBD"));
                        herramientasROM.setEnabled(false);
                        herramientasROM.setTextColor(Color.parseColor("#BDBDBD"));
                        foro.setEnabled(false);
                        foro.setTextColor(Color.parseColor("#BDBDBD"));
                        ImageButton img = new ImageButton(this);
                        img = (ImageButton) findViewById(R.id.imageButton1);
                        TextView tmodelo = new TextView(this);
                        t3 =(TextView) findViewById(R.id.textView3);
                        t3.setText(R.string.bienvenida);
                        TextView t4 = new TextView(this);
                        //TextView t5 = new TextView(this);
                        t4 = (TextView) findViewById(R.id.textView4);
                        //t5 = (TextView) findViewById(R.id.textView5);
                        t4.setText(version);
                        compilacion = Build.DISPLAY;
                        modelBuild=Build.MODEL;
                        fabricante = infoBrand();
                        tmodelo = (TextView) findViewById(R.id.textView1);

                        addListenerOnButton();
                        //if("ini".equals(ini)){
                            if ("".equals(modelo)) {
                                calcularTelefono();
                                modelo = model;
                            } else {
                                recalcularTelefono();
                                descargas.setEnabled(true);
                                herramientasROM.setEnabled(true);
                                //accesorios.setEnabled(true);
                                foro.setEnabled(true);
                                driversherramientas.setEnabled(true);
                                videotutoriales.setEnabled(true);
                                descargas.setTextColor(Color.BLACK);
                                herramientasROM.setTextColor(Color.BLACK);
                                foro.setTextColor(Color.BLACK);
                                driversherramientas.setTextColor(Color.BLACK);
                                videotutoriales.setTextColor(Color.BLACK);

                            }
                            if (modelo.length() < 10) {
                                Calendar cal=Calendar.getInstance();
                                editorAjustes = ajustes.edit();
                                editorAjustes.putString("modelo", modelo);
                                editorAjustes.commit();

                                descargas.setEnabled(true);
                                descargas.setTextColor(Color.BLACK);
                                //accesorios.setEnabled(true);
                                foro.setEnabled(true);
                                foro.setTextColor(Color.BLACK);
                                driversherramientas.setEnabled(true);
                                driversherramientas.setTextColor(Color.BLACK);
                                videotutoriales.setEnabled(true);
                                videotutoriales.setTextColor(Color.BLACK);
                                herramientasROM.setEnabled(true);
                                herramientasROM.setTextColor(Color.BLACK);
                                if (!"JIAYU".equals(fabricante.toUpperCase().trim()) && !"PIPO".equals(fabricante.toUpperCase().trim())) {
                                    t3.setTextColor(Color.parseColor("#449def"));
                                    t3.setTextColor(Color.RED);
                                    t3.setText(res.getString(R.string.msgIdentificado1) +" "+ modelo + res.getString(R.string.msgIdentificado2));
                                    t3.setClickable(false);
                                    t3.setVisibility(View.VISIBLE);
                                    //t5.setTextColor(Color.RED);
                                    //t5.setText(res.getString(R.string.msgIdentificado1) +" "+ modelo + res.getString(R.string.msgIdentificado2));
                                }else{
                                    t3.setTextColor(Color.BLACK);
                                    t3.setText(res.getString(R.string.bienvenida));
                                    t3.setClickable(false);
                                    t3.setVisibility(View.VISIBLE);
                                    //t5.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                t3.setTextColor(Color.parseColor("#ffffff"));
                                t3.setText(res.getString(R.string.enviarNoExisteBtn));
                                t3.setClickable(true);
                                t3.setVisibility(View.VISIBLE);
                                descargas.setEnabled(false);
                                descargas.setTextColor(Color.parseColor("#BDBDBD"));
                                driversherramientas.setEnabled(false);
                                driversherramientas.setTextColor(Color.parseColor("#BDBDBD"));
                                herramientasROM.setEnabled(false);
                                herramientasROM.setTextColor(Color.parseColor("#BDBDBD"));
                            }
                            tmodelo.setText(res.getString(R.string.msgModelo) + modelo+"\n"+res.getString(R.string.msgCompilacion) + compilacion);


                            if("T1".equals(modelo) || "T2".equals(modelo)){
                                herramientasROM.setEnabled(false);
                                herramientasROM.setTextColor(Color.parseColor("#BDBDBD"));
                            }
                if (modelo.length() < 10 && ("JIAYU".equals(fabricante.toUpperCase().trim()) || "PIPO".equals(fabricante.toUpperCase().trim()))) {
                    String externalStorageState = Environment.getExternalStorageState();
                    if (!"mounted".equals(externalStorageState.toLowerCase())) {
                        driversherramientas.setEnabled(false);
                        driversherramientas.setTextColor(Color.parseColor("#BDBDBD"));
                        descargas.setEnabled(false);
                        descargas.setTextColor(Color.parseColor("#BDBDBD"));
                        //herramientasROM.setEnabled(false);
                    /*if(t5.getVisibility()==View.INVISIBLE){
                        t5.setVisibility(View.VISIBLE);
                        t5.setTextColor(Color.RED);
                        t5.setText(res.getString(R.string.msgNoSD));*/
                        //if(envioNoExisteBtn.getVisibility()==View.INVISIBLE){
                        t3.setTextColor(Color.parseColor("#449def"));
                        t3.setTextColor(Color.RED);
                        t3.setText(res.getString(R.string.msgNoSD));
                        t3.setClickable(false);
                        t3.setVisibility(View.VISIBLE);
                    /*}else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgNoSD), Toast.LENGTH_LONG).show();
                    }*/
                    } else {
                        driversherramientas.setEnabled(true);
                        driversherramientas.setTextColor(Color.BLACK);
                        descargas.setEnabled(true);
                        descargas.setTextColor(Color.BLACK);
                        t3.setTextColor(Color.BLACK);
                        t3.setText(res.getString(R.string.bienvenida));
                        t3.setClickable(false);
                        t3.setVisibility(View.VISIBLE);
                    }
                }
                       // }
                /*if(ajustes.getBoolean("firstUse",true)){
                    btnInfo.setTextAppearance(getApplicationContext(), android.R.attr.textAppearanceMedium);
                    btnInfo.setBackgroundDrawable(res.getDrawable(R.drawable.btn_white_border));
                    btnInfo.setTextColor(Color.parseColor("#449def"));
                    btnInfo.setTextColor(Color.BLUE);
                    btnInfo.setText(R.string.msgNuevasOpciones);
                    btnInfo.setClickable(false);
                    btnInfo.setVisibility(View.VISIBLE);
                    config.setBackgroundDrawable(res.getDrawable(R.drawable.btn_yellow));
                    config.setFocusableInTouchMode(true);
                    config.requestFocus();
                }else{*/
                    //btnInfo.setVisibility(View.GONE);
                    //config.setFocusableInTouchMode(false);
                    //config.setBackgroundDrawable(res.getDrawable(R.drawable.btn_green));
                    //config.setBackgroundDrawable(res.getDrawable(R.drawable.bt08));

                /*}*/

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 101", Toast.LENGTH_SHORT).show();
            }
        obtenerDatosPhone();
    }

    private String infoBrand() throws IOException {
        String fabricante = Build.BRAND;
        String buildprop = "";
        if (fabricante.toUpperCase().indexOf("JIAYU") == -1 || fabricante.toUpperCase().indexOf("PIPO") == -1) {
            FileInputStream fis = new FileInputStream(new File("/system/build.prop"));
            byte[] input = new byte[fis.available()];
            while (fis.read(input) != -1) {
                buildprop += new String(input);
            }
            if (buildprop.toUpperCase().indexOf("JIAYU") != -1) {
                fabricante = "JIAYU";
            } else if(buildprop.toUpperCase().indexOf("PIPO") != -1){
                fabricante = "PIPO";
            }else{
                fabricante = "TERMINAL NO JIAYU";
            }
        }
        return fabricante.toUpperCase();
    }

    private void comprobarVersion(String version2) {
        try {
            VersionThread asyncTask = new VersionThread();
            asyncTask.delegate = this;
            asyncTask.execute(version2);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 102", Toast.LENGTH_SHORT).show();
        }
    }

    private void comprobarVersionInicio(String version2) {
        try {
            VersionThread asyncTask = new VersionThread();
            asyncTask.delegate = this;
            asyncTask.execute(version2, "inicio");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 103", Toast.LENGTH_SHORT).show();
        }
    }


    private void ActualizarVersion() {
        try {
            String nombreFichero = "";
            nombreFichero = urlActualizacion.split("/")[urlActualizacion.split("/").length - 1];
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlActualizacion));
            request.setDescription(nombreFichero);
            request.setTitle(nombreFichero);
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                if (".apk".equals(nombreFichero.substring(nombreFichero.length() - 4, nombreFichero.length()).toLowerCase())) {
                    request.setMimeType("application/vnd.android.package-archive");
                    new File(Environment.getExternalStorageDirectory() + "/JIAYUES/APP/Jiayu.apk").delete();
                }

            }
            request.setDestinationInExternalPublicDir("/JIAYUES/APP/", nombreFichero);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgIniciandoDescarga) + " " + nombreFichero, Toast.LENGTH_SHORT).show();
            downloadREF = manager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 104", Toast.LENGTH_SHORT).show();
        }
    }

    private void recalcularTelefono() {
        calcularTelefono();
        /*if(modelo.equals(model)){*/
        modelo = model;
		/*}else{
			modelo="Rom incorrecta para tu terminal, tu modelo real es: "+model;
		}*/
    }

    private void calcularTelefono() {
        Resources res = this.getResources();
        try {

            int height = 0;
            int width = 0;
            String procesador = "";
            String ram = "";



            try {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                height = dm.heightPixels;
                width = dm.widthPixels;
                //procesador=Build.HARDWARE;
                procesador = getInfoCPU();
                int orientation = getResources().getConfiguration().orientation;

                ram = getTotalRAM();
                int ramInt = (Integer.parseInt(ram) / 1000);
                if (ramInt <= 290 && ramInt >= 200) {
                    ram = "256MB";
                } else if (ramInt <= 530 && ramInt >= 300) {
                    ram = "512MB";
                } else if (ramInt <= 1100 && ramInt >= 900) {
                    ram = "1GB";
                } else if (ramInt <= 2100 && ramInt >= 1700) {
                    ram = "2GB";
                }
                if(width==1080 || (orientation==2 && height==1080)){
                    if ("qctapq8064mtp".equals(procesador.toLowerCase())) {
                        if ("2GB".equals(ram)) {
                            model = "S1";
                        } else {
                            model = "";
                        }
                    }
                    if ("mt6592".equals(procesador.toLowerCase())) {
                        if ("2GB".equals(ram)) {
                            if(compilacion.indexOf("G6")!=-1 || modelBuild.indexOf("G6")!=-1){
                                model = "G6A";
                            }else if(compilacion.indexOf("S2")!=-1 || modelBuild.indexOf("S2")!=-1){
                                model = "S2A";
                            } else {
                                model = "";
                            }
                        } else if ("1GB".equals(ram)) {
                            if(compilacion.indexOf("G6")!=-1 || modelBuild.indexOf("G6")!=-1){
                                model = "G6M";
                            }else if(compilacion.indexOf("S2")!=-1 || modelBuild.indexOf("S2")!=-1){
                                model = "S2M";
                            } else {
                                model = "";
                            }
                        }
                    }
                }else if (width == 720 || (orientation == 2 && height == 720)) {
                    if ("mt6577".equals(procesador.toLowerCase())) {
                        comprobarMT();
                        if ("MT6628".equals(chip)) {
                            model = "G3DCN";
                        } else if ("MT6620".equals(chip)) {
                            model = "G3DC";
                        } else {
                            model = "";
                        }
                    } else if ("mt6589".equals(procesador.toLowerCase()) || ("mt6589t".equals(procesador.toLowerCase()))) {
                        if ("1GB".equals(ram)) {
                            /*model = getCPUFreqG3();
                            model = getCPUFreqG4();*/
                            RandomAccessFile reader = null;
                            String load = "";
                            try {
                                reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                                load = reader.readLine();
                                int cpufreq = Integer.parseInt(load.trim());
                                if (cpufreq > 1400000) {
                                    load = "T";
                                } else {
                                    load = "B";
                                }
                            } catch (IOException ex) {

                            } finally {
                                if (reader != null) {
                                    reader.close();
                                }
                            }
                            if(compilacion.indexOf("G3")!=-1 || modelBuild.indexOf("G3")!=-1){
                                if("B".equals(load)){
                                    model="G3QC";
                                }else if("T".equals(load)){
                                    model="G3QCT";
                                }
                            }else if(compilacion.indexOf("G4")!=-1 || modelBuild.indexOf("G4")!=-1){
                                if("B".equals(load)){
                                    model="G4B";
                                }else if("T".equals(load)){
                                    model="G4T";
                                }else {
                                    model = "";
                                }
                            }else if(compilacion.indexOf("G5")!=-1 || modelBuild.indexOf("G5")!=-1){
                                if("B".equals(load)){
                                    model="G5B";
                                }else if("T".equals(load)){
                                    model="G5B";
                                }else {
                                    model = "";
                                }
                            }else{
                                model="Modelo Desconocido, Custom Rom no permite detectar correctamente tu dispositivo";
                            }
                        } else if ("2GB".equals(ram)) {
                            if(compilacion.indexOf("G4")!=-1|| modelBuild.indexOf("G4")!=-1){
                                model="G4A";
                            }else if(compilacion.indexOf("G5")!=-1|| modelBuild.indexOf("G5")!=-1){
                                model="G5A";
                            }else {
                                model = "";
                            }
                        } else if ("512MB".equals(ram)) {
                            File path = Environment.getExternalStorageDirectory();
                            StatFs stat = new StatFs(path.getAbsolutePath());
                            long blockSize = stat.getBlockSize();
                            long totalBlocks = stat.getBlockCount();
                            long totalSpace = totalBlocks * blockSize;
                            double gigaTotal = totalSpace / 1073741824;
                            RandomAccessFile reader = null;
                            String load = "";
                            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
                            load = reader.readLine();
                            int cpufreq = Integer.parseInt(load.trim());
                            if (cpufreq > 1400000) {
                                load = "T";
                            } else {
                                load = "B";
                            }


                            if(compilacion.indexOf("G4")!=-1|| modelBuild.indexOf("G4")!=-1){
                                if(gigaTotal>20.0){
                                    load="A";
                                }
                                model="G4"+load;
                            }else if(compilacion.indexOf("G5")!=-1|| modelBuild.indexOf("G5")!=-1){
                                if(gigaTotal>20.0){
                                    load="A";
                                }
                                model="G5";
                            }else if(compilacion.indexOf("G3")!=-1|| modelBuild.indexOf("G3")!=-1) {
                                if("B".equals(load)){
                                    load="";
                                }
                                model="G3QC"+load;
                            }else{
                                model="";
                            }

                            AlertDialog dialog = new AlertDialog.Builder(this).create();
                            dialog.setMessage(res.getString(R.string.msgIdentificadoParcial) + ": " + model + " " + res.getString(R.string.msgRamProblem)+": "+ram+" "+res.getString(R.string.msgRamProblem2));
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                                    res.getString(R.string.aceptarBtn),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int witch) {
                                            try {
                                                finish();
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 118", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            dialog.show();
                        }
                    } else if ("mt6582".equals(procesador.toLowerCase())) {
                        if ("1GB".equals(ram)) {
                            if(compilacion.indexOf("G3")!=-1|| modelBuild.indexOf("G3")!=-1){
                                model="G3C";
                            }else if(compilacion.indexOf("G4")!=-1|| modelBuild.indexOf("G4")!=-1){
                                model="G4C";
                            }else if(compilacion.indexOf("G2")!=-1|| modelBuild.indexOf("G2")!=-1){
                                model="G2F";
                            }
                        } else {
                            model = "";
                        }
                    } else if ("mt6592".equals(procesador.toLowerCase()) || "mt6592m".equals(procesador.toLowerCase())) {
                        if ("1GB".equals(ram)) {
                            if(compilacion.indexOf("G4")!=-1|| modelBuild.indexOf("G4")!=-1){
                                model="G4SM";
                            } else if(compilacion.indexOf("G5")!=-1|| modelBuild.indexOf("G5")!=-1){
                                model="G5SM";
                            } else{
                                model = "";
                            }
                        } else if ("2GB".equals(ram)) {
                            if(compilacion.indexOf("G4")!=-1|| modelBuild.indexOf("G4")!=-1){
                                model="G4S";
                            } else if(compilacion.indexOf("G5")!=-1|| modelBuild.indexOf("G5")!=-1){
                                model="G5S";
                            } else{
                                model = "";
                            }
                        } else
                            model = "";
                    }

                } else if (width == 540 || (orientation == 2 && height == 540)) {
                    if ("mt6577t".equals(procesador.toLowerCase())) {
                        model = "G2S";
                    } else {
                        model = "";
                    }
                } else if (width == 480 || (orientation == 2 && height == 480)) {
                    if ("mt6575".equals(procesador.toLowerCase())) {
                        if ((Build.DISPLAY.toUpperCase()).indexOf("G16B") != -1) {
                            model = "G2SCN";
                        } else {
                            model = "G2SC";
                        }
                    } else if ("mt6577".equals(procesador.toLowerCase())) {
                        //FALTA EL Jiayu G2TD
                        comprobarMT();
                        if ("512MB".equals(ram)) {
                            if ("MT6628".equals(chip)) {
                                model = "G2DCPVN";
                                if ((Build.DISPLAY.toUpperCase()).indexOf("G2LSQ") != -1
                                        ||(Build.DISPLAY.toUpperCase()).indexOf("G2DCPVNQ") != -1
                                        ||(Build.DISPLAY.toUpperCase()).indexOf("Q") != -1) {
                                    model="G2DCPVNQ";
                                }
                            } else if ("MT6620".equals(chip)) {
                                model = "G2DCPV";
                            } else {
                                model = "";
                            }
                        } else if ("1GB".equals(ram)) {
                            if ("MT6628".equals(chip)) {
                                model = "G2DCN";
                            } else if ("MT6620".equals(chip)) {
                                model = "G2DC";
                            } else {
                                model = "";
                            }

                        } else {
                            model = "";
                        }
                    } else if ("mt6572".equals(procesador.toLowerCase())) {
                        if ("512MB".equals(ram)) {
                            model="F1";
                        }
                    }else if ("mt6582".equals(procesador.toLowerCase())) {
                        if ("1GB".equals(ram)) {
                            if (compilacion.indexOf("G2") != -1 || modelBuild.indexOf("G2") != -1) {
                                model = "G2F";
                            }
                        }
                        } else {
                            model = "";
                        }
                } else if (width == 320 || (orientation == 2 && height == 320)) {
                    if ("256MB".equals(ram)) {
                        model = "G1";
                    } else {
                        model = "";
                    }
                } else if (width == 800 || (orientation == 2 && width == 1280)) {
                    if ("2GB".equals(ram)) {
                           model="T1";
                    } else {
                        model = "";
                    }
                }else if (width == 600 || (orientation == 2 && width == 1024)) {
                    if ("1GB".equals(ram)) {
                        model="T2";
                    } else {
                        model = "";
                    }
                }else{
                        model = res.getString(R.string.msgTerminalNoJiayu);
                }

            } catch (Exception e) {
                model = res.getString(R.string.msgErrorIdentificar);
            }

            if ("".equals(model.trim())) {
                model = res.getString(R.string.msgErrorIdentificar);
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 105", Toast.LENGTH_SHORT).show();
        }
        /*
					}
				});
		dialog.show();*/
        /*BufferedInputStream kernel;
        BufferedInputStream recovery;
        BufferedInputStream uboot;
        ByteArrayOutputStream kernelBAOS=new ByteArrayOutputStream();
        ByteArrayOutputStream recoveryBAOS=new ByteArrayOutputStream();
        ByteArrayOutputStream ubootBAOS=new ByteArrayOutputStream();
        try {
            java.lang.Process p=null;
            java.lang.Process p2=null;
            java.lang.Process p3=null;
            java.lang.Process p4=null;
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("su");
                p = rt.exec("dd if=/dev/bootimg of=/sdcard/JIAYUES/kernel.img bs=512\n");
                p2 = rt.exec("dd if=/dev/recovery of=/sdcard/JIAYUES/recovery.img bs=512\n");
                p3 = rt.exec("dd if=/dev/uboot of=/sdcard/JIAYUES/uboot.img bs=512\n");
                p4 = rt.exec("chmod -R 777 /sdcard/JIAYUES/\n");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 137", Toast.LENGTH_SHORT).show();
            }

            byte[] temp=new byte[1024];
            kernel=new BufferedInputStream(new FileInputStream(new File("/sdcard/JIAYUES/kernel.img")));
            recovery=new BufferedInputStream(new FileInputStream(new File("/sdcard/JIAYUES/recovery.img")));
            uboot=new BufferedInputStream(new FileInputStream(new File("/sdcard/JIAYUES/uboot.img")));

            while(kernel.read(temp)!=-1){
                kernelBAOS.write(temp);
                kernelBAOS.flush();
            }
            kernelBAOS.close();
            temp=new byte[1024];
            while(recovery.read(temp)!=-1){
                recoveryBAOS.write(temp);
                recoveryBAOS.flush();
            }
            recoveryBAOS.close();
            temp=new byte[1024];
            while(uboot.read(temp)!=-1){
                ubootBAOS.write(temp);
                ubootBAOS.flush();
            }
            ubootBAOS.close();
            byte[] kernelBytes = kernelBAOS.toByteArray();
            byte[] recoveryBytes = recoveryBAOS.toByteArray();
            byte[] ubootBytes = ubootBAOS.toByteArray();

            String s = kernelBAOS.toString();

            String s1 = recoveryBAOS.toString();
            String s2 = ubootBAOS.toString();
            System.out.println("FIN CALC");
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 105", Toast.LENGTH_SHORT).show();
        }*/
    }

    private boolean levantarBlueTooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean total = false;
        try {
            if (mBluetoothAdapter == null) {
                total = false;
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    total = true;
                } else {
                    mBluetoothAdapter.enable();
                    synchronized (mBluetoothAdapter) {
                        mBluetoothAdapter.wait(2000);
                    }
                    if (mBluetoothAdapter.isEnabled()) {
                        total = true;
                    } else {
                        total = false;
                    }
                    while (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                    }

                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 106", Toast.LENGTH_SHORT).show();
        }
        return total;
    }

    private boolean LevantarWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        boolean total = false;
        try {


            if (wifiManager.isWifiEnabled()) {
                total = true;
            } else {
                wifiManager.setWifiEnabled(true);
                synchronized (wifiManager) {
                    wifiManager.wait(2000);
                }
                if (wifiManager.isWifiEnabled()) {
                    total = true;
                } else {
                    total = false;
                }
                while (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 107", Toast.LENGTH_SHORT).show();
        }
        return total;
    }

    public void addListenerOnButton() {
        try {

            /*imageButton = (ImageButton) findViewById(R.id.imageButton1);
            imageButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if(App.noInternet){
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                    }else{
                        Uri uri = Uri.parse("http://www.jiayu.es");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }

            });*/
            descargas = (Button) findViewById(R.id.button1);
            descargas.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if(App.noInternet){
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                    }else{
                        openBrowser(arg0, "downloads");
                    }
                }

            });
            about = (Button) findViewById(R.id.button2);
            about.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        /*Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent);*/
                        if(App.noInternet){
                            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                            startActivity(intent);
                        }else{
                            openBrowser(arg0, "about");
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 108", Toast.LENGTH_SHORT).show();
                    }
                    /*Uri uri = Uri.parse("http://www.jiayu.es/4-jiayu-accesorios");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);*/
                }

            });

            foro = (Button) findViewById(R.id.button4);
            foro.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if(App.noInternet){
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                    }else{

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgTapaTalk), Toast.LENGTH_LONG).show();
                        /*Uri uri = Uri.parse("http://www.foro.jiayu.es");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);*/
                        openBrowser(arg0,"foro");
                    }
                }

            });
            videotutoriales = (Button) findViewById(R.id.button3);
            videotutoriales.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if(App.noInternet){
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                    }else{
                        openBrowser(arg0,"videos");
                    }
                }

            });
            driversherramientas = (Button) findViewById(R.id.button9);
            driversherramientas.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if(App.noInternet){
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                    }else{
                        openBrowser(arg0, "drivers");
                    }
                }

            });
            herramientasROM = (Button) findViewById(R.id.button10);
            herramientasROM.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Intent intent = new Intent(getApplicationContext(), SecondScreen.class);
                        intent.putExtra("modelo",modelo);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 110", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            config = (Button) findViewById(R.id.button5);

            config.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError) + " 121", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            t3=(TextView)findViewById(R.id.textView3);
            t3.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Intent intent = new Intent(getApplicationContext(), SendInformeActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 127", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 111", Toast.LENGTH_SHORT).show();
        }

    }

    private String asignaFecha() {
        String fecha_mod=null;
        Calendar cal=Calendar.getInstance();
        String day=String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month=String.valueOf((cal.get(Calendar.MONTH)+1));
        String year=String.valueOf(cal.get(Calendar.YEAR));
        if(day.length()<2){
            day="0"+day;
        }
        if(month.length()<2){
            month="0"+month;
        }
        fecha_mod=(day+"/"+month+"/"+year);
        return fecha_mod;
    }
    private int[] descomponerFecha(String fechaPasada) {
        int day=Integer.parseInt(fechaPasada.trim().split("/")[0]);
        int month=Integer.parseInt(fechaPasada.trim().split("/")[1])-1;
        int year=Integer.parseInt(fechaPasada.trim().split("/")[2]);
        int fecha[]=new int[3];
        fecha[0]=day;
        fecha[1]=month;
        fecha[2]=year;
        return fecha;
    }

    public void openBrowser(View v, String tipo) {
        try {
            Intent intent = new Intent(this, BrowserActivity.class);
            intent.putExtra("modelo", modelo);
            intent.putExtra("tipo", tipo);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 112", Toast.LENGTH_SHORT).show();
        }
    }

    /*public void openBrowserVideo(View v) {

        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/channel/UCL1i90sCYqJhehj45dM2Qhg/videos"));
            startActivity(myIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 113", Toast.LENGTH_SHORT).show();
        }
    }*/

    public static String getTotalRAM() throws Exception {
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            load = load.replaceAll(" ", "");
            int indexOf = load.indexOf(":");
            int indexOf2 = load.toLowerCase().indexOf("kb");
            load = load.substring(indexOf + 1, indexOf2);
            load = load.trim();
        } catch (IOException ex) {
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return load;
    }

    public static String getInfoCPU() throws Exception {
        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/proc/cpuinfo", "r");
            while (load.toLowerCase().indexOf("hardware") == -1) {
                load = reader.readLine();
            }

            load = load.replaceAll(" ", "");
            load = load.replaceAll("\t", "");
            load = load.toLowerCase();
            int indexOf = load.indexOf(":");
            int indexOf2 = load.length();
            load = load.substring(indexOf + 1, indexOf2);
        } catch (IOException ex) {

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return load.trim();
    }

    public static String getCPUFreqG4() throws Exception {
        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            load = reader.readLine();
            int cpufreq = Integer.parseInt(load.trim());
            if (cpufreq > 1400000) {
                load = "G4T";
            } else {
                load = "G4B";
            }
        } catch (IOException ex) {

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return load.trim();
    }

    public static String getCPUFreqG3() throws Exception {
        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            load = reader.readLine();
            int cpufreq = Integer.parseInt(load.trim());
            if (cpufreq > 1400000) {
                load = "G3QCT";
            } else {
                load = "G3QC";
            }
        } catch (IOException ex) {

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return load.trim();
    }

    @Override
    public void processFinish(String output) {
        try {
            if (output != null && !"TIMEOUT----".equals(output) && (!"firmaok".equals(output) && !"firmanok".equals(output))) {
                if(!updatemostrado){
                String inicio = output.split("-;-")[0];
                output = output.split("-;-")[1];
                String[] split = output.split("----");
                newversion = split[0].split(" ")[1];
                urlActualizacion = split[1];
                if (!"".equals(urlActualizacion) && !nversion.equals(newversion) && (Float.parseFloat(nversion.replaceAll("Jiayu.es ", "")) < Float.parseFloat(newversion.replaceAll("Jiayu.es ", "")))) {
                    updatemostrado=true;
                    Resources res = this.getResources();
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setMessage(res.getString(R.string.msgComprobarVersion) + " " + nversion + "->" + newversion + " " + res.getString(R.string.msgPreguntaVersion));
                    dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                            res.getString(R.string.cancelarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    App.updatemostrado=false;
                                }
                            });
                    dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                            res.getString(R.string.aceptarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    try {
                                        App.updatemostrado=false;
                                        ActualizarVersion();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 118", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    dialog.show();
                } else {
                    if ("".equals(inicio)) {
                        Resources res = this.getResources();
                        AlertDialog dialog = new AlertDialog.Builder(this).create();
                        dialog.setMessage(res.getString(R.string.msgLastVersion));
                        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                                res.getString(R.string.aceptarBtn),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int witch) {
                                    }
                                });
                        dialog.show();
                    }
                    if((split.length-2)>0){
                        String fecha=null;
                        String model=null;
                        boolean modeloEncontrado=false;
                        for (int x =2;x<split.length-1;x++){
                            model=split[x].split("->")[0];
                            fecha=split[x].split("->")[1];
                            if(modelo.equals(model)){
                                modeloEncontrado=true;
                                break;
                            }
                        }
                        if(modeloEncontrado){
                            String fechaAcceso=ajustes.getString("fechaUltimoAccesoDescargas",fecha);

                            int[] ints = descomponerFecha(fechaAcceso);
                            Calendar calAcceso=Calendar.getInstance();
                            calAcceso.set(Calendar.DAY_OF_MONTH,ints[0]);
                            calAcceso.set(Calendar.MONTH,ints[1]);
                            calAcceso.set(Calendar.YEAR,ints[2]);
                            calAcceso.set(Calendar.HOUR,0);
                            calAcceso.set(Calendar.MINUTE,0);
                            calAcceso.set(Calendar.SECOND,0);
                            calAcceso.set(Calendar.MILLISECOND,0);
                            int[] ints1 = descomponerFecha(fecha);
                            Calendar calModificacion=Calendar.getInstance();
                            calModificacion.set(Calendar.DAY_OF_MONTH,ints1[0]);
                            calModificacion.set(Calendar.MONTH,ints1[1]);
                            calModificacion.set(Calendar.YEAR,ints1[2]);
                            calModificacion.set(Calendar.HOUR,0);
                            calModificacion.set(Calendar.MINUTE,0);
                            calModificacion.set(Calendar.SECOND,0);
                            calModificacion.set(Calendar.MILLISECOND,0);
                            if(calModificacion.after(calAcceso)){
                                mNotificationManagerNews = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                final Notification notifyDetails = new Notification(R.drawable.ic_launcher,getApplicationContext().getResources().getString(R.string.ntfMinTxt),System.currentTimeMillis());
                                CharSequence contentTitle = getApplicationContext().getResources().getString(R.string.ntfTituloTxt);
                                CharSequence contentText = getApplicationContext().getResources().getString(R.string.ntfDetallesTxt);
                                Intent launch_intent = new Intent();
                                launch_intent.setComponent(new ComponentName("es.jiayu.jiayuid", "es.jiayu.jiayuid.BrowserActivity"));
                                launch_intent.putExtra("modelo", modelo);
                                launch_intent.putExtra("tipo", "downloads");
                                PendingIntent intent2;
                                intent2 = PendingIntent.getActivity(getApplicationContext(), 0,
                                        launch_intent, Intent.FLAG_ACTIVITY_NEW_TASK);

                                notifyDetails.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, intent2);
                                mNotificationManagerNews.notify(SIMPLE_NOTFICATION_NEWS, notifyDetails);
                            }

                        }
                    }
                }
            }
            }
            noInternet=false;
            WebView wv = (WebView) findViewById(R.id.webView);
            wv.loadUrl("http://www.jiayu.es/soporte/appbanner.php");
            wv.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            noInternet=true;
            WebView wv = (WebView) findViewById(R.id.webView);
            wv.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                comprobarVersion(nversion);
                return true;
            case R.id.action_about:
                try {
                    /*Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);*/
                    if(App.noInternet){
                        Intent intent = new Intent(this, AboutActivity.class);
                        startActivity(intent);
                    }else{
                        openBrowser(this.findViewById(R.id.action_about), "about");
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 120", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_config:
                try {
                    Intent intent = new Intent(this, ConfigActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 121", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_exit:
                finish();
            default:
                return super.onMenuItemSelected(featureId, item);

        }
    }
    public void comprobarMT() throws Exception{
        //Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgActivandoBTWifi), Toast.LENGTH_SHORT).show();
        String buildprop = "";
        FileInputStream fis = new FileInputStream(new File("/system/build.prop"));
        byte[] input = new byte[fis.available()];
        while (fis.read(input) != -1) {
            buildprop += new String(input);
        }
        if (buildprop.toLowerCase().lastIndexOf("mt6620") != -1) {
            chip = "MT6620";
        } else if (buildprop.toLowerCase().lastIndexOf("mt6628") != -1) {
            chip = "MT6628";
        } else {
            chip = "INDEFINIDO";
        }

        if("".equals(modelo)){
            boolean levantadoB = levantarBlueTooth();
            boolean levantadoW = LevantarWifi();
            if ("MT6628".equals(chip)) {
                if (!levantadoB && !levantadoW) {
                    chip = "MT6620";
                }

            } else if ("MT6620".equals(chip)) {
                if (!levantadoB && !levantadoW) {
                    chip = "MT6628";
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modelo="";
    }
    private void obtenerDatosPhone(){
        String modelo_apl=modelo;
        String imei_apl;
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei_apl=telephonyManager.getDeviceId();
        String email_apl="";
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                email_apl=email_apl+";"+possibleEmail;
            }
        }

        String telef_apl="";
        TelephonyManager tm = (TelephonyManager)this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        telef_apl =  tm.getLine1Number();

        String local_apl="";
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }
        /*if(providers.size()>0) {
            l = lm.getLastKnownLocation(providers.get(0));
        }*/
        if (l != null) {
            local_apl =local_apl+ l.getLatitude();
            local_apl =local_apl+ l.getLongitude();
        }

        String fecha_apl="";
        String fecha="";
        String dia=String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        String mes=String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
        String anyo=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if(dia.length()==1){
            dia="0"+dia;
        }
        if(mes.length()==1){
            mes="0"+mes;
        }
        ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
        editorAjustes=ajustes.edit();
        fecha_apl=ajustes.getString("fechaPrimerUso",dia+"/"+mes+"/"+anyo);
        editorAjustes.putString("fechaPrimerUso",ajustes.getString("fechaPrimerUso",dia+"/"+mes+"/"+anyo));
        editorAjustes.commit();
        String conecta_apl="";
        conecta_apl=String.valueOf(ajustes.getInt("aperturaAPP",0));
        String cadHttp="http://www.jiayu.es/soporte/appimei.php?";

        cadHttp=cadHttp+"mdl="+modelo_apl+"&imei="+imei_apl+"&email="+email_apl+"&tlf="+telef_apl+"&localiz="+local_apl+"&date="+fecha_apl+"&conecta="+conecta_apl+"*";
        //Toast.makeText(getBaseContext(),cadHttp,Toast.LENGTH_LONG).show();
    }
}
