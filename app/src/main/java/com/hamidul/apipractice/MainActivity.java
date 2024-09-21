package com.hamidul.apipractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hamidul.apipractice.databinding.ActivityMainBinding;
import com.hamidul.apipractice.databinding.GetJsonBinding;
import com.hamidul.apipractice.databinding.InsertDataIntoDbBinding;
import com.hamidul.apipractice.databinding.ItemBinding;
import com.hamidul.apipractice.databinding.UserItemBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    GetJsonBinding jsonBinding;
    InsertDataIntoDbBinding insertDataIntoDbBinding;
    HashMap<String,String> hashMap;
    ArrayList <HashMap<String,String>> arrayList = new ArrayList<>();
    boolean flag;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        insertIntoDatabase();

    }

    private void insertIntoDatabase() {

        insertDataIntoDbBinding = InsertDataIntoDbBinding.inflate(getLayoutInflater());
        setContentView(insertDataIntoDbBinding.getRoot());

        getUserDetails();

        insertDataIntoDbBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard();

                insertDataIntoDbBinding.progressBar.setVisibility(View.VISIBLE);

                String name = insertDataIntoDbBinding.edName.getText().toString();
                String mobile = insertDataIntoDbBinding.edMobile.getText().toString();
                String email = insertDataIntoDbBinding.edEmail.getText().toString();

                String url = "https://smhamidul.xyz/api_practice/data_insert.php?n="+name+"&m="+mobile+"&e="+email;

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        insertDataIntoDbBinding.progressBar.setVisibility(View.GONE);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Server Response")
                                .setMessage(response.toString())
                                .setCancelable(true)
                                .show();

                        insertDataIntoDbBinding.edName.setText("");
                        insertDataIntoDbBinding.edMobile.setText("");
                        insertDataIntoDbBinding.edEmail.setText("");
                        insertDataIntoDbBinding.edName.requestFocus();

                        getUserDetails();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        insertDataIntoDbBinding.progressBar.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(error.toString());
                        builder.show();

                    }
                });
                requestQueue.add(stringRequest);


            }
        });

    }

    private void getUserDetails(){

        String url = "https://smhamidul.xyz/api_practice/view.php";

        arrayList = new ArrayList<>();

        insertDataIntoDbBinding.progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                insertDataIntoDbBinding.progressBar.setVisibility(View.GONE);

                for (int x=0; x<response.length(); x++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(x);
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String mobile = jsonObject.getString("mobile");
                        String email = jsonObject.getString("email");

                        hashMap = new HashMap<>();
                        hashMap.put("id",id);
                        hashMap.put("name",name);
                        hashMap.put("mobile",mobile);
                        hashMap.put("email",email);
                        arrayList.add(hashMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }


                if (arrayList.size()>0){
                    UserAdapter userAdapter = new UserAdapter();
                    insertDataIntoDbBinding.listView.setAdapter(userAdapter);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("serverRes",error.toString());
                insertDataIntoDbBinding.progressBar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonArrayRequest);

    }

    public class UserAdapter extends BaseAdapter{

        UserItemBinding userItemBinding;

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myView = layoutInflater.inflate(R.layout.user_item,null);
            userItemBinding = UserItemBinding.bind(myView);

            hashMap = arrayList.get(i);
            String id = hashMap.get("id");
            String name = hashMap.get("name");
            String mobile = hashMap.get("mobile");
            String email = hashMap.get("email");

            userItemBinding.tvName.setText(name);
            userItemBinding.tvMobile.setText(mobile);
            userItemBinding.tvEmail.setText(email);

            userItemBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    insertDataIntoDbBinding.progressBar.setVisibility(View.VISIBLE);

                    String url = "https://smhamidul.xyz/api_practice/update.php?id="+id+"&n="+insertDataIntoDbBinding.edName.getText().toString()+"&m="+insertDataIntoDbBinding.edMobile.getText().toString()+"&e="+insertDataIntoDbBinding.edEmail.getText().toString();

                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            insertDataIntoDbBinding.progressBar.setVisibility(View.GONE);
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Server Response")
                                    .setMessage(response.toString())
                                    .show();

                            insertDataIntoDbBinding.edName.setText("");
                            insertDataIntoDbBinding.edMobile.setText("");
                            insertDataIntoDbBinding.edEmail.setText("");
                            insertDataIntoDbBinding.edName.requestFocus();

                            getUserDetails();
                            
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            insertDataIntoDbBinding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(stringRequest);

                }
            });

            return myView;
        }
    }

    //---------------------------------------------------------------------------

    private void getVideos() {

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        flag = sharedPreferences.getBoolean("flag",false);

        if (flag){
            getVideos();
            editor.putBoolean("flag",false);
            editor.apply();
        }
        else {
            getJson();
            editor.putBoolean("flag",true);
            editor.apply();
        }

        String url = "https://smhamidul.xyz/api_practice/video.json";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                binding.progressBar.setVisibility(View.GONE);

                for (int x=0; x<response.length(); x++){

                    try {
                        JSONObject jsonObject = response.getJSONObject(x);

                        String title = jsonObject.getString("title");
                        String video_id = jsonObject.getString("video_id");

                        hashMap = new HashMap<>();
                        hashMap.put("title",title);
                        hashMap.put("video_id",video_id);
                        arrayList.add(hashMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                MyAdapter myAdapter = new MyAdapter();
                binding.listView.setAdapter(myAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void getJson() {

        jsonBinding = GetJsonBinding.inflate(getLayoutInflater());
        setContentView(jsonBinding.getRoot());

        String url = "https://smhamidul.xyz/api_practice/dummyjson.json";
        jsonBinding.progressBar.setVisibility(View.VISIBLE);
        jsonBinding.textView.setText("");
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonBinding.progressBar.setVisibility(View.GONE);
                try {
                    JSONArray productArray = response.getJSONArray("products");

                    for (int i = 0; i<productArray.length(); i++){
                        JSONObject jsonObject = productArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String category = jsonObject.getString("category");
                        double price = jsonObject.getDouble("price");
                        double discountPercentage = jsonObject.getDouble("discountPercentage");
                        double rating = jsonObject.getDouble("rating");
                        int stock = jsonObject.optInt("stock");
                        jsonBinding.textView.append("Id : "+id+"\nTitle : "+title+"\nDescription : "+description+"\nCategory : "+category+"\n");
                        jsonBinding.textView.append("Price : "+price+"\nDiscount Percentage : "+discountPercentage);
                        jsonBinding.textView.append("\nRating : "+rating+"\nStock : "+stock+"\nTags : ");

                        JSONArray tagsArray = jsonObject.getJSONArray("tags");
                        for (int x=0; x<tagsArray.length(); x++){
                            String tags = tagsArray.getString(x);
                            int y = tagsArray.length() - 1;
                            if (x==y){
                                jsonBinding.textView.append(tags);
                            }
                            else {
                                jsonBinding.textView.append(tags+", ");
                            }

                        }

                        String brand = jsonObject.optString("brand");
                        String sku = jsonObject.optString("sku");
                        int weight = jsonObject.getInt("weight");

                        JSONObject dimensionsObject = jsonObject.getJSONObject("dimensions");
                        double width = dimensionsObject.optDouble("width");
                        double height = dimensionsObject.optDouble("height");
                        double depth = dimensionsObject.getDouble("depth");
                        jsonBinding.textView.append("\nBrand : "+brand+"\nSku : "+sku+"\nWeight : "+weight);
                        jsonBinding.textView.append("\nWidth : "+width+"\nHeight : "+height+"\nDepth : "+depth);

                        String warrantyInformation = jsonObject.optString("warrantyInformation");
                        String shippingInformation = jsonObject.optString("shippingInformation");
                        String availabilityStatus = jsonObject.optString("availabilityStatus");
                        jsonBinding.textView.append("\nWarranty Information : "+warrantyInformation+"\nShipping Information : "+shippingInformation);
                        jsonBinding.textView.append("\nAvailability Status : "+availabilityStatus+"\nProduct Review : \n");

                        JSONArray reviewsArray = jsonObject.getJSONArray("reviews");
                        for (int a=0; a<reviewsArray.length(); a++){
                            JSONObject reviewsObject = reviewsArray.getJSONObject(a);
                            int cRating = reviewsObject.optInt("rating");
                            String comment = reviewsObject.getString("comment");
                            String date = reviewsObject.getString("date");

                            if (a==0){
                                jsonBinding.textView.append("{\nRating : "+cRating+"\nComment : "+comment+"\nDate : "+date);
                            }
                            else {
                                jsonBinding.textView.append("\n\nRating : "+cRating+"\nComment : "+comment+"\nDate : "+date);
                            }
                            String reviewerName = reviewsObject.getString("reviewerName");
                            String reviewerEmail = reviewsObject.getString("reviewerEmail");
                            if (a==reviewsArray.length()-1){
                                jsonBinding.textView.append("\nReviewer Name : "+reviewerName+"\nReviewer Email : "+reviewerEmail+"\n}");
                            }
                            else {
                                jsonBinding.textView.append("\nReviewer Name : "+reviewerName+"\nReviewer Email : "+reviewerEmail);
                            }

                        }

                        String returnPolicy = jsonObject.getString("returnPolicy");
                        int minimumOrderQuantity = jsonObject.getInt("minimumOrderQuantity");
                        jsonBinding.textView.append("\nReturn Policy : "+returnPolicy+"\nMinimum Order Quantity : "+minimumOrderQuantity);
                        JSONObject metaObject = jsonObject.getJSONObject("meta");
                        String createdAt = metaObject.getString("createdAt");
                        String updatedAt = metaObject.getString("updatedAt");
                        String barcode = metaObject.getString("barcode");
                        String qrCode = metaObject.getString("qrCode");
                        jsonBinding.textView.append("\nCrated At : "+createdAt+"\nUpdate At : "+updatedAt+"\nBarcode : "+barcode);
                        jsonBinding.textView.append("\nQr Code : "+qrCode);

                        JSONArray imagesArray = jsonObject.getJSONArray("images");
                        for (int b=0; b<imagesArray.length(); b++){
                            String image = imagesArray.getString(b);
                            jsonBinding.textView.append("\nImage : "+image);
                        }
                        String thumbnail = jsonObject.getString("thumbnail");
                        jsonBinding.textView.append("\nThumbnail : "+thumbnail);


                        jsonBinding.textView.append("\n\n\n");
                    }







                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                jsonBinding.progressBar.setVisibility(View.GONE);
                jsonBinding.textView.setText("Volley Error");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }



    public class MyAdapter extends BaseAdapter{

        ItemBinding binding;

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myView = layoutInflater.inflate(R.layout.item,null);
            binding = ItemBinding.bind(myView);

            hashMap = arrayList.get(position);
            String title = hashMap.get("title");
            String video_id = hashMap.get("video_id");
            String image_url = "https://img.youtube.com/vi/"+video_id+"/0.jpg";
            String video_url = "https://www.youtube.com/embed/"+video_id;

            binding.tvTitle.setText(title);
            Picasso.get()
                    .load(image_url)
                    .placeholder(R.drawable.my_image)
                    .into(binding.imageThumb);



            binding.imageThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity2.video_url = video_url;
                    startActivity(new Intent(MainActivity.this,MainActivity2.class));
                }
            });

            return myView;
        }
    }


    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


}