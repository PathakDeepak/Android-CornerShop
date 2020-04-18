package com.coder24.cornershop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName;
    private Button addNewProductBtn;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private String pName, pDescription, pPrice;
    private String saveCurrentDate, saveCurrentTime;
    private String productUniqueKey, downloadImageUrl;

    private StorageReference productImageRef;
    private DatabaseReference productDatabaseRef;


    private static final int galleryPick = 1;
    private Uri imageUri;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        //Toast.makeText(this, "Welcome to Admin Panel!..", Toast.LENGTH_SHORT).show();

        addNewProductBtn = (Button)findViewById(R.id.add_new_product);
        inputProductImage = (ImageView) findViewById(R.id.select_product_image);
        inputProductName = (EditText) findViewById(R.id.product_name);
        inputProductDescription = (EditText) findViewById(R.id.product_description);
        inputProductPrice = (EditText) findViewById(R.id.product_price);

        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Products");

        categoryName = getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        loadingBar = new ProgressDialog(this);
    }

    public void clickProductImage(View view) {
        openGallery();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }

    public void addProduct(View view) {
        validateProductData();
    }

    private void validateProductData() {
        pDescription = inputProductDescription.getText().toString();
        pName = inputProductName.getText().toString();
        pPrice = inputProductPrice.getText().toString();
        
        if(imageUri == null){
            Toast.makeText(this, "Product image is mandatory..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pDescription)){
            Toast.makeText(this, "Please provide product description..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pName)){
            Toast.makeText(this, "Please provide product name..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pPrice)){
            Toast.makeText(this, "Please provide product price..", Toast.LENGTH_SHORT).show();
        }
        else{
            storeProductInfo();
        }
    }

    private void storeProductInfo() {
        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("Please wait, while product is being added.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM,dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productUniqueKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productUniqueKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);
        //if faliure occurs
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                //get Image URL link
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Got product image URL successfully..", Toast.LENGTH_SHORT).show();

                            //save product info to database
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productUniqueKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", pDescription);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", pPrice);
        productMap.put("name", pName);

        productDatabaseRef.child(productUniqueKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class );
                            startActivity(intent);
                            Toast.makeText(AdminAddNewProductActivity.this, "product is added successfully...", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
