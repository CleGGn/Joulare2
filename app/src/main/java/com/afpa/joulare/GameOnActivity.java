package com.afpa.joulare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GameOnActivity extends Activity {

    public final static String TAG = "GameOnActivity"; // Le TAG pour les Log
    public int WIDTH = 10; // Largeur de la grille
    public int HEIGHT = 12; // Longueur de la grille
    public float totalMine = 20; // Le nombre total de mine que l'on veut implémenter à la base
    public float compteurMine = totalMine; // Le compteur de mine qui va se décrémenter dans le tableau
    public boolean [][] tabMine = new boolean[HEIGHT][WIDTH]; // Tableau de booléen qui positionnera les mines
    public boolean [][] tabRevealed = new boolean[HEIGHT][WIDTH]; // Tableau de booléen qui determinera si une case est revelée ou non
    public boolean [][] tabFlag = new boolean[HEIGHT][WIDTH]; // Tableau de booléen qui determinera si à un drpeau ou non
    public boolean mine = true; // Booléen de case minée ou non
    public boolean revealed = false; // Booléen de case retournée ou non
    public boolean flagged = false; // Booléen utilisé pour les multiples clics long
    public int count = 0;
    public String playerName;
    // On determine ici l'aspect de la case lorqu'elle sera cliquée
    public String imgName = "@drawable/cell";

    public LinearLayout grid;

    public Button forfeit;
    public TextView timer;

    // Timer
    public CountDownTimer timeScore;

    // Lignes et colonnes
    public LinearLayout line;
    public LinearLayout.LayoutParams lineParams;
    public LinearLayout column;
    public LinearLayout.LayoutParams columnParams;

    public AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Charge la langue par défaut
        loadLocale();
        // charge l'affichage
        setContentView(R.layout.activity_gameon);
        forfeit = findViewById(R.id.ff);
        grid = findViewById(R.id.grille);
        timer = findViewById(R.id.timer);

        // Initialisation du TIMER
        timeScore = new CountDownTimer(8 * 60000, 1000) {
            @SuppressLint("SimpleDateFormat")
            public void onTick(long millisUntilFinished) {
                timer.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }
            public void onFinish() {
                timer.setText("X");
                defeat();
            }
        }.start();

        // Display and return player's name
        playerName = displayName();

        // Randomly puts mines
        mineDistribution();

        // Synchronize revealed tiles
        checkTileReveal();

        // Synchronize flagged tiles
        checkTileFlag();

        // Remplissage de la grille
        // Lignes
        for (int i = 0; i < HEIGHT; i++) {
            line = new LinearLayout(grid.getContext());
            lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            line.setGravity(Gravity.CENTER);
            grid.addView(line, lineParams);
            // Colonnes
            for (int j = 0; j < WIDTH; j++) {
                column = new LinearLayout(line.getContext());
                columnParams = new LinearLayout.LayoutParams(100, 100);
                column.setGravity(Gravity.CENTER);
                column.setBackground(getDrawable(getResources().getIdentifier(imgName, null, getPackageName())));
                column.setId(count);
                count++;
                line.addView(column, columnParams);

                // Actions on click
                clickTile(column, timeScore, line);

                // Actions on long clicks
                longClickTile(column);
            }
        }
        // Actions on forfeit button
        clickForfeit();
    }



    /////////////////////////////////////////////////////////////////// Méthodes Applicatives //////////////////////////////////////////////////////////////////////

    /**
     * Display player name from intent
     * @return player name
     */
    private String displayName() {
        TextView nomJoueur;
        Intent nom;
        String strNom ;

        //Affichage du nom du joueur
        nomJoueur = findViewById(R.id.nomJoueur);
        nom = getIntent();
        strNom = Objects.requireNonNull(nom.getExtras()).getString("nom");
        nomJoueur.setText(strNom);

        return strNom;
    }

    /**
     * Actions onclick on forfeit button
     */
    private void clickForfeit() {
        forfeit.setOnClickListener(v -> { // la fonction pour abandonner
            builder = new AlertDialog.Builder(GameOnActivity.this);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.quit);

            builder.setCancelable(true); // Si l'utilisateur clique à coté de la boite, ça annule tout

            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                timeScore.cancel();
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

    /**
     * Actions on long clicks
     * @param colonne , current tile
     */
    private void longClickTile(LinearLayout colonne) {
        String flagged = "@drawable/flaggedcell";
        String unflagged = "@drawable/emptycell";

        colonne.setOnLongClickListener(v -> { // la fonction onLongClick
            if(isFlagged(colonne.getId()) && isRevealed(colonne.getId())) {
                colonne.setBackground(getDrawable(getResources().getIdentifier(flagged, null, getPackageName())));
                return true;
            } else if (isFlagged(colonne.getId()) && !isRevealed(colonne.getId())){
                return false;
            } else if (!isFlagged(colonne.getId()) && isRevealed(colonne.getId())) {
                colonne.setBackground(getDrawable(getResources().getIdentifier(unflagged, null, getPackageName())));
            } else return false;
            return true;
        });
    }

    /**
     * Actions on clicks
     * @param colonne   , current tile
     * @param timeScore , current timer
     * @param line
     */
    private void clickTile(LinearLayout colonne, CountDownTimer timeScore, LinearLayout line) {
        // Local variables
        String emptyCell    = "@drawable/emptycell";
        String mineCell     = "@drawable/trex";
        String cell1        = "@drawable/n1";
        String cell2        = "@drawable/n2";
        String cell3        = "@drawable/n3";
        String cell4        = "@drawable/n4";
        String cell5        = "@drawable/n5";
        String cell6        = "@drawable/n6";
        String cell7        = "@drawable/n7";
        String cell8        = "@drawable/n8";

        // la fonction onClick
        colonne.setOnClickListener(v -> {

            // On regarde si on a cliqué sur une mine ou non
            if (isMined(colonne.getId())) {
                colonne.setBackground(getDrawable(getResources().getIdentifier(mineCell, null, getPackageName())));
                timeScore.cancel();
                defeat();
            } else {
                switch (distribImg(colonne.getId())) {
                    case 1:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell1, null, getPackageName())));
                        break;
                    case 2:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell2, null, getPackageName())));
                        break;
                    case 3:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell3, null, getPackageName())));
                        break;
                    case 4:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell4, null, getPackageName())));
                        break;
                    case 5:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell5, null, getPackageName())));
                        break;
                    case 6:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell6, null, getPackageName())));
                        break;
                    case 7:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell7, null, getPackageName())));
                        break;
                    case 8:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(cell8, null, getPackageName())));
                        break;
                    default:
                        colonne.setBackground(getDrawable(getResources().getIdentifier(emptyCell, null, getPackageName())));
                        //zoneReveal(mesColonnes.getId());
                        break;
                }
            }

            if(isRevealed(colonne.getId())){
                if(checkGameWin()){
                    timeScore.cancel();
                    NameActivity.mpInGame.stop();
                    Intent intent = new Intent(GameOnActivity.this, VictoryActivity.class);
                    intent.putExtra("nom", playerName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                Log.i(TAG, "victoire ? " + checkGameWin());
            }
        });
    }


    /**
     * @param idVue, id of the tile
     * @return
     */
    private boolean isFlagged(int idVue) {
        int i;
        int j;
        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
        } else if (idVue == WIDTH){
            i = 1;
            j = 0;
        } else {
            i = 0;
            j = idVue;
        }

        if (tabFlag[i][j] == flagged){
            tabFlag[i][j] = !flagged;
            return true;
        }
        return false;
    }

    /**
     * Fonction qui vérifie si la case est révélée ou non
     * @return booleén
     */
    public boolean isRevealed(int idVue) {
        int i, j;

        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
        } else if (idVue == WIDTH) {
            i = 1;
            j = 0;
        } else {
            i = 0;
            j = idVue;
        }

        if (tabRevealed[i][j] == revealed){
            tabRevealed[i][j] = !revealed;
            return true;
        }
        return false;
    }
    /**
     * Fonction qui vérifie si les conditions de victoire sont respectées
     * @return booleén
     */
    public boolean checkGameWin() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (tabMine[i][j] == !mine  && tabRevealed[i][j] == revealed) {
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
    private boolean isMined(int idVue) {
        int i, j;

        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
            return tabMine[i][j];

        } else if(idVue == WIDTH){
            i = 1;
            j = 0;
            return tabMine[i][j];
        } else {
            i = 0;
            j = idVue;
            return tabMine[i][j];
        }
    }

    public void mineDistribution() {
        // Tableau de booléen qui determine aléatoirement si une case sera minée ou non.
        while (compteurMine != 0) {
            for (int i = 0; i < HEIGHT; i++) {
                // On determine ici la manière dont seront réparties les bombes en fonction de la taille de la grille (actuellement 16% de chances d'être une mine)
                float distribution = totalMine / (WIDTH * HEIGHT);
                float mult100 = Math.round(distribution * 100);

                // Si toutes les mines ont été posées, on sort
                if (compteurMine == 0){
                    break;
                }

                // On va boucler dans la grille en repartissant les bombes aléatoirement jusqu'à arriver à 0
                for (int j = 0; j < WIDTH; j++) {
                    long random = Math.round(Math.random() * 100);
                    if (random < mult100 && tabMine[i][j] != mine) {
                        tabMine[i][j] = mine;
                        compteurMine--;
                        Log.i(TAG, "nb mines : " + compteurMine + "x: " + i + ", y: " + j);
                    } else {
                        if (tabMine[i][j] != mine) {
                            tabMine[i][j] = !mine;
                        }
                    }

                    // Si toutes les mines ont été posées, on sort
                    if (compteurMine == 0){
                        break;
                    }
                }
            }
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

        if (!isMined(idVue)) {
            if (i==0 && j==0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH + 1))){
                    count++;
                }
            } else if(i==0 && j==WIDTH-1){
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH - 1))){
                    count++;
                }
            } else if (i==0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue + (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH - 1))){
                    count++;
                }
            } else if (i == HEIGHT-1 && j == 0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue - (WIDTH - 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
            } else if (i == HEIGHT - 1 && j == WIDTH - 1 ) {
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
                if(isMined(idVue - (WIDTH + 1))){
                    count++;
                }
            } else if (i == HEIGHT - 1){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue - (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
                if(isMined(idVue - (WIDTH - 1))){
                    count++;
                }
            } else if (j == 0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue + (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue - (WIDTH - 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
            } else if ( j == WIDTH - 1){
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue - (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH - 1))){
                    count++;
                }
            } else {
                if (isMined(idVue + 1)) {
                    count++;
                }
                if (isMined(idVue - 1)) {
                    count++;
                }
                if (isMined(idVue - (WIDTH + 1))) {
                    count++;
                }
                if (isMined(idVue - WIDTH)) {
                    count++;
                }
                if (isMined(idVue - (WIDTH - 1))) {
                    count++;
                }
                if (isMined(idVue + (WIDTH + 1))) {
                    count++;
                }
                if (isMined(idVue + WIDTH)) {
                    count++;
                }
                if (isMined(idVue + (WIDTH - 1))) {
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

    private void zoneReveal(int idVue) {
        int count = 0;
        int i;
        int j;

        if (idVue > WIDTH) {
            i = idVue / WIDTH;
            j = idVue % WIDTH;
        } else if(idVue == WIDTH){
            i = 1;
            j = 0;

        } else {
            i = 0;
            j = idVue;
        }

        // Si on est pas sur une mine
        if (!isMined(idVue)) {
            // Si on est sur le coin supérieur gauche
            if (i==0 && j==0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH + 1))){
                    count++;
                }
            } else if(i==0 && j==WIDTH-1){
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH - 1))){
                    count++;
                }
            } else if (i==0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue + (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH - 1))){
                    count++;
                }
            } else if (i == HEIGHT-1 && j == 0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue - (WIDTH - 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
            } else if (i == HEIGHT - 1 && j == WIDTH - 1 ) {
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
                if(isMined(idVue - (WIDTH + 1))){
                    count++;
                }
            } else if (i == HEIGHT - 1){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue - (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
                if(isMined(idVue - (WIDTH - 1))){
                    count++;
                }
            } else if (j == 0){
                if(isMined(idVue + 1)){
                    count++;
                }
                if(isMined(idVue + (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue - (WIDTH - 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
            } else if ( j == WIDTH - 1){
                if(isMined(idVue - 1)){
                    count++;
                }
                if(isMined(idVue - (WIDTH + 1))){
                    count++;
                }
                if(isMined(idVue - WIDTH)){
                    count++;
                }
                if(isMined(idVue + WIDTH)){
                    count++;
                }
                if(isMined(idVue + (WIDTH - 1))){
                    count++;
                }
            } else {
                if (isMined(idVue + 1)) {
                    count++;
                }
                if (isMined(idVue - 1)) {
                    count++;
                }
                if (isMined(idVue - (WIDTH + 1))) {
                    count++;
                }
                if (isMined(idVue - WIDTH)) {
                    count++;
                }
                if (isMined(idVue - (WIDTH - 1))) {
                    count++;
                }
                if (isMined(idVue + (WIDTH + 1))) {
                    count++;
                }
                if (isMined(idVue + WIDTH)) {
                    count++;
                }
                if (isMined(idVue + (WIDTH - 1))) {
                    count++;
                }
            }
        }

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

    private void checkTileReveal() {
        //Tableau de booléen qui determine si les cases sont révélées ou non
        for (int k = 0; k < HEIGHT; k++) {
            for (int l = 0; l < WIDTH; l++) {
                tabRevealed[k][l] = revealed;
            }
        }
    }

    private void checkTileFlag() {
        //Tableau de booléen qui determine si les cases ont un drapeau ou non
        for (int k = 0; k < HEIGHT; k++) {
            for (int l = 0; l < WIDTH; l++) {
                tabFlag[k][l] = flagged;
            }
        }
    }
}
