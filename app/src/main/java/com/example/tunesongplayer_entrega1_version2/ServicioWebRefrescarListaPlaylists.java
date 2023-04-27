package com.example.tunesongplayer_entrega1_version2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServicioWebRefrescarListaPlaylists extends Worker {
    public ServicioWebRefrescarListaPlaylists(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private String[] nombresPlaylists;
    private int[] numsCanciones;
    private String[] imagenes;

    @NonNull
    @Override
    public Result doWork() {
        //Dirección del fichero php con el servicio web, ubicado en el servidor de la asignatura
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/refrescarListaPlaylists.php";

        //Realizamos la conexión al servidor
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            //Configuramos elementos de la conexión
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //La tarea ha recibido los parámetros que requiere el servicio web mediante un objeto de tipo Data
            String usuario = getInputData().getString("usuario");

            //Preparamos los parámetros para enviarlos al fichero php. Para ello haré uso de la clase Uri
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("usuario", usuario);
            String parametros = builder.build().getEncodedQuery();

            //Configuramos la variable de la conexión para indicarle que vamos a enviarle parámetros, especificando el método HTTP y el formato a utilizar
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Utilizamos el objeto PrintWriter para incluir los parámetros en la llamada
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();


            //Realizamos la llamada al servicio web y recogemos el resultado:
            int statusCode = urlConnection.getResponseCode();

            //Comprobamos si la llamada ha ido bien y procesamos el resultado:
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    //En la variable result vamos generando el resultado final
                    result += line;
                }
                inputStream.close();

                //El resultado es un array
                JSONArray jsonArray = new JSONArray(result);

                nombresPlaylists = new String[jsonArray.length()];
                numsCanciones = new int[jsonArray.length()];

                for(int i = 0; i < jsonArray.length(); i++)
                {
                    nombresPlaylists[i] = jsonArray.getJSONObject(i).getString("Nombre");
                    numsCanciones[i] = jsonArray.getJSONObject(i).getInt("NumCanciones");
                }


            } else {
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error en la obtención de la lista de playlists", Toast.LENGTH_LONG).show();
                Log.d("servicioWeb", "Status Refrescar Lista Playlists no es 200");
            }



        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Para devolver el resultado a la actividad que ha llamado a la tarea, creamos un objeto Data:
        Data resultadoADevolver = new Data.Builder()
                .putStringArray("nombresPlaylists", nombresPlaylists)
                .putIntArray("numsCanciones", numsCanciones)
                .build();


        return Result.success(resultadoADevolver);
    }
}
