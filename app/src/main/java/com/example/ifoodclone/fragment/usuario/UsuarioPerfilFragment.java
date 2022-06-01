package com.example.ifoodclone.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.autenticacao.CriarContaActivity;
import com.example.ifoodclone.activity.autenticacao.LoginActivity;
import com.example.ifoodclone.activity.usuario.UsuarioEnderecosActivity;
import com.example.ifoodclone.activity.usuario.UsuarioFavoritosActivity;
import com.example.ifoodclone.activity.usuario.UsuarioPerfilActivity;
import com.example.ifoodclone.helper.FirebaseHelper;


public class UsuarioPerfilFragment extends Fragment {

    private ConstraintLayout l_logado;
    private ConstraintLayout l_deslogado;
    private LinearLayout menu_perfil;
    private LinearLayout menu_favoritos;
    private LinearLayout menu_enderecos;
    private LinearLayout menu_deslogar;

    private Button btn_entrar;
    private Button btn_cadastrar;

    private TextView text_usuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_perfil, container, false);

        iniciaComponentes(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        verificaAutenticacao();
        configCliques();
    }

    private void verificaAutenticacao() {
        if (FirebaseHelper.getAutenticado()) {
            l_logado.setVisibility(View.VISIBLE);
            l_deslogado.setVisibility(View.GONE);
            menu_deslogar.setVisibility(View.VISIBLE);
            text_usuario.setText(getString(R.string.ola_usuario,
                    FirebaseHelper.getAuth().getCurrentUser().getDisplayName()));
        } else {
            l_logado.setVisibility(View.GONE);
            l_deslogado.setVisibility(View.VISIBLE);
            menu_deslogar.setVisibility(View.GONE);
        }
    }

    private void configCliques(){
        menu_perfil.setOnClickListener(v -> redirecionaUsuario(UsuarioPerfilActivity.class));
        menu_favoritos.setOnClickListener(v -> redirecionaUsuario(UsuarioFavoritosActivity.class));
        menu_enderecos.setOnClickListener(v -> redirecionaUsuario(UsuarioEnderecosActivity.class));
        menu_deslogar.setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            onStart();

        });
        btn_entrar.setOnClickListener(v -> startActivity(new Intent(requireActivity(),LoginActivity.class)));
        btn_cadastrar.setOnClickListener(v -> startActivity(new Intent(requireActivity(), CriarContaActivity.class)));
    }

    private void redirecionaUsuario (Class<?> classe){
        if (FirebaseHelper.getAutenticado()){
            startActivity(new Intent(requireActivity(),classe));
        }else{
            startActivity(new Intent(requireActivity(),LoginActivity.class));
        }
    }

    private void iniciaComponentes(View view) {
        l_logado = view.findViewById(R.id.l_logado);
        l_deslogado = view.findViewById(R.id.l_deslogado);
        menu_perfil = view.findViewById(R.id.menu_perfil);
        menu_favoritos = view.findViewById(R.id.menu_favoritos);
        menu_enderecos = view.findViewById(R.id.menu_enderecos);
        menu_deslogar = view.findViewById(R.id.menu_deslogar);
        btn_entrar = view.findViewById(R.id.btn_entrar);
        btn_cadastrar = view.findViewById(R.id.btn_cadastrar);
        text_usuario = view.findViewById(R.id.text_usuario);
    }
}