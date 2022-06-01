package com.example.ifoodclone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.helper.GetMask;
import com.example.ifoodclone.model.Empresa;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EmpresasAdapter extends RecyclerView.Adapter<EmpresasAdapter.MyViewHolder> {
    private List<Empresa> empresaList;
    private OnClickListener onClickListener;
    private Context context;

    public EmpresasAdapter(List<Empresa> empresaList, OnClickListener onClickListener, Context context) {
        this.empresaList = empresaList;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.empresa_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Empresa empresa = empresaList.get(position);

        Picasso.get().load(empresa.getUrlLogo()).into(holder.img_empresa);

        holder.text_nome.setText(empresa.getNome());
        holder.text_categoria.setText(empresa.getCategoria());

        holder.text_tempo_minimo.setText(String.valueOf(empresa.getTempoMinEntrega()));

        holder.text_tempo_maximo.setText("-" + empresa.getTempoMaxEntrega() + " min");

        if(empresa.getTaxaEntrega()>0){
            holder.text_taxa_entrega.setText(context.getString(R.string.text_valor, GetMask.getValor(empresa.getTaxaEntrega())));
        } else {
            holder.text_taxa_entrega.setText("ENTREGA GRÁTIS");
            holder.text_taxa_entrega.setTextColor(Color.parseColor("#2ED67E"));
        }

        holder.itemView.setOnClickListener(v -> onClickListener.OnClick(empresa));
    }

    @Override
    public int getItemCount() {
        return empresaList.size();
    }

    public interface OnClickListener {
        void OnClick(Empresa empresa);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_empresa;
        TextView text_nome;
        TextView text_categoria;
        TextView text_tempo_minimo;
        TextView text_tempo_maximo;
        TextView text_taxa_entrega;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_empresa = itemView.findViewById(R.id.img_empresa);
            text_nome = itemView.findViewById(R.id.text_nome);
            text_categoria = itemView.findViewById(R.id.text_categoria);
            text_tempo_minimo = itemView.findViewById(R.id.text_tempo_minimo);
            text_tempo_maximo = itemView.findViewById(R.id.text_tempo_maximo);
            text_taxa_entrega = itemView.findViewById(R.id.text_taxa_entrega);

        }
    }
}
