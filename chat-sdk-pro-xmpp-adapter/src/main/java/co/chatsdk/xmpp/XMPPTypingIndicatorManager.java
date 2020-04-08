package co.chatsdk.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;

import java.util.HashMap;
import java.util.Map;

import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.interfaces.ThreadType;
import co.chatsdk.core.session.ChatSDK;

/**
 * Created by ben on 8/16/17.
 */

public class XMPPTypingIndicatorManager {

    // A map in the form:
    // {
    //    threadID: {userID: userName}
    // }
    private HashMap<String, HashMap<String, String>> typing = new HashMap<>();

    public void handleMessage (Message message, User user) {
        handleMessage(null, message, user);
    }
    public void handleMessage (ChatState state, Message message, User user) {

        if (state == null) {
            ChatStateExtension extension = (ChatStateExtension) message.getExtension(ChatStateExtension.NAMESPACE);
            state = extension.getChatState();
        }

        Thread thread = ChatSDK.db().fetchThreadWithEntityID(message.getFrom().asBareJid().toString());
        // Make sure we don't get a notification before the first message has been sent
        if(user != null && !user.equals(ChatSDK.currentUser()) && thread != null) {
            setTyping(thread, user, state.equals(ChatState.composing));
            // TODO: We could create the thread here... so they will see typing... on the empty thread
            // if this thread has not been created yet
            ChatSDK.events().source().onNext(NetworkEvent.typingStateChanged(notificationForThread(thread), thread));
        }

    }

    private void setTyping (Thread thread, User user, boolean isTyping) {
        HashMap map = typing.get(thread.getEntityID());
        if(map == null) {
            map = new HashMap();
            typing.put(thread.getEntityID(), map);
        }
        if(isTyping) {
            map.put(user.getEntityID(), user.getName());
        }
        else {
            map.remove(user.getEntityID());
        }
    }

    private String notificationForThread (Thread thread) {
        if (thread.getEntityID() != null) {
            Map<String, String> map = typing.get(thread.getEntityID());
            if(map == null || map.keySet().size() == 0) {
                return null;
            }
            if(thread.typeIs(ThreadType.Private1to1)) {
                return "";
            }

            String message = "";
            for (String key : map.keySet()) {
                message += map.get(key) + ", ";
            }
            if(message.length() >= 2) {
                message = message.substring(0, message.length() - 2);
            }
            return message + " ";
        }
        return "";
    }

}
