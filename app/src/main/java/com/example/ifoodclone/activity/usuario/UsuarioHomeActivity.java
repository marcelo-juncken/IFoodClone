package com.example.ifoodclone.activity.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ifoodclone.DAO.EmpresaDAO;
import com.example.ifoodclone.DAO.ItemPedidoDAO;
import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.empresa.EmpresaCardapioActivity;
import com.example.ifoodclone.adapter.EmpresasAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Empresa;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class UsuarioHomeActivity extends AppCompatActivity {


    private TextView textQtdItemSacola;
    private TextView textTotalCarrinho;
    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;
    private ConstraintLayout l_sacola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        int id = getIntent().getIntExtra("id", 0);
        if (id == 3){
            bottomNavigationView.setSelectedItemId(R.id.menu_pedidos);
        }


        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        empresaDAO = new EmpresaDAO(getBaseContext());

        iniciaComponentes();
        configCliques();


    }


    @Override
    public void onStart() {
        super.onStart();
        configSacola();
    }

    private void configSacola() {
        if (!itemPedidoDAO.getList().isEmpty()) {
            double totalPedido = itemPedidoDAO.getTotal() + empresaDAO.getEmpresa().getTaxaEntrega();

            l_sacola.setVisibility(View.VISIBLE);
            textQtdItemSacola.setText(String.valueOf(itemPedidoDAO.getList().size()));
            textTotalCarrinho.setText(getString(R.string.text_valor, GetMask.getValor(totalPedido)));
        } else {
            l_sacola.setVisibility(View.GONE);
        }
    }

    private void configCliques() {
        l_sacola.setOnClickListener(v -> startActivity(new Intent(this, CarrinhoActivity.class)));

    }




    private void iniciaComponentes() {
        textQtdItemSacola = findViewById(R.id.textQtdItemSacola);
        textTotalCarrinho = findViewById(R.id.textTotalCarrinho);
        l_sacola = findViewById(R.id.l_sacola);

    }

}