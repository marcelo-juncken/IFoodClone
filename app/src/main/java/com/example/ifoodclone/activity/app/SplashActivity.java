package com.example.ifoodclone.activity.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ifoodclone.DAO.ItemPedidoDAO;
import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.autenticacao.LoginActivity;
import com.example.ifoodclone.activity.empresa.EmpresaFinalizaCadastroActivity;
import com.example.ifoodclone.activity.empresa.EmpresaHomeActivity;
import com.example.ifoodclone.activity.usuario.UsuarioFinalizaCadastroActivity;
import com.example.ifoodclone.activity.usuario.UsuarioHomeActivity;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Empresa;
import com.example.ifoodclone.model.Login;
import com.example.ifoodclone.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        limparSQLite();

        verificaAutenticacao();
    }

    private void verificaAutenticacao() {
        if (FirebaseHelper.getAutenticado()) {
            verificaCadastro();
        } else {
            startActivity(new Intent(this, UsuarioHomeActivity.class));
            finish();
        }
    }

    public void verificaCadastro() {
        DatabaseReference loginRef = FirebaseHelper.getDatabaseReference()
                .child("login")
                .child(FirebaseHelper.getIdFirebase());
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                login = snapshot.getValue(Login.class);
                verificaAcesso();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void verificaAcesso() {
        if (login != null) {
            if (login.getTipo().equals("U")) {
                if (login.getAcesso()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), UsuarioHomeActivity.class));
                } else {
                    recuperaUsuario();
                }
            } else {
                if (login.getAcesso()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), EmpresaHomeActivity.class));
                } else {
                    recuperaEmpresa();
                }
            }

        }
    }

    private void recuperaUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(login.getId());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null) {
                    finish();
                    Intent intent = new Intent(getBaseContext(), UsuarioFinalizaCadastroActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("login", login);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaEmpresa() {
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(login.getId());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Empresa empresa = snapshot.getValue(Empresa.class);
                if (empresa != null) {
                    finish();
                    Intent intent = new Intent(getBaseContext(), EmpresaFinalizaCadastroActivity.class);
                    intent.putExtra("empresa", empresa);
                    intent.putExtra("login", login);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void limparSQLite(){
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(getBaseContext());
        itemPedidoDAO.limparCarrinho();
    }
}