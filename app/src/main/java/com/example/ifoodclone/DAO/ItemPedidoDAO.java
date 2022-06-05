package com.example.ifoodclone.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ifoodclone.model.ItemPedido;

import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public ItemPedidoDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public long salvar(ItemPedido itemPedido) {
        long id = 0;
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUNA_ID_FIREBASE, itemPedido.getIdItem());
        cv.put(DbHelper.COLUNA_NOME, itemPedido.getItem());
        cv.put(DbHelper.COLUNA_URL_IMAGEM, itemPedido.getUrlImagem());
        cv.put(DbHelper.COLUNA_VALOR, itemPedido.getValor());
        cv.put(DbHelper.COLUNA_QUANTIDADE, itemPedido.getQuantidade());
        cv.put(DbHelper.COLUNA_OBSERVACAO, itemPedido.getObservacao());
        try {
            id = write.insert(DbHelper.TABELA_ITEM_PEDIDO, null, cv);
        } catch (Exception e) {
        }
        return id;
    }

    public void atualizar(ItemPedido itemPedido) {
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUNA_QUANTIDADE, itemPedido.getQuantidade());
        cv.put(DbHelper.COLUNA_OBSERVACAO, itemPedido.getObservacao());
        try {
            String where = "id=?";
            String[] args = {String.valueOf(itemPedido.getId())};
            write.update(DbHelper.TABELA_ITEM_PEDIDO, cv, where, args);
        } catch (Exception e) {
        }
    }

    public List<ItemPedido> getList() {
        List<ItemPedido> itemPedidoList = new ArrayList<>();

        String sql = " SELECT * FROM " + DbHelper.TABELA_ITEM_PEDIDO + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            Long id_local = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ID));
            String id_firebase = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ID_FIREBASE));
            String item_nome = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_NOME));
            String url_imagem = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_URL_IMAGEM));
            double valor = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_VALOR));
            int quantidade = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_QUANTIDADE));
            String observacao = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_OBSERVACAO));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setId(id_local);
            itemPedido.setIdItem(id_firebase);
            itemPedido.setItem(item_nome);
            itemPedido.setUrlImagem(url_imagem);
            itemPedido.setValor(valor);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setObservacao(observacao);

            itemPedidoList.add(itemPedido);

        }
        return itemPedidoList;
    }

    public Double getTotal() {
        double total = 0;
        for (ItemPedido itemPedido : getList()) {
            total += itemPedido.getValor() * itemPedido.getQuantidade();
        }
        return total;
    }

    public void remover(Long id) {
        try {
            String where = "id=?";
            String[] args = {String.valueOf(id)};
            write.delete(DbHelper.TABELA_ITEM_PEDIDO, where, args);
        } catch (Exception e) {
        }
    }

    public void removerTodos() {
        try {
            write.delete(DbHelper.TABELA_ITEM_PEDIDO, null, null);
        } catch (Exception e) {
        }
    }

    public void limparCarrinho() {
        try {
            write.delete(DbHelper.TABELA_EMPRESA, null, null);
            write.delete(DbHelper.TABELA_ENTREGA, null, null);
            write.delete(DbHelper.TABELA_ITEM_PEDIDO, null, null);
        } catch (Exception e) {
        }
    }

}
