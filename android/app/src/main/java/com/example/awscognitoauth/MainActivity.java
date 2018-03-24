package com.example.awscognitoauth;

// import for AWS

import android.os.Bundle;

import com.amazonaws.auth.*;
import com.amazonaws.cognito.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidentityprovider.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidentityprovider.model.AuthFlowType;


import java.util.HashMap;
import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class MainActivity extends FlutterActivity {

  private static final String CHANNEL = "samples.flutter.io/battery";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
      new MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, Result result) {
          if (call.method.equals("getBatteryLevel")) {

            int batteryLevel = getBatteryLevel();

            if (batteryLevel != -1) {
              result.success(batteryLevel);
            } else {
              result.error("UNAVAILABLE", "Battery level not available.", null);
            }
          } else {
            result.notImplemented();
          }

        }
      }
    );
  }

  private String _login() {
    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(), // Context
            "ap-northeast-1_o5lpbZ2LD", // Identity Pool ID
            Regions.AP_NORTHEAST_1 // Region
    );

    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

      @Override
      public void onSuccess(CognitoUserSession cognitoUserSession) {
        // Sign-in was successful, cognitoUserSession will contain tokens for the user
      }

      @Override
      public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
        // The API needs user sign-in credentials to continue
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);

        // Pass the user sign-in credentials to the continuation
        authenticationContinuation.setAuthenticationDetails(authenticationDetails);

        // Allow the sign-in to continue
        authenticationContinuation.continueTask();
      }

      @Override
      public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
        // Multi-factor authentication is required; get the verification code from user
        multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
        // Allow the sign-in process to continue
        multiFactorAuthenticationContinuation.continueTask();
      }

      @Override
      public void onFailure(Exception exception) {
        // Sign-in failed, check exception for the cause
      }
    };

    // Sign in the user
    cognitoUser.getSessionInBackground(authenticationHandler);

  }

  private int getBatteryLevel() {
    int batteryLevel = -1;

    return batteryLevel;
  }
}
