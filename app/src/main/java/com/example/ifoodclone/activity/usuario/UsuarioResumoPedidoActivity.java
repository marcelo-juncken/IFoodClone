package com.example.ifoodclone.activity.usuario;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ifoodclone.DAO.EmpresaDAO;
import com.example.ifoodclone.DAO.EntregaDAO;
import com.example.ifoodclone.DAO.ItemPedidoDAO;
import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.CarrinhoAdapter;
import com.example.ifoodclone.adapter.ProdutoCarrinhoAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Endereco;
import com.example.ifoodclone.model.ItemPedido;
import com.example.ifoodclone.model.Pagamento;
import com.example.ifoodclone.model.Pedido;
import com.example.ifoodclone.model.Produto;
import com.example.ifoodclone.model.StatusPedido;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;

import java.util.ArrayList;
import java.util.List;

public class UsuarioResumoPedidoActivity extends AppCompatActivity implements CarrinhoAdapter.OnClickListener {


    private static final int REQUEST_ENDERECO = 100;
    private static final int REQUEST_PAGAMENTO = 150;

    private RecyclerView rv_produtos;
    private CarrinhoAdapter carrinhoAdapter;

    private TextView text_endereco;
    private TextView text_forma_pagamento;
    private TextView text_subtotal;
    private TextView text_taxa_entrega;
    private TextView text_total;
    private Button btn_finalizar;

    private EntregaDAO entregaDAO;
    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;

    private Endereco endereco;
    private String pagamento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_resumo_pedido);

        entregaDAO = new EntregaDAO(getBaseContext());
        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        empresaDAO = new EmpresaDAO(getBaseContext());

        endereco = entregaDAO.getEndereco();
        iniciaComponentes();
        configCliques();
        configDados();
        configRv();

    }

    private void configRv() {
        rv_produtos.setLayoutManager(new LinearLayoutManager(this));
        rv_produtos.setHasFixedSize(true);
        carrinhoAdapter = new CarrinhoAdapter(itemPedidoDAO.getList(), getBaseContext(), this);
        rv_produtos.setAdapter(carrinhoAdapter);
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        findViewById(R.id.btn_finalizar).setOnClickListener(v -> finalizarPedido());
    }

    private void finalizarPedido() {

        double subTotal = itemPedidoDAO.getTotal();
        double taxaEntrega = empresaDAO.getEmpresa().getTaxaEntrega();
        double total = 0;

        Pedido pedido = new Pedido();

        pedido.setIdCliente(FirebaseHelper.getIdFirebase());
        pedido.setIdEmpresa(empresaDAO.getEmpresa().getId());
        pedido.setItemPedidoList(itemPedidoDAO.getList());
        pedido.setFormaPagamento(entregaDAO.getEntrega().getFormaPagamento());
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setSubTotal(subTotal);
        pedido.setTotalPedido(taxaEntrega + subTotal);
        pedido.setStatusPedido(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(entregaDAO.getEndereco());

        pedido.salvar();

        itemPedidoDAO.limparCarrinho();

        Intent intent = new Intent(this, UsuarioHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("id", 3);
        startActivity(intent);
    }

    private void configDados() {
        if (endereco == null) {
            endereco = entregaDAO.getEndereco();
        }
        if (pagamento == null) {
            pagamento = entregaDAO.getEntrega().getFormaPagamento();
        }
        StringBuilder enderecoCompleto = new StringBuilder()
                .append(endereco.getLogradouro())
                .append("\n")
                .append(endereco.getBairro())
                .append(", ")
                .append(endereco.getMunicipio())
                .append("\n")
                .append(endereco.getReferencia());
        text_endereco.setText(enderecoCompleto);

        text_forma_pagamento.setText(pagamento);
        configSaldo();

    }

    private void configSaldo() {
        double subTotal = 0;
        double taxaEntrega = 0;
        double total = 0;

        if (!itemPedidoDAO.getList().isEmpty()) {
            subTotal = itemPedidoDAO.getTotal();
            taxaEntrega = empresaDAO.getEmpresa().getTaxaEntrega();
            total = subTotal + taxaEntrega;
        }

        text_subtotal.setText(getString(R.string.text_valor, GetMask.getValor(subTotal)));
        text_taxa_entrega.setText(getString(R.string.text_valor, GetMask.getValor(taxaEntrega)));
        text_total.setText(getString(R.string.text_valor, GetMask.getValor(total)));

    }

    public void alterarEndereco(View view) {
        Intent intent = new Intent(this, UsuarioSelecionaEnderecoActivity.class);
        startActivityForResult(intent, REQUEST_ENDERECO);
    }

    public void alterarPagamento(View view) {
        Intent intent = new Intent(this, UsuarioSelecionaPagamentoActivity.class);
        startActivityForResult(intent, REQUEST_PAGAMENTO);
    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Resumo pedido");

        rv_produtos = findViewById(R.id.rv_produtos);
        text_endereco = findViewById(R.id.text_endereco);
        text_forma_pagamento = findViewById(R.id.text_forma_pagamento);
        text_subtotal = findViewById(R.id.text_subtotal);
        text_taxa_entrega = findViewById(R.id.text_taxa_entrega);
        text_total = findViewById(R.id.text_total);
        btn_finalizar = findViewById(R.id.btn_finalizar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ENDERECO) {
                endereco = (Endereco) data.getExtras().getSerializable("enderecoSelecionado");
                entregaDAO.atualizarEndereco(endereco);
                configDados();
            } else if (requestCode == REQUEST_PAGAMENTO) {
                Pagamento pagamentoSelecionado = (Pagamento) data.getExtras().getSerializable("pagamentoSelecionado");
                pagamento = pagamentoSelecionado.getDescricao();
                entregaDAO.salvarPagamento(pagamento);
                configDados();
            }
        }
    }

    @Override
    public void OnClick(ItemPedido itemPedido) {

    }
}