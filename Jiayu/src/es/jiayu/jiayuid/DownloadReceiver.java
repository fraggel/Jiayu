package es.jiayu.jiayuid;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        try {
            if(getClass().getPackage().getName().equals(intent.getPackage())){
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        Toast.makeText(context,context.getResources().getString(R.string.app_name)+" "+context.getResources().getString(R.string.terminadaDescarga),Toast.LENGTH_SHORT).show();
                        if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Jiayu.apk")!=null){
                            if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Jiayu.apk").exists()){
                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                intent2.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Jiayu.apk")), "application/vnd.android.package-archive");
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                                context.startActivity(intent2);
                            }
                        }
                    }
                    else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                        Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                        dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(dm);
                    }
            }
        }catch(Exception e){
            Toast.makeText(context, context.getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
        }
    }
}
