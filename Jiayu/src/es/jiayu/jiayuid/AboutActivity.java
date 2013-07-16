package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class AboutActivity extends Activity {
    Button contacto = null;
    Button visit = null;
    ImageButton imageButton = null;
    ImageButton mapsButton=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        addListenerOnButton();
        contacto = (Button) findViewById(R.id.button1);
        visit = (Button) findViewById(R.id.button2);
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
            mapsButton = (ImageButton) findViewById(R.id.imageButton);
            mapsButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    Uri uri = Uri.parse("https://maps.google.es/maps?q=Passatge+d'%C3%80ngels+i+Federic,+2,+46022+Valencia,+Comunidad+Valenciana&hl=es&ie=UTF8&geocode=FWkyWgIdztL6_w&split=0&hq=&hnear=Passatge+d'%C3%80ngels+i+Federic,+2,+46022+Valencia&ll=39.465769,-0.339031&spn=0.005069,0.00721&t=m&z=17&vpsrc=6&iwloc=A");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            });

            visit = (Button) findViewById(R.id.button2);
            visit.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Uri uri = Uri.parse("http://www.jiayu.es/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                    }
                }

            });
            contacto = (Button) findViewById(R.id.button1);
            contacto.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    Resources res2 = getResources();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{"info@jiayu.es"});
                    i.putExtra(Intent.EXTRA_SUBJECT, res2.getString(R.string.msgSubjectInfo));
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    try {

                        startActivity(Intent.createChooser(i,
                                res2.getString(R.string.msgEnviarEmail)));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.genericError), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
