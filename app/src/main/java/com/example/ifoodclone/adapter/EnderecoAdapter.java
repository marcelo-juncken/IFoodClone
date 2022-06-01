package com.example.ifoodclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.model.Endereco;

import java.util.List;

public class EnderecoAdapter extends RecyclerView.Adapter<EnderecoAdapter.MyViewHolder> {
    private List<Endereco> enderecoList;
    private OnClickListener onClickListener;

    public EnderecoAdapter(List<Endereco> enderecoList, OnClickListener onClickListener) {
        this.enderecoList = enderecoList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.endereco_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Endereco endereco = enderecoList.get(position);
        holder.text_logradouro.setText(endereco.getLogradouro());
        holder.text_referencia.setText(endereco.getReferencia());
        holder.itemView.setOnClickListener(v -> onClickListener.OnClick(endereco));
    }

    @Override
    public int getItemCount() {
        return enderecoList.size();
    }

    public interface OnClickListener {
        void OnClick(Endereco endereco);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_logradouro;
        TextView text_referencia;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text_logradouro = itemView.findViewById(R.id.text_logradouro);
            text_referencia = itemView.findViewById(R.id.text_referencia);
        }
    }
}
