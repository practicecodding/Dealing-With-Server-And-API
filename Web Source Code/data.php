<?php

$con = mysqli_connect("localhost","wpkkcuac_root","Mmy_databaseH","wpkkcuac_my_database") ; 

$name = $_GET['n'];
$mobile = $_GET['m'];
$email = $_GET['e'];


$sql = "INSERT INTO user_table (name,mobile,email) VALUES ('$name','$mobile','$email') ";

$result = mysqli_query($con,$sql);

if( $result ) echo "data insert successfully" ; 
else "Query Error";


?>