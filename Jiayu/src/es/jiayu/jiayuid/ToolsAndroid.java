package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Fraggel on 28/09/13.
 */
public class ToolsAndroid extends Activity implements View.OnClickListener {
    Button softkeysBtn=null;
    protected void onResume() {
        super.onResume();
        String listaIdiomas[]=getResources().getStringArray(R.array.languages_values);
        SharedPreferences ajustes=getSharedPreferences("JiayuesAjustes",Context.MODE_PRIVATE);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolsandroid);
        softkeysBtn=(Button) findViewById(R.id.softkeysBtn);
        softkeysBtn.setOnClickListener(this);
        modificarMargins();
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        try {


        if (button.getId() == R.id.softkeysBtn) {
            boolean mostrandoSoftK=false;
            Runtime rt = Runtime.getRuntime();
            Process pp = rt.exec("su");
            BufferedOutputStream bos = new BufferedOutputStream(
                    pp.getOutputStream());

                Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "/system/bin/sh"});
                DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
                stdin.writeBytes("getprop qemu.hw.mainkeys\n");
                DataInputStream stdout = new DataInputStream(p.getInputStream());
                byte[] buffer = new byte[4096];
                int read = 0;
                String out = new String();
                while(true){
                    read = stdout.read(buffer);
                    out += new String(buffer, 0, read);
                    if(read<4096){
                        break;
                    }
                }

            if(out.indexOf("0")!=-1){
                bos.write(("setprop qemu.hw.mainkeys 1\n").getBytes());
				cambiarFileGeneric(true);
            }else{
                bos.write(("setprop qemu.hw.mainkeys 0\n").getBytes());
				cambiarFileGeneric(false);
            }
            bos.write(("busybox pkill zygote\n").getBytes());
            bos.write(("exit").getBytes());
            bos.flush();
            bos.close();
            p.waitFor();
        }
        }catch(Exception e){

        }
    }
	private void cambiarFileGeneric(boolean b) {
        try {
            if(b){
                Runtime rt2 = Runtime.getRuntime();
                Process pp2 = rt2.exec("su");
                BufferedOutputStream bos2 = new BufferedOutputStream(
                        pp2.getOutputStream());
                bos2.write("busybox sed -i 's/key 102 .*/#key 102 MOVE_HOME/g' /system/usr/keylayout/Generic.kl".getBytes());
                bos2.write("busybox sed -i 's/key 139 .*/#key 139 MENU WAKE_DROPPED/g' /system/usr/keylayout/Generic.kl".getBytes());
                bos2.write("busybox sed -i 's/key 158 .*/#key 158 MENU WAKE_DROPPED/g' /system/usr/keylayout/Generic.kl".getBytes());
                bos2.flush();
                bos2.close();

            }else{
                Runtime rt3 = Runtime.getRuntime();
                Process pp3 = rt3.exec("su");
                BufferedOutputStream bos3 = new BufferedOutputStream(
                        pp3.getOutputStream());
                bos3.write("busybox sed -i 's/#key 102 .*/key 102 MOVE_HOME/g' /system/usr/keylayout/Generic.kl".getBytes());
                bos3.write("busybox sed -i 's/#key 139 .*/key 139 MENU WAKE_DROPPED/g' /system/usr/keylayout/Generic.kl".getBytes());
                bos3.write("busybox sed -i 's/#key 158 .*/key 158 MENU WAKE_DROPPED/g' /system/usr/keylayout/Generic.kl".getBytes());
                bos3.flush();
                bos3.close();

            }
        }catch(Exception e){

        }
    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 94);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 350, 0, 190);
        }
        scText.setLayoutParams((llp));


        Button b1=(Button) findViewById(R.id.softkeysBtn);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi==240) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);

            }
        }else if(dpi==320) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);

            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(200, 0, 0, 0);

            }
        }else if(dpi==480) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);

            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);

            }
        }

    }
}