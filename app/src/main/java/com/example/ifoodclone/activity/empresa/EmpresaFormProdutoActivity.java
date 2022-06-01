package com.example.ifoodclone.activity.empresa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Categoria;
import com.example.ifoodclone.model.Produto;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EmpresaFormProdutoActivity extends AppCompatActivity {

    private static final int REQUEST_CATEGORIA = 200;
    private EditText edit_produto;
    private CurrencyEditText edit_valor;
    private CurrencyEditText edit_valor_antigo;
    private Button btn_categoria;
    private EditText edit_descricao;

    private Categoria categoriaSelecionada;

    private LinearLayout l_edit_descricao;

    private static final int REQUEST_GALERIA = 100;
    private ImageView img_produto;
    private String caminhoImagem;
    private Bitmap imagem;

    private Produto produto;

    private TextView text_toolbar;

    private boolean novoProduto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_form_produto);

        iniciaComponentes();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            produto = (Produto) bundle.getSerializable("produtoSelecionado");
            recuperaProduto();
        }

        configCliques();
    }

    private void recuperaProduto() {
        text_toolbar.setText("Editar Produto");

        Picasso.get().load(produto.getUrlImagem())
                .into(img_produto);
        edit_produto.setText(produto.getNome());
        edit_valor.setText(GetMask.getValor(produto.getValor()));
        edit_valor_antigo.setText(GetMask.getValor(produto.getValorAntigo()));
        edit_descricao.setText(produto.getDescricao());
        recuperaCategoria();
        novoProduto = false;

    }

    private void recuperaCategoria() {
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                    .child("categorias")
                    .child(FirebaseHelper.getIdFirebase())
                    .child(produto.getIdCategoria());
            categoriaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Categoria categoria = snapshot.getValue(Categoria.class);
                    if (categoria != null) {
                        btn_categoria.setText(categoria.getNome());
                        categoriaSelecionada = categoria;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void validaDados(View view) {

        String nome = edit_produto.getText().toString().trim();
        double valor = (double) edit_valor.getRawValue() / 100;
        double valor_antigo = (double) edit_valor_antigo.getRawValue() / 100;
        String categoria = btn_categoria.getText().toString().trim();
        String descricao = edit_descricao.getText().toString().trim();

        if (!nome.isEmpty()) {
            if (valor > 0) {
                if (!categoria.isEmpty()) {
                    if (!descricao.isEmpty()) {
                        ocultarTeclado();

                        if (produto == null) produto = new Produto();

                        produto.setNome(nome);
                        produto.setValor(valor);
                        produto.setValorAntigo(valor_antigo);
                        produto.setIdCategoria(categoriaSelecionada.getId());
                        produto.setDescricao(descricao);

                        if (novoProduto == true) {
                            if (caminhoImagem != null) {
                                salvarImagemPerfil();
                            } else {
                                ocultarTeclado();
//                                erroSalvarProduto("A foto do produto é obrigatória");
                                Snackbar.make(
                                        img_produto,
                                        "Selecione uma imagem.",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            if (caminhoImagem != null) {
                                salvarImagemPerfil();
                            } else {
                                produto.salvar(novoProduto, this);
                            }
                        }


                    } else {
                        edit_descricao.requestFocus();
                        edit_descricao.setError("Informe um valor válido.");
                    }
                } else {
                    ocultarTeclado();
                    btn_categoria.setError("");
                    erroSalvarProduto("Informe uma categoria para o produto.");
                }
            } else {
                edit_valor.requestFocus();
                edit_valor.setError("Informe um valor válido.");
            }
        } else {
            edit_produto.requestFocus();
            edit_produto.setError("Informe um nome para o produto.");
        }


    }

    private void salvarImagemPerfil() {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(produto.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            String urlImagem = task.getResult().toString();
            produto.setUrlImagem(urlImagem);
            produto.salvar(novoProduto, this);
        })).addOnFailureListener(e -> erroSalvarProduto(e.getMessage()));


    }

    public void verificaPermissaoGaleria(View view) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissão negada.", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissaoGaleria(permissionListener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }


    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }


    private void showDialogPermissaoGaleria(PermissionListener listener, String[] permissoes) {
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões negadas.")
                .setDeniedMessage("Você negou as permissões para acessar a galeria do dispositivo, deseja permitir?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        btn_categoria.setOnClickListener(v -> {
            ocultarTeclado();
            Intent intent = new Intent(this, EmpresaCategoriasActivity.class);
            intent.putExtra("acesso", 1);
            startActivityForResult(intent, REQUEST_CATEGORIA);
        });
        l_edit_descricao.setOnClickListener(v -> {
            mostrarTeclado();
            edit_descricao.requestFocus();
        });
    }

    private void iniciaComponentes() {
        text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Adicionar Produto");

        img_produto = findViewById(R.id.img_produto);
        edit_produto = findViewById(R.id.edit_produto);
        edit_valor = findViewById(R.id.edit_valor);
        edit_valor_antigo = findViewById(R.id.edit_valor_antigo);
        btn_categoria = findViewById(R.id.btn_categoria);
        edit_descricao = findViewById(R.id.edit_descricao);
        l_edit_descricao = findViewById(R.id.l_edit_descricao);

        edit_valor.setLocale(new Locale("PT", "br"));
        edit_valor_antigo.setLocale(new Locale("PT", "br"));
    }

    private void erroSalvarProduto(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("Fechar", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ocultarTeclado() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edit_produto.getWindowToken(), 0
        );
    }

    private void mostrarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                Uri localImagemSelecionada = data.getData();
                caminhoImagem = localImagemSelecionada.toString();

                try {
                    if (Build.VERSION.SDK_INT < 31) {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                    }
                    img_produto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img_produto.setImageBitmap(imagem);
            } else if (requestCode == 200) {
                categoriaSelecionada = (Categoria) data.getExtras().getSerializable("categoriaSelecionada");
                btn_categoria.setText(categoriaSelecionada.getNome());
                btn_categoria.setError(null);
            }
        }
    }
}