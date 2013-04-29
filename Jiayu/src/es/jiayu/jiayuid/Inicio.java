package es.jiayu.jiayuid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Inicio extends Activity implements AsyncResponse{
	VersionThread asyncTask=new VersionThread();
	
	String nversion="";
	String version="Jiayu.es ";
	String G1[]={"20120330-212553"};
	String G2SCICS[]={"20120514-230501","20120527","20120629-114115","20120710-221105","20120816-201040"};
	String G2SCJB[]={"20121231-120925","20130109-091634"};
	String G2SCNICS[]={"20120627-220001","20120720-195850","20120817-155307"};
	String G2SCNJB[]={"20121231-121619","20130114-095647"};
	String G2DCICS[]={"20120823-182434","20120830-142135","20120910-132127"};
	String G2DCJB[]={"20130118-115623"};
	String G2DCPVICS[]={"20120914-174725","20121017-121813"};
	String G2DCPVJB[]={"20130118-102229"};
	String G2DCPVNICS[]={"20130308-110158"};
	String G2DCPVNJB[]={""};
	String G2DCTDICS[]={"20121225-180316"};
	String G2DCTDJB[]={""};
	String G3DCICS[]={"20121025","20121115-084708","20121129-082828"};
	String G3DCJB[]={"20130116-221844"};
	String G3DCNICS[]={"20130223-071508"};
	String G3DCNJB[]={""};
	String G2SICS[]={"20130306-143807"};
	String G2SJB[]={"20130109-104044"};
    ImageButton imageButton;
    Button descargas;
    Button accesorios;
    String modelo="";
    String model="";
    String urlActualizacion="";
    String fabricante="";
    String compilacion="";
    protected void onCreate(Bundle savedInstanceState) {
       try {
    	    nversion=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    	    version=version+nversion;
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_inicio);
	        addListenerOnButton();
	        asyncTask.delegate=this;
	        comprobarVersion(version);
	        descargas = (Button) findViewById(R.id.button1);
	        accesorios = (Button) findViewById(R.id.button2);
	        descargas.setEnabled(false);
	    	accesorios.setEnabled(false);
	        ImageButton img=new ImageButton(this);
	        img=(ImageButton)findViewById(R.id.imageButton1);
	        TextView t=new TextView(this); 
	        TextView t2=new TextView(this); 
	        TextView t4=new TextView(this); 
	        TextView t5=new TextView(this);
	        t4 = (TextView) findViewById(R.id.textView4);
	        t5 = (TextView) findViewById(R.id.textView5);
	        t4.setText(version);
	        compilacion=Build.DISPLAY;;
	        fabricante=Build.BRAND;
	        t=(TextView)findViewById(R.id.textView1);
	        t2=(TextView)findViewById(R.id.textView2);
	        String idd[]=Build.DISPLAY.split("-");
	        String id=idd[idd.length-1];
	        for (int i = 0; i < G1.length; i++) {
				String array_element = G1[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G1 con GB";
				}	
			}
	        for (int i = 0; i < G2SCICS.length; i++) {
				String array_element = G2SCICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2SC";
				}	
			}
	        for (int i = 0; i < G2SCJB.length; i++) {
				String array_element = G2SCJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2SC";
				}	
			}
	        
	        for (int i = 0; i < G2SCNICS.length; i++) {
				String array_element = G2SCNICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2SCN";
				}	
			}
	        
	        for (int i = 0; i < G2SCNJB.length; i++) {
				String array_element = G2SCNJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2SCN";
				}	
			}
	        
	        for (int i = 0; i < G2DCICS.length; i++) {
				String array_element = G2DCICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DC";
				}	
			}
	        
	        for (int i = 0; i < G2DCJB.length; i++) {
				String array_element = G2DCJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DC";
				}	
			}
	        
	        for (int i = 0; i < G2DCPVICS.length; i++) {
				String array_element = G2DCPVICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DCPV";
				}	
			}
	        
	        for (int i = 0; i < G2DCPVJB.length; i++) {
				String array_element = G2DCPVJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DCPV";
				}	
			}
	        
	        for (int i = 0; i < G2DCPVNICS.length; i++) {
				String array_element = G2DCPVNICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DCPVN";
				}	
			}
	        
	        for (int i = 0; i < G2DCPVNJB.length; i++) {
				String array_element = G2DCPVNJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DCPVN";
				}	
			}
	        
	        for (int i = 0; i < G2DCTDICS.length; i++) {
				String array_element = G2SJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DCTD";
				}	
			}
	        
	        for (int i = 0; i < G2DCTDJB.length; i++) {
				String array_element = G2DCTDJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2DCTD";
				}	
			}
	        
	        for (int i = 0; i < G3DCICS.length; i++) {
				String array_element = G3DCICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G3DC";
				}	
			}
	        
	        for (int i = 0; i < G3DCJB.length; i++) {
				String array_element = G3DCJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G3DC";
				}	
			}
	        
	        for (int i = 0; i < G3DCNICS.length; i++) {
				String array_element = G3DCNICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G3DCN";
				}	
			}
	        
	        for (int i = 0; i < G3DCNJB.length; i++) {
				String array_element = G3DCNJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G3DCN";
				}	
			}
	        for (int i = 0; i < G2SICS.length; i++) {
				String array_element = G2SICS[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2S";
				}	
			}
	        
	        for (int i = 0; i < G2SJB.length; i++) {
				String array_element = G2SJB[i];
				if(array_element.indexOf(id)!=-1){
					modelo="G2S";
				}	
			}
	        
	        if("".equals(modelo)){
	        	calcularTelefono();
	        	modelo=model;
	        }else{
	        	recalcularTelefono();
	        	descargas.setEnabled(true);
	        	accesorios.setEnabled(true);
	        	
	        }
	        if(modelo.length()<8){
	        	descargas.setEnabled(true);
	        	accesorios.setEnabled(true);
	        	if(!"JIAYU".equals(fabricante.toUpperCase().trim())){
		        	t5.setTextColor(Color.RED);
		        	t5.setText("Identificado como "+modelo+" aunque tu build.prop no indica JIAYU como fabricante");
	        	}
	        }
	        t.setText("Modelo: "+modelo);
	        t2.setText("Compilación Build.prop: "+compilacion);
       } catch (Exception e) {
   		// TODO: handle exception
       }
    }
    
    private void comprobarVersion(String version2) {
    	try {
    		asyncTask.execute(version2);	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    private void ActualizarVersion(){
    	try {
	    	Resources res = this.getResources();
	    	AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setMessage(res.getString(R.string.msgAyudaFirefox));
			dialog.setButton(AlertDialog.BUTTON_POSITIVE,
					res.getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int witch) {
							try {
								if(!"".equals(urlActualizacion)){
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlActualizacion));
							    	 intent.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
							    	 startActivity(intent);
								}
							} catch (Exception e) {
							}
						}
					});
			dialog.show();
    	} catch (Exception e) {
			// TODO: handle exception
		}
    }
	private void recalcularTelefono() {
		calcularTelefono();
		/*if(modelo.equals(model)){*/
			modelo=model;
		/*}else{
			modelo="Rom incorrecta para tu terminal, tu modelo real es: "+model;
		}*/
	}

	private void calcularTelefono(){
    	/*Resources res = this.getResources();
    	AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setMessage(res.getString(R.string.msgComprobarVersion));
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				res.getString(R.string.cancelar),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int witch) {
						modelo="Se ha cancelado la detección del modelo Jiayu";
					}
				});
		dialog.setButton(AlertDialog.BUTTON_POSITIVE,
				res.getString(R.string.aceptar),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int witch) {*/
						try {

					    	int height = 0;
					    	int width = 0;
					    	String procesador="";
					    	String ram="";
					    	String chip="";
					    	String buildprop = "";
					    	
					    	try {
					        	DisplayMetrics dm = new DisplayMetrics();
					        	getWindowManager().getDefaultDisplay().getMetrics(dm);
					        	height = dm.heightPixels;
					        	width = dm.widthPixels;
					    		procesador=Build.HARDWARE;
					    		
					    		FileInputStream fis = new FileInputStream(new File("/system/build.prop"));
					        	byte[] input = new byte[fis.available()];
					        	while (fis.read(input) != -1) {}
					        	buildprop += new String(input);	
							
						    	if(buildprop.toLowerCase().lastIndexOf("mt6620")!=-1){
						    		chip="MT6620";
						    	}else if(buildprop.toLowerCase().lastIndexOf("mt6628")!=-1){
						    		chip="MT6628";
						    	}else{
						    		chip="INDEFINIDO";
						    	}
						    	boolean levantadoB=levantarBlueTooth();
						    	boolean levantadoW=LevantarWifi();
						    	
						    	if("MT6628".equals(chip)){
						    		if(!levantadoB || !levantadoW){
						    			chip="MT6620";
						    		}
						    		
								}else if("MT6620".equals(chip)){
									if(!levantadoB || !levantadoW){
						    			chip="MT6628";
						    		}
								}
						    	
						    	ram=getTotalRAM();
						    	int ramInt=(Integer.parseInt(ram)/1000);
						    	if(ramInt<=290 && ramInt>=200){
						    		ram="256MB";
						    	}else if(ramInt<=530 && ramInt>=300){
						    		ram="512MB";
						    	}else if(ramInt<=1100 && ramInt>=900){
						    		ram="1GB";
						    	}else if(ramInt<=2100 && ramInt>=1700){
						    		ram="2GB";
						    	}
						    	if(width==720){
						    		if("mt6577".equals(procesador.toLowerCase())){
						    			if("MT6628".equals(chip)){
						    				model="G3DCN";
						    			}else if("MT6620".equals(chip)){
						    				model="G3DC";
						    			}else{
						    				model="";
						    			}
						    		}else if("mt6589".equals(procesador.toLowerCase())){
						    			/*if("1GB".equals(ram)){
						    				model="G4";
						    			}else if("2GB".equals(ram)){
						    				model="G4A";
						    			}else{
						    				model="";
						    			}*/
						    		}
						    	}else if(width==540){
						    		/*if("mt6577".equals(procesador.toLowerCase())){
						    			if("MT6628".equals(chip)){
						    				model="G2S";
						    			}else{
						    				model="";
						    			}
						    		}else{
					    				model="";
					    			}*/
						    		model="G2S";
						    	}else if(width==480){
						    		if("mt6575".equals(procesador.toLowerCase())){
						    			if(!"G2SC".equals(modelo) && !"G2SCN".equals(modelo)){
						    				model="G2SC o G2SCN";
						    			}
						    		}else if("mt6577".equals(procesador.toLowerCase())){
						    			//FALTA EL TD
						    			if("512MB".equals(ram)){
						    				if("MT6628".equals(chip)){
						    					model="G2DCPVN";
						    				}else if("MT6620".equals(chip)){
						    					model="G2DCPV";
						    				}else{
							    				model="";
							    			}
						    			}else if("1GB".equals(ram)){
						    				model="G2DC";
						    			}else{
						    				model="";
						    			}
						    		}else{
					    				model="";
					    			}
						    	}else if(width==320){
						    		if("256MB".equals(ram)){
						    			model="G1";
						    		}
						    	}else{
						    		model="Tu terminal no es Jiayu";
						    	}

					    	} catch (Exception e) {
								model="Un error evita detectar tu modelo de Jiayu";
							}
					    	
					    	if("".equals(model.trim())){
					    		model="Tu terminal no es Jiayu";
					    	}
					    	
						} catch (Exception e) {
						}/*
					}
				});
		dialog.show();*/
	}

	private boolean levantarBlueTooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean total=false;
		try {
			if (mBluetoothAdapter == null) {
			    total=false;
			} else {
				if(mBluetoothAdapter.isEnabled()){
					  total=true;
				  }else{  
					  mBluetoothAdapter.enable();
					  synchronized ( mBluetoothAdapter) {
							mBluetoothAdapter.wait(2000);
						}
					  if(mBluetoothAdapter.isEnabled()){
						  total=true;
					  }else{
						  total=false;
					  }
					  while(!mBluetoothAdapter.disable()){
						  mBluetoothAdapter.disable();  
					  }
					  
				  }  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}
	
	private boolean LevantarWifi() {
		WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);  
		boolean total=false;
		try {
			
		
		  if(wifiManager.isWifiEnabled()){
			  total=true;
		  }else{  
			  wifiManager.setWifiEnabled(true);
			  synchronized ( wifiManager) {
				  wifiManager.wait(2000);
				}
			  if(wifiManager.isWifiEnabled()){
				  total=true;
			  }else{
				  total=false;
			  }
			  while(wifiManager.isWifiEnabled()){
				  wifiManager.setWifiEnabled(false);
			  }
		  }  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return total;
	}

	public void addListenerOnButton() {
    	try {
			
			imageButton = (ImageButton) findViewById(R.id.imageButton1);
			imageButton.setOnClickListener(new View.OnClickListener() {
	 
				public void onClick(View arg0) {
	 
					Uri uri = Uri.parse("http://www.jiayu.es");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
	 
			});
			descargas = (Button) findViewById(R.id.button1);
			descargas.setOnClickListener(new View.OnClickListener() {
	 
				public void onClick(View arg0) {
	 				openBrowser(arg0);
				}
	 
			});
			accesorios = (Button) findViewById(R.id.button2);
			accesorios.setOnClickListener(new View.OnClickListener() {
	 
				public void onClick(View arg0) {
	 
					Uri uri = Uri.parse("http://www.jiayu.es/4-jiayu-accesorios");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
	 
			});
    	} catch (Exception e) {
			// TODO: handle exception
		} 
 
	}
    public void openBrowser(View v) {
    	try {
    		Intent intent = new Intent(this, BrowserActivity.class);
			intent.putExtra("modelo", modelo);
			startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    public static String getTotalRAM() {
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            load.replaceAll(" ","");
            int indexOf = load.indexOf(":");
            int indexOf2 = load.toLowerCase().indexOf("kb");
            load=load.substring(indexOf+1,indexOf2);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }
        return load.trim();
    }

	@Override
	public void processFinish(String output) {
		try {
			if(output!=null && !"TIMEOUT".equals(output)){
				String[] split = output.split("----");
				String newversion=split[0].split(" ")[1];
				urlActualizacion=split[1];
				if(!"".equals(urlActualizacion) && !nversion.equals(newversion)){
			    	Resources res = this.getResources();
			    	AlertDialog dialog = new AlertDialog.Builder(this).create();
					dialog.setMessage(res.getString(R.string.msgComprobarVersion)+" "+nversion+"->"+newversion+" "+res.getString(R.string.msgPreguntaVersion));
					dialog.setButton(AlertDialog.BUTTON_NEGATIVE,
							res.getString(R.string.cancelar),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int witch) {
								}
							});
					dialog.setButton(AlertDialog.BUTTON_POSITIVE,
							res.getString(R.string.aceptar),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int witch) {
									try {
										ActualizarVersion();
									} catch (Exception e) {
									}
								}
							});
					dialog.show();
			    }
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
