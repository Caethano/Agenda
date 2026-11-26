package com.caethano.agenda.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Classe auxiliar do SQLite: cria o banco e trata as migrações de versão.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contatos.db";
    private static final int DB_VERSION = 2;

    public static final String TABELA = "contato";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // executado apenas na primeira vez, quando o banco ainda não existe
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABELA + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT, " +
                "telefone TEXT NOT NULL, " +
                "foto TEXT);");
    }

    // migrations: executado quando o banco existe e a versão aumentou
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // versão 1 -> 2: inclusão da coluna foto, preservando os contatos já cadastrados
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABELA + " ADD COLUMN foto TEXT");
        }
    }
}
