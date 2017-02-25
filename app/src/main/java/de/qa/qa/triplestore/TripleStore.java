package de.qa.qa.triplestore;

import android.content.Context;
import android.util.Log;

import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;

public class TripleStore {
    private static final String TAG = TripleStore.class.getSimpleName();

    private static Repository sDatabase;

    private static void openDatabase(Context context) {
        File dbDir = new File(context.getExternalFilesDir(null), "offline_data");
        sDatabase = new SailRepository(new NativeStore(dbDir));
        Log.d(TAG, "Initializing database...");
        sDatabase.initialize();
        Log.d(TAG, "Done.");
    }

    public static TupleQueryResult query(Context context, String sparqlQuery) {
        if(sDatabase == null) openDatabase(context);
        RepositoryConnection connection = sDatabase.getConnection();
        TupleQuery tupleQuery = connection.prepareTupleQuery(sparqlQuery);
        TupleQueryResult result = tupleQuery.evaluate();
        return result;
    }
}
