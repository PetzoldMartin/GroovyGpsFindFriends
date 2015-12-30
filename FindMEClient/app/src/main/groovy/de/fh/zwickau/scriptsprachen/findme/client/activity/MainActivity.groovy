package de.fh.zwickau.scriptsprachen.findme.client.activity

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.ExpandableListView.OnChildClickListener
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnItemClick
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
import de.fh.zwickau.scriptsprachen.findme.client.ui.EFriendList
import de.fh.zwickau.scriptsprachen.findme.client.ui.ExpandableListAdapter
import de.fh.zwickau.scriptsprachen.findme.client.ui.OpenStreetMap
import de.fh.zwickau.scriptsprachen.findme.client.ui.Progress
import de.fh.zwickau.scriptsprachen.findme.client.util.Core
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import org.osmdroid.views.MapView

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
    EFriendList friendsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sidebar_container);

        initLayouts();
        ((FrameLayout) findViewById(R.id.content_frame)).addView(main);
        ((FrameLayout) findViewById(R.id.content_frame)).addView(friend);
        friend.setVisibility(LinearLayout.GONE)
        main.setVisibility(LinearLayout.VERTICAL)
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



    void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)
        getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    void initFriends() {

        friendsList = new EFriendList(Core.getConnector().getFriends(true))

    }

    void initLayouts() {
        LayoutInflater inflater2 = getLayoutInflater();
        friend = inflater2.inflate(R.layout.friends_list, null, false);
        LayoutInflater inflater = getLayoutInflater();
        main = inflater.inflate(R.layout.activity_main, null, false);
    }

    @OnBackground
    public void refresh() {
        Progress.showProgress("Aktualisiere Freundesliste", this)
        while (!Progress.isDialogShown())
            sleep(500)
        initFriends();
        Progress.dismissProgress()

        openStreetMap.removeAllMarkers()
        for (Friend friend : friendsList) {
            openStreetMap.createFriend(friend)
        }
        openStreetMap.setOwnLocationToCenter()
        openStreetMap.refreshMap()
    }


    @OnItemClick(R.id.navList)
    public void changeInlay(int position, AdapterView<?> parent, View view) {
        String item = ((TextView) view).getText().toString();
        switch (item) {
            case "Map":
                friend.setVisibility(LinearLayout.GONE)
                main.setVisibility(LinearLayout.VERTICAL)
                mDrawerLayout.closeDrawers()
                break
            case "Friendslist":
                friend.setVisibility(LinearLayout.VERTICAL)
                main.setVisibility(LinearLayout.GONE)
                setupFriendsList()
                mDrawerLayout.closeDrawers()
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
        int id = item.getItemId();
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
        initExpandeblelistListeners();
        listAdapter = new ExpandableListAdapter(this, friendsList);
        this.listDataHeader = listAdapter.getListDataHeader()
        this.listDataChild = listAdapter.getListDataChild()
        expListView.setAdapter(listAdapter);
    }


    private void initExpandeblelistListeners() {
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Switch cb = (Switch) v.findViewById(R.id.check1);
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition) + " details " + cb.isChecked()

                        , Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    final ExpandableListAdapter adapter = ((ExpandableListView) parent).getExpandableListAdapter();
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(position);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                    int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                    friendsList.getFriendByListId(groupPosition,childPosition).setState(FriendState.REMOVED);
                }
                return false
            }
        })

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
