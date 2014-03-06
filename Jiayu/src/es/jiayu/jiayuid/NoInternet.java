package es.jiayu.jiayuid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by u028952 on 6/03/14.
 */
public class NoInternet extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nointernet);
    }

    public void volver(View view) {
        super.onBackPressed();
    }
}