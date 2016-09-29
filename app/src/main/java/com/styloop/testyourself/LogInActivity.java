package com.styloop.testyourself;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.styloop.testyourself.constants.TestYouConstants;
import com.styloop.testyourself.dto.Usuario;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG="LoginActivity";

    private GoogleApiClient googleApiClient;

    private CallbackManager callbackManager;

    private LoginButton logginButton;

    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_log_in);

        prepareLoginGoogle();

        prepareLoginFb();

        prepareLoginTestYou();
    }


    public void prepareLoginGoogle(){
        customizeGoogleButton();
        GoogleSignInOptions googleSignInOptions = createOptions();
        createGoogleApiClient(googleSignInOptions);
    }

    public void customizeGoogleButton(){
        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        String valueButton=getResources().getString(R.string.googleButton);
        setGooglePlusButtonText(signInButton, valueButton);
    }
    protected void setGooglePlusButtonText(SignInButton  signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                return;
            }
        }
    }
    public GoogleSignInOptions createOptions(){
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

    }

    public void createGoogleApiClient(GoogleSignInOptions googleOptions){
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleOptions)
                .build();
    }

    public void prepareLoginFb(){
        callbackManager=CallbackManager.Factory.create();
        logginButton=(LoginButton)findViewById(R.id.connectWithFbButton);
        TestYouFacebookCallback callback=new TestYouFacebookCallback();
        callback.setContext(getApplicationContext());
        logginButton.registerCallback(callbackManager, callback);
    }

    public void prepareLoginTestYou(){
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
//        Button btnInicioSession=(Button) findViewById(R.id.btnIniciarSesionLogin);
//        btnInicioSession.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), InitActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        googleCallbackResult(requestCode,resultCode,data);
    }
    public void googleCallbackResult(int requestCode,int resultCode, Intent data){
        if(TestYouConstants.REQUEST_CODE_GOODLE==requestCode){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account=result.getSignInAccount();
                Usuario usuario=new Usuario();
                usuario.setEmail(account.getEmail());
                usuario.setNombre(account.getDisplayName());
                usuario.setUrlPhoto(account.getPhotoUrl().toString());
                usuario.setSourceUser(getResources().getString(R.string.src_user_google));
                Intent intentToLogin=new Intent(getApplicationContext(), InitActivity.class);
                intentToLogin.putExtra("Usuario", usuario);
                intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentToLogin);
            }
        }else if(requestCode==9001){
            PendingResult result=Auth.GoogleSignInApi.signOut(googleApiClient);
            if(result.isCanceled()){
                String mensaje=getResources().getString(R.string.mensaje_cancel);
                Toast.makeText(LogInActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public  void signIn(){
        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,TestYouConstants.REQUEST_CODE_GOODLE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        String mensaje=getResources().getString(R.string.mensaje_error);
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }


}


class TestYouFacebookCallback implements FacebookCallback<LoginResult>{

    private Context contextLogin;
    private String TestYouCallback_TAG="TestYouFacebookCallback";
    @Override
    public void onSuccess(LoginResult loginResult) {
        Profile profile=Profile.getCurrentProfile();
        Usuario user=new Usuario();
        user.setNombre(profile.getFirstName());
        user.setApPaterno(profile.getLastName());
        user.setUrlPhoto(profile.getProfilePictureUri(150, 150).toString());
        user.setSourceUser(contextLogin.getResources().getString(R.string.src_user_facebook));
        Intent intentToLogin=new Intent(contextLogin,InitActivity.class);
        intentToLogin.putExtra("Usuario", user);
        intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contextLogin.startActivity(intentToLogin);
    }

    @Override
    public void onCancel() {
        String mensaje=contextLogin.getResources().getString(R.string.mensaje_cancel);
        Toast.makeText(contextLogin, mensaje, Toast.LENGTH_SHORT).show();
        Toast.makeText(contextLogin, "Cancelo", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onError(FacebookException error) {
        String mensaje=contextLogin.getResources().getString(R.string.mensaje_error);
        Toast.makeText(contextLogin, mensaje, Toast.LENGTH_SHORT).show();
    }

    public void setContext(Context context){
        this.contextLogin=context;
    }

}
