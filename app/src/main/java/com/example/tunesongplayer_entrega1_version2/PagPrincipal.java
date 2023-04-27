package com.example.tunesongplayer_entrega1_version2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PagPrincipal extends AppCompatActivity {

    private String usuario;
    private PlaylistAdapter adapter;

    private String[] resultadoTarealistaNombresPlaylists; //Atributo donde se devolverá la lista de nombres de las playlists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal);

        //Recogemos el usuario, que nos viene dado desde la actividad de inicio de sesión
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Accedemos a los elementos de la vista

        TextView tv_TusPlaylists = (TextView) findViewById(R.id.tv_TusPlaylists);
        String texto = tv_TusPlaylists.getText().toString() + " " + usuario + ":";
        tv_TusPlaylists.setText(texto);

        Button bt_AñadirPlaylist = (Button) findViewById(R.id.bt_AñadirPlaylist);
        Button bt_EliminarPlaylist = (Button) findViewById(R.id.bt_EliminarPlaylist);

        //Listener para cuando se haga click en el botón de añadir una nueva playlist, que abre un diálogo
        bt_AñadirPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogNuevaPlaylist dialogNuevaPlaylist = new DialogNuevaPlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                dialogNuevaPlaylist.setArguments(args);
                dialogNuevaPlaylist.show(getSupportFragmentManager(), "nuevaPlaylist");
            }
        });

        //Listener para cuando se haga click en el botón de eliminar una playlist, que abre un diálogo
        bt_EliminarPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEliminarPlaylist dialogEliminarPlaylist = new DialogEliminarPlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);

                //Aquí enviamos al diálogo las opciones que podrá elegir el usuario
                args.putStringArray("listaNombresPlaylists", resultadoTarealistaNombresPlaylists);

                dialogEliminarPlaylist.setArguments(args);
                dialogEliminarPlaylist.show(getSupportFragmentManager(), "eliminarPlaylist");
            }
        });


        ListView listViewPlaylists = findViewById(R.id.listViewPlaylists);

        //Listener para cuando se haga click en una playlist
        listViewPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView nombrePlaylistClickada = (TextView) view.findViewById(R.id.tv_NombrePlaylist);

                //Cuando se pulsa una playlist se acceda a su contenido, que se muestra en otra actividad
                Intent i = new Intent (getApplicationContext(), CancionesPlaylist.class);
                //Enviamos el nombre de usuario y el nombre de la playlist a esta nueva actividad
                i.putExtra("usuario", usuario);
                i.putExtra("nombrePlaylist", nombrePlaylistClickada.getText().toString());
                startActivity(i);
            }
        });


        refrescarLista();


        //Aquí hago una consulta a la base de datos para recoger únicamente los nombres de las playlists del usuario
        //El resultado de esta consulta se enviará al diálogo que aparece al eliminar playlists (lo que devuelva la consulta serán las opciones que el usuario podrá elegir)
        //He tenido que hacer esta consulta aquí porque si la hacía en el momento en el que se abre el diálogo, al ser
        // el php asíncrono los datos no llegaban a tiempo para cuando se abriera el diálogo (por lo que no aparecían las opciones)
        resultadoTarealistaNombresPlaylists = listarNombresPlaylists();

    }

    //Para actualizar las playlists en cuanto se vuelve tras añadir elementos
    @Override
    protected void onResume() {
        super.onResume();
        refrescarLista();
        resultadoTarealistaNombresPlaylists = listarNombresPlaylists();
    }


    //Método para cuando se haga click en el signo de interrogación
    //Muestra un diálogo que explica brevemente algunas acciones que se pueden realizar en la aplicación
    public void instrucciones_onClick(View v){
        DialogInstrucciones dialogoInstrucciones = new DialogInstrucciones();
        dialogoInstrucciones.show(getSupportFragmentManager(),"instrucciones");
    }


    //Método para cuando se haga click en la lupa
    //Permite buscar entre las canciones disponibles una que se haya escrito en el EditText del buscador
    public void lupa_onClick(View v){

        EditText et_Buscador = (EditText) findViewById(R.id.et_BuscaUnaCancion);
        if (et_Buscador.getText().toString().equals("")) {
            Toast.makeText(this, "Escribe el nombre de una canción", Toast.LENGTH_LONG).show();
        }
        else{
            Intent i = new Intent (this, CancionesBuscadas.class);
            i.putExtra("cancionBuscada",et_Buscador.getText().toString());
            i.putExtra("usuario", usuario);
            startActivity(i);
        }

    }


    //Método para cuando se haga click en el icono de información
    //Muestra información sobre la aplicación
    public void informacion_onClick(View v){
        DialogInformacion dialogInformacion = new DialogInformacion();
        dialogInformacion.show(getSupportFragmentManager(), "informacion");
    }


    //Método para cuando se haga click en el icono del perfil de usuario
    //Permite al usuario acceder a su perfil y ver y modificar su información
    public void perfilUsuario_onClick(View v){
        //Accedemos al perfil del usuario mediante una actividad
        Intent i = new Intent (this, PerfilUsuario.class);
        //Enviamos el nombre de usuario a esta nueva actividad
        i.putExtra("usuario", usuario);
        startActivity(i);
    }


    //Método para mostrar las playlists del usuario
    public void refrescarLista(){

        //Recogemos los datos a mostrar desde la base de datos mediante un servicio web

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("usuario", usuario)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebRefrescarListaPlaylists.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea
                            String[] arrayPlaylists = workInfo.getOutputData().getStringArray("nombresPlaylists");
                            int[] arrayNumsCanciones = workInfo.getOutputData().getIntArray("numsCanciones");

                            //Le pasamos a la vista los datos a mostrar mediante el adaptador
                            ListView listViewPlaylists = (ListView) findViewById(R.id.listViewPlaylists);
                            adapter = new PlaylistAdapter(getApplicationContext(), arrayPlaylists, arrayNumsCanciones, usuario);
                            listViewPlaylists.setAdapter(adapter);

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

    }




    //Método para insertar una nueva playlist
    public void nuevaPlaylist(String nombrePlaylistAInsertar){
        //Comprobamos que el usuario no tiene ya una playlist con ese nombre
        //En caso de que no la tenga, la creamos

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("accion", "nuevaPlaylist")
                .putString("nombreUsuario", usuario)
                .putString("nombrePlaylist", nombrePlaylistAInsertar)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebInsertarEliminarPlaylist.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea
                            String resultadoTarea = workInfo.getOutputData().getString("devolver");

                            //Gestionamos qué se hará en base al resultado
                            if (resultadoTarea.equals("yaExistePlaylist")) {
                                Toast.makeText(getApplicationContext(), "Ya tienes una playlist con ese nombre", Toast.LENGTH_LONG).show();
                            }
                            else if (resultadoTarea.equals("playlistCreada")) {
                                Toast.makeText(getApplicationContext(), "Playlist creada", Toast.LENGTH_LONG).show();
                                refrescarLista();
                                resultadoTarealistaNombresPlaylists = listarNombresPlaylists();
                            }

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);
    }


    //Método para listar los nombres de las playlists del usuario, para que pueda elegir cuál o cuáles eliminar mediante un diálogo
    public String[] listarNombresPlaylists(){

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("accion", "listarPlaylistsUsuario")
                .putString("nombreUsuario", usuario)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebInsertarEliminarPlaylist.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea
                            resultadoTarealistaNombresPlaylists = workInfo.getOutputData().getStringArray("nombresPlaylists");

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

        return resultadoTarealistaNombresPlaylists;
    }


    //Método para eliminar una playlist
    //Se elimina la playlist de la tabla "Playlists" y también sus registros de "RelacionPlaylistsCanciones"
    public void eliminarPlaylist(String nombrePlaylistAEliminar){

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("accion", "eliminarPlaylist")
                .putString("nombreUsuario", usuario)
                .putString("nombrePlaylist", nombrePlaylistAEliminar)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebInsertarEliminarPlaylist.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea
                            String resultadoTarea = workInfo.getOutputData().getString("devolver");

                            if (resultadoTarea.equals("playlistEliminada")){
                                Toast.makeText(getApplicationContext(), "Se han eliminado las playlists seleccionadas", Toast.LENGTH_LONG).show();
                                refrescarLista();
                                resultadoTarealistaNombresPlaylists = listarNombresPlaylists();
                            }

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

    }


}