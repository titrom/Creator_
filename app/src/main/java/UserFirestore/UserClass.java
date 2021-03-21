package UserFirestore;

import android.net.Uri;

public class UserClass {
    private String nickname;
    private int XpUser;


    public UserClass(String nickname, int xpUser) {
        this.nickname = nickname;
        XpUser = xpUser;
    }

    public int getXpUser() {
        return XpUser;
    }

    public void setXpUser(int xpUser) {
        XpUser = xpUser;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
