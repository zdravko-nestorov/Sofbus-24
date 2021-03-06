package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import bg.znestorov.sofbus24.databases.VehiclesDataSource;
import bg.znestorov.sofbus24.entity.GlobalEntity;
import bg.znestorov.sofbus24.entity.MetroStationEntity;
import bg.znestorov.sofbus24.entity.PublicTransportStationEntity;
import bg.znestorov.sofbus24.entity.StationEntity;
import bg.znestorov.sofbus24.entity.VehicleTypeEnum;
import bg.znestorov.sofbus24.entity.VirtualBoardsStationEntity;
import bg.znestorov.sofbus24.utils.Constants;
import bg.znestorov.sofbus24.utils.LanguageChange;
import bg.znestorov.sofbus24.utils.MapUtils;
import bg.znestorov.sofbus24.utils.ThemeChange;
import bg.znestorov.sofbus24.utils.activity.ActivityUtils;

public class StationMap extends SherlockFragmentActivity {

    private Activity context;
    private GlobalEntity globalContext;

    private ActionBar actionBar;
    private VehiclesDataSource vehiclesDatasource;

    private GoogleMap stationMap;
    private LatLng centerStationLocation;

    private boolean isCurrentLocationFocused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChange.selectTheme(this);
        super.onCreate(savedInstanceState);

        LanguageChange.selectLocale(this);
        setContentView(R.layout.activity_map_station);

        // Get the current activity context
        context = StationMap.this;
        globalContext = (GlobalEntity) getApplicationContext();
        vehiclesDatasource = new VehiclesDataSource(context);

