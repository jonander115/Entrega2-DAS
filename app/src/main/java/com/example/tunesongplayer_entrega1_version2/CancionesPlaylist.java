package com.example.tunesongplayer_entrega1_version2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CancionesPlaylist extends AppCompatActivity {

    private String playlist;
    private String usuario;
    private ListView listaCancionesPlaylist;
    private ImageView fotoPlaylist;
    private Uri uriFoto;
    private int CODIGO_FOTO_ARCHIVO = 12;
    private byte[] byteArrayFotoPlaylist;
    private boolean sonandoMusica;
    private MediaPlayer musicaMP3;


    //Se recoge la foto hecha con la cámara y se pone en el ImageView correspondiente
    //La foto se sube al servidor y se guarda en una carpeta de la aplicación
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {

            //Tenemos la uri de la foto en el atributo uriFoto
            //Convertimos la uri en Bitmap
            //Código extraído de https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
            Bitmap bitmapFoto = null;
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFoto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Ajustamos la foto al ImageView
            Bitmap bitmapredimensionado = ajustarAImageView(bitmapFoto);

            //Ponemos la foto en el ImageView
            fotoPlaylist.setImageBitmap(bitmapredimensionado);

            //Enviamos la foto al servidor
            //Para ello, primero convertimos el Bitmap en un String en Base64
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapredimensionado.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            String fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);


            //Guardo el array de bytes de la imagen en un atributo
            //De esta manera puedo guardarlo en el Bundle de onSaveInstanceState, restaurarlo y decodificarlo
            //Así evito que la foto se pierda al girar el móvil
            byteArrayFotoPlaylist = fototransformada;

            //Subir foto al servidor
            subirFoto(fotoen64);

            //Ahora se podría lanzar un Broadcast para actualizar el repositorio multimedia donde se ha almacenado la foto sin tener que reiniciar el dispositivo
            //Pero, como es un directorio privado de la aplicación y no va a ser accedido por otras aplicaciones, no tiene sentido hacerlo

        }
        else {
            Log.d("fotoPlaylist", "Problema con foto");
        }
    }



    //Launcher que recoge la imagen elegida de la galería y la pone en el ImageView correspondiente
    //La imagen se sube al servidor
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        if (uri != null) {

            Log.d("fotoPlaylist", "URI elegida: " + uri);

            //Para poner la imagen en el ImageView, primero voy a ajustar su tamaño
            //Hay que convertir el uri en Bitmap
            //Código obtenido de https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
            Bitmap bitmapFoto = null;
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Ajustamos la imagen al ImageView
            Bitmap bitmapredimensionado = ajustarAImageView(bitmapFoto);

            //Ponemos la foto en el ImageView
            fotoPlaylist.setImageBitmap(bitmapredimensionado);

            //Enviamos la imagen al servidor
            //Para ello, primero convertimos el bitmap en un String en Base64
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapredimensionado.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            String fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);


            //Guardo el array de bytes de la imagen en un atributo
            //De esta manera puedo guardarlo en el Bundle de onSaveInstanceState, restaurarlo y decodificarlo
            //Así evito que la foto se pierda al girar el móvil
            byteArrayFotoPlaylist = fototransformada;

            subirFoto(fotoen64);

        } else {
            Log.d("fotoPlaylist", "No se ha elegido imagen");
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones_playlist);

        sonandoMusica = false;


        //Recogemos elementos de la vista
        fotoPlaylist = (ImageView) findViewById(R.id.fotoEnListaCancionesPlaylist);
        listaCancionesPlaylist = (ListView) findViewById(R.id.listaCancionesPlaylist);

        //Recogemos el usuario y el nombre de la playlist
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            playlist = extras.getString("nombrePlaylist");

            TextView tv_CancionesPlaylist = (TextView) findViewById(R.id.tv_CancionesPlaylist);
            String texto = tv_CancionesPlaylist.getText().toString() + " " + playlist;
            tv_CancionesPlaylist.setText(texto);

        }

        //Click largo para eliminar la canción seleccionada
        listaCancionesPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos información para enviar
                TextView tv_NombreCancionEnPlaylist = (TextView) view.findViewById(R.id.tv_NombreCancionEnPlaylist);
                String nombreCancionDePlaylistPulsada = tv_NombreCancionEnPlaylist.getText().toString();
                TextView tv_AutorCancionEnPlaylist = (TextView) view.findViewById(R.id.tv_AutorCancionEnPlaylist);
                String autorCancionDePlaylistPulsada = tv_AutorCancionEnPlaylist.getText().toString();

                //Preparamos y lanzamos el diálogo
                DialogEliminarCancionDePlaylist dialogEliminarCancionDePlaylist = new DialogEliminarCancionDePlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("cancion", nombreCancionDePlaylistPulsada);
                args.putString("autor", autorCancionDePlaylistPulsada);
                args.putString("playlist", playlist);
                dialogEliminarCancionDePlaylist.setArguments(args);
                dialogEliminarCancionDePlaylist.show(getSupportFragmentManager(), "eliminarCancion");

                return true;

            }
        });

        //Click corto para escuchar la canción seleccionada. Aparece una notificación, y se han pedido los permisos previamente
        listaCancionesPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos nombre y autor de la canción
                TextView tv_NombreCancionEnPlaylist = (TextView) view.findViewById(R.id.tv_NombreCancionEnPlaylist);
                String nombreCancionDePlaylistPulsada = tv_NombreCancionEnPlaylist.getText().toString();
                TextView tv_AutorCancionEnPlaylist = (TextView) view.findViewById(R.id.tv_AutorCancionEnPlaylist);
                String autorCancionDePlaylistPulsada = tv_AutorCancionEnPlaylist.getText().toString();


                //Ponemos / Paramos la música
                if (sonandoMusica==false){
                    musicaMP3 = iniciarMusica(nombreCancionDePlaylistPulsada);
                    musicaMP3.start();
                    sonandoMusica = true;
                }
                else{
                    musicaMP3.stop();
                    sonandoMusica = false;
                }



                //Definimos el Manager y el Builder
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canal1");

                //El Builder nos permite definir las características de la notificación
                builder.setSmallIcon(android.R.drawable.stat_sys_headset)
                        .setContentTitle("Escuchando música")
                        .setContentText("Escuchando " + nombreCancionDePlaylistPulsada + " de " + autorCancionDePlaylistPulsada)
                        .setSubText("De tu playlist " + playlist)
                        .setAutoCancel(true);

                //las versiones mayores que la Oreo necesitan un canal
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel canal = new NotificationChannel("canal1", "CanalNotificacionPlaylist", NotificationManager.IMPORTANCE_DEFAULT);

                    canal.setDescription("Canal de audio de playlist");
                    canal.setLightColor(Color.BLUE);

                    //Unimos el canal al manager
                    manager.createNotificationChannel(canal);

                    //El manager lanza la notificación
                    manager.notify(1, builder.build());
                }

            }
        });

        refrescarLista();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refrescarLista();
    }

    //Método para mostrar las canciones de la playlist
    public void refrescarLista() {
        //Recogemos los datos a mostrar desde la base de datos

        //Utilizamos un servicio web alojado en el servidor para obtener los datos de las canciones de la playlist

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("nombrePlaylist", playlist)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebRefrescarCancionesPlaylist.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea

                            String[] cancionesPlaylist = workInfo.getOutputData().getStringArray("cancionesPlaylist");
                            String[] autoresCanciones = workInfo.getOutputData().getStringArray("autoresCanciones");

                            //Le pasamos a la vista los datos a mostrar mediante el adaptador
                            CancionPlaylistAdapter adapter = new CancionPlaylistAdapter(getApplicationContext(), cancionesPlaylist, autoresCanciones);
                            listaCancionesPlaylist.setAdapter(adapter);

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

    }


    //Método para sacar una foto y ponerla como imagen de la playlist
    public void onClick_SacarFoto(View v){

        //Antes de lanzar el Intent hay que preparar el fichero donde se guardará la foto
        //Utilizamos un FileProvider (definido en res/xml/file_provider.xml)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefichero = "IMG_" + timeStamp + "_";
        File directorio = this.getFilesDir(); //Directorio donde almacenar la imagen, definido en el FileProvider
        //El directorio es: data/data/com.example.tunesongplayer_entrega1_version2/files
        //Se trata de un directorio privado de la aplicación

        File ficheroImagen = null;
        uriFoto = null;
        try{
            ficheroImagen = File.createTempFile(nombrefichero, ".jpg", directorio);
            uriFoto = FileProvider.getUriForFile(this, "com.example.tunesongplayer_entrega1_version2.provider", ficheroImagen);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Lanzamos el Intent para hacer la foto
        Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto); //Le mandamos al Intent la uri del fichero donde se almacenará la foto
        startActivityForResult(intentFoto,CODIGO_FOTO_ARCHIVO);

    }


    //Método para elegir una imagen de la galería y ponerla como imagen de la playlist
    public void onClick_ElegirGaleria(View v){
        //Construimos el builder para lanzar la selección de imagen

        //He adaptado código presente en https://stackoverflow.com/questions/73999566/how-to-construct-pickvisualmediarequest-for-activityresultlauncher
        // debido a que estaba teniendo problemas con los tipos de datos
        //Aun así, como comentan en esa página de Stack Overflow, Android Studio sigue indicando un error en la siguiente línea, aunque la selección
        // de fotos se haga correctamente:
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build());
    }


    //Método para eliminar una canción de una playlist cuando el usuario acepta el diálogo dedicado a ello
    public void eliminarCancionDePlaylist(String nombreCancion, String autorCancion){

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("nombrePlaylist", playlist)
                .putString("nombreCancion", nombreCancion)
                .putString("autorCancion", autorCancion)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebGestionarCancionesDePlaylist.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            Toast.makeText(getApplicationContext(), "Se ha eliminado la canción de la playlist", Toast.LENGTH_LONG).show();

                            refrescarLista();

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

    }


    //Método para ajustar una imagen al tamaño del ImageView
    private Bitmap ajustarAImageView(Bitmap bitmapFoto){

        //Recogemos ancho y alto del ImageView y de la imagen
        int anchoDestino = fotoPlaylist.getWidth();
        int altoDestino = fotoPlaylist.getHeight();
        int anchoImagen = bitmapFoto.getWidth();
        int altoImagen = bitmapFoto.getHeight();

        //Calculamos ancho y alto finales
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }

        //Escalamos la imagen al tamaño del ImageView
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);

        return bitmapredimensionado;
    }

    //Método para subir la imagen de la playlist al servidor
    public void subirFoto(String fotoen64){
        //Llamamos al servicio web encargado de subir la foto
        //He utilizado la librería Volley, porque al utilizar WorkManager no me dejaba enviar datos de más de 10240 bytes
        //https://developer.android.com/training/volley?hl=es-419

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/fotoPlaylist.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor tras haberse subido la imagen
                Toast.makeText(getApplicationContext(), "Foto actualizada", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Toast.makeText(getApplicationContext(), "Error al subir la foto", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios para subir la imagen
                params.put("accion", "nuevaFoto");
                params.put("imagen", fotoen64);
                params.put("nombreUsuario", usuario);
                params.put("nombrePlaylist", playlist);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putByteArray("foto", byteArrayFotoPlaylist);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Si hay un valor en el atributo bitmapFotoPlaylist es porque el usuario ha hecho una foto con la cámara
        //Ponemos la foto en el ImageView
        byteArrayFotoPlaylist = savedInstanceState.getByteArray("foto");

        if (byteArrayFotoPlaylist!=null){

            //Decodificamos la imagen
            //Código extraído de https://stackoverflow.com/questions/7620401/how-to-convert-image-file-data-in-a-byte-array-to-a-bitmap
            Bitmap imagenBitmap = BitmapFactory.decodeByteArray(byteArrayFotoPlaylist, 0, byteArrayFotoPlaylist.length);

            //Mostramos la imagen en el ImageView
            fotoPlaylist.setImageBitmap(imagenBitmap);

        }
    }


    //Método para reproducir la canción pulsada
    public MediaPlayer iniciarMusica(String nombreCancion){

        musicaMP3 = new MediaPlayer();
        if (nombreCancion.equals("Boundless space")){
            musicaMP3 = MediaPlayer.create(this, R.raw.boundlessspace);
        }
        else if (nombreCancion.equals("Closer to the sun")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.closertothesun);
        }
        else if (nombreCancion.equals("Plain")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.plain);
        }
        else{
            musicaMP3 = MediaPlayer.create(this, R.raw.boundlessspace);
        }
        return musicaMP3;
    }


}