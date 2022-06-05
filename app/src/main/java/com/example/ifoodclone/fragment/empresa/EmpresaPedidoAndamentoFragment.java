package com.example.ifoodclone.fragment.empresa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.EmpresaPedidoAdapter;
import com.example.ifoodclone.helper.FirebaseHelper;
import com.example.ifoodclone.model.Pedido;
import com.example.ifoodclone.model.StatusPedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EmpresaPedidoAndamentoFragment extends Fragment implements EmpresaPedidoAdapter.OnClickListener {

    private EmpresaPedidoAdapter empresaPedidoAdapter;
    private final List<Pedido> pedidoList = new ArrayList<>();
    private RecyclerView rv_pedidos;
    private ProgressBar progressBar;
    private TextView text_info;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_pedido_andamento, container, false);

        iniciaComponentes(view);
        recuperaPedidos();
        configRv();

        return view;
    }

    private void recuperaPedidos() {
        DatabaseReference pedidosRef = FirebaseHelper.getDatabaseReference()
                .child("empresaPedidos")
                .child(FirebaseHelper.getIdFirebase());
        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        if (pedido != null) {
                            addPedidoList(pedido);
                        }
                    }
                    text_info.setText("");
                } else {
                    text_info.setText("Nenhum pedido recebido.");

                }
                progressBar.setVisibility(View.GONE);
                empresaPedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addPedidoList(Pedido pedido){
        if (pedido.getStatusPedido() != StatusPedido.CANCELADO_EMPRESA ||
                pedido.getStatusPedido() != StatusPedido.CANCELADO_USUARIO ||
                pedido.getStatusPedido() != StatusPedido.ENTREGUE) {
            pedidoList.add(pedido);
        }
    }

    private void configRv() {
        rv_pedidos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_pedidos.setHasFixedSize(true);
        empresaPedidoAdapter = new EmpresaPedidoAdapter(pedidoList, this);
        rv_pedidos.setAdapter(empresaPedidoAdapter);
    }

    private void iniciaComponentes(View view) {
        rv_pedidos = view.findViewById(R.id.rv_pedidos);
        progressBar = view.findViewById(R.id.progressBar);
        text_info = view.findViewById(R.id.text_info);
    }

    @Override
    public void OnClick(Pedido pedido, int rota) {
        if (rota == 0) {

        } else if (rota == 1) {

        }
    }
}