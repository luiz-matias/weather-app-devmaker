package com.devmaker.luizmatias.weatherapp_devmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PrevisoesAdapter extends RecyclerView.Adapter<PrevisoesAdapter.ViewHolderPrevisao> {

    private ArrayList<Previsao> previsoes;
    private LayoutInflater inflater;

    public PrevisoesAdapter(Context context, ArrayList<Previsao> previsoes){
        //recebe informações da tela que chamou o adapter
        inflater = LayoutInflater.from(context);
        this.previsoes = previsoes;
    }

    @Override
    public ViewHolderPrevisao onCreateViewHolder(ViewGroup parent, int viewType) {
        //prepara o layout
        View view = inflater.inflate(R.layout.item_list_previsao, parent, false);
        return new ViewHolderPrevisao(view);
    }

    @Override
    public int getItemCount() {
        return previsoes.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderPrevisao holder, int position) {
        //recebe a cidade a ser mostrada no item do recycler view e seta os dados da mesma no layout
        Previsao previsao = this.previsoes.get(position);

        //seta os dados
        holder.setData(previsao, position);
    }

    class ViewHolderPrevisao extends RecyclerView.ViewHolder {

        private TextView textViewTemperatura, textViewUmidade, textViewNuvens, textViewDataPrevisao, textViewDescricao;
        private ImageView imageViewNuvens, imageViewChuva;

        public ViewHolderPrevisao(View itemView) {
            super(itemView);
        }

        public void setData(Previsao previsao, int position) {
            try {
                //seta as referencias das views
                textViewDataPrevisao = itemView.findViewById(R.id.textViewDataPrevisao);
                textViewTemperatura = itemView.findViewById(R.id.textViewTemperatura);
                textViewUmidade = itemView.findViewById(R.id.textViewUmidade);
                textViewNuvens = itemView.findViewById(R.id.textViewNuvens);
                imageViewChuva = itemView.findViewById(R.id.imageViewChuva);
                imageViewNuvens = itemView.findViewById(R.id.imageViewNuvens);
                textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
                imageViewChuva.setImageResource(R.drawable.ic_rain);
                imageViewNuvens.setImageResource(R.drawable.ic_cloud);

                //formata o horário para o padrão de Curitiba
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(previsao.getTimestamp()));
                calendar.setTimeInMillis(calendar.getTimeInMillis()-10800*1000);
                simpleDateFormat = new SimpleDateFormat("HH:mm (dd/MM)");
                String data = simpleDateFormat.format(calendar.getTime());

                //alimenta informações nas views
                textViewDataPrevisao.setText(data);
                String descricao = previsao.getCidade().getClima().getDescricao();
                String descricao_formatada = descricao.substring(0, 1).toUpperCase() + descricao.substring(1);
                textViewDescricao.setText(descricao_formatada);
                textViewTemperatura.setText(String.format("%.0f", previsao.getCidade().getClima().getTemperatura()));
                textViewUmidade.setText(String.format("%.0f", previsao.getCidade().getClima().getUmidade()));
                textViewNuvens.setText(String.format("%.0f", previsao.getCidade().getClima().getNuvens()));

            }catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
