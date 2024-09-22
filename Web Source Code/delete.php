<?php

$con = mysqli_connect("localhost","wpkkcuac_root","Mmy_databaseH","wpkkcuac_my_database");

$id = $_GET['id'];

$sql = "DELETE FROM user_table WHERE id = '$id' ";

$result = mysqli_query($con,$sql);

if($result) echo "Successfully Deleted";
else echo "Something Wrong";

?>