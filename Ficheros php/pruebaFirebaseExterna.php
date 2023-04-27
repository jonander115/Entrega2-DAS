<?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xjlopezdeahumad0"; #el usuario para esa base de datos
$DB_PASS="N9tVbYiq"; #la clave para ese usuario
$DB_DATABASE="Xjlopezdeahumad0_BaseDeDatos"; #la base de datos a la que hay que conectarse

#Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos la conexión:
if (mysqli_connect_errno()) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	exit();
}

#--------------------Este fichero php sirve para probar la mensajería Firebase de forma externa
#--------------------La prueba consiste en mandar una notificación de bienvenida a todos los usuarios
#(Se entiende que cada usuario solo va a usar la aplicación en un dispositivo, por eso solo se guarda un token por usuario)

#La url de este fichero es: http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/pruebaFirebaseExterna.php

#Recogemos todos los tokens guardados en la base de datos
$resultado = mysqli_query($con, "SELECT Token FROM Usuarios");

#Comprobar si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

if (mysqli_num_rows($resultado) != 0){
	
	#Guardamos los tokens en un array
	$cont = 0;
	while ($fila = mysqli_fetch_row($resultado)){
		$arrayresultados[$cont] = $fila[0];
		$cont++;
	}
	
	#Cabecera de la petición
	$cabecera= array(
		'Authorization: key=AAAAwvFjxvQ:APA91bFSidYIPblSTvWBRw2EI_3NBYhnBJ6dpypcOP-yZg271WwxN37rj-4dvqBhEFmZK_rj8rInYxihZdItN_nxKqQEUah08SdxIlfambb1Fkmccc82xocsUak1Sk0C606tFR5MSjKE',
		'Content-Type: application/json'
	);
	
	#Preparamos el mensaje para mandarlo a todos los dispositivos
	$msg= array(
		'registration_ids'=> $arrayresultados,
		'data' => array(
				"mensaje" => "Prueba de Firebase externa")
		);
	
	#Pasar mensaje a JSON
	$msgJSON= json_encode ( $msg);
		
	$ch = curl_init(); #inicializar el handler de curl

	#indicar el destino de la petición, el servicio FCM de google
	curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');

	#indicar que la conexión es de tipo POST
	curl_setopt( $ch, CURLOPT_POST, true );

	#agregar las cabeceras
	curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
		
	#Indicar que se desea recibir la respuesta a la conexión en forma de string
	curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
		
	#agregar los datos de la petición en formato JSON
	curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );

	#ejecutar la llamada
	$resultado2= curl_exec( $ch );

	#cerrar el handler de curl
	curl_close( $ch );
		
	if (curl_errno($ch)) {
		print curl_error($ch);
	}

	#Devolver el resultado
	if ($resultado2 == True){
		$devolver = 'Se ha enviado la notificación a los dispositivos';
	}
	else{
		$devolver = 'Ha ocurrido un error al enviar la notificación';
	}

	echo $devolver;
	
	
}
else{
	echo 'No hay ningún token en la base de datos';
}


#Cerramos la conexión con la base de datos
mysqli_close($con);

?>