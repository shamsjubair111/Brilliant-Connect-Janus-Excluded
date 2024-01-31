package sdk.chat.core.utils;

import java.util.ArrayList;
import java.util.List;

import sdk.chat.core.dao.User;
import sdk.chat.core.interfaces.UserListItem;

/**
 * Created by ben on 10/9/17.
 */

public class UserListItemConverter {
    public static List<UserListItem> toUserItemList (List<User> users) {
        return new ArrayList<>(users);
    }

    public static List<User> toUserList (List<UserListItem> items) {
        ArrayList<User> users = new ArrayList<>();
        for(UserListItem u : items) {
            if(u instanceof User) {
                users.add((User) u);
            }
        }
        return users;
    }

    public static User toUser (UserListItem item) {
        if(item instanceof User) {
            return (User) item;
        }
        return null;
    }

}
