package com.example.ifoodclone.model;

import android.app.Activity;
import android.content.Context;

import com.example.ifoodclone.helper.FirebaseHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Optional;

public class Produto implements Serializable {

    private String id;
    private String urlImagem;
    private String nome;
    private Double valor;
    private Double valorAntigo;
    private String idEmpresa;
    private String idCategoria;
    private String descricao;
    private int idLocal;
    private Boolean addMais = false;

    public Produto() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference();
        setId(produtoRef.push().getKey());
    }

    public void salvar(Boolean novo, Activity activity) {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(getId());
        produtoRef.setValue(this).addOnSuccessListener(unused -> {
            if(novo){
                activity.finish();
            }
        });



    }

    public void remover() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(getId());
        produtoRef.removeValue();

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("produtos")
                .child(FirebaseHelper.getIdFirebase())
                .child(getId()+".JPEG");
        storageReference.delete();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getValorAntigo() {
        return valorAntigo;
    }

    public void setValorAntigo(Double valorAntigo) {
        this.valorAntigo = valorAntigo;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Exclude
    public Boolean getAddMais() {
        return addMais;
    }

    public void setAddMais(Boolean addMais) {
        this.addMais = addMais;
    }

    @Exclude
    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }
}
