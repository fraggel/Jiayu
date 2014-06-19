package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.Calendar;

public class Inicio extends Activity implements AsyncResponse{
    SharedPreferences ajustes=null;
    SharedPreferences.Editor editorAjustes=null;
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

    @Override
    public void processFinish(String output) {

    }
}
