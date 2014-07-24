package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
    Button contacto = null;
    Button visit = null;
    ImageButton imageButton = null;
    ImageButton mapsButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about2);
        addListenerOnButton();
        contacto = (Button) findViewById(R.id.button1);
        visit = (Button) findViewById(R.id.button2);
        modificarMargins();
    }

    public void addListenerOnButton() {
        try {
            mapsButton = (ImageButton) findViewById(R.id.imageButton2);
            mapsButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    Uri uri = Uri.parse("https://maps.google.es/maps?q=Passatge+d'%C3%80ngels+i+Federic,+2,+46022+Valencia,+Comunidad+Valenciana&hl=es&ie=UTF8&geocode=FWkyWgIdztL6_w&split=0&hq=&hnear=Passatge+d'%C3%80ngels+i+Federic,+2,+46022+Valencia&ll=39.465769,-0.339031&spn=0.005069,0.00721&t=m&z=17&vpsrc=6&iwloc=A");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msgGenericError)+" 124", Toast.LENGTH_SHORT).show();
        }
    }
    private void modificarMargins() {
        TextView scText=(TextView) findViewById(R.id.scText);
        TableLayout.LayoutParams llp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dpi=getResources().getDisplayMetrics().densityDpi;
        if(dpi>=160 && dpi<240){
        }else if(dpi>=240 && dpi<320) {
            llp.setMargins(40, 0, 0, 84);
        }else if(dpi>=320 && dpi<480) {
            llp.setMargins(50, 0, 0, 130);
        }else if(dpi>=480 && dpi<680) {
            llp.setMargins(80, 0, 0, 190);
        }
        scText.setLayoutParams((llp));


        TextView b1=(TextView) findViewById(R.id.textView6);
        TextView b2=(TextView) findViewById(R.id.textView7);
        TextView b3=(TextView) findViewById(R.id.textView2);
        int orientation = getResources().getConfiguration().orientation;
        if(dpi>=160 && dpi<240){
        }else if(dpi>=240 && dpi<320) {
            if(orientation==2) {
                scText.setPadding(15, 0, 0, 0);
                b1.setPadding(250, 0, 0, 0);
                b2.setPadding(250, 0, 0, 0);
                b3.setPadding(250, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(140, 0, 0, 0);
                b2.setPadding(140, 0, 0, 0);
                b3.setPadding(140, 0, 0, 0);
            }
        }else if(dpi>=320 && dpi<480) {
            if(orientation==2) {
                scText.setPadding(40, 0, 0, 0);
                b1.setPadding(400, 0, 0, 0);
                b2.setPadding(400, 0, 0, 0);
                b3.setPadding(400, 0, 0, 0);
            }else{
                scText.setPadding(10, 0, 0, 0);
                b1.setPadding(230, 0, 0, 0);
                b2.setPadding(230, 0, 0, 0);
                b3.setPadding(230, 0, 0, 0);
            }
        }else if(dpi>=480 && dpi<680) {
            if(orientation==2) {
                scText.setPadding(100, 0, 0, 0);
                b1.setPadding(570, 0, 0, 0);
                b2.setPadding(570, 0, 0, 0);
                b3.setPadding(570, 0, 0, 0);
            }else{
                scText.setPadding(20, 0, 0, 0);
                b1.setPadding(350, 0, 0, 0);
                b2.setPadding(350, 0, 0, 0);
                b3.setPadding(350, 0, 0, 0);
            }
        }

    }
}
