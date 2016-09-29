package com.styloop.testyourself;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;
import com.styloop.testyourself.dto.Usuario;
import com.styloop.testyourself.util.CircleTransform;

public class  InitActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    private Usuario usuario;
    private static final String LOG_TAG="AppCompatActivity";
    private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        setToolbar();
        prepareLoginGoogle();



        Intent intent=getIntent();
        usuario=(Usuario)intent.getSerializableExtra("Usuario");
        ImageView profileImage= (ImageView)findViewById(R.id.profileImage);
        TextView textView=(TextView)findViewById(R.id.nameUser);
//
//
//
//        Button btnSimulacro=(Button)findViewById(R.id.btnSimulacro);
//        btnSimulacro.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), PreguntasActivity.class);
//                startActivity(intent);
//            }
//        });
        Picasso.with(getApplicationContext()).load(usuario.getUrlPhoto()).transform(new CircleTransform()).into(profileImage);

        textView.setText(usuario.getNombre());

        Log.d(LOG_TAG,usuario.getUrlPhoto());
    }
    public void initDrawer(Toolbar toolbar ){

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if(id==R.id.nav_cerrarSesion){
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test You");
        initDrawer(toolbar);
    }

    private void signOut() {
        String sourceUserFacebook=getResources().getString(R.string.src_user_facebook);
        String sourceUserGoogle=getResources().getString(R.string.src_user_facebook);
        Toast toast;
        if(sourceUserFacebook.equals(usuario.getSourceUser())){

        }else if(sourceUserGoogle.equals(usuario.getSourceUser())){
            if (googleApiClient.isConnected()){
                toast=Toast.makeText(this,"Connected",Toast.LENGTH_SHORT);
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (!status.isSuccess()){
                                    Toast.makeText(InitActivity.this, "Error, please try one more time", Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent intent=new Intent(getApplicationContext(),LogInActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });

            }else {
                Toast.makeText(InitActivity.this, "No Connected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void prepareLoginGoogle(){
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "StyloopsError 2");
    }
}
