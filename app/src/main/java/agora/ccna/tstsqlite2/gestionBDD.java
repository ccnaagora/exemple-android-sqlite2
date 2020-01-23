package agora.ccna.tstsqlite2;

/*
 * Exemple de gestion de base de données SQLite
 * Petite BDD pour l'application seulement
 * 
 * Stockage d'un panier d'articles: nom, prix et nombre
 * 
 * La classe de gestion de BDD SQLite doit dériver de SQLiteOpenHelper (SDK android)
 * Elle permet de récupèrer une référence sur la base de données (si elle existe).
 * Et doit surcharger:
 * 		le constructeur initialisant l'objet de connexion à la BDD
 * 		la méthode public void onCreate(SQLiteDatabase db) pour créer la base (1 seule fois)
 * 			Cette méthode ne peut être appelée que si la BDD n'existe pas.
 * 			Si elle existe, l'appel de la méthode déclenchera une exception
 * 		la méthode public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) pour mise à jour de la BDD
 * 			Généralement, cette méthode doit coder
 * 				a) la destruction de la base
 * 				b) la reconstruction avec un numéro de version éventuellement upgradée
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class gestionBDD extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "panier";
    
   
    /*
     * requête SQL pour la création de la base :
     * 1 table
     * 			articles
     * 4 champs
     * 			ide		clé primaire auto-incremente
     * 			nom		nom de l'article
     * 			prix	prix unitaire de l'article
     * 			nb		nombre d'unité de l'article
     */
    
    private static final String sqlPanier = "CREATE TABLE articles ( " +
            "ide INTEGER PRIMARY KEY NOT NULL , " + 
            "nom TEXT, "+
            "prix FLOAT, " +
            "nb INTEGER " +
            ")";

	public gestionBDD(Context context) {
		//this est l'objet de connexion à la BDD
		//appel au constructeur de la classe mère
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		// TODO Auto-generated constructor stub
		Log.i("ABS", "creation de BDD " +sqlPanier );
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try{
			if(db == null){
        		Log.e("ABS","Erreur BDD null");return;}
			db.execSQL(sqlPanier); 
			Log.i("ABS", "ON creation de BDD ");
		}catch(SQLiteException ie){Log.e("SQLITE",ie.getMessage());
			Log.e("SQLITE", sqlPanier);}
		}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * mise à jour de la base: destruction puis re-création
		 */
		// TODO Auto-generated method stub
		try{
        if(db == null){Log.e("SQLITE","Erreur bdd null");return;}
        //destruction de la base
		db.execSQL("DROP TABLE IF EXISTS articles");
		 
		Log.i("SQLITE", "ON upgrade de BDD ");
		//appel de la méthode onCreate(...)
		//=> re-création de la base
        this.onCreate(db);
		}catch(SQLiteException ie){Log.e("SQLITE",ie.getMessage());
		Log.e("SQLITE", sqlPanier);}
		        //db.close();
}

/*
 * Exemple d'une méthode qui retourne la liste de tous les enregistrements de la table 'articles'
 * par ordre alphabéthique
 * Requêtes SQL
 */
public ArrayList<String> getAfficheArticles(){
			ArrayList<String> ls = new ArrayList<String>();
			//curseur permettant de se déplacer dans une table virtuelle SQL
			Cursor c = null;
			//db = référence à la BDD
			SQLiteDatabase db = this.getReadableDatabase();
		    if(db == null){Log.e("SQLITE","Erreur BDD null");return null ;}
		    try{
		    	String req = "SELECT * FROM articles  ";
		    	
    			req += " ORDER BY nom ";
		    	Log.i("SQLITE" , "Requete getArticle :"+ req );
		    	/*
		    	 * c = curseur sur la table résultante de la requête SQL select...
		    	 */
		    	c = db.rawQuery(req, null);
		    	
		    	if(c == null){
		    		Log.e("ABS","Erreur curseur null");
		    			//db.close();
		    			return null;//}
		    		}
		    	//test : la table est-elle vide?
		    	if(c.getCount() == 0)return null;
		    	/*
		    	 * parcours de la table pour c= le premier enregistrement jusqu'au dernier
		    	 */
		    	
		    	if(c.moveToFirst()){
		    		do{
		    			//récupération des champs de chaque enregistrement selon leur type
		    			long id = c.getLong(0);
		    			String n = c.getString(1);
		    			float p = c.getFloat(2);
		    			int nba = c.getInt(3);
		    			String res= n;res += " p="; res += String.valueOf(p);res += " nb="; res += String.valueOf(nba);
		    			ls.add(res);
		    			Log.i("SQLITE" , "-> " + id + "   " + n + "   " + p + "  " + nba);
		    		}while(c.moveToNext());//passage à l'enregistrement suivant.
		    	}
		    	
		    	c.close();
		    	//db.close();
		    	}catch(SQLiteException ie){
		    		Log.e("SQLITE",ie.getMessage());
		    		if(c != null)c.close();
		    		//db.close();
		    		}
		    return ls;
	}
//*******************************************************************************
/*
 * Enregistrement d'un article
 */
public void addArticle(String n , float p, int nba){

    SQLiteDatabase db = this.getWritableDatabase();
	try{
        if(db == null){Log.e("SQLITE","Erreur bdd null");return;}
        //stockage des valeurs dans un contentValues
        //la clé primaire auto-incrémentée n'est pas insérées
        ContentValues values = new ContentValues();
        values.put("nom", n); // nom
        values.put("prix", p); // prix
        values.put("nb", nba);
       
        /*
         * insersion dans la table : pas de requête SQL mais appel d'une méthode de la classe.
         * insert( nomTable , null  , valeurs )
         */
        long r = db.insert("articles", null, values); // key/value -> keys = column names/ values = column values
        if(r == -1){
        					Log.e("SQLITE", "erreur insert: "+r);
        					}
        else {
        	//Log.i("ABS_REQADD", "ADD eleve "+e.toString());
        }
	}catch(SQLiteException ie){Log.e("SQLITE",ie.getMessage());
			Log.e("SQLITE", "ADD article: ");
		}
	//fermeture de la connexion à la base
	db.close();
}
//*************************************************************
/*
 * mise à jour du nombre d'un article donné :
 * Cherche le nom de l'article et update le nombre
 */
public void update(String nom , String n){
	int nb = Integer.parseInt(n);
	SQLiteDatabase db = this.getWritableDatabase();
	ContentValues cv = new ContentValues();
	cv.put("nb", nb);
	String nomt[] = {nom};
	//appel de update de la classe SQLiteDataBase
	db.update("articles", cv, "nom=?", nomt);
}
}

