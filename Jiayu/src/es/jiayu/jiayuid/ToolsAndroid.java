package es.jiayu.jiayuid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
            }else{
                bos.write(("setprop qemu.hw.mainkeys 0\n").getBytes());
            }
            bos.write(("busybox pkill zygote\n").getBytes());
            bos.flush();
            bos.close();
        }
        }catch(Exception e){

        }
    }
}