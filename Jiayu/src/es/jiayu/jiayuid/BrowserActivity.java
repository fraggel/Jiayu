package es.jiayu.jiayuid;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Button;

public class BrowserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		Intent intent = getIntent();
		String modelo = intent.getExtras().getString("modelo");
		WebView descargas = (WebView) findViewById(R.id.webView1);
		descargas.loadUrl("http://www.jiayu.es/soporte/appsoft.php?jiayu="+modelo);
	}
}
