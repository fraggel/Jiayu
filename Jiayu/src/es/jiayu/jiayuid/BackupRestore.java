package es.jiayu.jiayuid;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static es.jiayu.jiayuid.Utilidades.comprobarRecovery;
import static es.jiayu.jiayuid.Utilidades.controlRoot;

public class BackupRestore extends Activity implements OnItemSelectedListener,
        AdapterView.OnItemClickListener,DialogInterface.OnClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
	AlertDialog diag;
	Resources res;
	String nomBackup="";
	String[] types;
    String modelo="";
    RadioButton backupRdb=null;
    RadioButton restoreRdb=null;
    Button ejecutarBtn=null;
    boolean isRoot = false;
    String cadena="";
    File fff=null;
    CheckBox chkCWM = null;
    boolean detectRecovery=false;
    String recoveryDetectado="ori";
    SharedPreferences ajustes=null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		res = this.getResources();
		setContentView(R.layout.activity_backuprestore);
		diag = new Builder(this).create();
        Intent intent=getIntent();
        modelo = intent.getExtras().getString("modelo");
        ajustes=getSharedPreferences("JiayuesAjustes", Context.MODE_PRIVATE);
        backupRdb= (RadioButton) findViewById(R.id.rdbbackup);
        restoreRdb= (RadioButton) findViewById(R.id.rdbrestore);
        ejecutarBtn= (Button) findViewById(R.id.executeBtn);
        chkCWM = (CheckBox) findViewById(R.id.cwmChk);
        chkCWM.setOnCheckedChangeListener(this);
        if (controlRoot(getApplicationContext(),getResources(),"Backup/Restore")) {
            isRoot = true;
            if(isRoot){
                if(ajustes.getBoolean("recoveryChk",false)){
                    detectRecovery=true;
                    recoveryDetectado=comprobarRecovery(getApplicationContext(),getResources(),"Backup/Restore");
                }else{
                    detectRecovery=false;
                    chkCWM.setEnabled(true);
                    chkCWM.setVisibility(View.VISIBLE);
                }
                if(detectRecovery){
                    if("cwm".equals(recoveryDetectado)){
                        chkCWM.setChecked(true);
                        chkCWM.setEnabled(false);
                        chkCWM.setTextColor(Color.BLUE);
                        chkCWM.setText(getResources().getString(R.string.msgRecoveryDetectado)+" CWM RECOVERY");
                        //chkCWM.setVisibility(View.INVISIBLE);
                    }else if("ori".equals(recoveryDetectado)){
                        chkCWM.setChecked(false);
                        chkCWM.setEnabled(false);
                        chkCWM.setTextColor(Color.RED);
                        chkCWM.setText(getResources().getString(R.string.msgRecoveryDetectado)+" ORIGINAL RECOVERY");
                        //chkCWM.setVisibility(View.INVISIBLE);
                    }
                }
            }
            if (chkCWM.isChecked()) {
                backupRdb.setVisibility(View.VISIBLE);
                restoreRdb.setVisibility(View.VISIBLE);
                ejecutarBtn.setVisibility(View.VISIBLE);
            }else{
                backupRdb.setVisibility(View.INVISIBLE);
                restoreRdb.setVisibility(View.INVISIBLE);
                ejecutarBtn.setVisibility(View.INVISIBLE);
            }
        }
	}

	public void salir(View v) {
		finish();

	}

	public void makeBackupRestore(View v) {
		String cad="";
		try {
			if(backupRdb.isChecked()){
				Intent intent =new Intent(this,SelectNameBck.class);
				startActivityForResult(intent, 1);
			}else if(restoreRdb.isChecked()){
				Builder b = new Builder(this);
				File fff=new File(Environment.getExternalStorageDirectory().getPath()+"/clockworkmod/backup/");
			    b.setTitle(res.getString(R.string.restoreRdb));
			    types =fff.list(); 
			    b.setItems(types,this);
			    b.show();
			}	
		}catch(Exception e){
            e.printStackTrace();
		}
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		nomBackup=types[which];
		if(nomBackup!=null && !"".equals(nomBackup)){
			restore(res, diag, this, nomBackup);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	protected void onActivityResult(int request, int result, Intent data) {
		try {
			switch (request) {
			case 1:
				if (result == RESULT_OK) {
					nomBackup=data.getStringExtra("nomBck");
					backup(res, diag, this, nomBackup);
				}else{
					nomBackup="";
				}
			}
		}catch(Exception e){
		}
	}
    public String backup(Resources res,AlertDialog diag,DialogInterface.OnClickListener onClickListener,String nombreBck) {
        cadena="";
        BufferedOutputStream bos=null;
        try{
            Runtime rt=Runtime.getRuntime();
            Process exec = rt.exec("su");

            bos= new BufferedOutputStream(exec.getOutputStream());

            bos.write(("rm /cache/recovery/extendedcommand\n")
                    .getBytes());
            String fabricante= Build.BRAND;
            String procesador=Build.HARDWARE;

            fff=new File(Environment.getExternalStorageDirectory()+"/clockworkmod/backup/"+nombreBck+"/");
            if(fff.exists()){
                File[] listFiles = fff.listFiles();
                if(listFiles!=null && listFiles.length>0){
                    diag.setMessage(res.getString(R.string.msgExisteBackup));
                    diag.setButton(AlertDialog.BUTTON_NEGATIVE,
                            res.getString(R.string.cancelarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    finish();
                                }
                            });
                    diag.setButton(AlertDialog.BUTTON_POSITIVE,
                            res.getString(R.string.aceptarBtn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int witch) {
                                    try {
                                        File[] listFiles = fff.listFiles();
                                        for (int x=0;x<listFiles.length;x++){
                                            listFiles[x].delete();
                                        }
                                        fff.delete();
                                        cadena=prepPartitionsJIAYU(cadena);
                                        cadena=("echo 'backup_rom(\""+ buscarCWMySustituirRutas(fff.getPath())+"\");' >> /cache/recovery/extendedcommand\n");
                                        backup();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                    diag.show();
                }else{
                    cadena=prepPartitionsJIAYU(cadena);
                    cadena=("echo 'backup_rom(\""+ buscarCWMySustituirRutas(fff.getPath())+"\");' >> /cache/recovery/extendedcommand\n");
                }
            }else{
                //fff.mkdirs();
                cadena=prepPartitionsJIAYU(cadena);
                cadena=("echo 'backup_rom(\""+ buscarCWMySustituirRutas(fff.getPath())+"\");' >> /cache/recovery/extendedcommand\n");
            }
            bos.flush();
            bos.close();
            backup();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(bos!=null){
                    bos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return cadena;
        //Comprobar si existe ya backup y avisar, dar opcin a borrar
    }

    private void backup() {
        if(cadena!=null && !"".equals(cadena)){
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(getResources().getString(R.string.msgRebootBackupQF));
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                    getResources().getString(R.string.cancelarBtn),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int witch) {
                            finish();
                        }
                    });
            dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                    getResources().getString(R.string.aceptarBtn),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int witch) {
                            try {
                                Runtime rt=Runtime.getRuntime();
                                Process exec = rt.exec("su");
                                BufferedOutputStream bos=null;
                                bos= new BufferedOutputStream(exec.getOutputStream());
                                bos.write(cadena.getBytes());
                                bos.write(("reboot recovery").getBytes());
                                bos.flush();
                                bos.close();
                                //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 150", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            dialog.show();
        }
    }

    public String restore(Resources res,AlertDialog diag,DialogInterface.OnClickListener onClickListener,String nombreBck) {
        BufferedOutputStream bos=null;
        try{
            cadena="";
            Runtime rt=Runtime.getRuntime();
            Process exec = rt.exec("su");
            bos= new BufferedOutputStream(exec.getOutputStream());
            bos.write(("rm /cache/recovery/extendedcommand\n")
                    .getBytes());
            String fabricante=Build.BRAND;
            String procesador=Build.HARDWARE;



            File fff=new File(Environment.getExternalStorageDirectory()+"/clockworkmod/backup/"+nombreBck+"/");
            if(fff.exists()){
                File[] listFiles = fff.listFiles();
                if(listFiles==null || listFiles.length<=0){
                    diag.setMessage(res.getString(R.string.msgRebootRestoreQF));
                    diag.show();
                }else{
                    cadena=prepPartitionsJIAYU(cadena);
                    cadena=("echo 'restore_rom(\""+ buscarCWMySustituirRutas(fff.getPath())+"\");' >> /cache/recovery/extendedcommand\n");
                }
            }else{
                diag.setMessage(res.getString(R.string.msgExisteBackup));
                diag.show();
            }

            if(cadena!=null && !"".equals(cadena)){
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setMessage(getResources().getString(R.string.msgRebootRestoreQF));
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                        getResources().getString(R.string.cancelarBtn),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int witch) {
                                finish();
                            }
                        });
                dialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        getResources().getString(R.string.aceptarBtn),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int witch) {
                                try {
                                    Runtime rt=Runtime.getRuntime();
                                    Process exec = rt.exec("su");
                                    BufferedOutputStream bos=null;
                                    bos= new BufferedOutputStream(exec.getOutputStream());
                                    bos.write(cadena.getBytes());
                                    bos.write(("reboot recovery").getBytes());
                                    bos.flush();
                                    bos.close();
                                    //((PowerManager) getSystemService(getApplicationContext().POWER_SERVICE)).reboot("recovery");
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 150", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                dialog.show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(bos!=null){
                    bos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return cadena;

    }
    private String prepPartitionsJIAYU(
            String cad) {
        try {
            if("G4A".equals(modelo) || "S1".equals(modelo)){
                cad=(("echo 'run_program(\"/sbin/umount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n"));
                cad=cad+(("echo 'run_program(\"/sbin/mount\",\"/emmc\");' >> /cache/recovery/extendedcommand\n"));
            }else{
                cad=(("echo 'run_program(\"/sbin/umount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n"));
                cad=cad+(("echo 'run_program(\"/sbin/mount\",\"/sdcard\");' >> /cache/recovery/extendedcommand\n"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cad;
    }
    public String buscarCWMySustituirRutas(String fichero){
        String rutCWM="";
        String fabricante=Build.BRAND;
        String procesador=Build.HARDWARE;
        if("G4A".equals(modelo) || "S1".equals(modelo)){
            fichero=fichero.replaceFirst("/storage/sdcard0/", "/emmc/");
        }else{
            fichero=fichero.replaceFirst("/storage/sdcard0/", "/sdcard/");
        }
        rutCWM=fichero;
        return rutCWM;
    }
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isChecked()){
            if(!isRoot){
                backupRdb.setVisibility(View.INVISIBLE);
                restoreRdb.setVisibility(View.INVISIBLE);
                ejecutarBtn.setVisibility(View.INVISIBLE);
            }else{
                backupRdb.setVisibility(View.VISIBLE);
                restoreRdb.setVisibility(View.VISIBLE);
                ejecutarBtn.setVisibility(View.VISIBLE);
            }

        }else{
            backupRdb.setVisibility(View.INVISIBLE);
            restoreRdb.setVisibility(View.INVISIBLE);
            ejecutarBtn.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onClick(View view) {

    }
}
