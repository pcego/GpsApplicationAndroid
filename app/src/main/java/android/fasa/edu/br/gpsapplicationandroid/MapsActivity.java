package android.fasa.edu.br.gpsapplicationandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationResult;
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
    private LocationCallback locationCallback;

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
        // Carregando o serviço de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Array permissões
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
        };

        // Verifica e solicita permissões de acesso
        PermissionsUtil.validate(this, 0, permissions);
        // Latitude e Longitude Cidade Montes Claros
        // Definida de forma estática e exibida na carga do mapa

        lat = -16.7238229;
        lng = -43.8735205;

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Capturando a última Localização conhecida
        fusedLocationClient.getLastLocation().
                addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Recupere aqui a ultima localização conhecida
                    //Gerada ao clicar no botão Minha Localização
                }
            });

        // Definindo a localização e criando um marcador no mapa
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

        // Inicializando atualizações de localização do GPS
        fusedLocationClient
                .requestLocationUpdates
                        (locationRequest,locationCallback, null);
    }

    protected void stopLocationUpdates(){
        // Parando as atualizações do GPS
        fusedLocationClient.removeLocationUpdates(locationCallback);
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
    public void onRequestPermissionsResult
            (int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Log.d("Negada", "Permissão Negada");
            }
        }

        Log.d("ok", "permissões");

    }
    // Atualizando localização do dispositivo
    private void getLocationUpdates(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult == null){
                    return;
                }

                for(Location location : locationResult.getLocations()){
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    Toast.makeText(getApplicationContext(),
                            "lat "+ lat +" long " +
                                    lng,Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza localização do dispositivo quando o mapa estiver visivel
        getLocationUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Para as atualizações de localização
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
