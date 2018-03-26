package com.example.awscognitoauth;

// import for AWS

import android.os.Bundle;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.*;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
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

  private static final String CHANNEL = "aws_cognito_auth.sample/aws";

  public String _result = "Unknown login status...";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
      new MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, Result result) {
          if (call.method.equals("login")) {
            Map argument = call.arguments();
            String _email = argument.get("email").toString();
            String _password = argument.get("password").toString();
            String __result = _login(_email, _password);

            if (__result != null) {
              result.success(__result);
            } else {
              result.error("UNAVAILABLE", "Error", null);
            }
          } else {
            result.notImplemented();
          }

        }
      }
    );
  }

  private String _login(final String email, final String password) {
    // ユーザプールの初期化
    AmazonCognitoIdentityProviderClient identityProviderClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), new ClientConfiguration());
    identityProviderClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

    CognitoUserPool userPool = new CognitoUserPool(
            getApplicationContext(),
            "***",
            "***",
            "***",
            identityProviderClient
    );

    // サインイン対象とするユーザの検索
    CognitoUser user = userPool.getUser();

    user.getSessionInBackground(new AuthenticationHandler() {
      @Override
      public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice cognitoDevice) {
        Log.d("success",cognitoUserSession.getAccessToken().getJWTToken());
        _result = "success! JWTToken :" + cognitoUserSession.getAccessToken().getJWTToken();
        //_result = cognitoUserSession.getAccessToken().getJWTToken();
      }

      @Override
      public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String s) {
        _result = "getAuthenticationDetails!";
        // The API needs user sign-in credentials to continue
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(
                email,
                password,
                null
        );

        // Pass the user sign-in credentials to the continuation
        authenticationContinuation.setAuthenticationDetails(authenticationDetails);

        // Allow the sign-in to continue
        authenticationContinuation.continueTask();
      }

      @Override
      public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

      }

      @Override
      public void authenticationChallenge(ChallengeContinuation challengeContinuation) {

      }

      @Override
      public void onFailure(Exception e) {
        // Sign-in failed, check exception for the cause
        //Log.d("Error here",exception.toString());
        _result = "ERROR:" + e.toString();
      }

    });

    return _result;
  }

}

