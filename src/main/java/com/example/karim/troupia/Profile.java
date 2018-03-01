package com.example.karim.troupia;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.karim.troupia.Model.Memory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    CircleImageView profileImage;
    ImageView backIamage;
    DatabaseReference mDatabaseReference;
    String Urls;
    final int PICKIMAGE_REQUEST = 71;
    Uri filepath;
    DatabaseReference profChild;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseDatabase db;
    private void splitData(Map<String, Object> value) {
        Urls =null;
        for (Map.Entry<String, Object> enter : value.entrySet()) {
            Map<String,Object> singlMap = (Map) enter.getValue();
            String Key=enter.getKey();
            String Id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(Id.equals(Key))
                for(Map.Entry<String, Object> enter1:singlMap.entrySet()) {
                    // Urls.add(Url);
                    Urls=enter1.getValue().toString();
                }
        }
        Picasso.with(this).load(Urls).into(profileImage);
    }
    private void choseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICKIMAGE_REQUEST);

    }
    private String getExt(Uri Url){
        ContentResolver CR=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(CR.getType(Url));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICKIMAGE_REQUEST&&resultCode==RESULT_OK
                &&data!=null&&data.getData()!=null){
             filepath = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),filepath);
                profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Upload();
    }
    private void Upload() {
        final com.example.karim.troupia.Model.Profile profile=new com.example.karim.troupia.Model.Profile();
        if (filepath != null) {
            String img1Url = FirebaseAuth.getInstance().getCurrentUser().getUid() + Calendar.getInstance().getTime().toString()
                    + "." + getExt(filepath);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("adding....");
            progressDialog.show();
            StorageReference ref = storageReference.child("Troupia/" + img1Url);
            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    profile.SetUrl(taskSnapshot.getDownloadUrl().toString());
                    profChild.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
                    Toast.makeText(getBaseContext(), "done", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    // progressDialog.setMessage("Uploaded" + (int) progress);
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild((FirebaseAuth.getInstance().getCurrentUser().getUid()))) {

                    splitData((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profileImage=findViewById(R.id.profileImage);
        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                choseImage();
                return false;
            }
        });
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        profChild = db.getReference("Profiles");
        backIamage=findViewById(R.id.backImage);
        backIamage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
