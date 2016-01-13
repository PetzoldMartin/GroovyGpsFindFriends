package de.fh.zwickau.scriptsprachen.findme.client.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnBackground
import com.arasthel.swissknife.annotations.OnItemClick
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.rest.RESTRequests
import de.fh.zwickau.scriptsprachen.findme.client.ui.EFriendList
import de.fh.zwickau.scriptsprachen.findme.client.ui.ExpandableListAdapter
import de.fh.zwickau.scriptsprachen.findme.client.ui.OpenStreetMap
import de.fh.zwickau.scriptsprachen.findme.client.ui.Progress
import de.fh.zwickau.scriptsprachen.findme.client.util.Core
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import org.osmdroid.views.MapView

public class MainActivity extends AppCompatActivity {

    def openStreetMap;
    def activity
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
    static EFriendList friendsList;


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

        activity = this
        Core.init(activity)
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

    void initFriends(boolean update) {
        friendsList = new EFriendList(Core.getConnector().getFriends(update))
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
        initFriends(true);
        Progress.dismissProgress()

        openStreetMap.removeAllMarkers()
        for (Friend friend : friendsList) {
            if(friend.visibility)
                openStreetMap.createFriend(friend)
        }
        openStreetMap.setOwnLocationToCenter()
        openStreetMap.refreshMap()

        setupFriendsList()
    }


    @OnItemClick(R.id.navList)
    public void changeInlay(int position, AdapterView<?> parent, View view) {
        String item = ((TextView) view).getText().toString();
        switch (item) {
            case "Map":
                friend.setVisibility(LinearLayout.GONE)
                main.setVisibility(LinearLayout.VERTICAL)
                mDrawerLayout.closeDrawers()
                refresh()
                break
            case "Friendslist":
                friend.setVisibility(LinearLayout.VERTICAL)
                main.setVisibility(LinearLayout.GONE)
                mDrawerLayout.closeDrawers()
                break
            case "Logout":
                mDrawerLayout.closeDrawers()
                showLogoutDialog()
                break
            default:
                mDrawerLayout.closeDrawers()
                Toast.makeText(this, "Nicht implementiert", Toast.LENGTH_LONG).show()
                break
        }
    }


    private void addDrawerItems() {
        String[] InlayArray = ["Map", "Friendslist","Logout"];
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
        } else if (id == R.id.action_addFriend) {
            createAddFriendWindow()
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnUIThread
    void setupFriendsList() {
        expListView = (ExpandableListView) findViewById(R.id.Friend_list);
        initExpandeblelistListeners();
        listAdapter = new ExpandableListAdapter(this, friendsList);
        this.listDataHeader = listAdapter.getListDataHeader()
        this.listDataChild = listAdapter.getListDataChild()
        expListView.setAdapter(listAdapter)
    }

    void updateFriendList() {
        initFriends(false)
        setupFriendsList()
        listAdapter.notifyDataSetChanged()
        expListView.invalidateViews()
    }


    private void initExpandeblelistListeners() {
        /*
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                friendsList.getFriendByListId(groupPosition, childPosition).setState(FriendState.REMOVED)
                setupFriendsList();
                return false;
            }
        });
        */

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    final ExpandableListAdapter adapter = ((ExpandableListView) parent).getExpandableListAdapter();
                    long packedPos = ((ExpandableListView) parent).getExpandableListPosition(position);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                    int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                    showDeleteFriendRealy(groupPosition,childPosition);

                }
                return false
            }
        })

    }


    void showDeleteFriendRealy(int groupPosition,int childPosition){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder // set dialog message
                .setCancelable(false)
                .setTitle("Freund löschen?")
                .setMessage("Wollen Sie den Freund wirklich "+friendsList.getFriendByListId(groupPosition, childPosition).getEmail()+" löschen?")
                .setPositiveButton("Ja",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id2) {
                        Core.getConnector().removeFriend(friendsList.getFriendByListId(groupPosition, childPosition), true)
                        setupFriendsList();
                    }
                })
                .setNegativeButton("Nein",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id2) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void createAddFriendWindow() {
        LayoutInflater li = getLayoutInflater();
        def promptsView = li.inflate(R.layout.popup_friendadd, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Core.getConnector().requestFriend(userInput.getText().toString())
                    }
                })
                .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @OnUIThread
    def showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder // set dialog message
                .setCancelable(false)
                .setTitle("Logout?")
                .setMessage("Wollen Sie sich wirklich Ausloggen?")
                .setPositiveButton("Ja",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Progress.showProgress("Logout",MainActivity.this)
                        new RESTRequests().logout(MainActivity.this)
                    }
                })
                .setNegativeButton("Abrechen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @OnBackground
    public void logoutSucceeded(boolean success,String response){
        Progress.dismissProgress()
        if (success) {
            Core.stopServer()
            finish()
        } else {
            showHardLogoutDialog(response)
        }
    }


    @OnUIThread
    void showHardLogoutDialog(String response) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder // set dialog message
                .setCancelable(false)
                .setTitle("$response!")
                .setMessage("Wollen Sie trotzdem beenden?")
                .setPositiveButton("Ja",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Core.stopServer()
                        finish()
                    }
                })
                .setNegativeButton("Nein",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
