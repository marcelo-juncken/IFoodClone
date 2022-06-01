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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EmpresaConfigActivity extends AppCompatActivity {

    private ImageView img_logo;

    private EditText edit_nome;
    private EditText edit_tempo_minimo;
    private EditText edit_tempo_maximo;
    private EditText edit_categoria;

    private MaskEditText edit_telefone;
    private CurrencyEditText edit_taxa_entrega;
    private CurrencyEditText edit_pedido_minimo;

    private ProgressBar progressBar;

    private String caminhoLogo = "";
    private static final int REQUEST_GALERIA = 100;
    private Bitmap imagem;

    private Empresa empresa;

    private ImageButton ib_salvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_config);
        iniciaComponentes();
        configCliques();
        recuperaEmpresa();
    }

    public void recuperaEmpresa() {
        if (FirebaseHelper.getAutenticado()) {
            DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                    .child("empresas")
                    .child(FirebaseHelper.getIdFirebase());
            empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    empresa = snapshot.getValue(Empresa.class);
                    configDados();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void configDados() {
        Picasso.get().load(empresa.getUrlLogo()).into(img_logo);
        edit_nome.setText(empresa.getNome());
        edit_telefone.setText(empresa.getTelefone());

        edit_taxa_entrega.setText(String.valueOf(empresa.getTaxaEntrega()));
        edit_pedido_minimo.setText(String.valueOf(empresa.getPedidoMinimo()));

        edit_tempo_minimo.setText(String.valueOf(empresa.getTempoMinEntrega()));
        edit_tempo_maximo.setText(String.valueOf(empresa.getTempoMaxEntrega()));

        edit_categoria.setText(empresa.getCategoria());

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

    private void validaDados() {
        String nome = edit_nome.getText().toString().trim();
        String telefone = edit_telefone.getMasked();
        double taxa_entrega = (double) edit_taxa_entrega.getRawValue() / 100;
        double valor_minimo = (double) edit_pedido_minimo.getRawValue() / 100;


        int tempo_minimo = 0;
        if (!edit_tempo_minimo.getText().toString().isEmpty())
            tempo_minimo = Integer.parseInt(edit_tempo_minimo.getText().toString());

        int tempo_maximo = 0;
        if (!edit_tempo_maximo.getText().toString().isEmpty())
            tempo_maximo = Integer.parseInt(edit_tempo_maximo.getText().toString());

        String categoria = edit_categoria.getText().toString().trim();


        if (!nome.isEmpty()) {
            if (edit_telefone.isDone()) {
                if (tempo_minimo > 0) {
                    if (tempo_maximo > 0) {
                        if (!categoria.isEmpty()) {
                            ocultarTeclado();
                            configSalvar(true);

                            empresa.setNome(nome);
                            empresa.setTelefone(telefone);
                            empresa.setTaxaEntrega(taxa_entrega);
                            empresa.setPedidoMinimo(valor_minimo);
                            empresa.setTempoMinEntrega(tempo_minimo);
                            empresa.setTempoMaxEntrega(tempo_maximo);
                            empresa.setCategoria(categoria);

                            if (caminhoLogo != "") {
                                salvarImagemLogo();
                            } else {
                                empresa.salvar();
                                configSalvar(false);
                            }


                        } else {
                            edit_categoria.requestFocus();
                            edit_categoria.setError("Informe uma categoria.");
                        }
                    } else {
                        edit_tempo_maximo.requestFocus();
                        edit_tempo_maximo.setError("Informe o tempo máximo de entrega.");
                    }
                } else {
                    edit_tempo_minimo.requestFocus();
                    edit_tempo_minimo.setError("Informe o tempo mínimo de entrega.");
                }
            } else {
                edit_telefone.requestFocus();
                edit_telefone.setError("Informe o número da loja.");
            }

        } else {
            edit_nome.requestFocus();
            edit_nome.setError("Informe o nome da loja.");
        }
    }


    private void erroSalvarDados(String msg) {
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
                edit_nome.getWindowToken(), 0
        );
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALERIA) {
                Uri localImagemSelecionada = data.getData();
                caminhoLogo = localImagemSelecionada.toString();
                try {
                    if (Build.VERSION.SDK_INT < 31) {
                        imagem = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localImagemSelecionada);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localImagemSelecionada);
                        imagem = ImageDecoder.decodeBitmap(source);
                    }
                    img_logo.setImageBitmap(imagem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void salvarImagemLogo() {
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("perfil")
                .child(empresa.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoLogo));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            String urlImagem = task.getResult().toString();
            empresa.setUrlLogo(urlImagem);
            empresa.salvar();
            configSalvar(false);

            startActivity(new Intent(this, EmpresaHomeActivity.class));
        })).addOnFailureListener(e -> {
            erroSalvarDados(e.getMessage());
            progressBar.setVisibility(View.GONE);
        });
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        ib_salvar.setOnClickListener(v -> validaDados());
    }

    private void iniciaComponentes() {
        TextView text_tooolbar = findViewById(R.id.text_toolbar);
        text_tooolbar.setText("Perfil da empresa");

        ib_salvar = findViewById(R.id.ib_salvar);

        img_logo = findViewById(R.id.img_logo);
        edit_nome = findViewById(R.id.edit_nome);
        edit_tempo_minimo = findViewById(R.id.edit_tempo_minimo);
        edit_tempo_maximo = findViewById(R.id.edit_tempo_maximo);
        edit_categoria = findViewById(R.id.edit_categoria);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_taxa_entrega = findViewById(R.id.edit_taxa_entrega);
        edit_taxa_entrega.setLocale(new Locale("PT", "br"));
        edit_pedido_minimo = findViewById(R.id.edit_pedido_minimo);
        edit_pedido_minimo.setLocale(new Locale("PT", "br"));
        edit_nome = findViewById(R.id.edit_nome);
        edit_nome = findViewById(R.id.edit_nome);
        progressBar = findViewById(R.id.progressBar);
    }
}
