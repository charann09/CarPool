package com.example.javagradletest.Home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.example.javagradletest.Login.LoginActivity;
import com.example.javagradletest.Map.CustomInfoWindowAdapter;
import com.example.javagradletest.Map.PlaceInfo;
import com.example.javagradletest.MapDirectionHelper.FetchURL;
import com.example.javagradletest.MapDirectionHelper.TaskLoadedCallback;
import com.example.javagradletest.R;
import com.example.javagradletest.Utils.BottomNavigationViewHelper;
import com.example.javagradletest.Utils.UniversalImageLoader;
import com.example.javagradletest.dialogs.WelcomeDialog;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, TaskLoadedCallback {
    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUMBER = 0;

    private Context mContext = HomeActivity.this;

    //Google map permissions
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private Boolean mLocationPermissionsGranted = false;

    //Google map variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
//    private GeoDataClient mGeoDataClient;

    private PlaceInfo mPlace;
    private Marker mMarker;
    private double currentLatitude, currentLongtitude;
    private Polyline currentPolyline;
    private MarkerOptions place1, place2;
    private LatLng currentLocation;

    private String directionsRequestUrl;
    private String userID;

    //Widgets
    private AutoCompleteTextView destinationTextview, locationTextView;
    private Button mSearchBtn, mDirectionsBtn;
    private RadioButton findButton, offerButton;
    private RadioGroup mRideSelectionRadioGroup;
    private BottomNavigationView bottomNavigationView;
    private ImageView mLocationBtn;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;

    private Boolean carOwner;

    private PlacesClient placesClient;

    private String locationName;

    private String destinationName;

    private TextView selectedLocation;

    private Place locationPlace, destinationPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");

        mSearchBtn = (Button) findViewById(R.id.searchBtn);
        mRideSelectionRadioGroup = (RadioGroup) findViewById(R.id.toggle);
        mLocationBtn = (ImageView) findViewById(R.id.locationImage);

        //Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyB1kqpId6JGQlpyzB2hMmw_uGi-bfd5gFc");
        placesClient = Places.createClient(this);

        //For Autocomplete of Location
        try {

//Location Selector
            AutocompleteSupportFragment autocompleteFragment =
                    (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.location_fragment_container);

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    setPlacesObject(place);
                    locationPlace = place;
                    locationName = place.getName();
                    Log.d("Maps", "Location selected: " + place.getName());
                    currentLocation = place.getLatLng();
                    drawDirections();

                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());
                }

                @Override
                public void onError(Status status) {
                    Log.d("Maps", "An error occurred: " + status);
                }
            });

            //Destination Selector
            AutocompleteSupportFragment destinationLocationFragment =
                    (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destination_fragment_container);
            destinationLocationFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
            destinationLocationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    setPlacesObject(place); //check for error for this
                    destinationPlace = place;
                    destinationName = place.getName();
                    Log.d("Maps", "Destination selected: " + place.getName());
                    currentLocation = place.getLatLng();
                    drawDirections();

                    moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());
                }

                @Override
                public void onError(Status status) {
                    Log.d("Maps", "An error occurred: " + status);
                }
            });
        }catch(Exception e){
            System.out.println("Error for AutoComplete Maps: "+e);
        }


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        if (mAuth.getCurrentUser() != null) {
            //Gets userID of current user signed in
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //Disables offer button when the user is logged in and they have no car
            findButton = (RadioButton) findViewById(R.id.findButton);
            offerButton = (RadioButton) findViewById(R.id.offerButton);

//            getUserInformation(userID);
            ////////////////////////////////////////////////////////////////////////

            //Creates token with that new user ID
//            updateFirebaseToken();

            //  checkNotifications();

            //Subscribes to a topic with that user ID so only that user can see messages with that user ID
            FirebaseMessaging.getInstance().subscribeToTopic(userID);
        }


        //Intitate widgets
