package com.afpa.joulare.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afpa.joulare.R;

import java.util.Locale;

public class NameActivity  extends AppCompatActivity {

    public final static String TAG = "NameActivity"; // Le TAG pour les Log
    public static MediaPlayer mpInGame = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadLocale();
        setContentView(R.layout.activity_name);

        Button playGame = findViewById(R.id.playGame);
        Button retour = findViewById(R.id.retourOptions);

        playGame.setOnClickListener(v -> { // Fonction qui lance la partie sous certaines conditions
            Log.i(TAG, "clicPlay");
            EditText nom = findViewById(R.id.choiceName);
            String strNom = nom.getText().toString();
            if(strNom.isEmpty()){
                Toast.makeText(NameActivity.this, R.string.emptyName, Toast.LENGTH_SHORT).show();
            } else if(strNom.length() > 13){
                Toast.makeText(NameActivity.this, R.string.lengthName, Toast.LENGTH_SHORT).show();
            } else if (strNom.equals("ElPoivrot")){
                mpInGame = MediaPlayer.create(getBaseContext(),R.raw.molennel);
                mpInGame.start();
                MediaPlayer mpLee = MediaPlayer.create(getBaseContext(),R.raw.lee);
                mpLee.start();
                Intent intent = new Intent(NameActivity.this, GameOnActivity.class);
                intent.putExtra("nom", strNom);
                startActivity(intent);
            } else if (strNom.equals("WhiteBread")){
                mpInGame = MediaPlayer.create(getBaseContext(),R.raw.molennel);
                mpInGame.start();
                MediaPlayer mpLee = MediaPlayer.create(getBaseContext(),R.raw.putain);
                mpLee.start();
                Intent intent = new Intent(NameActivity.this, GameOnActivity.class);
                intent.putExtra("nom", strNom);
                startActivity(intent);
            } else if (strNom.equals("ToriiArigato")){
                mpInGame = MediaPlayer.create(getBaseContext(),R.raw.castaways);
                mpInGame.start();
                Intent intent = new Intent(NameActivity.this, GameOnActivity.class);
                intent.putExtra("nom", strNom);
                startActivity(intent);
            }
            else {
                mpInGame = MediaPlayer.create(getBaseContext(),R.raw.molennel);
                mpInGame.setLooping(true);
                mpInGame.start();
                MediaPlayer mpHaha = MediaPlayer.create(getBaseContext(),R.raw.haha);
                mpHaha.start();
                Intent intent = new Intent(NameActivity.this, GameOnActivity.class);
                intent.putExtra("nom", strNom);
                startActivity(intent);
            }
        });

        retour.setOnClickListener(v -> { // Fonction retour
            Log.i(TAG, "retourClic");
            finish();
        });
    }

    /////////////////////////////////////////////////////////////////// Méthodes Applicatives //////////////////////////////////////////////////////////////////////

    /**
     * Fonction executée au lancement, elle va récupérer la dernière langue choisie dans le fichier préférences
     */
    public void loadLocale() {
        //Log.i(TAG, "loadLocale");
        SharedPreferences prefs = getSharedPreferences("Mes_Prefs", Activity.MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        changeLang(language);
    }

    /**
     * Fonction qui adapte la langue en fonction de celle choisie dans les préférences
     * @param lang la langue présente dans les préférences
     */
    public void changeLang(String lang) {
        //Log.i(TAG, "changeLang");
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }


}
