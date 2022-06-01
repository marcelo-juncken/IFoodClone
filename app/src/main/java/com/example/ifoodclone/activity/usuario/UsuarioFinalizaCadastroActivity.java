package com.example.ifoodclone.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Login;
import com.example.ifoodclone.model.Usuario;
import com.santalu.maskara.widget.MaskEditText;

public class UsuarioFinalizaCadastroActivity extends AppCompatActivity {

    private EditText edit_nome;
    private MaskEditText edit_telefone;
    private ProgressBar progressBar;

    private Usuario usuario;
    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_finaliza_cadastro);
        iniciaComponentes();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuario = (Usuario) bundle.getSerializable("usuario");
            login = (Login) bundle.getSerializable("login");
        }
    }

    public void validaDados(View view) {
        String nome = edit_nome.getText().toString().trim();
        String telefone = edit_telefone.getMasked();

        if (!nome.isEmpty()) {
            if (!telefone.isEmpty()) {
                if (edit_telefone.isDone()) {
                    if (usuario != null) {
                        ocultarTeclado();
                        progressBar.setVisibility(View.VISIBLE);
                        usuario.setNome(nome);
                        usuario.setTelefone(telefone);

                        finalizaCadastro();
                    }
                } else {
                    edit_telefone.requestFocus();
                    edit_telefone.setError("Telefone inválido.");
                }
            } else {
                edit_telefone.requestFocus();
                edit_telefone.setError("Insira seu telefone.");
            }
        } else {
            edit_nome.requestFocus();
            edit_nome.setError("Insira seu nome.");
        }
    }

    private void finalizaCadastro(){
        login.setAcesso(true);
        login.salvar(progressBar);
        usuario.salvar();

        finish();
        startActivity(new Intent(this, UsuarioHomeActivity.class));
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edit_nome.getWindowToken(), 0
        );
    }

    private void iniciaComponentes() {
        edit_nome = findViewById(R.id.edit_nome);
        edit_telefone = findViewById(R.id.edit_telefone);
        progressBar = findViewById(R.id.progressBar);
    }
}