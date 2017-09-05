package com.tupelo.wellness.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;

/**
 * Created by Abhishek Singh Arya on 06-08-2015.
 */
public class DbAdapter extends SQLiteOpenHelper {
    private static DbAdapter mDbHelper;
    public static final String DATABASE_NAME = "walkingchallengedb";

    /*----------TUPELO TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_TUPELO = "tupelo";
    public static final String COLUMN_TUPELO_SESSION_ID = "sessid";
    public static final String COLUMN_TUPELO_USER_ID = "userId";
    public static final String COLUMN_TUPELO_USERNAME = "userName";
    public static final String COLUMN_TUPELO_USER_AVATAR = "userAvatarURL";
    public static final String COLUMN_TUPELO_CORP_ID = "corpId";
    private static final int DATABASE_VERSION = 4;

    /*----------FITBIT TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_FITBIT = "fitbit";
    public static final String COLUMN_FITBIT_USER_ID = "user_id";
    public static final String COLUMN_FITBIT_TOKEN_TYPE = "token_type";
    public static final String COLUMN_FITBIT_EXPIRES_IN = "expires_in";
    public static final String COLUMN_FITBIT_ACCESS_TOKEN = "access_token";
    public static final String COLUMN_FITBIT_EXPIRY_DATE = "date";

    /*----------GARMIN TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_GARMIN = "garmin";
    public static final String COLUMN_GARMIN_LOGIN = "login";

    /*----------JAWBONE TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_JAWBONE = "jawbone";
    public static final String COLUMN_JAWBONE_ACCESS_TOKEN = "access_token";
    public static final String COLUMN_JAWBONE_REFRESH_TOKEN = "refresh_token";

    /*----------MYMO TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_MYMO = "mymo";
    public static final String COLUMN_MYMO_LOGIN = "login";

    /*----------PEDOMETER TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_PEDOMETER = "padometer";
    public static final String COLUMN_PEDOMETER_LOGIN = "login";

    /*----------S Health TABLE CONSTANTS----------*/
    public static final String TABLE_NAME_SHEALTH = "shealth";
    public static final String COLUMN_SHEALTH_LOGIN = "login";

    /*----------TUPELO TABLE CREATION----------*/
    private final String DATABASE_CREATE_TUPELO_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_TUPELO + "("
            + COLUMN_TUPELO_SESSION_ID + ", "
            + COLUMN_TUPELO_USER_ID +", "
            + COLUMN_TUPELO_USERNAME +", "
            + COLUMN_TUPELO_CORP_ID +", "
            + COLUMN_TUPELO_USER_AVATAR
            + ")";

    /*----------FITBIT TABLE CREATION----------*/
    private final String DATABASE_CREATE_FITBIT_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_FITBIT + "("
            + COLUMN_FITBIT_USER_ID + " NOT NULL,"
            + COLUMN_FITBIT_TOKEN_TYPE +" NOT NULL,"
            + COLUMN_FITBIT_EXPIRES_IN +" NOT NULL,"
            + COLUMN_FITBIT_ACCESS_TOKEN +" NOT NULL,"
            + COLUMN_FITBIT_EXPIRY_DATE +" NOT NULL"
            + ")";

    /*----------GARMIN TABLE CREATION----------*/
    private final String DATABASE_CREATE_GARMIN_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_GARMIN + "("
            + COLUMN_GARMIN_LOGIN
            + ")";

    /*----------JAWBONE TABLE CREATION----------*/
    private final String DATABASE_CREATE_JAWBONE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_JAWBONE + "("
            + COLUMN_JAWBONE_ACCESS_TOKEN + " NOT NULL,"
            + COLUMN_JAWBONE_REFRESH_TOKEN +" NOT NULL"
            + ")";

    /*----------MYMO TABLE CREATION----------*/
    private final String DATABASE_CREATE_MYMO_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_MYMO + "("
            + COLUMN_MYMO_LOGIN
            + ")";

    /*----------PADOMETER TABLE CREATION----------*/
    private final String DATABASE_CREATE_PADOMETER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_PEDOMETER + "("
            + COLUMN_PEDOMETER_LOGIN
            + ")";

    /*----------S HEALTH TABLE CREATION----------*/
    private final String DATABASE_CREATE_SHEALTH_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_SHEALTH+ "("
            + COLUMN_SHEALTH_LOGIN
            + ")";

    /*----------ALTER TABLE----------*/
//    private static final String ALTER_TABLE_FITBIT = "ALTER TABLE "
//            + TABLE_NAME_FITBIT + " ADD COLUMN "
//            + COLUMN_FITBIT_REFRESH_TOKEN + " string;";


    public DbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DbAdapter getInstance(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new DbAdapter(context);
        }
        return mDbHelper;
    }

    public boolean isLogin(String TABLE_NAME){
        Cursor cursor = mDbHelper.fetchQuery(TABLE_NAME);
        if(cursor.getCount()>0)
            return true;
        return false;
    }
    public String insertQuery(String TABLE_NAME, ContentValues contentValues)
            throws SQLException {
        long id=0;
        try {
            final SQLiteDatabase writableDatabase = getWritableDatabase();
            id=writableDatabase.insert(TABLE_NAME, null, contentValues);
//            Log.e("in insert query", String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(id);
    }

    public Cursor fetchQuery(String TABLE_NAME) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        final SQLiteDatabase readableDatabase = getReadableDatabase();
        final Cursor cursor = readableDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void deleteAll(String TABLE_NAME) {
        final SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete(TABLE_NAME, null, null);
    }

    public void deleteAllDevices() {
        final SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete(TABLE_NAME_FITBIT, null, null);
        writableDatabase.delete(TABLE_NAME_GARMIN, null, null);
        writableDatabase.delete(TABLE_NAME_JAWBONE, null, null);
        writableDatabase.delete(TABLE_NAME_MYMO, null, null);
        writableDatabase.delete(TABLE_NAME_SHEALTH, null, null);
        writableDatabase.delete(TABLE_NAME_PEDOMETER, null, null);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE_TUPELO_TABLE);
        db.execSQL(DATABASE_CREATE_FITBIT_TABLE);
        db.execSQL(DATABASE_CREATE_GARMIN_TABLE);
        db.execSQL(DATABASE_CREATE_JAWBONE_TABLE);
        db.execSQL(DATABASE_CREATE_MYMO_TABLE);
        db.execSQL(DATABASE_CREATE_PADOMETER_TABLE);
        db.execSQL(DATABASE_CREATE_SHEALTH_TABLE);
        String s = "In";
//        Log.e("DbAdapter On Create", s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TUPELO);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FITBIT);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GARMIN);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_JAWBONE);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MYMO);
//        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_PADOMETER_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SHEALTH);
        onCreate(db);
    }
}
