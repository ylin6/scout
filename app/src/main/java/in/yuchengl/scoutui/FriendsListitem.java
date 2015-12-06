package in.yuchengl.scoutui;

/**
 * Friend List Item Class
 * Created by Yucheng on 10/3/15.
 */
class FriendsListItem {

    private String mName;
    private String mObjectId;
    private Boolean mLive;

    public FriendsListItem (String name, String id, Boolean live){
        mName = name;
        mObjectId = id;
        mLive = live;
    }

    public String getName() {return mName;}
    public String getId() {return mObjectId;}
    public Boolean getLive() {return mLive;}
}
