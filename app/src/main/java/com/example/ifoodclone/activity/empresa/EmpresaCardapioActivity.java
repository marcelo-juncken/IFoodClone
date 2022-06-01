package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.autenticacao.LoginActivity;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Categoria;
import com.example.ifoodclone.model.Empresa;
import com.example.ifoodclone.model.Favorito;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EmpresaCardapioActivity extends AppCompatActivity {

    private RecyclerView rv_categorias;
    private List<Categoria> categoriaList = new ArrayList<>();

    private ImageView img_empresa;
    private TextView text_nome;
    private TextView text_categoria;
    private TextView text_tempo_minimo;
    private TextView text_tempo_maximo;
    private TextView text_taxa_entrega;

    private ProgressBar progressBar;
    private TextView text_info;

    private LikeButton btn_like;
    private TextView text_toolbar;

    private Empresa empresa;

    private Favorito favorito = new Favorito();
    private List<String> favoritosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_cardapio);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empresa = (Empresa) bundle.getSerializable("empresaSelecionada");
            recuperaEmpresa();
        }

        configCliques();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaFavorito();
    }

    private void recuperaEmpresa() {
        progressBar.setVisibility(View.VISIBLE);
        text_toolbar.setVisibility(View.VISIBLE);

        Picasso.get().load(empresa.getUrlLogo()).into(img_empresa);
        text_nome.setText(empresa.getNome());
        text_categoria.setText(empresa.getCategoria());

        text_tempo_minimo.setText(String.valueOf(empresa.getTempoMinEntrega()));
        text_tempo_maximo.setText("-" + empresa.getTempoMaxEntrega() + " min");

        if (empresa.getTaxaEntrega() > 0) {
            text_taxa_entrega.setText(getString(R.string.text_valor, GetMask.getValor(empresa.getTaxaEntrega())));
        } else {
            text_taxa_entrega.setText("ENTREGA GRÁTIS");
            text_taxa_entrega.setTextColor(Color.parseColor("#2ED67E"));
        }

    }

    private void recuperaFavorito() {
        favoritosList.clear();
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference favoritosRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());
            favoritosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            favoritosList.add(ds.getValue(String.class));
                        }
                    }
                    btn_like.setLiked(favoritosList.contains(empresa.getId()));
                    progressBar.setVisibility(View.GONE);
                    text_toolbar.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    text_toolbar.setVisibility(View.GONE);

                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            text_toolbar.setVisibility(View.GONE);
        }
    }


    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());


        btn_like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                configFavorito();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                configFavorito();
            }
        });

    }

    private void configFavorito() {
        if (FirebaseHelper.getAutenticado()) {
            if (!favoritosList.contains(empresa.getId())) {
                favoritosList.add(empresa.getId());
            } else {
                favoritosList.remove(empresa.getId());
            }
            favorito.salvar(favoritosList);
        }else{
            btn_like.setLiked(false);
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void iniciaComponentes() {
        rv_categorias = findViewById(R.id.rv_categorias);

        img_empresa = findViewById(R.id.img_empresa);
        text_nome = findViewById(R.id.text_nome);
        text_categoria = findViewById(R.id.text_categoria);
        text_tempo_minimo = findViewById(R.id.text_tempo_minimo);
        text_tempo_maximo = findViewById(R.id.text_tempo_maximo);
        text_taxa_entrega = findViewById(R.id.text_taxa_entrega);

        progressBar = findViewById(R.id.progressBar);
        text_info = findViewById(R.id.text_info);

        btn_like = findViewById(R.id.btn_like);
        text_toolbar = findViewById(R.id.text_toolbar);
    }


}