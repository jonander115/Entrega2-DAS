package com.example.tunesongplayer_entrega1_version2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogInformacion extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Información de la aplicación");
        builder.setMessage("TuneSongPlayer");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialog_informacion, null);
        builder.setView(aspectoDialog);

        //Damos valor a los elementos del diálogo
        TextView tv_Informacion = aspectoDialog.findViewById(R.id.tv_Informacion);
        String informacion = "TuneSongPlayer es una aplicación desarrollada en Android como entrega de la asignatura \"Desarrollo Avanzado de Software\" " +
                "del grado en Ingeniería Informática de Gestión y Sistemas de Información. " +
                "Permite búsqueda entre una selección de canciones, creación de playlists o listas de reproducción, y reproducción de las canciones guardadas." +
                "\n\nLa aplicación ha sido desarrollada por el alumno Jon Ander López de Ahumada. Se prohíbe su venta.";
        tv_Informacion.setText(informacion);

        Button bt_CodigoFuente = (Button) aspectoDialog.findViewById(R.id.bt_CodigoFuente);
        bt_CodigoFuente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent implícito que abre el repositorio de GitHub con el código fuente de la aplicación
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jonander115/Entrega2-DAS"));
                startActivity(i);
            }
        });

        //Opción de Aceptar, que cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }

}
