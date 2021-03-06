package com.devmaker.luizmatias.weatherapp_devmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CidadesAdapter extends RecyclerView.Adapter<CidadesAdapter.ViewHolderCidade> {

    private ArrayList<Cidade> cidades;
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;

    public CidadesAdapter(Context context, Activity activity, ArrayList<Cidade> cidades){
        //recebe informações da tela que chamou o adapter
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.cidades = cidades;
        this.activity = activity;

    }

    @Override
    public ViewHolderCidade onCreateViewHolder(ViewGroup parent, int viewType) {
        //prepara o layout
        View view = inflater.inflate(R.layout.item_list_cidade, parent, false);
        return new ViewHolderCidade(view);
    }

    @Override
    public int getItemCount() {
        return cidades.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderCidade holder, int position) {
        //recebe a cidade a ser mostrada no item do recycler view e seta os dados da mesma no layout
        Cidade cidade = this.cidades.get(position);

        //seta os dados
        holder.setData(cidade, position);
    }

    class ViewHolderCidade extends RecyclerView.ViewHolder {

        private ImageView imageViewBackgroundCidade;
        private Button buttonDetalhes;
        private TextView textViewNome, textViewDescricao, textViewTemperatura;

        public ViewHolderCidade(View itemView) {
            super(itemView);
        }

        public void setData(final Cidade cidade, int position) {

            //seta as referencias das views
            imageViewBackgroundCidade = itemView.findViewById(R.id.imageViewBackgroundCidade);
            buttonDetalhes = itemView.findViewById(R.id.buttonDetalhes);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewTemperatura = itemView.findViewById(R.id.textViewTemperatura);

            //alimenta informações nas views

            textViewNome.setText(cidade.getNome());

            //adiciona letra maiúscula ao primeiro caractere
            String descricao = cidade.getClima().getDescricao();
            String descricao_formatada = descricao.substring(0, 1).toUpperCase() + descricao.substring(1);
            textViewDescricao.setText(descricao_formatada);

            textViewTemperatura.setText(String.format("%.0f", cidade.getClima().getTemperatura()));

            //adicionada imagem via lib Picasso
            Picasso.with(context).load(cidade.getFoto()).resize(640, 360).centerCrop().into(imageViewBackgroundCidade);

            //ação de abrir uma nova tela de detalhes da previsão do tempo
            buttonDetalhes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, DetalhesActivity.class);
                    intent.putExtra("id", cidade.getId());
                    intent.putExtra("nome", cidade.getNome());
                    intent.putExtra("descricao", cidade.getClima().getDescricao());
                    intent.putExtra("temperatura", cidade.getClima().getTemperatura());
                    intent.putExtra("temperatura_maxima", cidade.getClima().getTemperatura_maxima());
                    intent.putExtra("temperatura_minima", cidade.getClima().getTemperatura_minima());
                    intent.putExtra("umidade", cidade.getClima().getUmidade());
                    intent.putExtra("nuvens", cidade.getClima().getNuvens());
                    intent.putExtra("vento", cidade.getClima().getVento());

                    context.startActivity(intent);
                }
            });

        }
    }
}
