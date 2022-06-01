package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Endereco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EmpresaEnderecoActivity extends AppCompatActivity {

    private EditText edit_logradouro;
    private EditText edit_bairro;
    private EditText edit_municipio;

    private ImageButton ib_salvar;
    private ProgressBar progressBar;

    private Endereco endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_endereco);
        iniciaComponentes();
        configCliques();
        recuperaEndereco();
    }

    private void recuperaEndereco() {
        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());
        enderecoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        endereco = ds.getValue(Endereco.class);
                        configEndereco();
                    }
                } else {
                    configSalvar(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                configSalvar(false);
            }
        });
    }

    private void configEndereco() {
        edit_logradouro.setText(endereco.getLogradouro());
        edit_municipio.setText(endereco.getMunicipio());
        edit_bairro.setText(endereco.getBairro());
        configSalvar(false);
        ib_salvar.setColorFilter(Color.argb(255, 0, 0, 0));
    }

    private void validaDados() {
        String logradouro = edit_logradouro.getText().toString().trim();
        String bairro = edit_bairro.getText().toString().trim();
        String municipio = edit_municipio.getText().toString().trim();

        if (!logradouro.isEmpty()) {
            if (!bairro.isEmpty()) {
                if (!municipio.isEmpty()) {
                    configSalvar(true);
                    if (endereco == null) endereco = new Endereco();
                    ocultarTeclado();
                    endereco.setLogradouro(logradouro);
                    endereco.setMunicipio(municipio);
                    endereco.setBairro(bairro);
                    endereco.salvar();

                    configSalvar(false);
                    ib_salvar.setColorFilter(Color.argb(255, 18, 120, 76));

                } else {
                    edit_municipio.requestFocus();
                    edit_municipio.setError("Informe o município da loja");
                }
            } else {
                edit_bairro.requestFocus();
                edit_bairro.setError("Informe o bairro da loja");
            }
        } else {
            edit_logradouro.requestFocus();
            edit_logradouro.setError("Informe o logradouro da loja");
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
                edit_bairro.getWindowToken(), 0
        );
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        ib_salvar.setOnClickListener(v -> validaDados());

        edit_bairro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ib_salvar.setColorFilter(Color.argb(255, 255, 0, 0));
            }
        });

        edit_municipio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ib_salvar.setColorFilter(Color.argb(255, 255, 0, 0));
            }
        });

        edit_logradouro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ib_salvar.setColorFilter(Color.argb(255, 255, 0, 0));
            }
        });


    }

    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Meu Endereço");

        edit_logradouro = findViewById(R.id.edit_logradouro);
        edit_bairro = findViewById(R.id.edit_bairro);
        edit_municipio = findViewById(R.id.edit_municipio);
        ib_salvar = findViewById(R.id.ib_salvar);
        progressBar = findViewById(R.id.progressBar);
    }
}