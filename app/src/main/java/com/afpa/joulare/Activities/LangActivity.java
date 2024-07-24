package com.afpa.joulare.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.afpa.joulare.R;

import java.util.Locale;

public class LangActivity extends AppCompatActivity {

    public final static String TAG = "LangActivity"; // Le TAG pour les Log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadLocale();
        setContentView(R.layout.activity_lang);

        Button fr = findViewById(R.id.fr);
        Button eng = findViewById(R.id.en);
        Button es = findViewById(R.id.es);
        Button retour = findViewById(R.id.retourOptions);

        fr.setOnClickListener(v -> { // La fonction onClick pour passer en français
            Log.i(TAG, "clicFR");
            Locale locale = new Locale("fr");
            String strLocale = locale.toString();
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;

            SharedPreferences mesPrefs = getSharedPreferences(MainActivity.Mes_Prefs, MODE_PRIVATE);
            String language = mesPrefs.getString("Language", "");

            if (!language.equals(strLocale)) {
            SharedPreferences.Editor edMesPrefs = mesPrefs.edit();
            edMesPrefs.putString("Language", strLocale).apply();

            Intent intent = new Intent(LangActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            } else {
                MediaPlayer mpMundo = MediaPlayer.create(getBaseContext(),R.raw.mundo);
                mpMundo.start();
                Toast.makeText(LangActivity.this,R.string.sameLang, Toast.LENGTH_SHORT).show();
            }
        });


        eng.setOnClickListener(v -> {  // La fonction onClick pour passer en anglais
            Log.i(TAG, "clicENG");
            Locale locale = new Locale("eng");
            String strLocale = locale.toString();
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;

            SharedPreferences mesPrefs = getSharedPreferences(MainActivity.Mes_Prefs, MODE_PRIVATE);
            String language = mesPrefs.getString("Language", "");

            if (!language.equals(strLocale)) {
            SharedPreferences.Editor edMesPrefs = mesPrefs.edit();
            edMesPrefs.putString("Language", strLocale).apply();

            Intent intent = new Intent(LangActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            } else {
                MediaPlayer mpMundo = MediaPlayer.create(getBaseContext(),R.raw.mundo);
                mpMundo.start();
                Toast.makeText(LangActivity.this,R.string.sameLang, Toast.LENGTH_SHORT).show();
            }
        });


        es.setOnClickListener(v -> { // La fonction onClick pour passer en espagnol
            Log.i(TAG, "clicES");
            Locale locale = new Locale("es");
            String strLocale = locale.toString();
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;

            SharedPreferences mesPrefs = getSharedPreferences(MainActivity.Mes_Prefs, MODE_PRIVATE);
            String language = mesPrefs.getString("Language", "");

            if (!language.equals(strLocale)) {
            SharedPreferences.Editor edMesPrefs = mesPrefs.edit();
            edMesPrefs.putString("Language", strLocale).apply();
            Intent intent = new Intent(LangActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            } else {
                MediaPlayer mpMundo = MediaPlayer.create(getBaseContext(),R.raw.mundo);
                mpMundo.start();
                Toast.makeText(LangActivity.this,R.string.sameLang, Toast.LENGTH_SHORT).show();
            }
        });

        retour.setOnClickListener(v -> {  // La fonction de retour
            Log.i(TAG, "retourClic");
            finish();
        });
    }

    /////////////////////////////////////////////////////////////////// Méthodes Applicatives  ///////////////////////////////////////////////////////////////////

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
