package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.CategoriaCardapio;

import java.util.ArrayList;
import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.MyViewHolder> {

    private final List<CategoriaCardapio> categoriaList;
    private final Context context;

    public CardapioAdapter(List<CategoriaCardapio> categoriaList, Context context) {
        this.categoriaList = categoriaList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cardapio, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoriaCardapio categoriaCardapio = categoriaList.get(position);
        holder.text_categoria.setText(categoriaCardapio.getNome());

        holder.rv_produtos.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.rv_produtos.setHasFixedSize(true);
        ProdutoCardapioAdapter produtoCardapioAdapter = new ProdutoCardapioAdapter(categoriaCardapio.getProdutoList(), context); // --------- no lugar de anuncio List, aqui passa o endereco da lista. pode ser EstadosList.getList(), por exemplo

        holder.rv_produtos.setAdapter(produtoCardapioAdapter);
        produtoCardapioAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_categoria;
        RecyclerView rv_produtos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text_categoria = itemView.findViewById(R.id.textCategoriaNome);
            rv_produtos = itemView.findViewById(R.id.rvProdutos);

        }
    }
}
