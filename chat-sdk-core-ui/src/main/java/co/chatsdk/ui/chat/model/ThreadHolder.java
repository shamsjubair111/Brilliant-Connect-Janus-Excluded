package co.chatsdk.ui.chat.model;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.dao.Message;
import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.ui.custom.Customiser;

public class ThreadHolder implements IDialog<MessageHolder> {

    protected Thread thread;
    protected ArrayList<UserHolder> users = null;
    protected MessageHolder lastMessage = null;
    protected Integer unreadCount = null;

    public ThreadHolder(Thread thread) {
        this.thread = thread;
    }

    @Override
    public String getId() {
        return thread.getEntityID();
    }

    public void markRead() {
        unreadCount = null;
    }

    @Override
    public String getDialogPhoto() {
        return thread.getImageUrl();
    }

    @Override
    public String getDialogName() {
        return thread.getDisplayName();
    }

    @Override
    public List<UserHolder> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
            for (User user: thread.getUsers()) {
                if (!user.isMe()) {
                    users.add(new UserHolder(user));
                }
            }
        }
        return users;
    }

    @Override
    public MessageHolder getLastMessage() {
        if (lastMessage != null) {
            return lastMessage;
        }
        else {
            Message message = thread.lastMessage();
            if (message != null) {
                lastMessage = Customiser.shared().onNewMessageHolder(message);
            }
        }
        return lastMessage;
    }

    @Override
    public void setLastMessage(MessageHolder message) {
        lastMessage = message;
        unreadCount = null;
    }

    @Override
    public int getUnreadCount() {
        if (unreadCount == null) {
            unreadCount = thread.getUnreadMessagesCount();
        }
        return unreadCount;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ThreadHolder && getId().equals(((ThreadHolder)object).getId());
    }

    public Thread getThread() {
        return thread;
    }

}
