package com.example.ifoodclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.ItemPedido;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoAdapter.MyViewHolder> {
    private final List<ItemPedido> itemPedidoList;
    private final Context context;
    private final OnClickListener onClickListener;

    public CarrinhoAdapter(List<ItemPedido> itemPedidoList, Context context, OnClickListener onClickListener) {
        this.itemPedidoList = itemPedidoList;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_carrinho, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemPedido itemPedido = itemPedidoList.get(position);
        Picasso.get().load(itemPedido.getUrlImagem()).into(holder.imagemProduto);
        holder.textNomeProduto.setText(itemPedido.getItem());
        holder.textTotalProduto.setText(context.getString(R.string.text_valor, GetMask.getValor(itemPedido.getValor() * itemPedido.getQuantidade())));

        StringBuilder qtd = new StringBuilder()
                .append(itemPedido.getQuantidade())
                .append("x");

        holder.text_qtd.setText(qtd);
        holder.text_observacao.setText(itemPedido.getObservacao());
        holder.itemView.setOnClickListener(v -> onClickListener.OnClick(itemPedido));
    }


    @Override
    public int getItemCount() {
        return itemPedidoList.size();
    }

    public interface OnClickListener {
        void OnClick(ItemPedido itemPedido);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeProduto;
        TextView textTotalProduto;
        TextView text_qtd;
        TextView text_observacao;
        ImageView imagemProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeProduto = itemView.findViewById(R.id.textNomeProduto);
            textTotalProduto = itemView.findViewById(R.id.textTotalProduto);
            text_qtd = itemView.findViewById(R.id.text_qtd);
            text_observacao = itemView.findViewById(R.id.text_observacao);
            imagemProduto = itemView.findViewById(R.id.imagemProduto);
        }
    }
}
