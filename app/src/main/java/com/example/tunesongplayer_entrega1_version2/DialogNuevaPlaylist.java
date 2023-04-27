package com.example.tunesongplayer_entrega1_version2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogNuevaPlaylist extends DialogFragment {

    private String usuario;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nueva playlist");
        builder.setMessage("Introduce el nombre de la nueva playlist");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialog_nueva_playlist, null);
        builder.setView(aspectoDialog);

        //Opción de crear playlist, que añade la playlist y cierra el diálogo
        builder.setPositiveButton("Crear playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Recogemos el texto del EditText donde el usuario ha escrito el nombre de la playlist
                EditText et_NombreNuevaPlaylist = (EditText) aspectoDialog.findViewById(R.id.et_NuevaPlaylist);

                //Comprobamos que el nombre de la playlist no está en blanco
                if (et_NombreNuevaPlaylist.getText().toString().length() != 0){

                    String nombrePlaylist = et_NombreNuevaPlaylist.getText().toString();

                    //Llamamos al método de la página principal de la aplicación encargado de crear la playlist
                    //(No he podido hacer la llamada al servicio web desde el diálogo porque había problemas con el contexto de la aplicación)
                    ((PagPrincipal) getActivity()).nuevaPlaylist(nombrePlaylist);

                    dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Por favor, introduce un nombre para la playlist", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Opción de cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });


        return builder.create();
    }

}
