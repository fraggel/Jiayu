package es.jiayu.jiayuid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;

import static es.jiayu.jiayuid.Utilidades.comprobarRecovery;

/**
 * Created by Fraggel on 26/06/2014.
 */
public class SCHerramientasRom extends Activity implements View.OnClickListener {
    String modelo;
    SharedPreferences ajustes;
    Button recoveryBtn = null;
    Button romBtn = null;
    Button rebootRecoveryBtn = null;
    ImageButton imageButton = null;
    boolean isRoot = false;
    boolean detectRecovery=false;
    String recoveryDetectado="ori";

    protected void onResume() {
        super.onResume();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device);
        Intent intent = getIntent();
        modelo = intent.getExtras().getString("modelo");
        ajustes = getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        /*imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.jiayu.es");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });*/
        TextView scText=(TextView) findViewById(R.id.herramientasROMTxt);
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
        if (Utilidades.controlRootSinExec(getApplicationContext(),getResources(),"RomTools")) {
            isRoot = true;
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.msgOptDisabled),Toast.LENGTH_LONG).show();
        }
        recoveryBtn = (Button) findViewById(R.id.recoveryBtn);

        romBtn = (Button) findViewById(R.id.romBtn);


        String externalStorageState = Environment.getExternalStorageState();
        if(!"mounted".equals(externalStorageState.toLowerCase())){
            recoveryBtn.setEnabled(false);
            recoveryBtn.setTextColor(Color.parseColor("#BDBDBD"));
            romBtn.setEnabled(false);
            romBtn.setTextColor(Color.parseColor("#BDBDBD"));
        }
        rebootRecoveryBtn = (Button) findViewById(R.id.rebootRecoveryBtn);

        if (!isRoot) {
            recoveryBtn.setEnabled(false);
            recoveryBtn.setTextColor(Color.parseColor("#BDBDBD"));
            //recoveryBtn.setVisibility(View.INVISIBLE);
            rebootRecoveryBtn.setEnabled(false);
            rebootRecoveryBtn.setTextColor(Color.parseColor("#BDBDBD"));
            //rebootRecoveryBtn.setVisibility(View.INVISIBLE);

            //imeiBtn.setVisibility(View.INVISIBLE);
            //bootAnimationBtn.setVisibility(View.INVISIBLE);
            //backupBtn.setVisibility(View.INVISIBLE);
            //toolsAndroidBtn.setVisibility(View.INVISIBLE);
        }



        recoveryBtn.setOnClickListener(this);
        romBtn.setOnClickListener(this);




        rebootRecoveryBtn.setOnClickListener(this);


        //bootAnimationBtn.setEnabled(false);
        if(isRoot){
            if("mounted".equals(externalStorageState.toLowerCase())) {
                if (ajustes.getBoolean("recoveryChk", false)) {
                    detectRecovery = true;
                    recoveryDetectado = comprobarRecovery(getApplicationContext(), getResources(), "RomTools");

                } else {
                    detectRecovery = false;
                }
                if (detectRecovery) {
                    if ("cwm".equals(recoveryDetectado)) {

                        //chkCWM.setVisibility(View.INVISIBLE);
                    } else if ("ori".equals(recoveryDetectado)) {

                        //chkCWM.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        if (button.getId() == R.id.recoveryBtn) {
            try {
                Intent intent = new Intent(this, RecoveryScreen.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 162", Toast.LENGTH_SHORT).show();
            }
        }else if (button.getId()==R.id.rebootRecoveryBtn){
            {
                try {
                    rebootRecoveryQuestion();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 163", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (button.getId() == R.id.romBtn) {
            try {
                Intent intent = new Intent(this, RomScreen.class);
                intent.putExtra("modelo",modelo);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 165", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void rebootRecoveryQuestion() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage(getResources().getString(R.string.msgRebootRecoveryQ));

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.cancelarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {

                    }
                });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.aceptarBtn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int witch) {
                        try {
                            Runtime rt = Runtime.getRuntime();
                            java.lang.Process p = rt.exec("su");
                            BufferedOutputStream bos = new BufferedOutputStream(
                                    p.getOutputStream());
                            bos.write(("reboot recovery\n").getBytes());
                            bos.flush();
                            bos.close();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 168", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();
    }
}