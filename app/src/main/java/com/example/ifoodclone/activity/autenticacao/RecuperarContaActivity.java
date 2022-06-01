package com.example.ifoodclone.activity.autenticacao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.ifoodclone.R;

public class RecuperarContaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_conta);

        configCliques();
        iniciaComponentes();
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes(){
        TextView text_titulo = findViewById(R.id.text_toolbar);
        text_titulo.setText("Recuperar conta");
    }
}