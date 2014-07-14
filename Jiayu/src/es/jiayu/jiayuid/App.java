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
import android.webkit.WebViewClient;
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
    String urlActualizacion = "";

    ImageButton imageButton;
    Button descargas;
    Button foro;
    Button about;
    Button videotutoriales;
    Button driversherramientas;
    Button herramientasROM;
    Button shop;
    Button config;
    TextView t3;

    String modelo;
    String version;
    String fabricante;
    String nversion;
    String compilacion;

    //Button envioNoExisteBtn;
    Button btnInfo;
    static boolean noInternet=true;

    String newversion = "";
    public static boolean updatemostrado=false;
    String listaIdiomas[]=null;
    static NotificationManager mNotificationManagerUpdate=null;
    static NotificationManager mNotificationManagerNews=null;
    private int SIMPLE_NOTFICATION_UPDATE=8888;
    private int SIMPLE_NOTFICATION_NEWS=8889;
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
    public static boolean isRoot=false;
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
        modificarMargins();
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");
        version = intent.getExtras().getString("version");
        fabricante = intent.getExtras().getString("fabricante");
        nversion =  intent.getExtras().getString("nversion");
        compilacion =  intent.getExtras().getString("compilacion");
        isRoot =intent.getExtras().getBoolean("root");
    }

    protected void onCreate(Bundle savedInstanceState) {
            try {
                //Intent intent = getIntent();
                //String ini = intent.getExtras().getString("ini");

                super.onCreate(savedInstanceState);

                Resources res = this.getResources();

                setContentView(R.layout.activity_app);
                modificarMargins();
                Intent intent = getIntent();
                modelo = intent.getExtras().getString("modelo","");
                version = intent.getExtras().getString("version","");
                fabricante = intent.getExtras().getString("fabricante","");
                nversion = intent.getExtras().getString("nversion","");
                compilacion = intent.getExtras().getString("compilacion","");
                isRoot =intent.getExtras().getBoolean("root");
                noInternet = intent.getExtras().getBoolean("noInternet",true);
                noInternet=comprobarConexion();
                if (noInternet){
                    WebView wv = (WebView) findViewById(R.id.webView);
                    wv.setVisibility(View.INVISIBLE);
                }else{
                    WebView wv = (WebView) findViewById(R.id.webView);
                    wv.setVisibility(View.VISIBLE);
                    wv.loadUrl("http://www.jiayu.es/soporte/appbanner.php");
                }

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




                        descargas = (Button) findViewById(R.id.button1);
                        about = (Button) findViewById(R.id.button2);
                        config = (Button) findViewById(R.id.button5);
                        videotutoriales = (Button) findViewById(R.id.button3);
                        foro = (Button) findViewById(R.id.button4);
                        driversherramientas = (Button) findViewById(R.id.button9);
                        herramientasROM = (Button) findViewById(R.id.button10);
                        shop = (Button) findViewById(R.id.button11);

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

                        tmodelo = (TextView) findViewById(R.id.textView1);

                        addListenerOnButton();
                        //if("ini".equals(ini)){
                            /*if ("".equals(modelo)) {
                                calcularTelefono();
                                modelo = model;
                            } else {*/
                                //recalcularTelefono();
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

                           /* }*/
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
                TextView imgg=(TextView)findViewById(R.id.textView3);
                imgg.requestFocus();
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 101", Toast.LENGTH_SHORT).show();
            }
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

    /*private void recalcularTelefono() {
        calcularTelefono();

        modelo = model;

    }*/





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
            shop = (Button) findViewById(R.id.button11);
            shop.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if(App.noInternet){
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                    }else{
                        openBrowser(arg0,"shop");
                    }
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
                        intent.putExtra("root",isRoot);
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





    @Override


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

                                int[] ints = Utilidades.descomponerFecha(fechaAcceso);
                                Calendar calAcceso=Calendar.getInstance();
                                calAcceso.set(Calendar.DAY_OF_MONTH,ints[0]);
                                calAcceso.set(Calendar.MONTH,ints[1]);
                                calAcceso.set(Calendar.YEAR,ints[2]);
                                calAcceso.set(Calendar.HOUR,0);
                                calAcceso.set(Calendar.MINUTE,0);
                                calAcceso.set(Calendar.SECOND,0);
                                calAcceso.set(Calendar.MILLISECOND,0);
                                int[] ints1 = Utilidades.descomponerFecha(fecha);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void modificarMargins() {
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
        TextView t1=(TextView) findViewById(R.id.textView1);
        Button b1=(Button) findViewById(R.id.button1);
        Button b2=(Button) findViewById(R.id.button9);
        Button b3=(Button) findViewById(R.id.button10);
        Button b4=(Button) findViewById(R.id.button11);
        Button b5=(Button) findViewById(R.id.button4);
        Button b6=(Button) findViewById(R.id.button3);
        Button b7=(Button) findViewById(R.id.button2);
        Button b8=(Button) findViewById(R.id.button5);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                t1.setPadding(300, 0, 0, 0);
                b1.setPadding(240, 0, 0, 0);
                b2.setPadding(240, 0, 0, 0);
                b3.setPadding(240, 0, 0, 0);
                b4.setPadding(180, 0, 0, 0);
                b5.setPadding(150, 0, 0, 0);
                b6.setPadding(90, 0, 0, 0);
                b7.setPadding(160, 0, 0, 0);
                b8.setPadding(130, 0, 0, 0);
            }else{
                t1.setPadding(120, 0, 0, 0);
                b1.setPadding(120, 0, 0, 0);
                b2.setPadding(120, 0, 0, 0);
                b3.setPadding(120, 0, 0, 0);
                b4.setPadding(90, 0, 0, 0);
                b5.setPadding(80, 0, 0, 0);
                b6.setPadding(90, 0, 0, 0);
                b7.setPadding(100, 0, 0, 0);
                b8.setPadding(80, 0, 0, 0);
            }
        }else if(dpi==320) {
            if(orientation==2) {
                t1.setPadding(500, 0, 0, 0);
                b1.setPadding(270, 0, 0, 0);
                b2.setPadding(270, 0, 0, 0);
                b3.setPadding(270, 0, 0, 0);
                b4.setPadding(300, 0, 0, 0);
                b5.setPadding(250, 0, 0, 0);
                b6.setPadding(115, 0, 0, 0);
                b7.setPadding(250, 0, 0, 0);
                b8.setPadding(205, 0, 0, 0);
            }else{
                t1.setPadding(200, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);
                b2.setPadding(200, 0, 0, 0);
                b3.setPadding(200, 0, 0, 0);
                b4.setPadding(135, 0, 0, 0);
                b5.setPadding(120, 0, 0, 0);
                b6.setPadding(115, 0, 0, 0);
                b7.setPadding(140, 0, 0, 0);
                b8.setPadding(125, 0, 0, 0);
            }
        }else if(dpi==480) {
            if(orientation==2) {
                t1.setPadding(550, 0, 0, 0);
                b1.setPadding(550, 0, 0, 0);
                b2.setPadding(550, 0, 0, 0);
                b3.setPadding(550, 0, 0, 0);
                b4.setPadding(455, 0, 0, 0);
                b5.setPadding(360, 0, 0, 0);
                b6.setPadding(170, 0, 0, 0);
                b7.setPadding(390, 0, 0, 0);
                b8.setPadding(290, 0, 0, 0);
            }else{
                t1.setPadding(290, 0, 0, 0);
                b1.setPadding(290, 0, 0, 0);
                b2.setPadding(290, 0, 0, 0);
                b3.setPadding(290, 0, 0, 0);
                b4.setPadding(200, 0, 0, 0);
                b5.setPadding(190, 0, 0, 0);
                b6.setPadding(170, 0, 0, 0);
                b7.setPadding(200, 0, 0, 0);
                b8.setPadding(175, 0, 0, 0);
            }
        }

    }
    /*@Override
    public void onBackPressed() {
        super.finish();
    }*/
}