//        destinationTextview = (AutoCompleteTextView) findViewById(R.id.destinationTextview);
//        locationTextView = (AutoCompleteTextView) findViewById(R.id.locationTextview);


        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocationAndAddMarker();
            }
        });


        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int whichIndex = mRideSelectionRadioGroup.getCheckedRadioButtonId();
                if (whichIndex == R.id.offerButton && locationName.length() > 0) {
                    Intent offerRideActivity = new Intent(mContext, OfferRideFragment.class);
                    offerRideActivity.putExtra("LOCATION", locationName);
                    offerRideActivity.putExtra("DESTINATION", destinationName);
                    offerRideActivity.putExtra("currentLatitue", currentLatitude);
                    offerRideActivity.putExtra("currentLongtitude", currentLongtitude);
                    Bundle b = new Bundle();
                    b.putParcelable("LatLng", currentLocation);
                    offerRideActivity.putExtras(b);
                    Log.i(TAG, "Current Location for offerRide is " + currentLocation);
                    startActivity(offerRideActivity);
                } else if (whichIndex == R.id.findButton && locationName.length() > 0) {
                    Intent findRideActivity = new Intent(mContext, SearchRideActivity.class);
                    findRideActivity.putExtra("LOCATION", locationName);
                    findRideActivity.putExtra("DESTINATION", destinationName);
                    findRideActivity.putExtra("currentLatitue", currentLatitude);
                    findRideActivity.putExtra("currentLongtitude", currentLongtitude);
                    findRideActivity.putExtra("currentLocation", currentLongtitude);

                    startActivity(findRideActivity);
                } else {
                    Toast.makeText(mContext, "Please enter location and destination", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initImageLoader();
        setupBottomNavigationView();

        // For display the welcome dialog on first app launch
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            //... Display the dialog message here ...
            setupDialog();
            // Save the state
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }

        //If play services is true = ok, then check for permissions and setup google maps
        if (isServicesOk()) {
            getLocationPermission();
        }

    }


    public void drawDirections() {
        if(locationPlace!=null  && destinationPlace != null) {
            GeoApiContext geoContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyB1kqpId6JGQlpyzB2hMmw_uGi-bfd5gFc")
                    .build();

            // Assuming you have two LatLng objects for place1 and place2
            LatLng originLatLng = locationPlace.getLatLng();
            LatLng destinationLatLng = destinationPlace.getLatLng();

            Log.i(TAG,"Location used for drawing direction:\n"+originLatLng+"\n"+destinationLatLng);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DirectionsApiRequest directions = DirectionsApi.newRequest(geoContext)
                            .origin(new com.google.maps.model.LatLng(originLatLng.latitude, originLatLng.longitude))
                            .destination(new com.google.maps.model.LatLng(destinationLatLng.latitude, destinationLatLng.longitude))
                            .mode(TravelMode.DRIVING);


                    directions.setCallback(new PendingResult.Callback<DirectionsResult>() {

                        @Override
                        public void onResult(DirectionsResult result) {
                            if (result != null && result.routes != null && result.routes.length > 0) {
                                DirectionsRoute route = result.routes[0]; // Assuming you want the first route

                                List<LatLng> routeCoordinates = new ArrayList<>();

                                if (route != null && route.overviewPolyline != null) {
                                    List<com.google.maps.model.LatLng> decodedPath = route.overviewPolyline.decodePath();

                                }

                                // Create a PolylineOptions and add route coordinates as described in previous responses

                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(routeCoordinates)
                                        .width(8) // Width of the polyline
                                        .color(Color.BLUE); // Color of the polyline

                                // Add the polyline to the map
                                mMap.addPolyline(polylineOptions);
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            // Handle errors here
                            Log.e(TAG, e.toString());
                        }
                    });
                }});

        }
    }


    public void setPlacesObject(Place place) {

        try {

            mPlace = new PlaceInfo();
            mPlace.setName(place.getName().toString());
            Log.d(TAG, "onResult: name: " + place.getName());
            mPlace.setAddress(place.getAddress().toString());
            Log.d(TAG, "onResult: address: " + place.getAddress());
            // mPlace.setAttributions(place.getAttributions().toString());
            //Log.d(TAG, "onResult: attributions: " + place.getAttributions());
            mPlace.setId(place.getId());
            Log.d(TAG, "onResult: id: " + place.getId());
            mPlace.setLatLng(place.getLatLng());
            Log.d(TAG, "onResult: latLng: " + place.getLatLng());
            mPlace.setRating(place.getRating());
            Log.d(TAG, "onResult: rating: " + place.getRating());
            mPlace.setPhoneNumber(place.getPhoneNumber().toString());
            Log.d(TAG, "onResult: phoneNumber: " + place.getPhoneNumber());
            mPlace.setWebsiteUri(place.getWebsiteUri());
            Log.d(TAG, "onResult: websiteUri: " + place.getWebsiteUri());
            Log.d(TAG, "onResult: place: " + mPlace.toString());

        } catch (NullPointerException e) {
            Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
        }
    }

