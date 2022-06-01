package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Entrega;
import com.example.ifoodclone.model.Pagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaRecebimentosActivity extends AppCompatActivity {

    private List<Pagamento> pagamentoList = new ArrayList<>();

    private Pagamento dinEntrega = new Pagamento();
    private Pagamento dinRetirada = new Pagamento();
    private Pagamento cartaoCreditoEntrega = new Pagamento();
    private Pagamento cartaoCreditoRetirada = new Pagamento();
    private Pagamento cartaoCreditoApp = new Pagamento();


    private CheckBox cb_din_entrega;
    private CheckBox cb_din_retirada;
    private CheckBox cb_cc_entrega;
    private CheckBox cb_cc_retirada;
    private CheckBox cb_cc_app;

    private EditText edit_public_key;
    private EditText edit_access_token;

    private ProgressBar progressBar;
    private ImageButton ib_salvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_recebimentos);
        iniciaComponentes();
        configCliques();

        recuperaPagamentos();
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());

        // Dinheiro na entrega
        cb_din_entrega.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dinEntrega.setDescricao("Dinheiro na entrega");
            dinEntrega.setStatus(isChecked);
        });

        // Dinheiro na retirada
        cb_din_retirada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dinRetirada.setDescricao("Dinheiro na retirada");
            dinRetirada.setStatus(isChecked);
        });

        // Cartão de crédito na entrega
        cb_cc_entrega.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartaoCreditoEntrega.setDescricao("Cartão de crédito na entrega");
            cartaoCreditoEntrega.setStatus(isChecked);
        });

        // Cartão de crédito na retirada
        cb_cc_retirada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartaoCreditoRetirada.setDescricao("Cartão de crédito na retirada");
            cartaoCreditoRetirada.setStatus(isChecked);
        });
        // Cartão de crédito pelo app
        cb_cc_app.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartaoCreditoApp.setDescricao("Cartão de crédito pelo app");
            cartaoCreditoApp.setStatus(isChecked);
        });

        ib_salvar.setOnClickListener(v -> salvarPagamentos());
    }

    private void salvarPagamentos() {

        if(cb_din_entrega.isChecked()){
            if (!pagamentoList.contains(dinEntrega)) pagamentoList.add(dinEntrega);
        }

        if(cb_din_retirada.isChecked()){
            if (!pagamentoList.contains(dinRetirada)) pagamentoList.add(dinRetirada);
        }

        if(cb_cc_entrega.isChecked()){
            if (!pagamentoList.contains(cartaoCreditoEntrega)) pagamentoList.add(cartaoCreditoEntrega);
        }

        if(cb_cc_retirada.isChecked()){
            if (!pagamentoList.contains(cartaoCreditoRetirada)) pagamentoList.add(cartaoCreditoRetirada);
        }

        if(cb_cc_app.isChecked()){
            if (!pagamentoList.contains(cartaoCreditoApp)) pagamentoList.add(cartaoCreditoApp);
        }

        Pagamento.salvar(pagamentoList);
    }

    private void recuperaPagamentos() {
        pagamentoList.clear();
        DatabaseReference pagamentosRef = FirebaseHelper.getDatabaseReference()
                .child("recebimentos")
                .child(FirebaseHelper.getIdFirebase());
        pagamentosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pagamento pagamento = ds.getValue(Pagamento.class);
                        pagamentoList.add(pagamento);
                    }
                    configPagamentos();
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

    private void configPagamentos() {
        for (Pagamento pagamento : pagamentoList) {
            switch (pagamento.getDescricao()) {
                case "Dinheiro na entrega":
                    dinEntrega = pagamento;
                    cb_din_entrega.setChecked(pagamento.getStatus());
                    break;
                case "Dinheiro na retirada":
                    dinRetirada = pagamento;
                    cb_din_retirada.setChecked(pagamento.getStatus());
                    break;
                case "Cartão de crédito na entrega":
                    cartaoCreditoEntrega = pagamento;
                    cb_cc_entrega.setChecked(pagamento.getStatus());
                    break;
                case "Cartão de crédito na retirada":
                    cartaoCreditoRetirada = pagamento;
                    cb_cc_retirada.setChecked(pagamento.getStatus());
                    break;
                case "Cartão de crédito pelo app":
                    cartaoCreditoApp = pagamento;
                    cb_cc_app.setChecked(pagamento.getStatus());
                    break;
            }

        }
        configSalvar(false);
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

    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Formas de pagamento");

        cb_din_entrega = findViewById(R.id.cb_din_entrega);
        cb_din_retirada = findViewById(R.id.cb_din_retirada);
        cb_cc_entrega = findViewById(R.id.cb_cc_entrega);
        cb_cc_retirada = findViewById(R.id.cb_cc_retirada);
        cb_cc_app = findViewById(R.id.cb_cc_app);

        edit_public_key = findViewById(R.id.edit_public_key);
        edit_access_token = findViewById(R.id.edit_access_token);

        progressBar = findViewById(R.id.progressBar);
        ib_salvar = findViewById(R.id.ib_salvar);


    }
}