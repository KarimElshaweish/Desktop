package com.example.karim.troupia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class Home extends AppCompatActivity {

    private TextView mTextMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
                Toast.makeText(Home.this, "Navigation", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(Home.this, itemName, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(Home.this, itemName, Toast.LENGTH_SHORT).show();

            }
        });
        mTextMessage = (TextView) findViewById(R.id.message);
    }

}
