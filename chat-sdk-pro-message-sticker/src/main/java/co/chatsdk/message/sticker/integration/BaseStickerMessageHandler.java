package co.chatsdk.message.sticker.integration;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import org.pmw.tinylog.Logger;

import co.chatsdk.message.sticker.Configuration;
import sdk.chat.core.dao.Keys;
import sdk.chat.core.dao.Message;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.handlers.StickerMessageHandler;
import sdk.chat.core.rigs.MessageSendRig;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.MessageType;
import io.reactivex.Completable;
import sdk.chat.core.utils.StringChecker;

/**
 * Created by ben on 10/11/17.
 */

public class BaseStickerMessageHandler implements StickerMessageHandler {
    @Override
    public Completable sendMessageWithSticker(final String stickerImageName, final Thread thread) {
        return new MessageSendRig(new MessageType(MessageType.Sticker), thread, message -> {
            message.setText(stickerImageName);
            message.setValueForKey(stickerImageName, Keys.MessageStickerName);
        }).run();
    }

    @Override
    public String textRepresentation(Message message) {
        String name = message.stringForKey(Keys.MessageStickerName);
        String [] parts = name.split(".");
        if (parts.length == 2) {
            return parts[0];
        }
        return name;
    }

    @Override
    public String getImageURL(Message message) {
        if (message.getMessageType().is(MessageType.Sticker)  || message.getReplyType().is(MessageType.Sticker)) {
            Context context = ChatSDK.ctx();
            Resources resources = context.getResources();
            String stickerName = (String) message.valueForKey(Keys.MessageStickerName);

            Logger.debug("Sticker:" + System.identityHashCode(message) + ", " + message.getMetaValuesAsMap() + ", " + stickerName);

            // Do this because otherwise we get a crash because the message
            // holder tries to update before it's ready
            if (!StringChecker.isNullOrEmpty(stickerName)) {
                int resID = Configuration.resourceId(context, stickerName);

                Uri uri = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                        .authority(resources.getResourcePackageName(resID))
                        .appendPath(resources.getResourceTypeName(resID))
                        .appendPath(resources.getResourceEntryName(resID))
                        .build();

                return uri.toString();
            }
        }
        return null;
    }
}
