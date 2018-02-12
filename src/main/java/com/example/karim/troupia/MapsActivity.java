package com.example.karim.troupia;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
                Toast.makeText(MapsActivity.this, "Navigation", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(MapsActivity.this, itemName, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MapsActivity.this, itemName, Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                openDialog();
                return true;
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
}
