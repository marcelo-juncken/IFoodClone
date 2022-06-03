package com.example.ifoodclone.activity.empresa;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.AddMaisAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.AddMais;
import com.example.ifoodclone.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmpresaPecaMaisActivity extends AppCompatActivity implements AddMaisAdapter.OnClickListener {

    private RecyclerView rv_produtos;
    private AddMaisAdapter addMaisAdapter;

    private ProgressBar progressBar;

    private List<Produto> produtoList = new ArrayList<>();
    private List<String> addMaisList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_peca_mais);

        iniciaComponentes();
        recuperaProdutos();
        configCliques();
        configRV();

    }

    private void recuperaProdutos() {
        produtoList.clear();
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                    .child("produtos")
                    .child(FirebaseHelper.getIdFirebase());
            produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Produto produto = ds.getValue(Produto.class);
                            produtoList.add(produto);
                        }
                        recuperaItens();
                    }
                    Collections.reverse(produtoList);
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void recuperaItens() {
        addMaisList.clear();
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(FirebaseHelper.getIdFirebase());
        addMaisRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        addMaisList.add(ds.getValue(String.class));
                    }
                    configProdutos();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configProdutos() {
        for (Produto produto : produtoList) {
            if (addMaisList.contains(produto.getId())) {
                produto.setAddMais(true);
            }
        }
        addMaisAdapter.notifyDataSetChanged();
    }

    private void configRV() {
        rv_produtos.setLayoutManager(new LinearLayoutManager(this));
        rv_produtos.setHasFixedSize(true);
        addMaisAdapter = new AddMaisAdapter(produtoList, addMaisList, this, this); // --------- no lugar de anuncio List, aqui passa o endereco da lista. pode ser EstadosList.getList(), por exemplo
        rv_produtos.setAdapter(addMaisAdapter);
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Peça também");

        progressBar = findViewById(R.id.progressBar);
        rv_produtos = findViewById(R.id.rv_produtos);
    }

    @Override
    public void OnClick(String idProduto, Boolean status) {
        if (status) {
            if (!addMaisList.contains(idProduto)) {
                addMaisList.add(idProduto);
            }
        } else {
            addMaisList.remove(idProduto);
        }
        AddMais.salvar(addMaisList);
    }
}