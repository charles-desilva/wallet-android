package Stables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "boffin.db";
    private static String query;

    public LocalDatabase(Context context,String query){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.query=query;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(query);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void createTablesIfNotExists(){
        String account="CREATE TABLE IF NOT EXIST Account (id TEXT PRIMARY KEY,accountName TEXT,shortCode TEXT,status INTEGER,user TEXT)";
    }
}
