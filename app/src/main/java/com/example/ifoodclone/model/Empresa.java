package com.example.ifoodclone.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.ifoodclone.helper.FirebaseHelper;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String urlLogo;
    private Boolean acesso;
    private Double pedidoMinimo;
    private Double taxaEntrega;
    private int tempoMinEntrega;
    private int tempoMaxEntrega;
    private String categoria;
    private Long dataCadastro;

    public Empresa() {
    }

    public void salvar() {
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(getId());
        empresaRef.setValue(this);

        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        UserProfileChangeRequest perfil;
        if (getUrlLogo() != null){
             perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getNome())
                    .setPhotoUri(Uri.parse(getUrlLogo()))
                    .build();
        }else {
            perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getNome())
                    .build();
        }

        if (user != null) user.updateProfile(perfil);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public Boolean getAcesso() {
        return acesso;
    }

    public void setAcesso(Boolean acesso) {
        this.acesso = acesso;
    }

    public Double getPedidoMinimo() {
        return pedidoMinimo;
    }

    public void setPedidoMinimo(Double pedidoMinimo) {
        this.pedidoMinimo = pedidoMinimo;
    }

    public Double getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(Double taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public int getTempoMinEntrega() {
        return tempoMinEntrega;
    }

    public void setTempoMinEntrega(int tempoMinEntrega) {
        this.tempoMinEntrega = tempoMinEntrega;
    }

    public int getTempoMaxEntrega() {
        return tempoMaxEntrega;
    }

    public void setTempoMaxEntrega(int tempoMaxEntrega) {
        this.tempoMaxEntrega = tempoMaxEntrega;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Long dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
