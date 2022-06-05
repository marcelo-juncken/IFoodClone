package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Entrega;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmpresaEntregasActivity extends AppCompatActivity {

    private List<Entrega> entregaList = new ArrayList<>();
    private Entrega domicilio = new Entrega();
    private Entrega retirada = new Entrega();
    private Entrega outra = new Entrega();

    private CheckBox cb_domicilio;
    private CheckBox cb_retirada;
    private CheckBox cb_outra;

    private CurrencyEditText edit_domicilio;
    private CurrencyEditText edit_retirada;
    private CurrencyEditText edit_outra;

    private ImageButton ib_salvar;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_entregas);
        iniciaComponentes();
        configCliques();

        recuperaEntregas();


    }

    private void recuperaEntregas() {
        if (FirebaseHelper.getAutenticado()) {
            configSalvar(true);
            DatabaseReference recuperaRef = FirebaseHelper.getDatabaseReference()
                    .child("entregas")
                    .child(FirebaseHelper.getIdFirebase());
            recuperaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        entregaList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Entrega entrega = ds.getValue(Entrega.class);
                            entregaList.add(entrega);
                            configEntregas(entrega);
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
    }

    private void configEntregas(Entrega entrega) {
        switch (entrega.getDescricao()) {
            case "Domicilio":
                domicilio = entrega;
                edit_domicilio.setText(String.valueOf(domicilio.getTaxa() ));
                cb_domicilio.setChecked(domicilio.getStatus());
                break;

            case "Retirada":
                retirada = entrega;
                edit_retirada.setText(String.valueOf(retirada.getTaxa()));
                cb_retirada.setChecked(retirada.getStatus());
                break;

            case "Outra":
                outra = entrega;
                edit_outra.setText(String.valueOf(outra.getTaxa()));
                cb_outra.setChecked(outra.getStatus());
                break;
        }
        configSalvar(false);
    }

    private void validaEntregas() {

        entregaList.clear();

        configSalvar(true);


        domicilio.setTaxa((double) edit_domicilio.getRawValue()/100);
        domicilio.setStatus(cb_domicilio.isChecked());
        domicilio.setDescricao("Domicilio");

        retirada.setTaxa((double) edit_retirada.getRawValue()/100);
        retirada.setStatus(cb_retirada.isChecked());
        retirada.setDescricao("Retirada");

        outra.setTaxa((double) edit_outra.getRawValue()/100);
        outra.setStatus(cb_outra.isChecked());
        outra.setDescricao("Outra");

        entregaList.add(domicilio);
        entregaList.add(retirada);
        entregaList.add(outra);

        Entrega.salvar(entregaList);

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

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        ib_salvar.setOnClickListener(v -> validaEntregas());
    }


    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Formas de entrega");

        cb_domicilio = findViewById(R.id.cb_domicilio);
        cb_retirada = findViewById(R.id.cb_retirada);
        cb_outra = findViewById(R.id.cb_outra);

        edit_domicilio = findViewById(R.id.edit_domicilio);
        edit_domicilio.setLocale(new Locale("PT", "br"));

        edit_retirada = findViewById(R.id.edit_retirada);
        edit_retirada.setLocale(new Locale("PT", "br"));

        edit_outra = findViewById(R.id.edit_outra);
        edit_outra.setLocale(new Locale("PT", "br"));

        ib_salvar = findViewById(R.id.ib_salvar);
        progressBar = findViewById(R.id.progressBar);


    }
}