package com.example.ifoodclone.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.ifoodclone.model.Endereco;

public class UsuarioFormEnderecoActivity extends AppCompatActivity {

    private EditText edit_endereco;
    private EditText edit_referencia;
    private EditText edit_bairro;
    private EditText edit_municipio;

    private ImageButton ib_salvar;
    private ProgressBar progressBar;

    private Endereco endereco;

    private TextView text_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_form_endereco);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            endereco = (Endereco) bundle.getSerializable("enderecoSelecionado");
            configEndereco();
        } else {
            configSalvar(false);
        }

        configCliques();
    }


    private void configEndereco() {
        edit_endereco.setText(endereco.getLogradouro());
        edit_referencia.setText(endereco.getReferencia());
        edit_municipio.setText(endereco.getMunicipio());
        edit_bairro.setText(endereco.getBairro());
        text_toolbar.setText("Editar Endereço");

        configSalvar(false);
    }

    private void validaDados() {
        String logradouro = edit_endereco.getText().toString().trim();
        String referencia = edit_referencia.getText().toString().trim();
        String bairro = edit_bairro.getText().toString().trim();
        String municipio = edit_municipio.getText().toString().trim();

        if (!logradouro.isEmpty()) {
            if (!referencia.isEmpty()) {
                if (!bairro.isEmpty()) {
                    if (!municipio.isEmpty()) {
                        configSalvar(true);
                        if (endereco == null) endereco = new Endereco();
                        ocultarTeclado();
                        endereco.setLogradouro(logradouro);
                        endereco.setMunicipio(municipio);
                        endereco.setBairro(bairro);
                        endereco.setReferencia(referencia);
                        endereco.salvar();
                        finish();

                        configSalvar(false);
                        ib_salvar.setColorFilter(Color.argb(255, 18, 120, 76));

                    } else {
                        edit_municipio.requestFocus();
                        edit_municipio.setError("Informe o município de entrega");
                    }
                } else {
                    edit_bairro.requestFocus();
                    edit_bairro.setError("Informe o bairro de entrega");
                }
            } else {
                edit_referencia.requestFocus();
                edit_referencia.setError("Informe uma referencia");
            }
        } else {
            edit_endereco.requestFocus();
            edit_endereco.setError("Informe o logradouro de entrega");
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

        edit_endereco.addTextChangedListener(new TextWatcher() {
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
        text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Adicionar Endereço");

        edit_endereco = findViewById(R.id.edit_endereco);
        edit_referencia = findViewById(R.id.edit_referencia);
        edit_bairro = findViewById(R.id.edit_bairro);
        edit_municipio = findViewById(R.id.edit_municipio);

        ib_salvar = findViewById(R.id.ib_salvar);
        progressBar = findViewById(R.id.progressBar);
    }
}