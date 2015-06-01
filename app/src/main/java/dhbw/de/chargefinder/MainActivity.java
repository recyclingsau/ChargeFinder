package dhbw.de.chargefinder;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


// TODO zur Deprecation: http://stackoverflow.com/questions/29877692/why-was-actionbaractivity-deprecated
public class MainActivity extends ActionBarActivity implements SearchAsync.SearchAsyncListener,
        GeocoderAsync.GeocoderAsyncListener, LocationFinderAsync.LocationFinderAsyncListener
{

    protected EditText _editText_search = null;
    protected ImageButton _btn_search = null;
    protected ImageButton _btn_searchSettings = null;
    protected ImageButton _btn_searchPosition = null;
    protected ListView _listView_searchResults = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _editText_search = (EditText) findViewById(R.id.editText_search);
        _btn_search = (ImageButton) findViewById(R.id.btn_search);
        _btn_searchPosition = (ImageButton) findViewById(R.id.btn_searchPosition);
        _btn_searchSettings = (ImageButton) findViewById(R.id.btn_searchSettings);
        _listView_searchResults = (ListView) findViewById(R.id.listView_searchResults);


        _btn_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Check connectivity
                ConnectivityManager conMan = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // Mobile Daten
                NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();   // Wifi

                if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING
                        || wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
                {
                    new GeocoderAsync(MainActivity.this).
                            execute(_editText_search.getText().toString());
                }
                else {
                    // TODO Benachrichtigen dass keine Internetverbindung besteht
                }
            }
        });

        _btn_searchPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LocationFinderAsync(MainActivity.this).execute();
            }
        });

    }

    @Override
    public void updateListView(ArrayList<OpenChargePoint> points) {
        ArrayAdapter<OpenChargePoint> arrayAdapter = new ArrayAdapter<OpenChargePoint>(
                this,
                android.R.layout.simple_list_item_1,
                points);

        _listView_searchResults.setAdapter(arrayAdapter);
    }

    @Override
    public void receiveAddresses(List<Address> addresses) {

        // TODO: "Meinten Sie..." einbauen. Momentan wird nur die erste Addresse genutzt

        new SearchAsync(MainActivity.this).
                execute(addresses.toArray(new Address[addresses.size()]));
    }

    @Override
    public void receiveLocation(double lat, double lon) {
        Address a = new Address(Locale.getDefault());
        a.setLatitude(lat);
        a.setLongitude(lon);

        List<Address> addressList = new ArrayList<Address>();
        addressList.add(a);

    }

    @Override
    public Context getContext(){
        return this;
    }
}