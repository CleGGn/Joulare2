package com.afpa.joulare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameOnActivity extends Activity {

    public final static String TAG = "GameOnActivity"; // Le TAG pour les Log
    public int WIDTH = 10; // Largeur de la grille
    public int HEIGHT = 12; // Longueur de la grill
    public float totalMine = 20; // Le nombre total de mine que l'on veut implémenter à la base
    public float compteurMine = totalMine; // Le compteur de mine qui va se décrémenter dans le tableau
    public boolean [][] checkMine = new boolean[HEIGHT][WIDTH]; // Tableau de booléen qui positionnera les mines
    public boolean [][] checkReveal = new boolean[HEIGHT][WIDTH]; // Tableau de booléen qui determinera si une case est revelée ou non
    public boolean [][] checkFlag = new boolean[HEIGHT][WIDTH]; // Tableau de booléen qui determinera si à un drpeau ou non
    boolean mine = true; // Booléen de case minée ou non
    boolean revealed = false; // Booléen de case retournée ou non
    boolean flagged = false; // Booléen utilisé pour les multiples clics long



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadLocale();
        setContentView(R.layout.activity_gameon);

        Button forfeit = findViewById(R.id.ff);

        // Initialisation du TIMER
        TextView timer = findViewById(R.id.timer);
        CountDownTimer timeScore = new CountDownTimer(8 * 60000, 1000) {
            @SuppressLint("SimpleDateFormat")
            public void onTick(long millisUntilFinished) {
                timer.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }
            public void onFinish() {
                timer.setText("X");
                defeat();
            }
        }.start();

        //Affichage du nom du joueur
        TextView nomJoueur = findViewById(R.id.nomJoueur);
        Intent nom = getIntent();
        String strNom = nom.getExtras().getString("nom");
        nomJoueur.setText(strNom);

        // TextView flags = findViewById(R.id.flags);


        //Tableau de booléen qui determine aléatoirement si une case sera minée ou non.
        while (compteurMine != 0) {
            for (int i = 0; i < HEIGHT; i++) {
                float distribution = totalMine / (WIDTH * HEIGHT);
                float mult100 = Math.round(distribution * 100);
                if (compteurMine == 0){
                    break;
                }
                for (int j = 0; j < WIDTH; j++) {
                    long random = Math.round(Math.random() * 100);
                    /*
                    if(checkMine[i][j] == mine){
                        j++;
                    }

                     */

                  /*  if(i == HEIGHT - 1 && j == WIDTH - 1 && compteurMine > 0){
                        i = 0;
                        j = 0;
                    }

                   */

                     if (random < mult100 && checkMine[i][j] != mine) {
                        checkMine[i][j] = mine;
                        compteurMine--;
                    } else {
                        checkMine[i][j] = !mine;
                    }

                     if (compteurMine == 0){
                         break;
                     }
                    Log.i(TAG, "Compteur Mine :" + compteurMine);
                }
            }
        }

        //Tableau de booléen qui determine si les cases sont révélées ou non
        for (int k = 0; k < HEIGHT; k++) {
            for (int l = 0; l < WIDTH; l++) {
                checkReveal[k][l] = revealed;
            }
        }

        //Tableau de booléen qui determine si les cases ont un drapeau ou non
        for (int k = 0; k < HEIGHT; k++) {
            for (int l = 0; l < WIDTH; l++) {
                checkFlag[k][l] = flagged;
            }
        }

        //Affichage de la grille
        LinearLayout monLayout = findViewById(R.id.grille);

        int count = -1;
            for (int i = 0; i < HEIGHT; i++) {
                LinearLayout mesRangs = new LinearLayout(monLayout.getContext());
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mesRangs.setGravity(Gravity.CENTER);
                monLayout.addView(mesRangs, linearParams);
                for (int j = 0; j < WIDTH; j++) {
                    LinearLayout mesColonnes = new LinearLayout(mesRangs.getContext());
                    LinearLayout.LayoutParams linearParams2 = new LinearLayout.LayoutParams(100, 100);
                    mesColonnes.setGravity(Gravity.CENTER);
                    String imgName = "@drawable/cell";
                    mesColonnes.setBackground(getDrawable(getResources().getIdentifier(imgName, null, getPackageName())));
                    mesColonnes.setGravity(Gravity.CENTER);
                    count++;
                    mesColonnes.setId(count);
                    mesRangs.addView(mesColonnes, linearParams2);

                    mesColonnes.setOnClickListener(v -> { // la fonction onClick
                        clicCase(v);
                        // On determine ici l'aspect de la case lorqu'elle sera cliquée
                        String emptyCell = "@drawable/emptycell";
                        String mineCell = "@drawable/trex";
                        String cell1 = "@drawable/n1";
                        String cell2 = "@drawable/n2";
                        String cell3 = "@drawable/n3";
                        String cell4 = "@drawable/n4";
                        String cell5 = "@drawable/n5";
                        String cell6 = "@drawable/n6";
                        String cell7 = "@drawable/n7";
                        String cell8 = "@drawable/n8";

                        if (verifyBoard(mesColonnes.getId())) {
                            mesColonnes.setBackground(getDrawable(getResources().getIdentifier(mineCell, null, getPackageName())));
                            defeat();
                        } else
                            mesColonnes.setBackground(getDrawable(getResources().getIdentifier(emptyCell, null, getPackageName())));

                        if (!verifyBoard(mesColonnes.getId())) {
                            mesColonnes.setBackground(getDrawable(getResources().getIdentifier(emptyCell, null, getPackageName())));
                            if (distribImg(mesColonnes.getId()) == 1) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell1, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 2) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell2, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 3) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell3, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 4) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell4, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 5) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell5, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 6) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell6, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 7) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell7, null, getPackageName())));
                            } else if (distribImg(mesColonnes.getId()) == 8) {
                                mesColonnes.setBackground(getDrawable(getResources().getIdentifier(cell8, null, getPackageName())));
                            }
                        }

                        if(revealing(mesColonnes.getId())){
                            if(checkGameWin()){
                                timeScore.cancel();
                                NameActivity.mpInGame.stop();
                                Intent intent = new Intent(GameOnActivity.this, VictoryActivity.class);
                                intent.putExtra("nom", strNom);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            Log.i(TAG, "victoire ? " + checkGameWin());
                        }

                    });

                    mesColonnes.setOnLongClickListener(v -> { // la fonction onLongClick
                        if(isFlagged(mesColonnes.getId()) && revealing(mesColonnes.getId())) {
                            String flagged = "@drawable/flaggedcell";
                            mesColonnes.setBackground(getDrawable(getResources().getIdentifier(flagged, null, getPackageName())));
                            return true;
                        } else if (isFlagged(mesColonnes.getId()) && !revealing(mesColonnes.getId())){
                            return false;
                        } else if (!isFlagged(mesColonnes.getId()) && revealing(mesColonnes.getId())) {
                            String unflagged = "@drawable/emptycell";
                            mesColonnes.setBackground(getDrawable(getResources().getIdentifier(unflagged, null, getPackageName())));
                        } else return false;
                        return true;
                    });
                }
            }

        forfeit.setOnClickListener(v -> { // la fonction pour abandonner
            AlertDialog.Builder builder = new AlertDialog.Builder(GameOnActivity.this);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.quit);

            builder.setCancelable(true); // Si l'utilisateur clique à coté de la boite, ça annule tout

            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                NameActivity.mpInGame.stop();
                Intent intent = new Intent(GameOnActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });

            builder.setNegativeButton(R.string.no   , (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }


    /////////////////////////////////////////////////////////////////// Méthodes Applicatives //////////////////////////////////////////////////////////////////////

    /**
     * Fonction qui affiche quelque chose au moment du clic sur la vue
     * @param v la vue
     */
    private void clicCase(View v) {
        int idVue = v.getId();
        Log.i(TAG, "CASE : " + idVue);
    }

    private boolean isFlagged(int idVue) {
        int i;
        int j;
        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
        } else if(idVue == WIDTH){
            i = 1;
            j = 0;
        } else{
            i = 0;
            j = idVue;
        }

        if (checkFlag[i][j] == flagged){
            checkFlag[i][j] = !flagged;
            return true;
        }
        return false;

    }

    /**
     * Fonction qui vérifie si la case est révélée ou non
     * @return booleén
     */
    public boolean revealing(int idVue) {
        int i;
        int j;
        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
        } else if(idVue == WIDTH){
            i = 1;
            j = 0;
        } else{
            i = 0;
            j = idVue;
        }

        if (checkReveal[i][j] == revealed){
            checkReveal[i][j] = !revealed;
            return true;
        }
        return false;
    }
    /**
     * Fonction qui vérifie si les conditions de victoire sont respectées
     * @return booleén
     */
    public boolean checkGameWin() {
        for (int i = 1; i < HEIGHT; i++) {
            for (int j = 1; j < WIDTH; j++) {
                if (checkMine[i][j] == !mine  && checkReveal[i][j] == revealed) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Fonction qui parcours le tableau et retourne true pour les cases minés
     * @param idVue l'ID de la case
     */
    private boolean verifyBoard(int idVue) {
        int i;
        int j;
        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
            return checkMine[i][j];
        } else if(idVue == WIDTH){
            i = 1;
            j = 0;
            return checkMine[i][j];
        } else{
            i = 0;
            j = idVue;
            return checkMine[i][j];
        }
    }

    /**
     * Fonction qui vérifie si la case possède des mines autour et renvoie un compteur qui indiquera le nombre de mines aux alentours
     * @param idVue l'id de la case
     * @return le compteur de mine autour
     */
    public int distribImg(int idVue) {
        int count = 0;
        int i;
        int j;
        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
        } else if(idVue == WIDTH){
            i = 1;
            j = 0;

        } else{
            i = 0;
            j = idVue;
        }

        if (!verifyBoard(idVue)) {
            if (i==0 && j==0){
                if(verifyBoard(idVue + 1)){
                    count++;
                }
                if(verifyBoard(idVue + WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue + (WIDTH + 1))){
                    count++;
                }
            } else if(i==0 && j==WIDTH-1){
                if(verifyBoard(idVue - 1)){
                    count++;
                }
                if(verifyBoard(idVue + WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue + (WIDTH - 1))){
                    count++;
                }
            } else if (i==0){
                if(verifyBoard(idVue + 1)){
                    count++;
                }
                if(verifyBoard(idVue - 1)){
                    count++;
                }
                if(verifyBoard(idVue + (WIDTH + 1))){
                    count++;
                }
                if(verifyBoard(idVue + WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue + (WIDTH - 1))){
                    count++;
                }
            } else if (i == HEIGHT-1 && j == 0){
                if(verifyBoard(idVue + 1)){
                    count++;
                }
                if(verifyBoard(idVue - (WIDTH - 1))){
                    count++;
                }
                if(verifyBoard(idVue - WIDTH)){
                    count++;
                }
            } else if (i == HEIGHT - 1 && j == WIDTH - 1 ) {
                if(verifyBoard(idVue - 1)){
                    count++;
                }
                if(verifyBoard(idVue - WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue - (WIDTH + 1))){
                    count++;
                }
            } else if (i == HEIGHT - 1){
                if(verifyBoard(idVue + 1)){
                    count++;
                }
                if(verifyBoard(idVue - 1)){
                    count++;
                }
                if(verifyBoard(idVue - (WIDTH + 1))){
                    count++;
                }
                if(verifyBoard(idVue - WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue - (WIDTH - 1))){
                    count++;
                }
            } else if (j == 0){
                if(verifyBoard(idVue + 1)){
                    count++;
                }
                if(verifyBoard(idVue + (WIDTH + 1))){
                    count++;
                }
                if(verifyBoard(idVue + WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue - (WIDTH - 1))){
                    count++;
                }
                if(verifyBoard(idVue - WIDTH)){
                    count++;
                }
            } else if ( j == WIDTH - 1){
                if(verifyBoard(idVue - 1)){
                    count++;
                }
                if(verifyBoard(idVue - (WIDTH + 1))){
                    count++;
                }
                if(verifyBoard(idVue - WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue + WIDTH)){
                    count++;
                }
                if(verifyBoard(idVue + (WIDTH - 1))){
                    count++;
                }
            } else {
                if (verifyBoard(idVue + 1)) {
                    count++;
                }
                if (verifyBoard(idVue - 1)) {
                    count++;
                }
                if (verifyBoard(idVue - (WIDTH + 1))) {
                    count++;
                }
                if (verifyBoard(idVue - WIDTH)) {
                    count++;
                }
                if (verifyBoard(idVue - (WIDTH - 1))) {
                    count++;
                }
                if (verifyBoard(idVue + (WIDTH + 1))) {
                    count++;
                }
                if (verifyBoard(idVue + WIDTH)) {
                    count++;
                }
                if (verifyBoard(idVue + (WIDTH - 1))) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Fonction qui provoque un immense sentiment d'amertume et de colère, et affiche une boite d'alerte permettant de retourner au menu principal
     */
    public void defeat(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameOnActivity.this);
        builder.setTitle(R.string.defeat);
        builder.setMessage(R.string.fail);
        builder.setCancelable(false); //
        builder.setPositiveButton(R.string.sousTitreMenu, (dialog, which) -> {
            NameActivity.mpInGame.stop();
            Intent intent = new Intent(GameOnActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Fonction executée au lancement, elle va récupérer la dernière langue choisie dans le fichier préférences
     */
    public void loadLocale() {
        Log.i(TAG, "loadLocale");
        SharedPreferences prefs = getSharedPreferences("Mes_Prefs", Activity.MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        changeLang(language);
    }

    /**
     * Fonction qui adapte la langue en fonction de celle choisie dans les préférences
     * @param lang la langue présente dans les préférences
     */
    public void changeLang(String lang) {
        Log.i(TAG, "changeLang");
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }
}
