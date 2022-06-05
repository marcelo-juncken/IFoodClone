package com.example.ifoodclone.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.DAO.EmpresaDAO;
import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.EnderecoAdapter;
import com.example.ifoodclone.adapter.SelecionaEnderecoAdapter;
import com.example.ifoodclone.adapter.SelecionaPagamentoAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Endereco;
import com.example.ifoodclone.model.Pagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSelecionaPagamentoActivity extends AppCompatActivity implements SelecionaPagamentoAdapter.OnClickListener {

    private RecyclerView rv_pagamentos;
    private SelecionaPagamentoAdapter selecionaPagamentoAdapter;
    private final List<Pagamento> pagamentoList = new ArrayList<>();

    private TextView text_info;
    private ProgressBar progressBar;

    private EmpresaDAO empresaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_seleciona_pagamento);

        empresaDAO = new EmpresaDAO(getBaseContext());
        iniciaComponentes();
        configCliques();
        configRV();
        recuperaPagamentos();
    }

    private void recuperaPagamentos() {
        pagamentoList.clear();
        DatabaseReference pagamentosRef = FirebaseHelper.getDatabaseReference()
                .child("recebimentos")
                .child(empresaDAO.getEmpresa().getId());
        pagamentosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pagamento pagamento = ds.getValue(Pagamento.class);
                        if (pagamento.getStatus()) {
                            pagamentoList.add(pagamento);
                        }
                    }
                    text_info.setText("");
                } else {
                    text_info.setText("Nenhuma forma de pagamento habilitada.");
                }
                progressBar.setVisibility(View.GONE);
                selecionaPagamentoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void configRV() {
        rv_pagamentos.setLayoutManager(new LinearLayoutManager(this));
        rv_pagamentos.setHasFixedSize(true);
        selecionaPagamentoAdapter = new SelecionaPagamentoAdapter(pagamentoList,this);
        rv_pagamentos.setAdapter(selecionaPagamentoAdapter);

    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes(){
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Formas de pagamento");

        rv_pagamentos = findViewById(R.id.rv_pagamentos);
        text_info = findViewById(R.id.text_info);
        progressBar = findViewById(R.id.progressBar);

    }

    @Override
    public void OnClicK(Pagamento pagamento) {
        Intent intent = new Intent();
        intent.putExtra("pagamentoSelecionado", pagamento);
        setResult(RESULT_OK,intent);
        finish();
    }
}