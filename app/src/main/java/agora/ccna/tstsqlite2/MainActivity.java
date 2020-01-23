package agora.ccna.tstsqlite2;

import android.database.Cursor;
import android.os.Bundle;

import agora.ccna.tstsqlite2.R;
import agora.ccna.tstsqlite2.gestionBDD;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //shared de tstShared
    public static final String PREFS = "Preferences";

    Button creer = null;
    Button enregister = null;
    Button voir = null;
    Button reconstruire = null;
    Button update = null;
    //zones d'édition pour enregistrer de nouvellles données
    EditText ednom = null;
    EditText edprix = null;
    EditText ednb = null;
    EditText edres = null;
    /*
     * objet gérant la BDD : hérite de SQLiteOpenHelper
     */
    gestionBDD bd ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        creer = (Button)findViewById(R.id.button1);
        reconstruire = (Button)findViewById(R.id.button4);
        enregister = (Button)findViewById(R.id.button2);
        voir = (Button)findViewById(R.id.button3);
        update = (Button)findViewById(R.id.button5);
        ednom = (EditText)findViewById(R.id.editText1);
        edprix = (EditText)findViewById(R.id.editText2);
        ednb = (EditText)findViewById(R.id.editText3);
        edres = (EditText)findViewById(R.id.editText4);
        //création de l'objet permettant l'accès à la base de données.
        //celle-ci étant définie dans la classe "gestionBDD"
        bd = new gestionBDD(getApplicationContext());

        creer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bd.onCreate(bd.getWritableDatabase());
            }});
        enregister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bd.addArticle(ednom.getText().toString(), Float.parseFloat(edprix.getText().toString()),Integer.parseInt(ednb.getText().toString()));
                //Log.i("SQLITE" , "ajout de " + ednom.getText().toString() + "   " + edprenom.getText().toString());

            }});
        voir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                edres.setText("");
                //récupère toutes les données de la base
                ArrayList<String> ls = bd.getAfficheArticles();
                //affichage des données dans un EditText multiligne
                if(ls!=null){
                    for (int i=0;i< ls.size();i++)
                        edres.append("i=" + i + "  " + ls.get(i)+ "\r\n");
                    edres.append("\n");

                }
                else
                    Log.i("SQLITE" , "rien à lire ls=null");


            }});
        reconstruire.setOnClickListener(new View.OnClickListener(){
            @Override
            //re-construction de la base : (destruction + construction )
            public void onClick(View v) {
                //paramètres : SQLiteDataBase   ,  oldVersion   , newVersion
                bd.onUpgrade(bd.getWritableDatabase(), 1, 2);
            }});
        update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //mise à jour d'un enregistrement
                bd.update(ednom.getText().toString(), ednb.getText().toString());
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
