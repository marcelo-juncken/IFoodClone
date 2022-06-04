package com.example.ifoodclone.fragment.usuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ifoodclone.R;


public class UsuarioAvaliacoesProdutoFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario_avaliacoes_produto, container, false);


        iniciaComponentes(view);

        configCliques();

        return view;
    }

    private void configCliques() {
    }

    private void iniciaComponentes(View view) {
    }


}