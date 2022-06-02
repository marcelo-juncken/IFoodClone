package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.CategoriaAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Categoria;
import com.example.ifoodclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EmpresaCategoriasActivity extends AppCompatActivity implements CategoriaAdapter.OnClickListener {

    private SwipeableRecyclerView rv_categorias;
    private CategoriaAdapter categoriaAdapter;
    private List<Categoria> categoriaList = new ArrayList<>();

    private ProgressBar progressBar;

    private TextView text_info;

    private AlertDialog dialog;

    private Categoria categoriaSelecionada;
    private int categoriaIndex = 0;
    private Boolean novaCategoria = true;

    private int acesso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_categorias);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            acesso = bundle.getInt("acesso");
        }

        iniciaComponentes();

        configCliques();

        recuperaCategorias();
        configRV();
    }

    private void recuperaCategorias() {
        if (FirebaseHelper.getAutenticado()) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference categoriasRef = FirebaseHelper.getDatabaseReference()
                    .child("categorias")
                    .child(FirebaseHelper.getIdFirebase());
            categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Categoria categoria = ds.getValue(Categoria.class);
                            categoriaList.add(categoria);
                        }
                        text_info.setText("");
                        Collections.sort(categoriaList, (o1, o2) -> Math.toIntExact((o1.getPosicao() - o2.getPosicao())));
                        categoriaAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        text_info.setText("Nenhuma categoria cadastrada.");
                        progressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            text_info.setText("Não autenticado");
            progressBar.setVisibility(View.GONE);
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.START | ItemTouchHelper.END,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(categoriaList, fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);

            long positionTemp;
            positionTemp = categoriaList.get(fromPosition).getPosicao();
            categoriaList.get(fromPosition).setPosicao(categoriaList.get(toPosition).getPosicao());
            categoriaList.get(toPosition).setPosicao(positionTemp);
            categoriaList.get(fromPosition).salvar();
            categoriaList.get(toPosition).salvar();

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void configRV() {
        rv_categorias.setLayoutManager(new LinearLayoutManager(this));
        rv_categorias.setHasFixedSize(true);
        categoriaAdapter = new CategoriaAdapter(categoriaList, this); // --------- no lugar de anuncio List, aqui passa o endereco da lista. pode ser EstadosList.getList(), por exemplo
        rv_categorias.setAdapter(categoriaAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_categorias);

        rv_categorias.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

            }

            @Override
            public void onSwipedRight(int position) {
                showDialogRemove(categoriaList.get(position), position);
            }
        });
    }

    private void showDialogRemove(Categoria categoria, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir categoria");
        builder.setCancelable(false);
        builder.setMessage("Deseja excluir a categoria selecionada?");
        builder.setNegativeButton("Não", (dialog, which) -> {
            dialog.dismiss();
            categoriaAdapter.notifyDataSetChanged();
        });
        builder.setPositiveButton("Sim", (dialog, which) -> {
            categoria.remover();
            categoriaList.remove(position);
            categoriaAdapter.notifyDataSetChanged();
            if (categoriaList.size() == 0) {
                text_info.setText("Nenhuma categoria cadastrada.");
            }
            dialog.dismiss();
        });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();


    }


    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        findViewById(R.id.ib_add).setOnClickListener(v -> {
            novaCategoria = true;
            showDialog();
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_categoria, null);
        builder.setView(view);

        EditText edit_categoria = view.findViewById(R.id.edit_categoria);
        Button btn_fechar = view.findViewById(R.id.btn_fechar);
        Button btn_salvar = view.findViewById(R.id.btn_salvar);

        if (!novaCategoria) {
            edit_categoria.setText(categoriaSelecionada.getNome());
        }

        btn_fechar.setOnClickListener(v -> dialog.dismiss());
        btn_salvar.setOnClickListener(v -> {

            String nomeCategoria = edit_categoria.getText().toString().trim();


            if (!nomeCategoria.isEmpty()) {

                if (novaCategoria) {
                    Categoria categoria = new Categoria();
                    categoria.setNome(nomeCategoria);
                    categoria.setPosicao(System.currentTimeMillis());
                    categoria.salvar();

                    categoriaList.add(categoria);
                } else {
                    categoriaSelecionada.setNome(nomeCategoria);
                    categoriaList.set(categoriaIndex, categoriaSelecionada);
                    categoriaSelecionada.salvar();
                }

                if (categoriaList.size() > 0) {
                    text_info.setText("");
                }

                categoriaAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                edit_categoria.requestFocus();
                edit_categoria.setError("Informe um nome para a categoria");
            }

        });
        dialog = builder.create();
        dialog.show();
    }

    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Categorias");

        rv_categorias = findViewById(R.id.rv_categorias);
        progressBar = findViewById(R.id.progressBar);
        text_info = findViewById(R.id.text_info);

    }


    @Override
    public void OnClick(Categoria categoria, int position) {
        if (acesso == 0) {
            categoriaSelecionada = categoria;
            categoriaIndex = position;
            novaCategoria = false;

            showDialog();
        } else if (acesso == 1) {
            Intent intent = new Intent();
            intent.putExtra("categoriaSelecionada", categoria);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}