//    private void updateFirebaseToken() {
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference tokens = db.getReference("Tokens");
//
//        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
//        tokens.child(userID)
//                .setValue(token);
//    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyB1kqpId6JGQlpyzB2hMmw_uGi-bfd5gFc";
        return url;
    }

    public void setupDialog() {
        WelcomeDialog dialog = new WelcomeDialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Hides phone keyboard if clicked
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /** --------------------------- Setting up google maps / permissions and services ---------------------------- **/
    /**
     * Check if google play services is enabled or available for mobile device
     *
     * @return
     */
    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is ok and user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occurred but it can be resolved
            Log.d(TAG, "isServicesOK: an error occurred but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "App cannot make map requests current", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    /**
     * sets up map from the view
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            System.out.println("Location Granted");

            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        init();
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: getting found location!");
                            Location currentLocation = (Location) task.getResult();
                            Log.i(TAG,"Current Location is: "+currentLocation.getLatitude()+" ,"+currentLocation.getLongitude());
                            moveCameraNoMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");

                            currentLatitude = currentLocation.getLatitude();
                            currentLongtitude = currentLocation.getLongitude();
                            Log.i(TAG, "Current Location is \n" + currentLatitude + "\n" + currentLongtitude);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomeActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getDeviceLocationAndAddMarker() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: getting found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My location");
                            currentLatitude = currentLocation.getLatitude();
                            currentLongtitude = currentLocation.getLongitude();

                            geoDecoder(currentLocation);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomeActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCameraNoMarker(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideKeyboard(HomeActivity.this);
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(markerOptions);
        }

        hideKeyboard(HomeActivity.this);
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {
        Log.d(TAG, "moveCamera: moving the camera to: lat:" + latLng.latitude + ", lng: " + latLng.longitude);

        hideKeyboard(HomeActivity.this);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));

        if (placeInfo != null) {
            try {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Phone Number: " + placeInfo.getPhoneNumber() + "\n" +
                        "Website: " + placeInfo.getWebsiteUri() + "\n" +
                        "Price Rating: " + placeInfo.getRating() + "\n";

                if (destinationTextview.hasFocus() == true) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

                    place1 = new MarkerOptions()
                            .position(latLng)
                            .title(placeInfo.getName())
                            .snippet(snippet);

                    mMarker = mMap.addMarker(place1);

                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f));

                    place2 = new MarkerOptions()
                            .position(latLng)
                            .title(placeInfo.getName())
                            .snippet(snippet);

                    mMarker = mMap.addMarker(place2);

                    directionsRequestUrl = getUrl(locationPlace.getLatLng(), destinationPlace.getLatLng(), "driving");

                    //                    new FetchURL(HomeActivity.this, mMap).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                }

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideKeyboard(HomeActivity.this);
    }

    private void geoDecoder(Location latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);
        destinationTextview.setText(address);
        destinationTextview.dismissDropDown();
    }

    private void init() {
        Log.d(TAG, "init: initializing");


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
     /*
        ---------------------------- google places API autocomplete suggestions -------------------------------
     */
//    private AdapterView.OnItemClickListener mAuotcompleteClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            hideKeyboard(HomeActivity.this);
    //dO SOMETHING WITH SELECTED LOCATION
//            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
//            final String placeId = item.getPlaceId();
//
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//        }
//    };

//    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()) {
//                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            final Place place = places.get(0);
//
//            try {
//
//                mPlace = new PlaceInfo();
//                mPlace.setName(place.getName().toString());
//                Log.d(TAG, "onResult: name: " + place.getName());
//                mPlace.setAddress(place.getAddress().toString());
//                Log.d(TAG, "onResult: address: " + place.getAddress());
//                // mPlace.setAttributions(place.getAttributions().toString());
//                //Log.d(TAG, "onResult: attributions: " + place.getAttributions());
//                mPlace.setId(place.getId());
//                Log.d(TAG, "onResult: id: " + place.getId());
//                mPlace.setLatLng(place.getLatLng());
//                Log.d(TAG, "onResult: latLng: " + place.getLatLng());
//                mPlace.setRating(place.getRating());
//                Log.d(TAG, "onResult: rating: " + place.getRating());
//                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                Log.d(TAG, "onResult: phoneNumber: " + place.getPhoneNumber());
//                mPlace.setWebsiteUri(place.getWebsiteUri());
//                Log.d(TAG, "onResult: websiteUri: " + place.getWebsiteUri());
//                Log.d(TAG, "onResult: place: " + mPlace.toString());
//
//                if (destinationTextview.isFocused()) {
//                    currentLocation = mPlace.getLatLng();
//                }
//
//            } catch (NullPointerException e) {
//                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
//            }
//
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);
//
//            places.release();
//
//        }
//    };

    /*
     * Permission locations
     * */

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: get location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /***
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        //BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView);


        //Change current highlighted icon
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setupBadge(int reminderLength) {
        if (reminderLength > 0) {
            //Adds badge and notification number to the BottomViewNavigation
            BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView, reminderLength);
        }
    }

    /**
     * Checks if there are notifications available for the current logged in user.
     */
    private void checkNotifications() {
        mRef.child("Reminder").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int reminderLength = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        reminderLength++;
                    }
                }
                //Passes the number of notifications onto the setup badge method
                setupBadge(reminderLength);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /** --------------------------- Firebase ---------------------------- **/

    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // userID = currentUser.getUid();
        checkCurrentUser(currentUser);
    }

    /**
     * Cheks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user if logged in");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = mMap.addPolyline((PolylineOptions) values[1]);
        }
    }

    public void getUserInformation(String uid) {
        mRef.child("user").child(uid).child("carOwner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carOwner = dataSnapshot.getValue(Boolean.class);
                if (carOwner == false) {
                    offerButton.setEnabled(false);
                    offerButton.setAlpha(.5f);
                    offerButton.setClickable(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
