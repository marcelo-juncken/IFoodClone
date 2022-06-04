package com.example.ifoodclone.activity.usuario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ifoodclone.DAO.EmpresaDAO;
import com.example.ifoodclone.DAO.EntregaDAO;
import com.example.ifoodclone.DAO.ItemPedidoDAO;
import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.autenticacao.LoginActivity;
import com.example.ifoodclone.activity.empresa.EmpresaProdutoDetalhesActivity;
import com.example.ifoodclone.adapter.CarrinhoAdapter;
import com.example.ifoodclone.adapter.ProdutoCarrinhoAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Endereco;
import com.example.ifoodclone.model.ItemPedido;
import com.example.ifoodclone.model.Pagamento;
import com.example.ifoodclone.model.Produto;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarrinhoActivity extends AppCompatActivity implements CarrinhoAdapter.OnClickListener, ProdutoCarrinhoAdapter.OnClickListener {

    private static final int REQUEST_LOGIN = 50;
    private static final int REQUEST_ENDERECO = 100;
    private static final int REQUEST_PAGAMENTO = 150;

    private final List<Produto> produtoList = new ArrayList<>();

    private RecyclerView rv_produtos;
    private CarrinhoAdapter carrinhoAdapter;
    private RecyclerView rv_add_mais;
    private ProdutoCarrinhoAdapter produtoCarrinhoAdapter;

    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;
    private EntregaDAO entregaDAO;

    private TextView text_logradouro;
    private TextView text_referencia;

    private Button btn_add_mais;
    private TextView text_subtotal;
    private TextView text_taxa_entrega;
    private TextView text_total;
    private TextView text_escolher;
    private TextView text_forma_pagamento;
    private Button btn_continuar;

    private LinearLayout ll_endereco;

    private Endereco endereco = null;

    private Pagamento pagamento;

    private BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        empresaDAO = new EmpresaDAO(getBaseContext());
        entregaDAO = new EntregaDAO(getBaseContext());
        iniciaComponentes();
        configCliques();

        configRv();
        recuperaEndereco();
        recuperaIdsItensAddMais();
        configSaldoCarrinho();
    }

    private void showBottomSheet(){
View view = getLayoutInflater().inflate(R.layout.bottom_sheet_item_carrinho, null);
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void configSaldoCarrinho(){
        double subTotal = 0;
        double taxaEntrega = 0;
        double total = 0;

        if (!itemPedidoDAO.getList().isEmpty()){
            subTotal = itemPedidoDAO.getTotal();
            taxaEntrega = empresaDAO.getEmpresa().getTaxaEntrega();
            total = subTotal + taxaEntrega;
        }

        text_subtotal.setText(getString(R.string.text_valor, GetMask.getValor(subTotal)));
        text_taxa_entrega.setText(getString(R.string.text_valor, GetMask.getValor(taxaEntrega)));
        text_total.setText(getString(R.string.text_valor, GetMask.getValor(total)));

    }

    private void recuperaEndereco() {
        if (FirebaseHelper.getAutenticado()) {
            if (endereco == null) {
                DatabaseReference enderecosRef = FirebaseHelper.getDatabaseReference()
                        .child("enderecos")
                        .child(FirebaseHelper.getIdFirebase());
                enderecosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                endereco = ds.getValue(Endereco.class);
                            }

                            if (entregaDAO.getEndereco() == null) {
                                entregaDAO.salvarEndereco(endereco);
                            } else {
                                entregaDAO.atualizarEndereco(endereco);
                            }
                        }
                        configEndereco();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        } else {
            configEndereco();
        }
    }

    private void recuperaIdsItensAddMais() {
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(empresaDAO.getEmpresa().getId());
        addMaisRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> idsItensList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idProduto = ds.getValue(String.class);
                        if (itemPedidoDAO.getList().stream().noneMatch(o -> o.getIdItem().equals(idProduto))) {
                            idsItensList.add(idProduto);
                        }
                    }
                    recuperaProdutos(idsItensList);
                } else {
                    findViewById(R.id.l_peca_mais).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configEndereco() {
        if (endereco != null) {
            text_logradouro.setText(endereco.getLogradouro());
            text_referencia.setText(endereco.getReferencia());
            ll_endereco.setVisibility(View.VISIBLE);
        }
        configStatus();

    }

    private void configPagamento() {
        text_forma_pagamento.setText(pagamento.getDescricao());
        text_forma_pagamento.setTextColor(Color.parseColor("#000000"));
        configStatus();
    }

    private void configStatus() {
        if (FirebaseHelper.getAutenticado()) {
            if (endereco == null) {
                btn_continuar.setText("Selecione o endereço");
            } else {
                if (pagamento == null) {
                    btn_continuar.setText("Escolher a forma de pagamento");
                } else {
                    text_escolher.setText("Trocar");
                    btn_continuar.setText("Continuar");
                }
            }
        } else {
            btn_continuar.setText("Fazer o login");
        }
    }

    private void recuperaProdutos(List<String> idsItensList) {
        DatabaseReference produtosRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(empresaDAO.getEmpresa().getId());
        produtosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Produto produto = ds.getValue(Produto.class);
                    if (idsItensList.contains(produto.getId())) produtoList.add(produto);
                }

                if (produtoList.size() == 0) {
                    findViewById(R.id.l_peca_mais).setVisibility(View.GONE);
                } else {
                    Collections.reverse(produtoList);
                    produtoCarrinhoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        findViewById(R.id.btn_esvaziar).setOnClickListener(v -> {
            itemPedidoDAO.limparCarrinho();
            finish();
        });
        ll_endereco.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuarioSelecionaEnderecoActivity.class);
            startActivityForResult(intent, REQUEST_ENDERECO);
        });
        btn_add_mais.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });

        btn_continuar.setOnClickListener(v -> continuar());

        text_escolher.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuarioSelecionaPagamentoActivity.class);
            startActivityForResult(intent, REQUEST_PAGAMENTO);
        });
    }

    private void continuar() {
        if (FirebaseHelper.getAutenticado()) {
            if (endereco == null) {
                Intent intent = new Intent(this, UsuarioSelecionaEnderecoActivity.class);
                startActivityForResult(intent, REQUEST_ENDERECO);
            } else if (pagamento == null) {
                Intent intent = new Intent(this, UsuarioSelecionaPagamentoActivity.class);
                startActivityForResult(intent, REQUEST_PAGAMENTO);
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }

    }

    private void configRv() {
        rv_produtos.setLayoutManager(new LinearLayoutManager(this));
        rv_produtos.setHasFixedSize(true);
        carrinhoAdapter = new CarrinhoAdapter(itemPedidoDAO.getList(), getBaseContext(), this);
        rv_produtos.setAdapter(carrinhoAdapter);
        rv_add_mais.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_add_mais.setHasFixedSize(true);
        produtoCarrinhoAdapter = new ProdutoCarrinhoAdapter(produtoList, getBaseContext(), this);
        rv_add_mais.setAdapter(produtoCarrinhoAdapter);

    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Carrinho");

        rv_produtos = findViewById(R.id.rv_produtos);
        rv_add_mais = findViewById(R.id.rv_add_mais);
        text_logradouro = findViewById(R.id.text_logradouro);
        text_referencia = findViewById(R.id.text_referencia);
        btn_add_mais = findViewById(R.id.btn_add_mais);
        text_subtotal = findViewById(R.id.text_subtotal);
        text_taxa_entrega = findViewById(R.id.text_taxa_entrega);
        text_total = findViewById(R.id.text_total);
        text_escolher = findViewById(R.id.text_escolher);
        btn_continuar = findViewById(R.id.btn_continuar);
        text_forma_pagamento = findViewById(R.id.text_forma_pagamento);

        ll_endereco = findViewById(R.id.ll_endereco);

    }

    @Override
    public void OnClick(ItemPedido itemPedido) {

    }

    @Override
    public void OnClick(Produto produto) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                recuperaEndereco();
            } else if (requestCode == REQUEST_ENDERECO) {
                endereco = (Endereco) data.getSerializableExtra("enderecoSelecionado");
                if (entregaDAO.getEndereco() == null) {
                    entregaDAO.salvarEndereco(endereco);
                } else {
                    entregaDAO.atualizarEndereco(endereco);
                }
                configEndereco();
            } else if (requestCode == REQUEST_PAGAMENTO) {
                pagamento = (Pagamento) data.getSerializableExtra("pagamentoSelecionado");
                configPagamento();
                entregaDAO.salvarPagamento(pagamento.getDescricao());
            }
        }
    }
}