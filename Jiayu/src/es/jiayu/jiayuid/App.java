package es.jiayu.jiayuid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class App extends Activity implements AsyncResponse{
	VersionThread asyncTask=new VersionThread();
	
	String nversion="";
	String version="";
	//G2DCPV/N,G2DC/N,G3DC/N,G3QC,G4B
	String url[]={"http://www.jiayu.es/es/jiayu-moviles/10-jiayu-g2.html","http://www.jiayu.es/es/jiayu-moviles/16-jiayu-g2.html","http://www.jiayu.es/es/jiayu-moviles/13-jiayu-g2s.html","","http://www.jiayu.es/es/jiayu-moviles/12-jiayu-g3.html","http://www.jiayu.es/es/jiayu-moviles/17-jiayu-g4.html"};
	/*String G1[]={"20120330-212553"};
	String G2SCICS[]={"20120514-230501","20120527","20120629-114115","20120710-221105","20120816-201040"};
	String G2SCJB[]={"20121231-120925","20130109-091634"};
	String G2SCNICS[]={"20120627-220001","20120720-195850","20120817-155307"};
	String G2SCNJB[]={"20121231-121619","20130114-095647"};
	String G2DCICS[]={"20120823-182434","20120830-142135","20120910-132127"};
	String G2DCJB[]={"20130118-115623"};
	String G2DCNICS[]={"20130413-113742"};
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
	String G2SJB[]={"20130109-104044"};*/
    ImageButton imageButton;
    Button descargas;
    Button accesorios;
    Button videotutoriales;
    String modelo="";
    String model="";
    String urlActualizacion="";
    String fabricante="";
    String compilacion="";
    String newversion="";
    protected void onCreate(Bundle savedInstanceState) {
    	try {
    		super.onCreate(savedInstanceState);
    		nversion=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
     	    version="Jiayu.es ";
     	    version=version+nversion;
    		Resources res = this.getResources();
    		
    		setContentView(R.layout.activity_app);
    		addListenerOnButton();
	        asyncTask.delegate=this;
	        comprobarVersion(version);
	        descargas = (Button) findViewById(R.id.button1);
	        accesorios = (Button) findViewById(R.id.button2);
	        videotutoriales = (Button) findViewById(R.id.button3);
	        descargas.setEnabled(false);
	    	accesorios.setEnabled(false);
	        TextView t=new TextView(this); 
	        TextView t2=new TextView(this); 
	        TextView t4=new TextView(this); 
	        TextView t5=new TextView(this);
	        t4 = (TextView) findViewById(R.id.textView4);
	        t5 = (TextView) findViewById(R.id.textView5);
	        t4.setText(version);
	        compilacion=Build.DISPLAY;;
	        fabricante=infoBrand();
	        t=(TextView)findViewById(R.id.textView1);
	        t2=(TextView)findViewById(R.id.textView2);
	        
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
		        	t5.setText(res.getString(R.string.msgIdentificado1)+modelo+res.getString(R.string.msgIdentificado2));
	        	}
	        }
	        t.setText(res.getString(R.string.msgModelo)+modelo);
	        t2.setText(res.getString(R.string.msgCompilacion)+compilacion);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private String infoBrand() throws IOException {
		String fabricante=Build.BRAND;
		String buildprop="";
		FileInputStream fis=null;
		if(fabricante.toUpperCase().indexOf("JIAYU")==-1){
			try {
				fis = new FileInputStream(new File("/system/build.prop"));
	        	byte[] input = new byte[fis.available()];
	        	while (fis.read(input) != -1) {}
	        	buildprop += new String(input);	
	        	if(buildprop.toUpperCase().indexOf("JIAYU")!=-1){
	        		fabricante="JIAYU";
	        	}else{
	        		fabricante="TERMINAL NO JIAYU";
	        	}
	        	fis.close();
			} catch (Exception e) {
				if(fis!=null){
					fis.close();
				}
			}
			
		}
		return fabricante.toUpperCase();
	}

	private void comprobarVersion(String version2) {
    	try {
   			asyncTask.execute(version2);
    	} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private void MenuActualizacion(){
    	
    	if(!"".equals(urlActualizacion) && !nversion.equals(newversion) && (Float.parseFloat(nversion.replaceAll("Jiayu.es ", ""))<Float.parseFloat(newversion.replaceAll("Jiayu.es ", "")))){
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
								PackageManager pm= getPackageManager();
								 Intent it = pm.getLaunchIntentForPackage("org.mozilla.firefox");
								if(it==null){
									instalarFirefoxActualizacion();
									
								}else{
									ActualizarVersion();
								}
							} catch (Exception e) {
							}
						}
					});
			dialog.show();
	    }else{
	    	Resources res = this.getResources();
	    	AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setMessage(res.getString(R.string.msgLastVersion));
			dialog.setButton(AlertDialog.BUTTON_POSITIVE,
					res.getString(R.string.aceptar),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int witch) {
						}
					});
	    	dialog.show();
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
    		Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
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
		Resources res = this.getResources();
    					try {

					    	int height = 0;
					    	int width = 0;
					    	String procesador="";
					    	String ram="";
					    	String chip="";
					    	String buildprop = "";
					    	FileInputStream fis =null;
					    	try {
					        	DisplayMetrics dm = new DisplayMetrics();
					        	getWindowManager().getDefaultDisplay().getMetrics(dm);
					        	height = dm.heightPixels;
					        	width = dm.widthPixels;
					    		//procesador=Build.HARDWARE;
					        	procesador=getInfoCPU();
					    		int orientation = getResources().getConfiguration().orientation;
					    		fis = new FileInputStream(new File("/system/build.prop"));
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
						    		if(!levantadoB && !levantadoW){
						    			chip="MT6620";
						    		}
						    		
								}else if("MT6620".equals(chip)){
									if(!levantadoB && !levantadoW){
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
						    	if(width==720 ||(orientation==2 && height==720)){
						    		if("mt6577".equals(procesador.toLowerCase())){
						    			if("MT6628".equals(chip)){
						    				model="G3DCN";
						    			}else if("MT6620".equals(chip)){
						    				model="G3DC";
						    			}else{
						    				model="";
						    			}
						    		}else if("mt6589".equals(procesador.toLowerCase())){
						    			if("1GB".equals(ram)){
						    				android.hardware.Camera cam = android.hardware.Camera.open(1);
						    				List<Size> supportedPictureSizes = cam.getParameters().getSupportedPictureSizes();
						    				int result=-1;
						    				for (Iterator<Size> iterator = supportedPictureSizes
													.iterator(); iterator
													.hasNext();) {
												Size sizes = (Size) iterator.next();
												result=sizes.width;
												
											}
						    				cam.release();
						    				//if(modelo.indexOf("G3")!=-1 || disp.indexOf("G3")!=-1 || "1200X1600".equals(result)){
						    				if(result!=-1 && result<=1600){
						    					model="G3QC";
						    				}else{
						    					model="G4B";
						    				}
						    			}else if("2GB".equals(ram)){
						    				model="G4A";
						    			}else{
						    				model="";
						    			}
						    		}
						    	}else if(width==540 ||(orientation==2 && height==540)){
						    		/*if("mt6577".equals(procesador.toLowerCase())){
						    			if("MT6628".equals(chip)){
						    				model="G2S";
						    			}else{
						    				model="";
						    			}
						    		}else{
					    				model="";
					    			}*/
						    		if("mt6577t".equals(procesador.toLowerCase())){
						    			model="G2S";
						    		}
						    	}else if(width==480 ||(orientation==2 && height==480)){
						    		if("mt6575".equals(procesador.toLowerCase())){
						    			if(!"G2SC".equals(modelo) && !"G2SCN".equals(modelo)){
						    				model="G2SC o G2SCN";
						    			}
						    		}else if("mt6577".equals(procesador.toLowerCase())){
						    			//FALTA EL Jiayu G2TD
						    			if("512MB".equals(ram)){
						    				if("MT6628".equals(chip)){
						    					model="G2DCPVN";
						    				}else if("MT6620".equals(chip)){
						    					model="G2DCPV";
						    				}else{
							    				model="";
							    			}
						    			}else if("1GB".equals(ram)){
						    				if("MT6628".equals(chip)){
						    					model="G2DCN";
						    				}else if("MT6620".equals(chip)){
						    					model="G2DC";
						    				}else{
							    				model="";
							    			}
						    				
						    			}else{
						    				model="";
						    			}
						    		}else{
					    				model="";
					    			}
						    	}else if(width==320 ||(orientation==2 && height==320)){
						    		if("256MB".equals(ram)){
						    			model="G1";
						    		}
						    	}else{
						    		model=res.getString(R.string.msgTerminalNoJiayu);
						    	}

					    	} catch (Exception e) {
								model=res.getString(R.string.msgErrorIdentificar);
								fis.close();
							}
					    	
					    	if("".equals(model.trim())){
					    		model=res.getString(R.string.msgTerminalNoJiayu);
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
			Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
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
			videotutoriales = (Button) findViewById(R.id.button3);
			videotutoriales.setOnClickListener(new View.OnClickListener() {
	 
				public void onClick(View arg0) {
	 				openBrowserVideo(arg0);
				}
	 
			});
    	} catch (Exception e) {
    		Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
		} 
 
	}
    public void openBrowser(View v) {
    	try {
    		Intent intent = new Intent(this, BrowserActivity.class);
			intent.putExtra("modelo", modelo);
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
		}
	}
    public void openBrowserVideo(View v) {
    	
    	try {
    		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/channel/UCL1i90sCYqJhehj45dM2Qhg/videos"));
    		startActivity(myIntent);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
		}
	}
    public static String getTotalRAM() throws Exception {
        RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            load.replaceAll(" ","");
            int indexOf = load.indexOf(":");
            int indexOf2 = load.toLowerCase().indexOf("kb");
            load=load.substring(indexOf+1,indexOf2);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        	if(reader!=null){
        		reader.close();
        	}
        }
        return load.trim();
    }
    public static String getInfoCPU() throws Exception{
        RandomAccessFile reader = null;
        String load = "";
        try {
            reader = new RandomAccessFile("/proc/cpuinfo", "r");
            while(load.toLowerCase().indexOf("hardware")==-1){
            	load = reader.readLine();
            }
            
            load=load.replaceAll(" ","");
            load=load.replaceAll("\t","");
            load=load.toLowerCase();
            int indexOf = load.indexOf(":");
            int indexOf2 = load.length();
            load=load.substring(indexOf+1,indexOf2);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        	if(reader!=null){
        		reader.close();
        	}
        }
        return load.trim();
    }

	@Override
	public void processFinish(String output) {
		try {
			if(output!=null && !"TIMEOUT----".equals(output)){
				String[] split = output.split("----");
				newversion=split[0].split(" ")[1];
				urlActualizacion=split[1];
				if(!"".equals(urlActualizacion) && !nversion.equals(newversion)&& (Float.parseFloat(nversion.replaceAll("Jiayu.es ", ""))<Float.parseFloat(newversion.replaceAll("Jiayu.es ", "")))){
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
										PackageManager pm= getPackageManager();
										 Intent it = pm.getLaunchIntentForPackage("org.mozilla.firefox");
										if(it==null){
											instalarFirefoxActualizacion();
											
										}else{
											ActualizarVersion();
										}
									} catch (Exception e) {
									}
								}
							});
					dialog.show();
			    }
			}
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
		}
	}
	private void instalarFirefoxActualizacion() {
		Resources res=this.getResources();
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setMessage(res.getString(R.string.msgNoFirefox));
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
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri
									.parse("market://details?id=org.mozilla.firefox"));
							startActivity(intent);
							ActualizarVersion();
						} catch (Exception e) {
						}
					}
				});
		dialog.show();
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_update:
			MenuActualizacion();
			return true;
		case R.id.action_about:
			try {
	    		Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), getResources().getString(R.string.errorGenerico), Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_exit:
			finish();
		default:
			return super.onMenuItemSelected(featureId, item);
				
		}
	}
}
