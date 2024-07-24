package com.afpa.joulare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import com.afpa.joulare.R;

import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    public final static String Mes_Prefs = "Mes_Prefs"; // Ref fichier préférences
    public final static String TAG = "MainActivity"; // Le TAG pour les Log
    public static MediaPlayer mpInGame = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadLocale();
        setContentView(R.layout.activity_main);

        Button play = findViewById(R.id.play);
        Button rank = findViewById(R.id.rank);
        Button options = findViewById(R.id.option);
        Button exit = findViewById(R.id.exit);

        mpInGame = MediaPlayer.create(getBaseContext(),R.raw.joulare);
        mpInGame.start();

        play.setOnClickListener(v -> { // Fonction qui lance une nouvelle partie
            Log.i(TAG, "clicPlay");
            Intent intent = new Intent(MainActivity.this, NameActivity.class);
            mpInGame.stop();
            startActivity(intent);
        });

        rank.setOnClickListener(v -> { // Fonction qui renvoie vers la vue classement
            Log.i(TAG, "clicRank");
            Intent intent = new Intent(MainActivity.this, RankActivity.class);
            startActivity(intent);
        });

        options.setOnClickListener(v -> { // Fonction qui renvoie vers la vue des options
            Log.i(TAG, "clicOptions");
            Intent intent = new Intent(MainActivity.this, ParamActivity.class);
            startActivity(intent);
        });

        exit.setOnClickListener(v -> { // Fonction qui quitte l'application
            Log.i(TAG, "clicExit");
            finish();
            System.exit(0);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mpInGame.start();
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