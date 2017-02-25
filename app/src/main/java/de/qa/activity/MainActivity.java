package de.qa.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.io.IOException;

import de.qa.fragment.QAFragment;
import de.qa.qa.triplestore.TripleStore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TupleQueryResult result = TripleStore.query(this, "SELECT ?x WHERE { <http://dbpedia.org/resource/Germany> <http://dbpedia.org/ontology/capital> ?x . }");
        while (result.hasNext()) {
            BindingSet solution = result.next();
            Log.d(TAG, "?x = " + solution.getValue("x").stringValue());
        }
        result = TripleStore.query(this, "SELECT ?x WHERE { <http://dbpedia.org/resource/Germany> <http://dbpedia.org/ontology/areaTotal> ?x . }");
        while (result.hasNext()) {
            BindingSet solution = result.next();
            Log.d(TAG, "?x = " + solution.getValue("x").stringValue());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new QAFragment())
                .commit();
    }
}
