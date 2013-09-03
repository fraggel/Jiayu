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

public class BootAnimation extends Activity {
    Resources res;
    String urlDestino = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootanimation);
        res = this.getResources();
        isDownloadManagerAvailable(getBaseContext());
        Intent intent = getIntent();
        String modelo = intent.getExtras().getString("modelo");
        String tipo = intent.getExtras().getString("tipo");
        WebView descargas = (WebView) findViewById(R.id.webView1);
        descargas.setWebViewClient(new JiayuWebViewClient());
        descargas.setDownloadListener(new JiayuDownloadListener());

        if ("bootanimation".equals(tipo)) {
            descargas.loadUrl("http://www.jiayu.es/soporte/bootanimations.php");
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

                    String rutaDescarga = null;
                    if (nombreFichero.indexOf("bootanimation") != -1) {
                        rutaDescarga = "/JIAYUES/BOOTANIMATION/";
                    }
                    request.setDestinationInExternalPublicDir(rutaDescarga, nombreFichero);

                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.msgIniciandoDescarga) + " " + nombreFichero, Toast.LENGTH_SHORT).show();
                    App.listaDescargas.put(String.valueOf(manager.enqueue(request)), nombreFichero);
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
}
