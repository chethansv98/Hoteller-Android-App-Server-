package developers.bmsce.mank.com.foodorderserver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import developers.bmsce.mank.com.foodorderserver.Common.Common;
import developers.bmsce.mank.com.foodorderserver.Remote.IGeoCordinates;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackOrder extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener
        {

    private GoogleMap mMap;
    private final static int PLAY_SERVICE_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1000;

    private Location mLastLocation;

    private GoogleApiClient mgoogleApiClient;
    private LocationRequest mLocationRequest;


    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL =5000;
    private static int DISAPLACEMMENT = 10;

    private IGeoCordinates mServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);



        mServices = Common.getGeoCodeServices();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                  requestRunTimePermissio();



        }else {

            if(checkPlayServices())
            {

                buildGoogleApiClient();
                createLocationRequest();
            }

        }
            displayLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation() {


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
            requestRunTimePermissio();



        }else {


            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);

            if (mLastLocation != null) {

                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                LatLng yourLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


                drawRoute(yourLocation, Common.currentRequest.getAddress());


            } else {
                Toast.makeText(this,"Couldnt get the location",Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void drawRoute(LatLng yourLocation, String address) {


        mServices.getGeoCode(address).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());

                    String lat = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lat").toString();
                    String lng = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();

                    LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box);
                    bitmap = Common.scalBitmap(bitmap, 70, 70);

                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Order of" + Common.currentRequest.getPhone())
                            .position(orderLocation);
                    mMap.addMarker(markerOptions);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISAPLACEMMENT);
    }

    protected synchronized void buildGoogleApiClient() {

        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mgoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCOde = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCOde != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCOde)) {


                      GooglePlayServicesUtil.getErrorDialog(resultCOde,this,PLAY_SERVICE_RESOLUTION_REQUEST).show();


            }
            else {

                Toast.makeText(TrackOrder.this,"This device Not Support",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;
    }

    private void requestRunTimePermissio() {

        ActivityCompat.requestPermissions(this,new String[]
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION

                },LOCATION_PERMISSION_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if(checkPlayServices())
                    {

                        buildGoogleApiClient();
                        createLocationRequest();

                        displayLocation();
                    }

                }
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    //        // Add a marker in Sydney and move the camera
    //        LatLng sydney = new LatLng(-34, 151);
    //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        displayLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mgoogleApiClient != null) {
            mgoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();

        startLocationUpdate();

    }

    private void startLocationUpdate() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED){


            return;



        }




        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient,mLocationRequest,this);
       //LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient,mLocationRequest, );


        }

    @Override
    public void onConnectionSuspended(int i) {
        mgoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
