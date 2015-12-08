package com.example.aisma.findmeclient

import android.content.res.Configuration
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.OnItemClick
import org.osmdroid.views.MapView
import android.os.Bundle

public class MainActivity extends AppCompatActivity {
    def static ILocator
    def openStreetMap;
    RESTRequests restRequests;
    RESTServer restServer;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_container);
        this.setLayoutInlay(R.layout.activity_main);
        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);
        // This mus be called for automatic parsing of intent extras
        SwissKnife.loadExtras(this)

        ILocator = new ClientLocator(this);
        openStreetMap = new OpenStreetMap(this, (MapView) findViewById(R.id.mapview), ILocator)
        restRequests = new RESTRequests()
        restServer = new RESTServer()
        //def toolbar = (Toolbar) findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar)
        getSupportActionBar().show();


        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems()
        setupDrawer()
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @OnItemClick(R.id.navList)
    public void changeInlay(int position,AdapterView<?> parent,View view){
        String item = ((TextView)view).getText().toString();
        Toast.makeText(baseContext, "Time for an upgrade!"+item, Toast.LENGTH_SHORT).show();
        switch (item){
            case "Map":
                this.setLayoutInlay(R.layout.activity_main);
                break
            case "Friendslist":
                this.setLayoutInlay(R.layout.friends_list);
                break
        }



    }

    @OnClick(R.id.jetty)
    public void startJetty() {
        restServer.startServer(ILocator)
    }

    @OnClick(R.id.restClient)
    public void restResponse() {
        //restRequests.testRestRequest()
        //restRequests.getAllUsers("testemail")
        //restRequests.register("myemail", "Tobias")
        //restRequests.login("myemail")
        //restRequests.getIpForEmail("testemail", "myemail")
        //restRequests.logout("myemail")
        restRequests.getLocation("localhost:8080")
    }

    public static ClientLocator getLocator() {
        return this.ILocator
    }

    private void setLayoutInlay(id){
        ((FrameLayout) findViewById(R.id.content_frame)).removeAllViews()


        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(id, null);
        ((FrameLayout)findViewById(R.id.content_frame)).addView(v);
    }
    private void addDrawerItems() {
        String[] InlayArray = ["Map","Friendslist"];
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, InlayArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