        // Set up the action bar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the station map fragment
        stationMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.station_map)).getMap();

        // Check if the station map is found
        if (stationMap != null) {
            // Get the MetroStation object from the Bundle
            Bundle extras = getIntent().getExtras();
            final StationEntity stationBundle = (StationEntity) extras
                    .get(Constants.BUNDLE_STATION_MAP);

            // Set ActionBar title and subtitle
            actionBar.setTitle(getActionBarTitle(stationBundle));
            actionBar.setSubtitle(stationBundle.getName());

            // Check if the station has coordinates in the DB
            try {
                centerStationLocation = new LatLng(
                        Double.parseDouble(stationBundle.getLat()),
                        Double.parseDouble(stationBundle.getLon()));
            } catch (Exception e) {
                centerStationLocation = new LatLng(
                        Constants.GLOBAL_PARAM_SOFIA_CENTER_LATITUDE,
                        Constants.GLOBAL_PARAM_SOFIA_CENTER_LONGITUDE);
                stationBundle.setType(VehicleTypeEnum.NOIMAGE);
            }

            // Initialize GoogleMaps without the current location
            initGoogleMaps(stationBundle, null);

            // Activate my location, set a location button that center the map
            // over a point and start a LocationChangeListener
            stationMap.setMyLocationEnabled(true);
            stationMap.getUiSettings().setMyLocationButtonEnabled(true);
            stationMap
                    .setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location currentLocation) {
                            if (currentLocation != null) {
                                stationMap.clear();
                                initGoogleMaps(stationBundle, currentLocation);
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getSupportMenuInflater().inflate(R.menu.activity_map_station_actions,
                menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sm_google_street_view:
                if (globalContext.isGoogleStreetViewAvailable()) {
                    Uri streetViewUri = Uri.parse("google.streetview:cbll="
                            + centerStationLocation.latitude + ","
                            + centerStationLocation.longitude
                            + "&cbp=1,90,,0,1.0&mz=20");
                    Intent streetViewIntent = new Intent(Intent.ACTION_VIEW,
                            streetViewUri);
                    startActivity(streetViewIntent);
                } else {
                    ActivityUtils.showGoogleStreetViewErrorDialog(StationMap.this);
                }
                return true;
            case R.id.action_sm_map_mode_normal:
                stationMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(context,
                        Html.fromHtml(getString(R.string.cs_map_normal)),
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sm_map_mode_terrain:
                stationMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(context,
                        Html.fromHtml(getString(R.string.cs_map_terrain)),
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sm_map_mode_satellite:
                stationMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(context,
                        Html.fromHtml(getString(R.string.cs_map_satellite)),
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sm_map_mode_hybrid:
                stationMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(context,
                        Html.fromHtml(getString(R.string.cs_map_hybrid)),
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize the GoogleMaps and all of its objects
     *
     * @param stationBundle   the station received as an extra content from the previous
     *                        activity
     * @param currentLocation the current location
     */
    private void initGoogleMaps(StationEntity stationBundle,
                                final Location currentLocation) {
        // Check the type of the bundle object
        if (stationBundle instanceof MetroStationEntity) {
            MetroStationEntity metroStation = (MetroStationEntity) stationBundle;
            processMetroStationObject(currentLocation, metroStation);
        } else if (stationBundle instanceof PublicTransportStationEntity) {
            PublicTransportStationEntity ptStation = (PublicTransportStationEntity) stationBundle;
            processPTStationObject(currentLocation, ptStation);
        } else {
            VirtualBoardsStationEntity vbTimeStation = (VirtualBoardsStationEntity) stationBundle;
            processVBTimeStationObject(currentLocation, vbTimeStation);
        }

        // Animate the map to the station position
        animateMapFocus(centerStationLocation);
        stationMap
                .setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        if (isCurrentLocationFocused) {
                            animateMapFocus(centerStationLocation);
                            isCurrentLocationFocused = false;
                        } else {
                            animateMapFocus(currentLocation);
                            isCurrentLocationFocused = true;
                        }
                        return true;
                    }
                });

        // Remove the locationChangeListener
        stationMap.setOnMyLocationChangeListener(null);
    }

    /**
     * Process the MetroStation object
     *
     * @param currentLocation the current user location
     * @param metroStation    the chosen metro station
     */
    private void processMetroStationObject(Location currentLocation,
                                           MetroStationEntity metroStation) {
        MarkerOptions stationMarkerOptions = new MarkerOptions()
                .position(centerStationLocation)
                .title(String.format(metroStation.getName() + " (%s)",
                        metroStation.getNumber()))
                .icon(BitmapDescriptorFactory
                        .fromResource(getMarkerIcon(metroStation)));

        // Check if the user is already localized
        if (currentLocation != null) {
            stationMarkerOptions = stationMarkerOptions.snippet(String.format(
                    context.getString(R.string.app_distance), MapUtils
                            .getMapDistance(context, currentLocation,
                                    metroStation)));
        } else {
            stationMarkerOptions = stationMarkerOptions.snippet(String
                    .format(context.getString(R.string.app_distance_none)));
        }

        Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
        stationMarker.showInfoWindow();
    }

    /**
     * Process the PublicTranspStation object
     *
     * @param currentLocation the current user location
     * @param ptStation       the chosen public transport station
     */
    private void processPTStationObject(Location currentLocation,
                                        PublicTransportStationEntity ptStation) {
        MarkerOptions stationMarkerOptions = new MarkerOptions()
                .position(centerStationLocation)
                .title(String.format(ptStation.getName() + " (%s)",
                        ptStation.getNumber()))
                .icon(BitmapDescriptorFactory
                        .fromResource(getMarkerIcon(ptStation)));

        // Check if the user is already localized
        if (currentLocation != null) {
            stationMarkerOptions = stationMarkerOptions
                    .snippet(String.format(context
                                    .getString(R.string.app_distance),
                            MapUtils.getMapDistance(context, currentLocation,
                                    ptStation)));
        } else {
            stationMarkerOptions = stationMarkerOptions.snippet(String
                    .format(context.getString(R.string.app_distance_none)));
        }

        Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
        stationMarker.showInfoWindow();
    }

    /**
     * Process the VirtualBoardsTime station object
     *
     * @param currentLocation the current user location
     * @param vbTimeStation   the chosen virtual boards station
     */
    private void processVBTimeStationObject(Location currentLocation,
                                            VirtualBoardsStationEntity vbTimeStation) {
        MarkerOptions stationMarkerOptions = new MarkerOptions()
                .position(centerStationLocation)
                .title(String.format(vbTimeStation.getName() + " (%s)",
                        vbTimeStation.getNumber()))
                .icon(BitmapDescriptorFactory
                        .fromResource(getMarkerIcon(vbTimeStation)));

        // Check if the user is already localized
        if (currentLocation != null) {
            stationMarkerOptions = stationMarkerOptions.snippet(String.format(
                    context.getString(R.string.app_distance), MapUtils
                            .getMapDistance(context, currentLocation,
                                    vbTimeStation)));
        } else {
            stationMarkerOptions = stationMarkerOptions.snippet(String
                    .format(context.getString(R.string.app_distance_none)));
        }

        Marker stationMarker = stationMap.addMarker(stationMarkerOptions);
        stationMarker.showInfoWindow();
    }

    /**
     * Construct a CameraPosition focusing on Mountain View and animate the
     * camera to that position
     *
     * @param stationLocation the location of the station over the map (using LatLng object)
     */
    private void animateMapFocus(LatLng stationLocation) {
        LatLng focussedLatLng;

        // Check if the user is already localized
        if (stationLocation != null) {
            focussedLatLng = stationLocation;
        } else {
            focussedLatLng = centerStationLocation;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(focussedLatLng).zoom(17).build();
        stationMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    /**
     * Construct a CameraPosition focusing on Mountain View and animate the
     * camera to that position
     *
     * @param stationLocation the location of the station over the map (using Location
     *                        object)
     */
    private void animateMapFocus(Location stationLocation) {
        LatLng focussedLatLng;

        // Check if the user is already localized
        if (stationLocation != null) {
            focussedLatLng = new LatLng(stationLocation.getLatitude(),
                    stationLocation.getLongitude());
        } else {
            focussedLatLng = centerStationLocation;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(focussedLatLng).zoom(17).build();
        stationMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    /**
     * Form the ActionBar title, according to the type of the vehicle and its
     * number
     *
     * @param station the chosen station
     * @return the action bar title
     */
    private String getActionBarTitle(StationEntity station) {
        String stationText;

        switch (station.getType()) {
            case METRO1:
            case METRO2:
                stationText = getString(R.string.metro_item_station_number_text_sign);
                break;
            default:
                stationText = getString(R.string.pt_item_station_number_text_sign);
                break;
        }

        String actionBarTitle = String.format(stationText, station.getNumber());

        return actionBarTitle;
    }

    /**
     * Get the appropriate marker icon according to the station type
     *
     * @param stationType the type of the station
     * @return the marker icon from the resources
     */
    private int getMarkerIcon(StationEntity station) {

        int markerIcon;

        vehiclesDatasource.open();
        VehicleTypeEnum stationType = vehiclesDatasource
                .getVehicleTypesViaStation(station);
        vehiclesDatasource.close();

        switch (stationType) {
            case BUS:
                markerIcon = R.drawable.ic_bus_map_marker;
                break;
            case TROLLEY:
                markerIcon = R.drawable.ic_trolley_map_marker;
                break;
            case TRAM:
                markerIcon = R.drawable.ic_tram_map_marker;
                break;
            case BUS_TROLLEY:
                markerIcon = R.drawable.ic_bus_trolley_map_marker;
                break;
            case BUS_TRAM:
                markerIcon = R.drawable.ic_bus_tram_map_marker;
                break;
            case TRAM_TROLLEY:
                markerIcon = R.drawable.ic_trolley_tram_map_marker;
                break;
            case METRO1:
            case METRO2:
                markerIcon = R.drawable.ic_metro_map_marker;
                break;
            default:
                markerIcon = R.drawable.ic_bus_map_marker;
                break;
        }

        return markerIcon;
    }

}