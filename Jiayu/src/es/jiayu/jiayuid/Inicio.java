package es.jiayu.jiayuid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
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
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import static es.jiayu.jiayuid.Utilidades.controlRootConExec;

public class Inicio extends Activity implements AsyncResponse{
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
    String nversion = "";
    String version = "";
    String modelo = "";
    String model = "";
    static long downloadREF = -1;
    String fabricante = "";
    String compilacion = "";
    String modelBuild="";
    String newversion = "";
    public static boolean updatemostrado=false;
    String chip = "";
    String urlActualizacion = "";
    static NotificationManager mNotificationManagerUpdate=null;
    static NotificationManager mNotificationManagerNews=null;
    private int SIMPLE_NOTFICATION_UPDATE=8888;
    private int SIMPLE_NOTFICATION_NEWS=8889;
    public boolean isRoot=false;
    public static boolean noInternet=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        descargarFirmas();

        ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
        editorAjustes=ajustes.edit();
        editorAjustes.putInt("aperturaAPP", (ajustes.getInt("aperturaAPP", 0))+1);
        editorAjustes.commit();
        String fecha="";
        String dia=String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        String mes=String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        String anyo=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if(dia.length()==1){
            dia="0"+dia;
        }
        if(mes.length()==1){
            mes="0"+mes;
        }

        editorAjustes.putString("fechaPrimerUso",ajustes.getString("fechaPrimerUso",dia+"/"+mes+"/"+anyo));
        noInternet=comprobarConexion();

        comprobarVersionInicio(version);

        calcularMod();
        modelo=model;
        if (controlRootConExec(getApplicationContext(),getResources(),"Inicio")) {
            isRoot = true;
        }else{
            isRoot=false;
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgOptDisabled),Toast.LENGTH_LONG).show();
        }
        /*Intent intent = new Intent(getApplicationContext(), App.class);
        intent.putExtra("modelo",modelo);
        intent.putExtra("version",version);
        intent.putExtra("fabricante",fabricante);
        intent.putExtra("nversion",nversion);
        intent.putExtra("compilacion",compilacion);
        startActivity(intent);*/
        Calendar calc = Calendar.getInstance();
        calc.add(Calendar.SECOND,2);
        Intent intent2 = new Intent(getApplicationContext(), NotifyService.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent2,
                0);
        AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, calc.getTimeInMillis(),60000, pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calc.getTimeInMillis(),21600000, pintent);

        Calendar calc2 = Calendar.getInstance();
        calc2.add(Calendar.SECOND,2);
        Intent intent3 = new Intent(getApplicationContext(), NotifyNewsService.class);
        PendingIntent pintent2 = PendingIntent.getService(getApplicationContext(), 0, intent3,
                0);
        AlarmManager alarm2 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        //alarm2.setRepeating(AlarmManager.RTC_WAKEUP, calc2.getTimeInMillis(),60000, pintent2);
        alarm2.setRepeating(AlarmManager.RTC_WAKEUP, calc2.getTimeInMillis(),21600000, pintent2);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
                Intent i3 = new Intent(getApplicationContext(), App.class);
                i3.putExtra("ini","ini");
                i3.putExtra("modelo",modelo);
                i3.putExtra("version",version);
                i3.putExtra("fabricante",fabricante);
                i3.putExtra("nversion",nversion);
                i3.putExtra("compilacion",compilacion);
                i3.putExtra("nointernet",noInternet);
                i3.putExtra("root",isRoot);
                startActivity(i3);
            }
        }, 500);
    }


    private void descargarFirmas(){
        try {
            MD5Thread asyncTask = new MD5Thread();
            asyncTask.delegate = this;
            asyncTask.execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 103", Toast.LENGTH_SHORT).show();
        }
    }
    private void calcularMod() {
        try {
            compilacion = Build.DISPLAY;
            modelBuild=Build.MODEL;
            fabricante = infoBrand();
            nversion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version = "Jiayu.es ";
            version = version + nversion;

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
            calcularTelefono();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 199", Toast.LENGTH_SHORT).show();
        }


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
            obtenerDatosPhone();
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
    private void comprobarVersionInicio(String version2) {
        try {
            VersionThread asyncTask = new VersionThread();
            asyncTask.delegate = this;
            asyncTask.execute(version2, "inicio");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 103", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            noInternet=true;
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
}
