package com.caethano.agenda.controller;

import android.content.Context;

import com.caethano.agenda.model.Contato;
import com.caethano.agenda.model.ContatoDAO;

import java.util.List;

/**
 * Controller de Contatos (camada de negócio do MVC).
 * Faz a intermediação entre as Activities (View) e o DAO (Model),
 * aplicando as regras de negócio antes de acessar o banco.
 */
public class ContatoController {

    private final ContatoDAO contatoDAO;

    public ContatoController(Context context) {
        contatoDAO = new ContatoDAO(context);
        contatoDAO.open();
    }

    /** Regra de negócio: nome e telefone são obrigatórios. */
    public boolean dadosValidos(String nome, String telefone) {
        return nome != null && !nome.trim().isEmpty()
                && telefone != null && !telefone.trim().isEmpty();
    }

    // create
    public long adicionarContato(String nome, String email, String telefone, String foto) {
        if (!dadosValidos(nome, telefone)) return -1;
        Contato contato = new Contato(nome.trim(), email.trim(), telefone.trim(), foto);
        return contatoDAO.adicionarContato(contato);
    }

    // read
    public List<Contato> listarContatos() {
        return contatoDAO.listarContatos();
    }

    public Contato buscarContato(int id) {
        return contatoDAO.buscaContatoPorId(id);
    }

    // update
    public int atualizarContato(int id, String nome, String email, String telefone, String foto) {
        if (!dadosValidos(nome, telefone)) return 0;
        Contato contato = new Contato(id, nome.trim(), email.trim(), telefone.trim(), foto);
        return contatoDAO.atualizarContato(contato);
    }

    // delete
    public void apagarContato(int id) {
        contatoDAO.apagarContato(id);
    }

    public void close() {
        contatoDAO.close();
    }
}
