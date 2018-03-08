package com.example.karim.troupia;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.aviary.android.feather.sdk.internal.headless.utils.MegaPixels;
import com.example.karim.troupia.Model.Memory;
import com.example.karim.troupia.Model.Story;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class photoeidt extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    private static final int IMG_CODE_EDIT = 263;
    private static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView ivImg;
    Uri imguri ;
    Uri filepath;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference storyDb;
    FirebaseDatabase db;
    LocationManager locationManager;
    double longtuide,lontuide;
    Geocoder gecoder;
    List<Address> list;
    boolean locationCheck=false;
    CircleImageView BtnUpload;
    ImageView ImageBack;
    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            longtuide = location.getLongitude();
            lontuide = location.getLatitude();

            gecoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                list = gecoder.getFromLocation(lontuide, longtuide, 1);
                String Address = list.get(0).getAddressLine(0);
                String Area = list.get(0).getLocality();
                String City = list.get(0).getAdminArea();
                String Country = list.get(0).getCountryName();
                locationCheck=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void Upload() throws FileNotFoundException {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location1 = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location1);
        if(!locationCheck) Toast.makeText(this, "Please enter the location", Toast.LENGTH_SHORT).show();
        else {
            final Story story=new Story();
           // story.SetDate(Date);
            story.SetLongtuide(String.valueOf(longtuide));
            story.SetLituide(String.valueOf(lontuide));
            if (filepath != null) {
                String img1Url=FirebaseAuth.getInstance().getCurrentUser().getUid()+ Calendar.getInstance().getTime().toString()
                        +"."+getExt(filepath);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("adding....");
                progressDialog.show();
                StorageReference ref = storageReference.child("Stories/" + img1Url);
                File file=new File(filepath.toString());
                FileInputStream fileInputStream=new FileInputStream(file);
                ref.putStream(fileInputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        story.SetURL(taskSnapshot.getDownloadUrl().toString());
                        Date currentTime = Calendar.getInstance().getTime();
                        storyDb.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentTime.toString()).setValue(story);
                        Toast.makeText(getBaseContext(), "done", Toast.LENGTH_SHORT).show();
                        finish();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoeidt);
        ImageBack=findViewById(R.id.ImageBack);
        ImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        db = FirebaseDatabase.getInstance();
        storyDb = db.getReference("Stories");
        ivImg = findViewById(R.id.iv_img);
        Intent intent = AviaryIntent.createCdsInitIntent(this);
        startService(intent);
        BtnUpload =findViewById(R.id.upload_btn);
        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Upload();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

}
    private String getExt(Uri Url){
        ContentResolver CR=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(CR.getType(Url));
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == IMG_CODE_EDIT && resultCode == RESULT_OK
                &&data!=null&&data.getData()!=null){
            filepath = data.getData();
            Intent intent = new AviaryIntent.Builder(this)
                    .setData( imguri )
                    .withOutputSize(MegaPixels.Mp10)
                    .withOutputQuality(100)
                    .build();
            //catch
            ivImg.setImageURI( filepath );
            Log.i("filePath", "--> "+filepath);
        }
        if( requestCode == PICK_IMAGE
                && resultCode ==RESULT_OK  ){
            imguri = data.getData();
            Intent intent = new AviaryIntent.Builder(this)
                    .setData( imguri )
                    .withOutputSize(MegaPixels.Mp10)
                    .withOutputQuality(100)
                    .build();
            startActivityForResult( intent, IMG_CODE_EDIT );
        }

        if( requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode ==RESULT_OK  ){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            File f = new File(Environment.getExternalStorageDirectory(),"1");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100/*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            //OutputS
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            imguri=Uri.fromFile(f);
            Intent intent = new AviaryIntent.Builder(this)
                    .setData( imguri )
                    .withOutputSize(MegaPixels.Mp10)
                    .withOutputQuality(100)
                    .build();
            startActivityForResult( intent, IMG_CODE_EDIT );
        }
    }

    public void callEditImage(View view ){
        Intent  gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    public void start_camera(View view ) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
