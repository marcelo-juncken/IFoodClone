package com.example.ifoodclone.fragment.usuario;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ifoodclone.R;
import com.example.ifoodclone.activity.usuario.UsuarioFinalizaCadastroActivity;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Login;
import com.example.ifoodclone.model.Usuario;

public class UsuarioFragment extends Fragment {

    private EditText edit_email;
    private EditText edit_senha;
    private ProgressBar progressBar;

    private Button btn_criar_conta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);
        iniciaComponentes(view);
        configCliques();
        return view;
    }


    private void validaDados() {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                ocultarTeclado();
                progressBar.setVisibility(View.VISIBLE);

                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);

                criarConta(usuario);
            } else {
                edit_senha.requestFocus();
                edit_senha.setError("Digite uma senha");
            }
        } else {
            edit_email.requestFocus();
            edit_email.setError("Digite um email");
        }


    }

    private void criarConta(Usuario usuario) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String id = task.getResult().getUser().getUid();
                usuario.setId(id);
                usuario.salvar();

                Login login = new Login(id,"U",false);
                login.salvar(progressBar);

                requireActivity().finish();
                Intent intent = new Intent(requireActivity(), UsuarioFinalizaCadastroActivity.class);
                intent.putExtra("usuario",usuario);
                intent.putExtra("login",login);
                startActivity(intent);


            } else {
                erroAutenticacao(FirebaseHelper.validaErros(task.getException().getMessage()));
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void erroAutenticacao(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("Fechar", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void configCliques() {
        btn_criar_conta.setOnClickListener(v -> validaDados());
    }

    private void iniciaComponentes(View view) {
        edit_email = view.findViewById(R.id.edit_email);
        edit_senha = view.findViewById(R.id.edit_senha);
        progressBar = view.findViewById(R.id.progressBar);
        btn_criar_conta = view.findViewById(R.id.btn_criar_conta);
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                btn_criar_conta.getWindowToken(), 0
        );
    }
}