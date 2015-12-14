package de.fh.zwickau.scriptsprachen.findme.client.activity

import android.content.res.Configuration
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.OnItemClick
import de.fh.zwickau.scriptsprachen.findme.client.util.Core
import de.fh.zwickau.scriptsprachen.findme.client.ui.ExpandableListAdapter
import de.fh.zwickau.scriptsprachen.findme.client.ui.OpenStreetMap
import de.fh.zwickau.scriptsprachen.findme.client.ui.Progress
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.util.Friend
import org.osmdroid.views.MapView
import android.os.Bundle

public class MainActivity extends AppCompatActivity {

    def openStreetMap;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ArrayList<String> friendArray;
    private Toolbar toolbar;
    View main, friend;
    def friendlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_container);



        initLayouts();
        ((FrameLayout) findViewById(R.id.content_frame)).addView(main);
        initToolbar()
        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);
        // This mus be called for automatic parsing of intent extras
        SwissKnife.loadExtras(this)

        Core.init(this)
        openStreetMap = new OpenStreetMap(this, (MapView) findViewById(R.id.mapview), Core.getLocator())


        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems()
        setupDrawer()

refresh()
    }
    void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    void initFriends() {
        friendlist = Core.getConnector().getFriends(true)
        friendArray = new ArrayList<String>()
        for (String text : friendlist) {
            def array=text.tokenize(',')
            def ntext=array[0]+" :) "+array[1]
            friendArray.add(ntext)
        }
        //friendArray=friendlist;
    }

    void initLayouts() {
        LayoutInflater inflater = getLayoutInflater();
        main = inflater.inflate(R.layout.activity_main, null);
        LayoutInflater inflater2 = getLayoutInflater();
        friend = inflater2.inflate(R.layout.friends_list, null);
    }

    @Override
    public void onDestroy() {
        super.onStop()
        Core.stopServer()
    }
    public void refresh(){
        Toast.makeText(baseContext, "refresh", Toast.LENGTH_SHORT).show();
        Progress.showProgress("Aktualisiere Freundesliste", this)
        initFriends();

        Progress.dismissProgress()

        Progress.showProgress("Aktualisiere Karte", this)
        for (Friend friend : friendlist) {
            openStreetMap.createFriend(friend)
        }
        openStreetMap.refreshMap()
        Progress.dismissProgress()
    }


    @OnItemClick(R.id.navList)
    public void changeInlay(int position, AdapterView<?> parent, View view) {
        String item = ((TextView) view).getText().toString();
        Toast.makeText(baseContext, "Time for an upgrade!" + item, Toast.LENGTH_SHORT).show();
        switch (item) {
            case "Map":
                ((FrameLayout) findViewById(R.id.content_frame)).removeAllViews()
                ((FrameLayout) findViewById(R.id.content_frame)).addView(main);
                initToolbar()
                break
            case "Friendslist":
                ((FrameLayout) findViewById(R.id.content_frame)).removeAllViews()
                ((FrameLayout) findViewById(R.id.content_frame)).addView(friend);
                initToolbar()
                setupFriendsList()
                break
        }
    }


    private void addDrawerItems() {
        String[] InlayArray = ["Map", "Friendslist"];
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
        if (id == R.id.action_refresh) {
            refresh()
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setupFriendsList() {
        expListView = (ExpandableListView) findViewById(R.id.Friend_list);

        // preparing list data
        prepareListData(friendArray);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0)
    }

    private void prepareListData(ArrayList<String> friendArray) {
        initExpandeblelistListeners();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> friendsList
        // Adding child data
        listDataHeader.add("Friendslist");
        if (friendArray != null) {
            friendsList = friendArray;
        } else {
            friendsList = new ArrayList<String>()
            friendsList.add("Friend 1");
            friendsList.add("Friend Smurf");
            friendsList.add("Friend Now");
            friendsList.add("Friend Canyons");
            friendsList.add("Friend Report");
        }



        listDataChild.put(listDataHeader.get(0), friendsList); // Header, Child data
    }

    private void setFriendslist(ArrayList<String> new_fList) {
        friendArray = new_fList
    }

    private void initExpandeblelistListeners() {
        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
