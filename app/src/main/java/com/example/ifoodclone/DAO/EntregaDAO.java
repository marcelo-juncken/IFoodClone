package com.example.ifoodclone.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.ifoodclone.model.Endereco;
import com.example.ifoodclone.model.EntregaPedido;

public class EntregaDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public EntregaDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public void salvarEndereco(Endereco endereco){

        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUNA_FORMA_PAGAMENTO, "");
        cv.put(DbHelper.COLUNA_ENDERECO_LOGRADOURO, endereco.getLogradouro());
        cv.put(DbHelper.COLUNA_ENDERECO_BAIRRO, endereco.getBairro());
        cv.put(DbHelper.COLUNA_ENDERECO_MUNICIPIO, endereco.getMunicipio());
        cv.put(DbHelper.COLUNA_ENDERECO_REFERENCIA, endereco.getReferencia());

        try {
            write.insert(DbHelper.TABELA_ENTREGA, null, cv);
        } catch (Exception e) {
        }

    }

    public void atualizarEndereco(Endereco endereco){

        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUNA_ENDERECO_LOGRADOURO, endereco.getLogradouro());
        cv.put(DbHelper.COLUNA_ENDERECO_BAIRRO, endereco.getBairro());
        cv.put(DbHelper.COLUNA_ENDERECO_MUNICIPIO, endereco.getMunicipio());
        cv.put(DbHelper.COLUNA_ENDERECO_REFERENCIA, endereco.getReferencia());

        try {
            write.update(DbHelper.TABELA_ENTREGA, cv, null, null);
        } catch (Exception e) {
        }

    }

    public void salvarPagamento(String formaPagamento){

        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUNA_FORMA_PAGAMENTO, formaPagamento);

        try {
            write.update(DbHelper.TABELA_ENTREGA, cv, null, null);
        } catch (Exception e) {
        }

    }

    public EntregaPedido getEntrega(){
        EntregaPedido entregaPedido = new EntregaPedido();
        Endereco endereco = new Endereco();

        String sql = " SELECT * FROM " + DbHelper.TABELA_ENTREGA + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()){
            String forma_pagamento = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_FORMA_PAGAMENTO));
            String logradouro = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_LOGRADOURO));
            String bairro = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_BAIRRO));
            String municipio = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_MUNICIPIO));
            String referencia = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_REFERENCIA));

            endereco.setLogradouro(logradouro);
            endereco.setBairro(bairro);
            endereco.setMunicipio(municipio);
            endereco.setReferencia(referencia);

            entregaPedido.setFormaPagamento(forma_pagamento);
            entregaPedido.setEndereco(endereco);

        }

        return entregaPedido;
    }

    public Endereco getEndereco(){
        Endereco endereco = null;

        String sql = " SELECT * FROM " + DbHelper.TABELA_ENTREGA + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()){
            String logradouro = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_LOGRADOURO));
            String bairro = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_BAIRRO));
            String municipio = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_MUNICIPIO));
            String referencia = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUNA_ENDERECO_REFERENCIA));

            endereco = new Endereco();
            endereco.setLogradouro(logradouro);
            endereco.setBairro(bairro);
            endereco.setMunicipio(municipio);
            endereco.setReferencia(referencia);


        }

        return endereco;
    }

}
