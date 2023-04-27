package com.example.tunesongplayer_entrega1_version2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class PlaylistAdapter extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private String[] nombresPlaylists;
    private int[] numCancEnPlaylists;
    private String usuario;

    //Constructora
    public PlaylistAdapter(Context pContexto, String[] pNombresPlaylists, int[] pNumCancEnPlaylists, String pUsuario) {
        contexto = pContexto;
        nombresPlaylists = pNombresPlaylists;
        numCancEnPlaylists = pNumCancEnPlaylists;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        usuario = pUsuario;
    }


    //Devuelve el número de elementos
    @Override
    public int getCount() {
        return nombresPlaylists.length;
    }

    //Devuelve el elemento i (en este caso, el nombre de la playlist)
    @Override
    public Object getItem(int i) {
        return nombresPlaylists[i];
    }

    //Devuelve el identificador del elemento i
    @Override
    public long getItemId(int i) {
        return i;
    }

    //Devuelve la vista (cómo se visualiza el elemento i)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_playlist,null);

        //Recogemos los elementos de la vista
        TextView nombrePlaylist = (TextView) view.findViewById(R.id.tv_NombrePlaylist);
        TextView numCancionesPlaylist = (TextView) view.findViewById(R.id.tv_NumCanciones);
        ImageView fotoPlaylist = (ImageView) view.findViewById(R.id.fotoPlaylist);
        LinearLayout layoutPlaylist = (LinearLayout) view.findViewById(R.id.layoutPlaylist);

        //Asignamos a los elementos de la vista los valores que el adaptador ha recibido en la constructora
        // (los valores de los atributos)
        nombrePlaylist.setText(nombresPlaylists[i]);
        numCancionesPlaylist.setText(numCancEnPlaylists[i] + " canciones");

        //Ahora hay que cargar la imagen de la playlist
        //Lo hacemos mediante un servicio web

        //Llamamos al servicio web encargado de recoger la imagen
        //He utilizado la librería Volley, porque al utilizar WorkManager no me dejaba enviar datos de más de 10240 bytes
        //https://developer.android.com/training/volley?hl=es-419

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(contexto);

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/fotoPlaylist.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor tras haberse recogido la imagen
                try {

                    //La respuesta es un JSON
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(response);

                    //Extraemos la imagen
                    String imagenString = (String) json.get("imagen");

                    if (imagenString.equals("")){
                        //El usuario no ha puesto ninguna foto a la playlist. Ponemos la imagen predefinida de las playlists
                        fotoPlaylist.setImageResource(R.drawable.iconoplaylist);
                    }
                    else{
                        //Decodificamos la imagen
                        byte[] imagenByteArray = Base64.decode(imagenString, Base64.DEFAULT);
                        //Código extraído de https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap
                        Bitmap imagenBitmap = BitmapFactory.decodeByteArray(imagenByteArray, 0, imagenByteArray.length);

                        //Mostramos la imagen en el ImageView
                        fotoPlaylist.setImageBitmap(imagenBitmap);
                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(contexto, "Error al recoger la imagen", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios para recoger la imagen
                params.put("accion","recogerFoto");
                params.put("nombreUsuario", usuario);
                params.put("nombrePlaylist", nombresPlaylists[i]);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);


        return view;
    }



}
