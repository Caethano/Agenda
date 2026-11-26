package com.caethano.agenda.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) de Contato: concentra todo o acesso ao banco
 * de dados para a entidade Contato (operações do CRUD).
 * Todas as consultas usam parâmetros ("id=?"), evitando SQL injection.
 */
public class ContatoDAO {

    private SQLiteDatabase database;
    private final DatabaseHelper databaseHelper;

    public ContatoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    private ContentValues paraValues(Contato contato) {
        ContentValues values = new ContentValues();
        values.put("nome", contato.getNome());
        values.put("email", contato.getEmail());
        values.put("telefone", contato.getTelefone());
        values.put("foto", contato.getFoto());
        return values;
    }

    private Contato doCursor(Cursor cursor) {
        Contato contato = new Contato();
        contato.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        contato.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
        contato.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        contato.setTelefone(cursor.getString(cursor.getColumnIndexOrThrow("telefone")));
        contato.setFoto(cursor.getString(cursor.getColumnIndexOrThrow("foto")));
        return contato;
    }

    // CREATE
    public long adicionarContato(Contato contato) {
        return database.insert(DatabaseHelper.TABELA, null, paraValues(contato));
    }

    // READ - todos, em ordem alfabética
    public List<Contato> listarContatos() {
        List<Contato> contatos = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABELA, null, null, null,
                null, null, "nome COLLATE NOCASE ASC");
        if (cursor.moveToFirst()) {
            do {
                contatos.add(doCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contatos;
    }

    // READ - por id
    public Contato buscaContatoPorId(int id) {
        Contato contato = null;
        Cursor cursor = database.query(DatabaseHelper.TABELA, null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            contato = doCursor(cursor);
        }
        cursor.close();
        return contato;
    }

    // UPDATE
    public int atualizarContato(Contato contato) {
        return database.update(DatabaseHelper.TABELA, paraValues(contato), "id=?",
                new String[]{String.valueOf(contato.getId())});
    }

    // DELETE
    public int apagarContato(int id) {
        return database.delete(DatabaseHelper.TABELA, "id=?",
                new String[]{String.valueOf(id)});
    }
}
