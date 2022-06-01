package com.example.ifoodclone.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Empresa;
import com.example.ifoodclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskara.widget.MaskEditText;

public class UsuarioPerfilActivity extends AppCompatActivity {

    private EditText edit_nome;
    private MaskEditText edit_telefone;
    private EditText edit_email;

    private ProgressBar progressBar;
    private ImageButton ib_salvar;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_perfil);

        iniciaComponentes();
        configCliques();

        recuperaDados();
    }



    private void recuperaDados() {
        if (FirebaseHelper.getAutenticado()) {
            configSalvar(true);
            DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                    .child("usuarios")
                    .child(FirebaseHelper.getIdFirebase());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        usuario = snapshot.getValue(Usuario.class);
                        configDados();
                    }else{
                        configSalvar(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    configSalvar(false);
                }
            });
        }
    }

    private void configDados() {
        edit_nome.setText(usuario.getNome());
        edit_telefone.setText(usuario.getTelefone());
        edit_email.setText(usuario.getEmail());

        progressBar.setVisibility(View.GONE);

    }


    private void validaDados() {
        String nome = edit_nome.getText().toString().trim();
        String telefone = edit_telefone.getMasked();

        if(!nome.isEmpty()){
            if(edit_telefone.isDone()){

                if(usuario == null) usuario = new Usuario();
                ocultarTeclado();
                configSalvar(true);
                usuario.setNome(nome);
                usuario.setTelefone(telefone);
                usuario.salvar();
                progressBar.setVisibility(View.GONE);
            }else{
                edit_telefone.requestFocus();
                edit_telefone.setError("Informe seu número de telefone");
            }
        }else{
            edit_nome.requestFocus();
            edit_nome.setError("Informe seu nome");
        }
    }

    private void configSalvar(boolean progress) {
        if (progress) {
            progressBar.setVisibility(View.VISIBLE);
            ib_salvar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            ib_salvar.setVisibility(View.VISIBLE);
        }
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edit_nome.getWindowToken(), 0
        );
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        ib_salvar.setOnClickListener(v -> validaDados());

        diferenciaTrocas();
    }

    private void diferenciaTrocas(){
        edit_nome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!edit_nome.getText().toString().equals(usuario.getNome()) ||
                        !edit_telefone.getMasked().equals(usuario.getTelefone())){
                    ib_salvar.setVisibility(View.VISIBLE);
                }else{
                     ib_salvar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_telefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!edit_nome.getText().toString().equals(usuario.getNome()) ||
                        !edit_telefone.getMasked().equals(usuario.getTelefone())){
                    ib_salvar.setVisibility(View.VISIBLE);
                }else{
                    ib_salvar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Perfil");

        edit_nome = findViewById(R.id.edit_nome);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_email = findViewById(R.id.edit_email);

        progressBar = findViewById(R.id.progressBar);
        ib_salvar = findViewById(R.id.ib_salvar);
    }
}