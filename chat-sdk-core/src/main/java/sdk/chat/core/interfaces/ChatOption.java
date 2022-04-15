package sdk.chat.core.interfaces;

import android.app.Activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import io.reactivex.Completable;
import sdk.chat.core.dao.Thread;
import sdk.chat.core.ui.AbstractKeyboardOverlayFragment;
import sdk.chat.core.ui.KeyboardOverlayHandler;

/**
 * Created by ben on 10/11/17.
 */

public interface ChatOption {

    @StringRes int getTitle();
    @DrawableRes int getImage();
    Completable execute(Activity activity, Thread thread);

    AbstractKeyboardOverlayFragment getOverlay(KeyboardOverlayHandler sender);
    boolean hasOverlay();

}
