package com.example.karim.troupia;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.karim.troupia.Model.Memory;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener, MaterialSearchBar.OnSearchActionListener {

    private GoogleMap mMap;
    private final static int MY_PERMSSION_FINE_LOCATION = 101;
    final int PICKIMAGE_REQUEST = 71;
    Uri filepath;
    ImageView imageAdd;
    EditText Detials;
    String detailsTxt;
    EditText MemoNameTxt;
    TextView findLocation;
    String MemoName;
    LocationManager locationManager;
    String Date;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference memo;
    UserSessionManager session;
    double longtuide,lontuide;
    Geocoder gecoder;
    List<Address>list;
     boolean locationCheck=false;
    ArrayList<String>Urls;
    ArrayList<String> Names,Details,LongtuideList,LituideList,DateList;
    DatabaseReference mDatabaseReference;
    CircleImageView profileCricle;
    HashMap<Pair<Double,Double>,Integer>MapMemo,MapStory;
    private MaterialSearchBar searchBar;
    boolean SearchBarVisivable=false;
    private List<String> lastSearches;


    private void splitData(Map<String, Object> value) {
        Urls = new ArrayList<>();
        Names = new ArrayList<>();
        Details = new ArrayList<>();
        LongtuideList = new ArrayList<>();
        LituideList = new ArrayList<>();
        DateList=new ArrayList<>();
        for (Map.Entry<String, Object> enter : value.entrySet()) {
            Map<String,Object> singlMap = (Map) enter.getValue();
            String Key=enter.getKey();
            String Id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(Id.equals(Key))
                for(Map.Entry<String, Object> enter1:singlMap.entrySet()) {
                    // Urls.add(Url);
                    Names.add(enter1.getKey());
                    Urls.add(((Map) enter1.getValue()).get("ImageUrl").toString());
                    Details.add((((Map) enter1.getValue()).get("Text")).toString());
                    LongtuideList.add((((Map) enter1.getValue()).get("Longtuide")).toString());
                    LituideList.add((((Map) enter1.getValue()).get("Lontuide")).toString());
                    DateList.add((((Map) enter1.getValue()).get("Date")).toString());
                }
        }
        drawPins();
    }
    int i=0;
    private void drawPins() {
        MapMemo=new HashMap<>();
        for(i=0;i<LituideList.size();i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(LituideList.get(i))
                    ,Double.parseDouble(LongtuideList.get(i)))));
            Double lit=Double.parseDouble(LituideList.get(i));
            Double lon=Double.parseDouble(LongtuideList.get(i));
           MapMemo.put(new Pair<Double, Double>(lit,lon),i);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Double latiuide=marker.getPosition().latitude;
                Double longtuide=marker.getPosition().longitude;
                try {
                    Integer val=MapMemo.get(new Pair<Double, Double>(latiuide,longtuide));
                            if(val!=null)
                                 openMemoryDialog(latiuide,longtuide);
                            else
                                startActivity(new Intent(getBaseContext(),Stroies.class));


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

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
                findLocation.setText(Address);
                locationCheck=!locationCheck;
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
    private String getExt(Uri Url){
        ContentResolver CR=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(CR.getType(Url));
    }
    private void Upload() {
        if(detailsTxt.isEmpty())Detials.setError("Enter descripton of the memory");
        else if(MemoName.isEmpty())MemoNameTxt.setError("please enter the Title of memory");
        else if(!locationCheck) Toast.makeText(this, "Please enter the location", Toast.LENGTH_SHORT).show();
        else {
            final Memory memory=new Memory();
            memory.SetDate(Date);
            memory.SetText(detailsTxt);
            memory.SetName(MemoName);
            memory.SetLongtuide(String.valueOf(longtuide));
            memory.SetLontuide(String.valueOf(lontuide));
            if (filepath != null) {
                String img1Url=FirebaseAuth.getInstance().getCurrentUser().getUid()+MemoName+ Calendar.getInstance().getTime().toString()
                        +"."+getExt(filepath);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("adding....");
                progressDialog.show();
                StorageReference ref = storageReference.child("Troupia/" + img1Url);
                ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        memory.SetImageUrl(taskSnapshot.getDownloadUrl().toString());
                        memo.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(MemoName).setValue(memory);
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
    private FusedLocationProviderClient mFusedLocationClient;
    private void choseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICKIMAGE_REQUEST);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICKIMAGE_REQUEST&&resultCode==RESULT_OK
                &&data!=null&&data.getData()!=null){
            filepath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),filepath);
                imageAdd.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void openDialogAdd() {
        AlertDialog.Builder RegisterDialog=new AlertDialog.Builder(this);
        final AlertDialog RegisterAlert=RegisterDialog.create();
        LayoutInflater inflater=this.getLayoutInflater();
        RegisterAlert.setView(inflater.inflate(R.layout.add_memory_layout,null));
        RegisterAlert.show();
        Detials=RegisterAlert.findViewById(R.id.detials_txt);
        ImageView ImageDone=RegisterAlert.findViewById(R.id.addImage);
        ImageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsTxt=Detials.getText().toString();
                MemoName=MemoNameTxt.getText().toString();
                Upload();
            }
        });
        findLocation=RegisterAlert.findViewById(R.id.LocationTxt);
        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        MemoNameTxt=RegisterAlert.findViewById(R.id.MemoNameTxt);
        imageAdd=RegisterAlert.findViewById(R.id.image_add);
        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImage();
            }
        });
        final TextView text=RegisterAlert.findViewById(R.id.dateText);
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "MM/dd/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date=sdf.format(myCalendar.getTime()).toString();
                text.setText(Date);
            }
        };
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MapsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    ArrayAdapter<String> adapter;
    LinearLayout SearchLinearLayout;
    DatabaseReference mDatabaseReference2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        TextView fbTxt=findViewById(R.id.facebookBtn);
        fbTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),faceLogin.class));
            }
        });

        //check user session
        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin()) {
            finish();
            Intent intent = new Intent(getApplicationContext(), Silder.class);
            startActivity(intent);
            return;
        } else {
            //resoter memory
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Memories");
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
            mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("Stories");
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild((FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                        splitData2((Map<String, Object>) dataSnapshot.getValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        ///
        //intilize db
        //search Actions
        SearchLinearLayout = findViewById(R.id.searchLayout);
        ListView listView = findViewById(R.id.ListViewSearch);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Karim");
        arrayList.add("Yasmine");
        arrayList.add("Omar");
        arrayList.add("mostfa");
        arrayList.add("Hussini");
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        memo = db.getReference("Memories");
        profileImage = findViewById(R.id.profileImage);
        //Access Db
        memo.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Menue", R.drawable.menu_ic));
        spaceNavigationView.addSpaceItem(new SpaceItem("Search", R.drawable.search_ic));
        spaceNavigationView.addSpaceItem(new SpaceItem("Friends", R.drawable.friends_ic));
        spaceNavigationView.addSpaceItem(new SpaceItem("Stroies", R.drawable.stories_ic));
        spaceNavigationView.showIconOnly();
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                openDialogAdd();
            }


            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        openDialogItems();
                        break;
                    case 1:
                        if (!SearchBarVisivable) {
                            SearchLinearLayout.setVisibility(View.VISIBLE);
                            SearchBarVisivable = !SearchBarVisivable;
                        } else {
                            SearchBarVisivable = !SearchBarVisivable;
                            SearchLinearLayout.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                        startActivity(new Intent(getBaseContext(), photoeidt.class));
                        break;
                }
            }
            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        openDialogItems();
                        break;
                    case 1:
                        if (!SearchBarVisivable) {
                            SearchLinearLayout.setVisibility(View.VISIBLE);
                            SearchBarVisivable = !SearchBarVisivable;
                        } else {
                            SearchBarVisivable = !SearchBarVisivable;
                            SearchLinearLayout.setVisibility(View.GONE);
                        }
                        break;
                    case 3:
                       // startActivity(new Intent(getBaseContext(), Stroies.class));
                        startActivity(new Intent(getBaseContext(),photoeidt.class));
                        break;
                }
            }
        });
        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference().child("Profiles");
        mDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild((FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                   /* com.example.karim.troupia.Model.Profile profil=dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(com.example.karim.troupia.Model.Profile.class);
                    Picasso.with(getBaseContext()).load(profil.GetUrl()).into(profileImage);*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    ArrayList<String>UrlsStories,LongtuideListStroies,LituideListStroies;
    private void splitData2(Map<String, Object> value) {
        UrlsStories = new ArrayList<>();
        LongtuideListStroies = new ArrayList<>();
        LituideListStroies = new ArrayList<>();
        for (Map.Entry<String, Object> enter : value.entrySet()) {
            Map<String,Object> singlMap = (Map) enter.getValue();
            String Key=enter.getKey();
            String Id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(Id.equals(Key))
                for(Map.Entry<String, Object> enter1:singlMap.entrySet()) {
                    // Urls.add(Url);
                    UrlsStories.add(((Map) enter1.getValue()).get("URL").toString());
                    LongtuideListStroies.add((((Map) enter1.getValue()).get("Longtuide")).toString());
                    LituideListStroies.add((((Map) enter1.getValue()).get("Lituide")).toString());
                }
                common.URLSStroies=UrlsStories;
        }
        drawStories();
    }

    private void drawStories() {
        MapStory=new HashMap<>();
        for(i=0;i<LituideListStroies.size();i++){
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.story)).position(new LatLng(Double.parseDouble(LituideListStroies.get(i))
                    ,Double.parseDouble(LongtuideListStroies.get(i)))));
            Double lit=Double.parseDouble(LituideListStroies.get(i));
            Double lon=Double.parseDouble(LongtuideListStroies.get(i));
            MapStory.put(new Pair(lit,lon),i);
        }

    }

    String ImageUrl;
    CircleImageView profileImage;
    private void ProfilesplitData(Map<String, Object> value1) {
        ImageUrl =null;
        for (Map.Entry<String, Object> enter3 : value1.entrySet()) {
            Map<String,Object> singlMap2 = (Map) enter3.getValue();
            String Key=enter3.getKey();
            String Id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(Id.equals(Key))
                for(Map.Entry<String, Object> enter4:singlMap2.entrySet()) {
                    // Urls.add(Url);
                    ImageUrl=enter4.getValue().toString();
                }
        }
        Picasso.with(this).load(ImageUrl).into(profileImage);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMSSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "this app required location permission to be granted",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMyLocationEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String []{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMSSION_FINE_LOCATION);
            }
        }

    }

    private void openMemoryDialog(double latitude, double longitude) throws IOException {
        int index=MapMemo.get(new Pair<Double, Double>(latitude,longitude));
        AlertDialog.Builder MemoDialog=new AlertDialog.Builder(this);
        AlertDialog MemoAlert=MemoDialog.create();
        LayoutInflater layoutInflater=this.getLayoutInflater();
        MemoAlert.setView(layoutInflater.inflate(R.layout.memoryenter,null));
        MemoAlert.show();
        ImageView ImageViewTitle=MemoAlert.findViewById(R.id.ImageViewTitle);
        Glide.with(this).load(Urls.get(index)).into(ImageViewTitle);
      //  Picasso.with(this).load(Urls.get(index)).into(ImageViewTitle);
        TextView TitleTxt=MemoAlert.findViewById(R.id.TitleText);
        TitleTxt.setText(Names.get(index));
        TextView DetialsText=MemoAlert.findViewById(R.id.detials_txt);
        DetialsText.setText(Details.get(index));
        gecoder = new Geocoder(getBaseContext(), Locale.getDefault());
        list = gecoder.getFromLocation(latitude, longitude, 1);
        String Area = list.get(0).getLocality();
        TextView Address=MemoAlert.findViewById(R.id.AddressText);
        Address.setText(Area);
    }

    private void openDialogItems(){
        AlertDialog.Builder RegisterDialog=new AlertDialog.Builder(this);
        AlertDialog RegisterAlert=RegisterDialog.create();
        LayoutInflater inflater=this.getLayoutInflater();
        RegisterAlert.setView(inflater.inflate(R.layout.items_layout,null));
        RegisterAlert.show();
        CircleImageView circleImageView=RegisterAlert.findViewById(R.id.ProfileImage);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),Profile.class));
            }
        });
    }
    private void openDialog() {
        AlertDialog.Builder RegisterDialog=new AlertDialog.Builder(this);
        AlertDialog RegisterAlert=RegisterDialog.create();
        LayoutInflater inflater=this.getLayoutInflater();
        RegisterAlert.setView(inflater.inflate(R.layout.memoryenter,null));
        RegisterAlert.show();
    }
    private void openStroy(){
        startActivity(new Intent(this,Stroies.class));
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
