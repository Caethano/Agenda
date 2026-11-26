package com.caethano.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caethano.agenda.controller.ContatoController;
import com.caethano.agenda.model.Contato;

/**
 * Formulário de contato (View).
 * - Aberto sem extra: inclusão (campos vazios)
 * - Aberto com extra "contato": alteração (campos preenchidos)
 */
public class AdicionarContatoActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editTelefone;
    private Button btnSalvar, btnSelecionarFoto;
    private ImageView imageViewFoto;

    private ContatoController contatoController;
    private Contato contatoEmEdicao; // null quando é inclusão
    private String fotoSelecionada = "";

    // seletor de imagens: abre a galeria/arquivos e devolve a URI escolhida
    private final ActivityResultLauncher<String[]> seletorFoto =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                try {
                    // mantém a permissão de leitura mesmo após reiniciar o app
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) { }
                fotoSelecionada = uri.toString();
                FotoUtil.exibir(imageViewFoto, fotoSelecionada);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_contato);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contatoController = new ContatoController(this);
        inicializarViews();
        carregarContatoRecebido();
    }

    public void inicializarViews() {
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editTelefone = findViewById(R.id.editTextTelefone);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSelecionarFoto = findViewById(R.id.btnSelecionarFoto);
        imageViewFoto = findViewById(R.id.imageView);

        btnSalvar.setOnClickListener(v -> salvarContato());
        btnSelecionarFoto.setOnClickListener(v -> seletorFoto.launch(new String[]{"image/*"}));
    }

    /** Se veio um contato pelo Intent, preenche o formulário para alteração. */
    private void carregarContatoRecebido() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("contato")) {
            contatoEmEdicao = (Contato) intent.getSerializableExtra("contato");
            if (contatoEmEdicao != null) {
                editNome.setText(contatoEmEdicao.getNome());
                editEmail.setText(contatoEmEdicao.getEmail());
                editTelefone.setText(contatoEmEdicao.getTelefone());
                fotoSelecionada = contatoEmEdicao.getFoto() == null ? "" : contatoEmEdicao.getFoto();
                FotoUtil.exibir(imageViewFoto, fotoSelecionada);
                btnSalvar.setText("Salvar alterações");
            }
        }
    }

    public void salvarContato() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        // validação: interrompe o salvamento se faltar campo obrigatório
        if (!contatoController.dadosValidos(nome, telefone)) {
            Toast.makeText(this, "Nome e telefone são obrigatórios", Toast.LENGTH_SHORT).show();
            if (nome.isEmpty()) editNome.setError("Obrigatório");
            if (telefone.isEmpty()) editTelefone.setError("Obrigatório");
            return;
        }

        if (contatoEmEdicao == null) {
            // INSERT
            contatoController.adicionarContato(nome, email, telefone, fotoSelecionada);
            Toast.makeText(this, "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE
            contatoController.atualizarContato(contatoEmEdicao.getId(), nome, email, telefone, fotoSelecionada);
            Toast.makeText(this, "Contato atualizado com sucesso", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contatoController.close();
    }
}
