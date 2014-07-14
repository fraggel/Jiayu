package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Locale;

public class SelectNameBck extends Activity{
	SharedPreferences sp;
	AlertDialog diag;

	public void onCreate(Bundle savedInstanceState) {
		diag = new AlertDialog.Builder(this).create();
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_selectnamebck);
            modificarMargins();
		} catch (Exception e) {
		}

	}
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
        modificarMargins();

    }
	public void guardarNombre(View v){
		try {
			Intent it = new Intent();
			EditText texto=(EditText)findViewById(R.id.txtnombck);
			it.putExtra("nomBck", texto.getText().toString());
			setResult(Activity.RESULT_OK, it);
			finish();
		} catch (Exception e) {
		}
	}
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 86);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 350, 0, 176);
        }
        scText.setLayoutParams((llp));


        Button b1=(Button) findViewById(R.id.executeBtn);
        EditText b2=(EditText) findViewById(R.id.txtnombck);

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
