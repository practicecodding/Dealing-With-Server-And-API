<?php

$con = mysqli_connect("localhost","wpkkcuac_root","Mmy_databaseH","wpkkcuac_my_database");

$id = $_GET['id'];
$name = $_GET['n'];
$mobile = $_GET['m'];
$email = $_GET['e'];

$sql = "UPDATE user_table SET name = '$name', mobile = '$mobile', email = '$email' WHERE id = '$id' ";

$result = mysqli_query($con,$sql);

if($result) echo "Successfully Updated";
else echo "Something Wrong";

?>
