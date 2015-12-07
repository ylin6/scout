package in.yuchengl.scoutui;

/**
 * Created by gmartnz on 12/6/15.
 */
public class RequestListItem {
    private String mRequestId;
    private String mInviterName;

    public RequestListItem (String name, String id) {
        mRequestId = id;
        mInviterName = name;
    }

    public String getId() {return mRequestId;}
    public String getInviterName() {return mInviterName;}
}
