package com.example.jaredkohler.ontimefitness;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Daily_Route_Activity extends AppCompatActivity {
    long startTime;
    private final String TAG = getClass().getSimpleName();
    Handler handler = new Handler();

    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute;
    private LocationServices locationServices;
    double lat= 0.0, lng= 0.0;
    double dist = 0.0;
    private Cursor cur;
    TextView current;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "+++ onStop() +++");
        startTime = SystemClock.elapsedRealtime();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "+++ onRestart() +++");
        if(startTime + 5000 <SystemClock.elapsedRealtime()){
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "+++ onCreate() +++");
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_schedule);
        TextView goal = (TextView) findViewById(R.id.textGoal);

        TextView steps = (TextView) findViewById(R.id.textCurrent);

        //Gets the ID of the logged in user from Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID",null);

        //Gets data repository in read mode
        final LoginDbHelper mDbHelper = new LoginDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Selects the steps column to be returned after query
        String[] projection = {
                LogInContract.LogInEntry.COLUMN_NAME_STEPS
        };

        //Filters the results where the id is equal to the logged in user's id
        String selection = LogInContract.LogInEntry._ID + " = ?";
        String[] selectionArgs = {id};

        //Query the database with the setting set above
        Cursor cursor = db.query(
                LogInContract.LogInEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Gets the steps from teh result of the query
        if(cursor.moveToNext()){
            steps.setText(cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_STEPS)));
        }else{
            Toast.makeText(this, "Failed to get number of steps", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }
        
        //Selects the goal column to be returned after query
        projection[0] = LogInContract.LogInEntry.COLUMN_NAME_GOAL;


        //Query the database with the setting set above
        cursor = db.query(
                LogInContract.LogInEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        //Gets the goal from the result of the query
        if(cursor.moveToNext()){
            goal.setText(cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_GOAL)));
        }else{
            Toast.makeText(this, "Failed to get goal number of steps", Toast.LENGTH_SHORT).show();
            intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
        }

        //Creates a thread to refresh the step counter every 10 seconds.
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        //Refreshes every 10 seconds
                        Thread.sleep(10000);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView steps = (TextView) findViewById(R.id.textCurrent);

                                //Gets the ID of the logged in user from Shared Preferences
                                SharedPreferences sharedPreferences = getSharedPreferences(Login_Activity.MyPREFERENCES, Context.MODE_PRIVATE);
                                String id = sharedPreferences.getString("ID",null);

                                //Gets data repository in read mode
                                final LoginDbHelper mDbHelper = new LoginDbHelper(Daily_Route_Activity.this);
                                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                                //Selects the steps column to be returned after query
                                final String[] projection = {
                                        LogInContract.LogInEntry.COLUMN_NAME_STEPS
                                };

                                //Filters the results where the id is equal to the logged in user's id
                                String selection = LogInContract.LogInEntry._ID + " = ?";
                                String[] selectionArgs = {id};

                                Cursor cursor = db.query(
                                        LogInContract.LogInEntry.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null
                                );

                                if(cursor.moveToNext()){
                                    steps.setText(cursor.getString(cursor.getColumnIndex(LogInContract.LogInEntry.COLUMN_NAME_STEPS)));
                                }else{

                                }
                            }
                        });

                    } catch(Exception e){

                    }
                }
            }
        }).start();

        MapboxAccountManager.start(this, getString(R.string.access_token));

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        CalendarOnTime cal = new CalendarOnTime(CalendarsHelper.getCurCalID(Daily_Route_Activity.this));
        String SELECTION = CalendarContract.Events.CALENDAR_ID + " == '" + cal.getCalID() + "' AND " +
                CalendarContract.Events.DELETED + " != '1' AND " + CalendarContract.Events.DTSTART +
                " >= '" + CalendarsHelper.currentDate() + "' AND " + CalendarContract.Events.DTSTART +
                " < '"  + CalendarsHelper.endOfCurrentDate() +"'";
        cur = cal.getListOfEvents(Daily_Route_Activity.this, SELECTION);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                toggleGps(true);

                String addr = "";
                if(cur.moveToFirst()) {
                    do {
                        Long start = Long.parseLong(cur.getString(2));
                        Long curTime = System.currentTimeMillis();
                        Log.d(TAG, "Start time: " + start + ", Cur time: " + curTime);
                        if (start >= curTime) {
                            addr = cur.getString(3);
                        }
                        if (!addr.equals("")) {
                            break;
                        }
                    } while (cur.moveToNext());
                }
                if(addr.equals("") || !hasNetworkConnection()){
                    return;
                }
                getLatLongFromAddress(addr);
                Position destination = Position.fromCoordinates( lng, lat);
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(destination.getLatitude(), destination.getLongitude()))
                        .title("Destination").snippet(addr));
                //Seperate return to allow at least the destination to be shown if the GPS isn't working.
                Location lastLocation = Daily_Route_Activity.this.getCheapLocation(Daily_Route_Activity.this);
                if(lastLocation == null){
                    return;
                }
                Position origin = Position.fromCoordinates(lastLocation.getLongitude(), lastLocation.getLatitude());
                Log.d(TAG, "Lat: " + origin.getLatitude() + " Long:" + origin.getLongitude());

                // Get route from API
                try {
                    getRoute(origin, destination ,false);
                } catch (ServicesException servicesException) {
                    servicesException.printStackTrace();
                }

            }
        });
        current = (TextView) findViewById(R.id.textExpect);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Calculate expected steps.
                Location lastLocation = Daily_Route_Activity.this.getCheapLocation(Daily_Route_Activity.this);
                if(lastLocation != null) {
                    Position origin = Position.fromCoordinates(lastLocation.getLongitude(), lastLocation.getLatitude());
                    while (cur.moveToNext()) {
                        String addr = "";
                        Long start = Long.parseLong(cur.getString(2));
                        Long curTime = System.currentTimeMillis();
                        Log.d(TAG, "Start time: " + start + ", Cur time: " + curTime);
                        addr = cur.getString(3);
                        if (!addr.equals("") && hasNetworkConnection()) {
                            getLatLongFromAddress(addr);
                            Position destination = Position.fromCoordinates(lng, lat);

                            // Get route from API for just calculation
                            try {
                                getRoute(origin, destination, true);
                                origin = destination;
                            } catch (ServicesException servicesException) {
                                servicesException.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "GPS could not return location, either a failure or GPS is off has occurred");
                }
            }
        }).start();

    }

    private void getRoute(Position origin, Position destination, boolean Calc) throws ServicesException {

        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_CYCLING)
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();
        if(Calc){
            client.enqueueCall(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    // You can get the generic HTTP info about the response
                    Log.d(TAG, "Response code: " + response.code());
                    if (response.body() == null) {
                        Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                        return;
                    } else if (response.body().getRoutes().size() < 1) {
                        Log.e(TAG, "No routes found");
                        return;
                    }
                    // Print some info about the route
                    currentRoute = response.body().getRoutes().get(0);
                    dist = dist + currentRoute.getDistance() *1.3;
                    current.setText(String.valueOf((int)dist));
                }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                    Log.e(TAG, "Error: " + throwable.getMessage());
                    Toast.makeText(Daily_Route_Activity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            client.enqueueCall(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    // You can get the generic HTTP info about the response
                    Log.d(TAG, "Response code: " + response.code());
                    if (response.body() == null) {
                        Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                        return;
                    } else if (response.body().getRoutes().size() < 1) {
                        Log.e(TAG, "No routes found");
                        return;
                    }
                    // Print some info about the route
                    currentRoute = response.body().getRoutes().get(0);
                    Log.d(TAG, "Distance: " + currentRoute.getDistance());
                    // Draw the route on the map
                    drawRoute(currentRoute);
                }
                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                    Log.e(TAG, "Error: " + throwable.getMessage());
                    Toast.makeText(Daily_Route_Activity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(coordinates.get(i).getLatitude(), coordinates.get(i).getLongitude());
        }

        // Draw Points on MapView
        map.addPolyline(new PolylineOptions().add(points).color(Color.parseColor("#009688"))
                .width(5));
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (!LocationServices.getLocationServices(this).areLocationPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 279);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            Location lastLocation = this.getCheapLocation(this);
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
            }

            LocationServices.getLocationServices(this).addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                        LocationServices.getLocationServices(Daily_Route_Activity.this).removeLocationListener(this);
                    }
                }
            });
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 279) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation(true);
            }
        }
    }
    private void getLatLongFromAddress(String address)
    {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try
        {

            List<Address> addresses = geoCoder.getFromLocationName(address , 1);
            if (addresses.size() > 0)
            {
                LatLng p = new LatLng((addresses.get(0).getLatitude()), (addresses.get(0).getLongitude()));
                lat=p.getLatitude();
                lng=p.getLongitude();
                Log.d("Latitude, dest", ""+lat);
                Log.d("Longitude, dest", ""+lng);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private boolean hasNetworkConnection(){
        ConnectivityManager connectivityManager	=
                (ConnectivityManager)	getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo	=
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected	=	true;
        boolean isWifiAvailable	=	networkInfo.isAvailable();
        boolean isWifiConnected	=	networkInfo.isConnected();
        networkInfo	=
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileAvailable	=	networkInfo.isAvailable();
        boolean isMobileConnnected	=	networkInfo.isConnected();
        isConnected	=	(isMobileAvailable&&isMobileConnnected)	||
                (isWifiAvailable&&isWifiConnected);
        return(isConnected);
    }

    public Location getCheapLocation(Context	theContext)	{
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    279);

        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        LocationManager manager	= (LocationManager)getSystemService(theContext.LOCATION_SERVICE);
        String cheapestProvider	= manager.getBestProvider(criteria,	true);

        Location myLocation=null;
        myLocation = manager.getLastKnownLocation(cheapestProvider);
        if	(myLocation == null)	{
            myLocation = manager.getLastKnownLocation("network");
        }
        if	(myLocation != null)	{
            System.out.println("GeoLocation	is>" + myLocation.toString() + "<");
        }
        return myLocation;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    public void Exit(View view) {
        finish();
    }
}
