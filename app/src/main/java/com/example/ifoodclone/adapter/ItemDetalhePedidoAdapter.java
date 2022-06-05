package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.ItemPedido;

import java.util.List;

public class ItemDetalhePedidoAdapter extends RecyclerView.Adapter<ItemDetalhePedidoAdapter.MyViewHolder> {
    private final List<ItemPedido> itemPedidoList;
    private final Context context;

    public ItemDetalhePedidoAdapter(List<ItemPedido> itemPedidoList, Context context) {
        this.itemPedidoList = itemPedidoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_detalhe_pedido, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemPedido itemPedido = itemPedidoList.get(position);
        holder.text_qtd_item_pedido.setText(String.valueOf(itemPedido.getQuantidade()));
        holder.text_nome_item_pedido.setText(itemPedido.getItem());
        holder.text_observacao.setText(String.format("Obs: %1$s", itemPedido.getObservacao()));
        holder.text_valor.setText(context.getString(R.string.text_valor, GetMask.getValor(itemPedido.getValor()*itemPedido.getQuantidade())));
    }

    @Override
    public int getItemCount() {
        return itemPedidoList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_qtd_item_pedido;
        TextView text_nome_item_pedido;
        TextView text_observacao;
        TextView text_valor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text_qtd_item_pedido = itemView.findViewById(R.id.text_qtd_item_pedido);
            text_nome_item_pedido = itemView.findViewById(R.id.text_nome_item_pedido);
            text_observacao = itemView.findViewById(R.id.text_observacao);
            text_valor = itemView.findViewById(R.id.text_valor);
        }
    }
}
