package com.example.karim.troupia;

import android.app.Application;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.internal.auth.AdobeAuthIMSEnvironment;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;

public class MainApplication extends Application implements IAviaryClientCredentials {

    private static final String CREATIVE_SDK_CLIENT_ID = "ca7d8213fae846489420653c7465db0f";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "3c400583-38bf-4c80-9cd8-5433c0e3df3c";

    @Override
    public void onCreate() {
        super.onCreate();

        AdobeCSDKFoundation.initializeCSDKFoundation(
                getApplicationContext(),
                AdobeAuthIMSEnvironment.AdobeAuthIMSEnvironmentProductionUS
        );
    }

    @Override
    public String getBillingKey() {
        return "";
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }
}
