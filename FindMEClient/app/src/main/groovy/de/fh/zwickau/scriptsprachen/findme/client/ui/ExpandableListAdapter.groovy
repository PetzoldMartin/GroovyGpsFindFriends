package de.fh.zwickau.scriptsprachen.findme.client.ui

import android.widget.CompoundButton
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.Switch
import de.fh.zwickau.scriptsprachen.findme.client.R
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
import de.fh.zwickau.scriptsprachen.findme.client.util.Core

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    def friendGroupID = 1
    def requestedGroupID = 0
    private def friendsList
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    public List<String> getListDataHeader() {
        return _listDataHeader;
    }

    public HashMap<String, List<String>> getListDataChild() {
        return _listDataChild;
    }


    public ExpandableListAdapter(Context context, def friendList) {
        this.friendsList = friendList;
        def listDataHeader = new ArrayList<String>();
        def listChildData = new HashMap<String, List<String>>();

        ArrayList<String> friendArray = new ArrayList<String>()
        ArrayList<String> reqFriendArray = new ArrayList<String>()
        friendList.each {
            if (it.state == FriendState.FRIEND) {
                friendArray.add(it.name.toString() + " : " + it.email)
                it.setViewGroupNr(friendGroupID);
                it.setViewNr(friendArray.size() - 1)
            }
            if (it.state == FriendState.REQUESTED) {
                reqFriendArray.add(it.name.toString() + " : " + it.email)
                it.setViewGroupNr(requestedGroupID);
                it.setViewNr(reqFriendArray.size() - 1)
            }
        }



        //order off the groups have to synct with group ids
        listDataHeader.add("Requesting Friends");
        listDataHeader.add("Friendslist");


        listChildData.put(listDataHeader.get(requestedGroupID), (List<String>) reqFriendArray);
        listChildData.put(listDataHeader.get(friendGroupID), (List<String>) friendArray);
        // Header, Child data
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }



    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        if (groupPosition == friendGroupID) {
            convertView.findViewById(R.id.add).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.check1).setVisibility(View.VISIBLE);
            Switch toggle = (Switch) convertView.findViewById(R.id.check1)
            if (toggle.isChecked()) {
                friendsList.getFriendByListId(groupPosition, childPosition).setVisibility(true);
            } else {
                friendsList.getFriendByListId(groupPosition, childPosition).setVisibility(false);
            }

            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        friendsList.getFriendByListId(groupPosition, childPosition).setVisibility(true);
                    } else {
                        friendsList.getFriendByListId(groupPosition, childPosition).setVisibility(false);
                    }
                }
            });
        }

        if (groupPosition == requestedGroupID) {
            convertView.findViewById(R.id.add).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.delete).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.check1).setVisibility(View.INVISIBLE);
            def imageButton = (ImageButton) convertView.findViewById(R.id.add);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Core.getConnector().updateFriend(friendsList.getFriendByListId(groupPosition, childPosition),FriendState.ACCEPTED)
                    //friendsList.getFriendByListId(groupPosition, childPosition).setState(FriendState.ACCEPTED);
                }
            });
            def imageButton2 = (ImageButton) convertView.findViewById(R.id.delete);
            imageButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Core.getConnector().updateFriend(friendsList.getFriendByListId(groupPosition, childPosition),FriendState.DENIED)
                    //friendsList.getFriendByListId(groupPosition, childPosition).setState(FriendState.DENIED);
                }
            });
        }
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}