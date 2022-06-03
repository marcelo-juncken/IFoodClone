package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifoodclone.DAO.EmpresaDAO;
import com.example.ifoodclone.DAO.ItemPedidoDAO;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Categoria;
import com.example.ifoodclone.model.Empresa;
import com.example.ifoodclone.model.ItemPedido;
import com.example.ifoodclone.model.Produto;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EmpresaProdutoDetalhesActivity extends AppCompatActivity {

    private ImageView img_produto;
    private TextView text_nome;
    private TextView text_descricao;
    private TextView text_valor;
    private TextView text_valor_antigo;
    private TextView text_empresa;
    private TextView text_tempo;
    private TextView text_qtd;
    private TextView text_total_produto;

    private ImageButton btn_del;
    private ImageButton btn_add;
    private ConstraintLayout btn_adicionar;

    private Produto produto;
    private Empresa empresa;

    private EmpresaDAO empresaDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private int quantidade = 1;

    private AlertDialog dialog;

    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_produto_detalhes);

        empresaDAO = new EmpresaDAO(getBaseContext());
        itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            produto = (Produto) bundle.getSerializable("produtoSelecionado");
            recuperaEmpresa();
        }
        configCliques();
    }

    private void addItemCarrinho() {
        if (empresaDAO.getEmpresa() != null) {
            if (empresaDAO.getEmpresa().getId().equals(produto.getIdEmpresa())) {
                salvarProduto();
            } else {
                snackbarEsvaziarCarrinho();
            }
        } else {
            salvarProduto();
        }
    }

    public void snackbarEsvaziarCarrinho() {
        // Create the Snackbar
        LinearLayout.LayoutParams objLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        snackbar = Snackbar.make(btn_adicionar, "", Snackbar.LENGTH_INDEFINITE);
        // Get the Snackbar's layout view

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set snackbar layout params

        layout.setPadding(0, 0, 0, 0);

        // Inflate our custom view
        View snackView = getLayoutInflater().inflate(R.layout.snackbar_esvazia_carrinho, null);

        // Configure our custom view
        Button btn_fechar = snackView.findViewById(R.id.btn_fechar);

        Button btn_salvar = snackView.findViewById(R.id.btn_salvar);

        btn_fechar.setOnClickListener(v -> snackbar.dismiss());

        btn_salvar.setOnClickListener(v -> esvaziaCarrinho());

        // Add the view to the Snackbar's layout
        layout.addView(snackView, objLayoutParams);
        // Show the Snackbar
        snackbar.show();
    }

    private void esvaziaCarrinho() {
        empresaDAO.removerEmpresa();
        itemPedidoDAO.removerTodos();
        salvarProduto();
        snackbar.dismiss();
    }

    private void salvarProduto() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setValor(produto.getValor());
        itemPedido.setItem(produto.getNome());
        itemPedido.setQuantidade(quantidade);
        itemPedido.setIdItem(produto.getId());
        itemPedido.setUrlImagem(produto.getUrlImagem());

        itemPedidoDAO.salvar(itemPedido);

        if (empresaDAO.getEmpresa() == null) empresaDAO.salvar(empresa);
        snackbarProdutoAdicionado(itemPedido);
    }

    private void snackbarProdutoAdicionado(ItemPedido itemPedido) {
        Snackbar snackbarProdAdd = Snackbar.make(btn_adicionar, "Produto adicionado com sucesso!", Snackbar.LENGTH_LONG);
        snackbarProdAdd.setAction("Desfazer", v -> {
            desfazerProduto(itemPedido);
            Log.i("INFOTESTE", "snackbarProdutoAdicionado: " + itemPedido.getId() + " - " + itemPedido.getItem() + " - " +itemPedido.getItem());
        });

        TextView text_snack_bar = snackbarProdAdd.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        text_snack_bar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        text_snack_bar.setCompoundDrawablePadding(24);
        snackbarProdAdd.setActionTextColor(Color.parseColor("#EA1C2B"))
                .setTextColor(Color.parseColor("#FFFFFF"))
                .show();
    }

    private void desfazerProduto(ItemPedido itemPedido) {
        itemPedidoDAO.remover(itemPedidoDAO.getList().get(itemPedidoDAO.getList().size()-1).getId());
        if (itemPedidoDAO.getList().size() == 0) {
            empresaDAO.removerEmpresa();
        }
    }

    private void addQtdItem() {
        quantidade++;
        configQuantidade(false);
    }

    private void delQtdItem() {
        if (quantidade > 1) {
            quantidade--;
            configQuantidade(quantidade == 1);
        }
    }

    private void recuperaEmpresa() {
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(produto.getIdEmpresa());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    empresa = snapshot.getValue(Empresa.class);
                    configDados();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        Picasso.get().load(produto.getUrlImagem()).into(img_produto);
        text_nome.setText(produto.getNome());
        text_descricao.setText(produto.getDescricao());
        text_valor.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor())));
        if (produto.getValorAntigo() > 0) {
            text_valor_antigo.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValorAntigo())));
            text_valor_antigo.setPaintFlags(text_valor_antigo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            text_valor_antigo.setText("");
        }
        text_empresa.setText(empresa.getNome());
        text_tempo.setText(empresa.getTempoMinEntrega() + "-" + empresa.getTempoMaxEntrega() + " min");

        text_total_produto.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor() * quantidade)));
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_altera_qtd_produtos, null);
        builder.setView(view);

        EditText edit_quantidade = view.findViewById(R.id.edit_quantidade);
        Button btn_fechar = view.findViewById(R.id.btn_fechar);
        Button btn_salvar = view.findViewById(R.id.btn_salvar);

        btn_fechar.setOnClickListener(v -> dialog.dismiss());

        btn_salvar.setOnClickListener(v -> {
            if (!edit_quantidade.getText().toString().trim().isEmpty()) {
                int itemQuantidade = Integer.parseInt(edit_quantidade.getText().toString().trim());

                if (itemQuantidade >= 1) {
                    quantidade = itemQuantidade;
                    configQuantidade(itemQuantidade == 1);
                    dialog.dismiss();
                } else {
                    edit_quantidade.requestFocus();
                    edit_quantidade.setError("O valor tem que ser maior do que 0.");
                }
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void configQuantidade(Boolean valorUm) {
        if (valorUm) {
            btn_del.setColorFilter(Color.parseColor("#DCDCDC"));
            btn_del.setEnabled(false);
            btn_del.setClickable(false);
        } else {
            btn_del.setColorFilter(Color.parseColor("#EA1C2B"));
            btn_del.setEnabled(true);
            btn_del.setClickable(true);
        }
        text_qtd.setText(String.valueOf(quantidade));
        text_total_produto.setText(getString(R.string.text_valor, GetMask.getValor(produto.getValor() * quantidade)));
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        btn_add.setOnClickListener(v -> addQtdItem());
        btn_del.setOnClickListener(v -> delQtdItem());
        text_qtd.setOnClickListener(v -> showDialog());
        btn_adicionar.setOnClickListener(v -> addItemCarrinho());

    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Adicionar item");

        img_produto = findViewById(R.id.img_produto);
        text_nome = findViewById(R.id.text_nome);
        text_descricao = findViewById(R.id.text_descricao);
        text_valor = findViewById(R.id.text_valor);
        text_valor_antigo = findViewById(R.id.text_valor_antigo);
        text_empresa = findViewById(R.id.text_empresa);
        text_tempo = findViewById(R.id.text_tempo);
        text_qtd = findViewById(R.id.text_qtd);
        text_total_produto = findViewById(R.id.text_total_produto);
        btn_del = findViewById(R.id.btn_remover);
        btn_add = findViewById(R.id.btn_add);
        btn_adicionar = findViewById(R.id.btn_adicionar);

    }
}