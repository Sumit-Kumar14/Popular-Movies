package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.infinity.dev.popularmovies.MovieContract;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    private static final String DB_NAME = "MoviesDB";
    private static final int DATABASE_VERSION = 1;

    private static final String MOVIE_TABLE_NAME = "MOVIES";
    private static final String MOVIES_TABLE_SQL = "CREATE TABLE " + MOVIE_TABLE_NAME + "("
            + "ADULT TEXT, "
            + "BACKDROP_PATH TEXT, "
            + "HOMEPAGE TEXT, "
            + "ID TEXT PRIMARY KEY, "
            + "IMDB_ID TEXT, "
            + "ORIGINAL_LANGUAGE TEXT, "
            + "ORIGINAL_TITLE TEXT, "
            + "OVERVIEW TEXT, "
            + "POPULARITY REAL, "
            + "POSTER_PATH TEXT, "
            + "RELEASE_DATE TEXT, "
            + "RUNTIME INTEGER, "
            + "STATUS TEXT, "
            + "TAGLINE TEXT, "
            + "TITLE TEXT, "
            + "VOTE_AVERAGE REAL, "
            + "VOTE_COUNT INTEGER"
            + ")";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(MOVIES_TABLE_SQL);
        }catch (SQLException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE_NAME);
            onCreate(db);
        }catch (SQLException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE_NAME);
            onCreate(db);
        }catch (SQLException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }
    }

    public long addToFavourite(MovieContract contract) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ADULT", contract.isAdult());
        values.put("BACKDROP_PATH", contract.getBackdrop_path());
        values.put("HOMEPAGE", contract.getHomepage());
        values.put("ID", contract.getId());
        values.put("IMDB_ID", contract.getImdb_id());
        values.put("ORIGINAL_LANGUAGE", contract.getOriginal_language());
        values.put("ORIGINAL_TITLE", contract.getOriginal_title());
        values.put("OVERVIEW", contract.getOverview());
        values.put("POPULARITY", contract.getPopularity());
        values.put("POSTER_PATH", contract.getPoster_path());
        values.put("RELEASE_DATE", contract.getRelease_date());
        values.put("RUNTIME", contract.getRuntime());
        values.put("STATUS", contract.getStatus());
        values.put("TAGLINE", contract.getTagline());
        values.put("TITLE", contract.getTitle());
        values.put("VOTE_AVERAGE", contract.getVote_average());
        values.put("VOTE_COUNT", contract.getVote_count());

        return db.insert(MOVIE_TABLE_NAME, null, values);
    }

    public boolean removeFromFavourite(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MOVIE_TABLE_NAME, "ID = ?", new String[]{id}) > 0;
    }

    public boolean isFavourite(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOVIE_TABLE_NAME + " WHERE ID = '" + id + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    public Cursor getFavourite() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOVIE_TABLE_NAME;
        return db.rawQuery(sql, null);
    }

    public Cursor getMovieDetail(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MOVIE_TABLE_NAME + " WHERE ID = '" + id + "'";
        return db.rawQuery(sql, null);
    }
}