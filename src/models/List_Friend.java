package models;

public class List_Friend {
    int user1;
    int user2;
    boolean isFriend;

    List_Friend(int user_id1, int user_id2, boolean isFriend){
        this.user1 = user_id1;
        this.user2 = user_id2;
        this.isFriend = isFriend;
    }

}
