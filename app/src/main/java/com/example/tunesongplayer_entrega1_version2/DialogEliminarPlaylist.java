package com.example.tunesongplayer_entrega1_version2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class DialogEliminarPlaylist extends DialogFragment {

    private String usuario;

    private String[] listaNombresPlaylists;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            listaNombresPlaylists = getArguments().getStringArray("listaNombresPlaylists");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar playlists");

        final ArrayList<Integer> opcionesElegidas = new ArrayList<Integer>();

        if (listaNombresPlaylists.length == 0){
            builder.setMessage("- No tienes ninguna playlist -");
        }
        else{

            builder.setMultiChoiceItems(listaNombresPlaylists, null, new DialogInterface.OnMultiChoiceClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if (b == true){
                        opcionesElegidas.add(i);
                    }
                    else if (opcionesElegidas.contains(i)){
                        opcionesElegidas.remove(Integer.valueOf(i));
                    }
                }

            });


      }

        //Opción de Aceptar, que borra las playlists seleccionadas y cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (opcionesElegidas.size() == 0){
                    Toast.makeText(getContext(), "No se ha eliminado ninguna playlist", Toast.LENGTH_LONG).show();
                }
                else{

                    //Recogemos y eliminamos las playlists seleccionadas
                    for (int elem=0; elem<opcionesElegidas.size(); elem++){

                        int numeroOpcionElegida = opcionesElegidas.get(elem);
                        String nombrePlaylistABorrar = listaNombresPlaylists[numeroOpcionElegida];

                        //Eliminamos la playlist (el método está en la página principal)
                        //(No he podido hacer la llamada al servicio web desde el diálogo porque había problemas con el contexto de la aplicación)
                        ((PagPrincipal) getActivity()).eliminarPlaylist(nombrePlaylistABorrar);

                    }

                }

                dismiss();
            }
        });


        //Opción de Cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });


        return builder.create();
    }

}
