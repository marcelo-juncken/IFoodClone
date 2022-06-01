package com.example.ifoodclone.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.ifoodclone.R;

public class UsuarioFavoritosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_favoritos);

        iniciaComponentes();
        configCliques();
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes(){
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Favoritos");
    }
}