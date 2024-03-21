package com.chirag.tybcaproject.AdminSide;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chirag.tybcaproject.Domain.Foods;
import com.chirag.tybcaproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.CropImageView;

import java.io.File;

public class Upload_Activity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, priceEditText, ratingEditText, categoryIdEditText;
    private Button postButton;
    private ImageButton imageButton;
    CropImageView cropImageView;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize Firebase components
        databaseReference = FirebaseDatabase.getInstance().getReference("Foods");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("imagesPath");

        // Initialize views
        titleEditText = findViewById(R.id.titleTxt);
        descriptionEditText = findViewById(R.id.descriptionTxt);
        priceEditText = findViewById(R.id.priceTxt);
        ratingEditText = findViewById(R.id.ratingTxt);
        categoryIdEditText = findViewById(R.id.categoryIdTxt);
        postButton = findViewById(R.id.post);
        imageButton = findViewById(R.id.image_upload);
        cropImageView = findViewById(R.id.crop_image_view);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToFirebase();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
    }

    private void onSelectImageClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            // Open image picker
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI
            imageUri = data.getData();
            cropImageView.setImageURI(imageUri);

            // Start image cropping
            startUCrop();
        }
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                imageUri = resultUri;
                imageButton.setImageURI(imageUri); // Set cropped image URI to ImageView
            }
        }
    }

    private void startUCrop() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(true);

        UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "cropped_image")))
                .withOptions(options)
                .start(this);
    }

    private void uploadDataToFirebase() {
        // First, upload image to Firebase Storage
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("Please wait while we upload and process the image.");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUri);

            // Add progress listener
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading " + (int) progress + "%");
                }
            });

            // Continue with success and failure listeners
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            // Image uploaded successfully, now get its URL
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    // Image URL retrieved, now proceed to upload other data to Firebase Database

                                    String Title = titleEditText.getText().toString().trim();
                                    String Description = descriptionEditText.getText().toString().trim();
                                    String Price = priceEditText.getText().toString().trim();
                                    String Star = ratingEditText.getText().toString().trim();
                                    String CategoryId= categoryIdEditText.getText().toString().trim();

                                    if (Title.isEmpty() || Description.isEmpty() || Price.isEmpty() || Star.isEmpty() || CategoryId.isEmpty() ) {
                                        Toast.makeText(Upload_Activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    double price = Double.parseDouble(Price);
                                    double star = Double.parseDouble(Star);
                                    int categoryId = Integer.parseInt(CategoryId);

                                    Foods food = new Foods();
                                    food.setTitle(Title);
                                    food.setDescription(Description);
                                    food.setPrice(Double.parseDouble(Price));
                                    food.setStar(Double.parseDouble(Star));
                                    food.setCategoryId(Integer.parseInt(CategoryId));
                                    food.setImagePath(imageUrl);

                                    // Push data to Firebase Database
                                    databaseReference.push().setValue(food)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Upload_Activity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    // Clear input fields after successful upload
                                                    titleEditText.setText("");
                                                    descriptionEditText.setText("");
                                                    priceEditText.setText("");
                                                    ratingEditText.setText("");
                                                    categoryIdEditText.setText("");
                                                } else {
                                                    Toast.makeText(Upload_Activity.this, "Failed to upload data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Upload_Activity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Upload_Activity.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}
