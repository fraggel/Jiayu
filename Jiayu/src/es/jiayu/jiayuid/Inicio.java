package es.jiayu.jiayuid;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Inicio extends Activity {
	String version="Jiayu.es 0.65";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        addListenerOnButton();
        descargas = (Button) findViewById(R.id.button1);
        accesorios = (Button) findViewById(R.id.button2);
        descargas.setEnabled(false);
    	accesorios.setEnabled(false);
        ImageButton img=new ImageButton(this);
        img=(ImageButton)findViewById(R.id.imageButton1);
        TextView t=new TextView(this); 
        TextView t2=new TextView(this); 
        TextView t4=new TextView(this); 
        t4 = (TextView) findViewById(R.id.textView4);
        t4.setText(version);
        String compilacion=Build.DISPLAY;;
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
        	modelo="Custom ROM o tu modelo no es JIAYU";
        }else{
        	descargas.setEnabled(true);
        	accesorios.setEnabled(true);
        }
        t.setText("Modelo: "+modelo);
        t2.setText("Compilación: "+compilacion);
    }
    
    public void addListenerOnButton() {
    	 
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
 
	}
    public void openBrowser(View v) {
			Intent intent = new Intent(this, BrowserActivity.class);
			intent.putExtra("modelo", modelo);
			startActivity(intent);
			
	}
    
}
