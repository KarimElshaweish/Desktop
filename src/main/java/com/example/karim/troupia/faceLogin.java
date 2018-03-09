package com.example.karim.troupia;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class faceLogin extends AppCompatActivity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
    LoginButton loginButton;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_login);
        //facebook login
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(
                    "com.example.karim.troupia",
                    PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (Signature signature : info.signatures) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
        //
        loginButton = findViewById(R.id.FBlogin_button);
        loginButton.setReadPermissions(Arrays.asList("user_actions.music"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
             //   GraphData();
            }
            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Error",error.toString());
                Toast.makeText(faceLogin.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    String path="";
    public void GraphData() {
        GraphRequest request=GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken()
                ,new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        Log.d("object",object.toString());
                          //  System.out.println(object.toString());
                        try {
                            path="/"+object.get("id").toString()+"/events?end_time>\"2018-02-21\"";
                            new GraphRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    path,
                                    null,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {
                                            System.out.println(response);
                                            try {
                                                JSONArray jsonArray=response.getJSONObject().getJSONArray("data");
                                                common.jsonArray=jsonArray;
                                              /*  for(int i = 0; i < jsonArray.length(); i++){
                                                    JSONObject oneAlbum = jsonArray.getJSONObject(i);
                                                    //get your values
                                                    System.out.print(oneAlbum.getString("description")); // this will return you the album's name.
                                                    System.out.print(oneAlbum.getString("start_time")); // this will return you the album's name.
                                                }*/

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            ).executeAsync();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
