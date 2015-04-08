package com.jordanec.agendacontactos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database{
	public static String iDContacto = "iDContacto";
	public static String nombre = "nombre";
	public static String primerApellido = "primerApellido";
	public static String segundoApellido = "segundoApellido";
	public static String telefonoCelular = "telefonoCelular";
	public static String telefonoCasa = "telefonoCasa";
	public static String direccion = "direccion";

    private static final String DATABASE_NAME = "Agenda";
    private static final String TABLE_CONTACTO = "Contacto";
    private static final int DATABASE_VERSION = 1;

    //Fake information, testing purposes
    private static final String insert1 = String.format("INSERT INTO %s VALUES(24, \"Carlos\", \"Ramírez\", \"Vargas\", 89453265, 22657458, \"Santo Domingo de Heredia\");", TABLE_CONTACTO);
    private static final String insert2 = String.format("INSERT INTO %s VALUES(2, \"María\", \"Solano\", \"Fallas\", 65745124, 25346978, \"San Antonio de Desamparados\");", TABLE_CONTACTO);
    private static final String insert3 = String.format("INSERT INTO %s VALUES(32, \"Luis\", \"Aguilar\", \"Gómez\", 57642105, 20104591, \"Curridabat\");", TABLE_CONTACTO);
    private static final String insert4 = String.format("INSERT INTO %s VALUES(98, \"Mario\", \"Arias\", \"Mora\", 65986366, 23270491, \"Moravia\");", TABLE_CONTACTO);
    private static final String insert5 = String.format("INSERT INTO %s VALUES(10, \"Andrea\", \"Castro\", \"Estrada\", 88223340, 28047365, \"Liberia\");", TABLE_CONTACTO);
    private static final String insert6 = String.format("INSERT INTO %s VALUES(1, \"Juan\", \"Marín\", \"Guzmán\", 80453974, 23987854, \"Siquirres\");", TABLE_CONTACTO);
    private static final String insert7 = String.format("INSERT INTO %s VALUES(5, \"Ana\", \"Salas\", \"Ruíz\", 81693674, 27356945, \"Aserrí\");", TABLE_CONTACTO);


    private static final String CREATE_TABLE_CONTACTO = String.format("create table %s("
    		+ "%s INTEGER NOT NULL UNIQUE, "
    		+ "%s TEXT NOT NULL, "
    		+ "%s TEXT, "
    		+ "%s TEXT, "
            + "%s NUMERIC, "
            + "%s NUMERIC, "
            + "%s TEXT, "
            + "PRIMARY KEY(%s)"
            +");", TABLE_CONTACTO,
    		iDContacto, nombre, primerApellido, segundoApellido,telefonoCelular,
            telefonoCasa, direccion, iDContacto);

    private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

    public Database(Context ctx) {
		this.context = ctx;
		dbHelper = new DatabaseHelper(context);
    }
	
    public void dropDatabase() {
        context.deleteDatabase(DATABASE_NAME);

    }
    
	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}
	    @Override	
	    public void onCreate(SQLiteDatabase db) {
	    	try {
	    		db.execSQL(CREATE_TABLE_CONTACTO);
                insertarDatosIniciales(db);
	    	}
	    	catch(SQLException e) {
	    		Log.e("Database, onCreate: ", e.getMessage());
	    	}
	    }
	    
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	db.execSQL(String.format("DROP TABLE IF EXISTS %s", CREATE_TABLE_CONTACTO));
	    	onCreate(db);
	    }

        private void insertarDatosIniciales(SQLiteDatabase db){
            db.execSQL(insert1);
            db.execSQL(insert2);
            db.execSQL(insert3);
            db.execSQL(insert4);
            db.execSQL(insert5);
            db.execSQL(insert6);
            db.execSQL(insert7);
        }
    }
	
	public Database open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		return this;
	} 
	
	public void close() {
		dbHelper.close();
	}
		
    public boolean insertarContacto(int iDContacto, String nombre, String primerApellido, String segundoApellido,
                                 int telefonoCelular, int telefonoCasa, String direccion) {
    	try {
    		ContentValues values = new ContentValues();
	    	values.put(Database.iDContacto, iDContacto);
	    	values.put(Database.nombre, nombre);
            values.put(Database.primerApellido, primerApellido);
            values.put(Database.segundoApellido, segundoApellido);
            values.put(Database.telefonoCelular, telefonoCelular);
            values.put(Database.telefonoCasa, telefonoCasa);
            values.put(Database.direccion, direccion);
	    	db.insertOrThrow(TABLE_CONTACTO, null, values);
            values = null;
            return true;
    	}catch(SQLException e) {
    		Log.e("Database, insertarContacto: ",e.getMessage());
            return false;


        }
    	
    }

    public boolean editarContacto(int iDContactoOld, int iDContacto, String nombre, String primerApellido, String segundoApellido,
                                    int telefonoCelular, int telefonoCasa, String direccion) {
        try {
            ContentValues values = new ContentValues();
            values.put(Database.iDContacto, iDContacto);
            values.put(Database.nombre, nombre);
            values.put(Database.primerApellido, primerApellido);
            values.put(Database.segundoApellido, segundoApellido);
            values.put(Database.telefonoCelular, telefonoCelular);
            values.put(Database.telefonoCasa, telefonoCasa);
            values.put(Database.direccion, direccion);
            db.updateWithOnConflict(TABLE_CONTACTO, values, String.format("%s = %d", Database.iDContacto, iDContactoOld), null, SQLiteDatabase.CONFLICT_FAIL);
            values = null;
            return true;
        }catch(SQLException e) {
            Log.e("Database, insertarContacto: ",e.getMessage());
            return false;

        }

    }

    public Cursor getContactos(boolean allColumns){
        Cursor cursor;
        if(allColumns)
            cursor = db.rawQuery(String.format("SELECT * FROM %s order by %s;", TABLE_CONTACTO, primerApellido), null);
        else
            cursor = db.rawQuery(String.format("SELECT %s, %s, %s FROM %s order by %s;", iDContacto, nombre, primerApellido, TABLE_CONTACTO, primerApellido), null);

    	return cursor;
    }

    public Cursor getContacto(String nombre){
        Cursor cursor;
        cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%';", TABLE_CONTACTO, Database.nombre, nombre), null);
        return cursor;
    }

    public Cursor getContacto(int iDContacto){
        Cursor cursor;
        cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = %d;", TABLE_CONTACTO, Database.iDContacto, iDContacto), null);
        return cursor;
    }


    public boolean borrarContacto(int iDContacto){
        int r = db.delete(Database.TABLE_CONTACTO, String.format("%s = %d", Database.iDContacto, iDContacto), null);
        return r != 0;
    }
}
