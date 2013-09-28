package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SelectNameBck extends Activity{
	SharedPreferences sp;
	AlertDialog diag;

	public void onCreate(Bundle savedInstanceState) {
		diag = new AlertDialog.Builder(this).create();
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_selectnamebck);
			
		} catch (Exception e) {
		}

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
}
