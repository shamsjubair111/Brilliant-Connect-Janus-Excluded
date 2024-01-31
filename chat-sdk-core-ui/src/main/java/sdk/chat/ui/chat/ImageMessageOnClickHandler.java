package sdk.chat.ui.chat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import sdk.chat.ui.ChatSDKUI;
import sdk.chat.ui.R;
import sdk.chat.ui.activities.BaseActivity;
import sdk.chat.ui.views.PopupImageView;


/**
 * Created by benjaminsmiley-andrews on 20/06/2017.
 */

public class ImageMessageOnClickHandler {

    public static void onClick(Activity activity, View view, String url) {
        BaseActivity.hideKeyboard(activity);

        if (!url.replace(" ", "").isEmpty()) {

            PopupImageView popupView = ChatSDKUI.provider().popupImageView(activity);

//            PopupImageView popupView = new PopupImageView(activity);

            final PopupWindow imagePopup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            imagePopup.setOnDismissListener(popupView::dispose);

            imagePopup.setContentView(popupView);
            imagePopup.setBackgroundDrawable(new BitmapDrawable());
            imagePopup.setOutsideTouchable(true);
            imagePopup.setAnimationStyle(R.style.ImagePopupAnimation);

            popupView.setUrl(activity, url, imagePopup::dismiss);

            popupView.setOnDismiss(imagePopup::dismiss);

            imagePopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public static void onClick(Activity activity, View view, Bitmap bitmap) {
        BaseActivity.hideKeyboard(activity);

//        PopupImageView popupView = new PopupImageView(activity);
        PopupImageView popupView = ChatSDKUI.provider().popupImageView(activity);

        final PopupWindow imagePopup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        imagePopup.setOnDismissListener(popupView::dispose);

        imagePopup.setContentView(popupView);
        imagePopup.setBackgroundDrawable(new BitmapDrawable());
        imagePopup.setOutsideTouchable(true);
        imagePopup.setAnimationStyle(R.style.ImagePopupAnimation);

        popupView.setBitmap(activity, bitmap);

        popupView.setOnDismiss(imagePopup::dismiss);

        imagePopup.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

}
