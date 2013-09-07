package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class BrowserActivity extends Activity {
    Resources res;
    PackageManager pm = null;
    String urlDestino = "";
    static NotificationManager mNotificationManagerUpdate=null;
    static NotificationManager mNotificationManagerNews=null;
    private int SIMPLE_NOTFICATION_UPDATE=8888;
    private int SIMPLE_NOTFICATION_NEWS=8889;
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        res = this.getResources();
        pm = this.getPackageManager();
        isDownloadManagerAvailable(getBaseContext());
        Intent intent = getIntent();
        String modelo = intent.getExtras().getString("modelo");
        String tipo = intent.getExtras().getString("tipo");
        WebView descargas = (WebView) findViewById(R.id.webView1);
        descargas.setWebViewClient(new JiayuWebViewClient());
        descargas.setDownloadListener(new JiayuDownloadListener());

        if ("drivers".equals(tipo)) {
            descargas.loadUrl("http://www.jiayu.es/soporte/apptools.php");
        } else if ("bootanimation".equals(tipo)) {
            descargas.loadUrl("http://www.jiayu.es/soporte/bootanimation.php");
        }else if ("downloads".equals(tipo)) {
            mNotificationManagerNews = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManagerNews.cancel(SIMPLE_NOTFICATION_NEWS);
            ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
            editorAjustes = ajustes.edit();
            editorAjustes.putString("modelo", modelo);
            editorAjustes.putString("fechaUltimoAccesoDescargas", asignaFecha());
            editorAjustes.commit();
            descargas.loadUrl("http://www.jiayu.es/soporte/appsoft.php?jiayu=" + modelo);
        }

    }

    class JiayuDownloadListener implements DownloadListener {

        public void onDownloadStart(String s, String s2, String s3, String s4, long l) {

        }

    }

    class JiayuWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            urlDestino = url;
            if (urlDestino.lastIndexOf("/desarrollo/") != -1) {
                try {
                    String nombreFichero = "";
                    nombreFichero = urlDestino.split("/")[urlDestino.split("/").length - 1];
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDestino));
                    request.setDescription(nombreFichero);
                    request.setTitle(nombreFichero);
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
                    if (Build.VERSION.SDK_INT >= 11) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        if (".apk".equals(nombreFichero.substring(nombreFichero.length() - 4, nombreFichero.length()).toLowerCase())) {
                            request.setMimeType("application/vnd.android.package-archive");
                            if (nombreFichero.indexOf("Jiayu.apk") == -1) {
                                try {
                                    new File(f1.getAbsolutePath() + "/Jiayu.apk").delete();
                                } catch (Exception e) {

                                }

                            }
                        }

                    }
                    String rutaDescarga = null;
                    if (nombreFichero.indexOf("recovery") != -1) {
                        rutaDescarga = "/JIAYUES/RECOVERY/";
                    } else if (nombreFichero.indexOf(".apk") != -1) {
                        rutaDescarga = "/JIAYUES/APP/";
                    } else if (nombreFichero.indexOf("signed_") != -1) {
                        rutaDescarga = "/JIAYUES/ROMS/";
                    } else if (nombreFichero.indexOf("bootanimation") != -1) {
                        rutaDescarga = "/JIAYUES/BOOTANIMATION/";
                    }else{
                        rutaDescarga = "/JIAYUES/DOWNLOADS/";
                    }
                    request.setDestinationInExternalPublicDir(rutaDescarga, nombreFichero);

                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgIniciandoDescarga) + " " + nombreFichero, Toast.LENGTH_SHORT).show();
                    App.listaDescargas.put(String.valueOf(manager.enqueue(request)), nombreFichero);
                    if (nombreFichero.indexOf("bootanimation") != -1) {
                        request.setDestinationInExternalPublicDir(rutaDescarga, nombreFichero.substring(0,nombreFichero.length()-4)+".gif");
                        App.listaDescargas.put(String.valueOf(manager.enqueue(request)), nombreFichero.substring(0,nombreFichero.length()-4)+".gif");
                    }
                    //manager.enqueue(request);

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgGenericError), Toast.LENGTH_SHORT).show();
                }
                return true;
            } else {
                Uri uri = Uri.parse(urlDestino);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }
    }

    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
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
}
