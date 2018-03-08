package com.devmaker.luizmatias.weatherapp_devmaker;

import android.app.Activity;
import android.content.Context;
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
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.cidades = cidades;

    }

    @Override
    public ViewHolderCidade onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_cidade, parent, false);
        return new ViewHolderCidade(view);
    }

    @Override
    public int getItemCount() {
        return cidades.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderCidade holder, int position) {
        Cidade cidade = this.cidades.get(position);
        holder.setData(cidade, position);
    }

    class ViewHolderCidade extends RecyclerView.ViewHolder {

        private ImageView imageViewBackgroundCidade;
        private Button buttonDetalhes;
        private TextView textViewNome, textViewDescricao, textViewTemperatura;
        private int position;
        private Cidade cidadeAtual;

        public ViewHolderCidade(View itemView) {
            super(itemView);
        }

        public void setData(Cidade cidade, int position) {

            //seta as referencias das views (findviewbyid)
            imageViewBackgroundCidade = itemView.findViewById(R.id.imageViewBackgroundCidade);
            buttonDetalhes = itemView.findViewById(R.id.buttonDetalhes);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewTemperatura = itemView.findViewById(R.id.textViewTemperatura);
            this.position = position;
            this.cidadeAtual = cidade;

            textViewNome.setText(cidade.getNome());
            String descricao = cidade.getClima().getDescricao();
            String descricao_formatada = descricao.substring(0, 1).toUpperCase() + descricao.substring(1);
            textViewDescricao.setText(descricao_formatada);
            textViewTemperatura.setText(String.format("%.0f", cidade.getClima().getTemperatura()));

            Picasso.with(context).load(cidade.getFoto()).resize(640, 360).centerCrop().into(imageViewBackgroundCidade);

            buttonDetalhes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Ação de detalhes do clima", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
