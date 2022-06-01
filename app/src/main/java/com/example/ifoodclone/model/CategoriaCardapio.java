package com.example.ifoodclone.model;

import java.util.ArrayList;
import java.util.List;

public class CategoriaCardapio {
    private String nome;
    private List<Produto> produtoList = new ArrayList<>();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Produto> getProdutoList() {
        return produtoList;
    }

    public void setProdutoList(List<Produto> produtoList) {
        this.produtoList = produtoList;
    }

    public CategoriaCardapio() {


    }
}
