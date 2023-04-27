package com.example.tunesongplayer_entrega1_version2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CancionPlaylistAdapter extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private String[] nombresCanciones;
    private String[] autoresCanciones;

    //Constructora
    public CancionPlaylistAdapter(Context pContexto, String[] pNombresCanciones, String[] pAutoresCanciones) {
        contexto = pContexto;
        nombresCanciones = pNombresCanciones;
        autoresCanciones = pAutoresCanciones;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Devuelve el número de elementos
    @Override
    public int getCount() {
        return nombresCanciones.length;
    }

    //Devuelve el elemento i (en este caso, el nombre de la canción)
    @Override
    public Object getItem(int i) {
        return nombresCanciones[i];
    }

    //Devuelve el identificador del elemento i
    @Override
    public long getItemId(int i) {
        return i;
    }

    //Devuelve la vista (cómo se visualiza el elemento i)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_cancion_de_playlist,null);

        //Recogemos los elementos de la vista
        TextView nombreCancion = (TextView) view.findViewById(R.id.tv_NombreCancionEnPlaylist);
        TextView autorCancion = (TextView) view.findViewById(R.id.tv_AutorCancionEnPlaylist);
        ImageView fotoCancionEnPlaylist = (ImageView) view.findViewById(R.id.fotoCancionEnPlaylist);

        //Asignamos a los elementos de la vista los valores que el adaptador ha recibido en la constructora
        // (los valores de los atributos)
        nombreCancion.setText(nombresCanciones[i]);
        autorCancion.setText(autoresCanciones[i]);
        fotoCancionEnPlaylist.setImageResource(R.drawable.iconocancion);


        return view;
    }
}
