package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BrowserActivity extends Activity {
    Resources res;
    PackageManager pm = null;
    String urlDestino = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        res = this.getResources();
        pm = this.getPackageManager();

        try {
            Intent it = pm.getLaunchIntentForPackage("org.mozilla.firefox");
            if (it == null) {
                instalarFirefox();
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
        }

        Intent intent = getIntent();
        String modelo = intent.getExtras().getString("modelo");
        WebView descargas = (WebView) findViewById(R.id.webView1);
        descargas.setWebViewClient(new MyWebViewClient());

        descargas.loadUrl("http://www.jiayu.es/soporte/appsoft.php?jiayu=" + modelo);

    }

    private void instalarFirefox() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(res.getString(R.string.msgNoFirefox));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                res.getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        finish();
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                res.getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri
                                    .parse("market://details?id=org.mozilla.firefox"));
                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();
    }

    class MyWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            urlDestino = url;
            if (urlDestino.lastIndexOf("www.jiayu.es") == -1) {
                AlertDialog dialog = new AlertDialog.Builder(view.getContext()).create();
                dialog.setMessage(res.getString(R.string.msgAyudaFirefox));
                dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        res.getString(R.string.aceptar),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int witch) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlDestino));
                                    intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
                return true;
            } else {
                Uri uri = Uri.parse(urlDestino);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }
    }
}
