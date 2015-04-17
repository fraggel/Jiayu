package es.jiayu.jiayuid;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by Fraggel on 9/08/13.
 */

public class UpdateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND,2);
        Intent intent2 = new Intent(context, NotifyService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent2,
                0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),60000, pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),21600000, pintent);
        context.startService(new Intent(context,NotifyService.class));

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.SECOND,2);
        Intent intent3 = new Intent(context, NotifyNewsService.class);
        PendingIntent pintent2 = PendingIntent.getService(context, 0, intent3,
                0);
        AlarmManager alarm2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //alarm2.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(),60000, pintent2);
        alarm2.setRepeating(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(),21600000, pintent2);
        context.startService(new Intent(context,NotifyNewsService.class));
        try {
            FileOutputStream fos=new FileOutputStream("/sdcard/JIAYUES/notification.log",true);
            fos.write(("UpdateReceiver1"+Calendar.getInstance().get(Calendar.HOUR)+":"+Calendar.getInstance().get(Calendar.MINUTE)+":"+Calendar.getInstance().get(Calendar.SECOND)).getBytes());
            fos.flush();
            fos.close();
        }catch(Exception e){

        }

    }
}
