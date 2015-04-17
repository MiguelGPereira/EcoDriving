<?php
	echo "EcoDriving</br>"; 

	$servername = "127.0.0.1";
	$username = "root";
	$password = "";

	// Create connection
	$conn = new mysqli($servername, $username, $password);

	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 
	echo "Connected successfully</br>";


	echo "Sample query</br>";
	if ($result = $conn->query("SELECT * FROM test.horario_ume3400 LIMIT 10")) {
	    printf("Select returned %d rows.</br>", $result->num_rows);

	    while($row = $result->fetch_row()){
	    	for ($i=0; $i <count($row) ; $i++) { 
	    		printf("%s ",$row[$i]);
	    	}
	    	echo "</br>";
	    }
	    $result->close();
	}


	$conn->close();
	echo "Connection closed</br>";

	
	
?>