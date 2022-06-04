package com.example.ifoodclone.activity.empresa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;
import com.example.ifoodclone.R;
import com.example.ifoodclone.adapter.ViewPagerAdapter;
import com.example.ifoodclone.fragment.usuario.UsuarioAvaliacoesProdutoFragment;
import com.example.ifoodclone.fragment.usuario.UsuarioDetalhesProdutoFragment;
import com.google.android.material.tabs.TabLayout;
public class EmpresaProdutoDetalhesActivity extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager view_pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_produto_detalhes);

        iniciaComponentes();
        configCliques();
        configTabsLayout();
    }


    private void configTabsLayout() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new UsuarioDetalhesProdutoFragment(), "");
        viewPagerAdapter.addFragment(new UsuarioAvaliacoesProdutoFragment(), "");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);


        view_pager.setAdapter(viewPagerAdapter);
        tab_layout.setElevation(0);
        tabLayout.setupWithViewPager(view_pager, true);
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes() {
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Adicionar item");

        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);
    }

}