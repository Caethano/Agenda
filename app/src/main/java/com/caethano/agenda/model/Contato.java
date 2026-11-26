package com.caethano.agenda.model;

import java.io.Serializable;

/**
 * Entidade Contato ("Bean"): mapeia a tabela "contato" do banco SQLite.
 * Implementa Serializable para poder ser enviada entre Activities via Intent.
 */
public class Contato implements Serializable {

    // propriedades - correspondem às colunas da tabela no BD
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String foto; // URI da imagem escolhida na galeria

    public Contato() {
    }

    // construtor para novos contatos (o id é gerado pelo BD)
    public Contato(String nome, String email, String telefone, String foto) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
    }

    // construtor para contatos já existentes (usado na alteração)
    public Contato(int id, String nome, String email, String telefone, String foto) {
        this(nome, email, telefone, foto);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    @Override
    public String toString() {
        return nome;
    }
}
