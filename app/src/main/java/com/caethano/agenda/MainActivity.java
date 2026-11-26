package com.caethano.agenda;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caethano.agenda.controller.ContatoController;
import com.caethano.agenda.model.Contato;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela principal (View): lista os contatos salvos no SQLite.
 * - Clique no botão +       -> abre o formulário vazio (inclusão)
 * - Clique em um contato    -> abre o formulário preenchido (alteração)
 * - Clique longo no contato -> confirmação de exclusão
 * - Menu "Meu nome"         -> salva o nome do dono da agenda no SharedPreferences
 */
public class MainActivity extends AppCompatActivity {

    private static final String ARQUIVO_PREFERENCIAS = "preferencias_agenda";
    private static final String CHAVE_NOME = "nome";

    private Toolbar toolbar;
    private ListView listView;
    private TextView textVazio;
    private FloatingActionButton fabAdicionar;

    private final List<Contato> contatos = new ArrayList<>();
    private ArrayAdapter<Contato> adapter;
    private ContatoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        controller = new ContatoController(this);
        inicializarViews();
        configurarAdapter();
        configurarListeners();
        exibirNomeSalvo();
    }

    private void inicializarViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);
        textVazio = findViewById(R.id.textVazio);
        fabAdicionar = findViewById(R.id.fabAdicionar);

        fabAdicionar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AdicionarContatoActivity.class)));
    }

    /** O ArrayAdapter "transforma" cada Contato em um item visual usando list_item.xml */
    private void configurarAdapter() {
        adapter = new ArrayAdapter<Contato>(this, R.layout.list_item, contatos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }
                Contato contato = getItem(position);
                TextView textNome = convertView.findViewById(R.id.textNomeLista);
                TextView textTelefone = convertView.findViewById(R.id.textTelLista);
                ImageView imageFoto = convertView.findViewById(R.id.imageView2);

                textNome.setText(contato.getNome());
                textTelefone.setText(contato.getTelefone());
                FotoUtil.exibir(imageFoto, contato.getFoto());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    private void carregarContatos() {
        contatos.clear();
        contatos.addAll(controller.listarContatos());
        adapter.notifyDataSetChanged();
        textVazio.setVisibility(contatos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void configurarListeners() {
        // clique curto: alterar contato
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            Intent intent = new Intent(MainActivity.this, AdicionarContatoActivity.class);
            intent.putExtra("contato", contato);
            startActivity(intent);
        });

        // clique longo: excluir contato
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação de Exclusão")
                    .setMessage("Deseja realmente excluir o contato " + contato.getNome() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        controller.apagarContato(contato.getId());
                        carregarContatos();
                        Toast.makeText(this, "Contato excluído", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
    }

    // ---------- SharedPreferences: nome do dono da agenda ----------

    private void exibirNomeSalvo() {
        SharedPreferences preferencias = getSharedPreferences(ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        if (preferencias.contains(CHAVE_NOME)) {
            toolbar.setSubtitle("Agenda de " + preferencias.getString(CHAVE_NOME, ""));
        } else {
            toolbar.setSubtitle("Defina seu nome no menu ⋮");
        }
    }

    private void editarNome() {
        SharedPreferences preferencias = getSharedPreferences(ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        EditText editNome = new EditText(this);
        editNome.setHint("Seu nome");
        editNome.setText(preferencias.getString(CHAVE_NOME, ""));

        new AlertDialog.Builder(this)
                .setTitle("Meu nome")
                .setView(editNome)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = editNome.getText().toString().trim();
                    SharedPreferences.Editor editor = preferencias.edit();
                    if (nome.isEmpty()) {
                        editor.remove(CHAVE_NOME);
                    } else {
                        editor.putString(CHAVE_NOME, nome);
                    }
                    editor.commit();
                    exibirNomeSalvo();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_meu_nome) {
            editarNome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarContatos(); // ao voltar do formulário, recarrega a lista
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }
}
