package android.fasa.edu.br.gpsapplicationandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleApiClient googleApiClient;
    private Double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtem o SupportMapFragment e notifica quando o mapa estiver pronto para ser usado.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Solicita as permissões
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        // Verifica e solicita permissões de acesso
        PermissionsUtil.validate(this, 0, permissions);
        // Latitude e Longitude Cidade Montes Claros
        // Definida de forma estática e exibida na carga do mapa
        lat = -16.7280803;
        lng = -43.9211237;

    }

    //Anotação para suprimir a verificação de permissões
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        fusedLocationClient.getLastLocation().
                addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Recupere aqui a ultima localização conhecida
                    //Gerada ao clicar no botão Minha Localização
                }
            });

        // Add a marker in Sydney and move the camera
        LatLng moc = new LatLng(lat, lng);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(moc).title("Montes Claros-MG"));
        mMap.setMaxZoomPreference(100);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(moc));

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Conectando ao google play services
        googleApiClient.connect();


    }

    @Override
    protected void onStop(){
        super.onStop();
        //Desconectando do google play services
        googleApiClient.disconnect();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates(){
        // Criando um objeto location request
        // configurando GPS
        LocationRequest locationRequest = new LocationRequest();

        // Intervalo para receber localização
        // fixado em 10 segundos
        locationRequest.setInterval(10000);
        // Intervalo mínino para receber localização
        locationRequest.setFastestInterval(5000);
        //Define a prioridade para uma localização mais precisa
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices
                .FusedLocationApi
                .requestLocationUpdates
                        (googleApiClient,
                                locationRequest,this);
    }

    protected void stopLocationUpdates(){
        LocationServices
                .FusedLocationApi
                .removeLocationUpdates(googleApiClient,this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Log.d("Negada", "Permissão Negada");
            }
        }

        Log.d("ok", "permissões");

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        Toast.makeText(this,"lat "+ lat +" long "+lng,Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
