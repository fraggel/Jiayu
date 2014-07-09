package es.jiayu.jiayuid;

import android.app.Activity;
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

/**
 * Created by Fraggel on 28/09/13.
 */
public class ToolsAndroid extends Activity implements View.OnClickListener {
    Button softkeysBtn=null;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolsandroid);
        softkeysBtn=(Button) findViewById(R.id.softkeysBtn);
        softkeysBtn.setOnClickListener(this);
        TextView scText=(TextView) findViewById(R.id.herramientasUsuTxt);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi==240) {
            llp.setMargins(40, 175, 0, 86);
        }else if(dpi==320) {
            llp.setMargins(50, 230, 0, 130);
        }else if(dpi==480) {
            llp.setMargins(80, 360, 0, 176);
        }
        scText.setLayoutParams((llp));
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
}