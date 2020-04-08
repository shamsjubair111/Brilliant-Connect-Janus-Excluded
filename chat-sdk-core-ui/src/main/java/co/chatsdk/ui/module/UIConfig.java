package co.chatsdk.ui.module;

import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;

import sdk.guru.common.BaseConfig;
import co.chatsdk.ui.R;

public class UIConfig<T> extends BaseConfig<T> {

    /**
     * The theme to use in all activities
     */
    @StyleRes
    public int theme;

    /**
     * Default image for profile header
     */
    @DrawableRes
    public int profileHeaderImage = R.drawable.header2;

    @DrawableRes
    public int defaultProfileImage = R.drawable.icn_100_profile;

    public boolean resetPasswordEnabled = true;

    // Message types
    public boolean imageMessagesEnabled = true;
    public boolean locationMessagesEnabled = true;

    // Chat options
    public boolean groupsEnabled = true;
    public boolean threadDetailsEnabled = true;
    public boolean publicRoomCreationEnabled = false;
    public boolean saveImagesToDirectory = false;

    public String dateFormat = "HH:mm";

    public String usernameHint = null;

    public boolean imageCroppingEnabled = true;

    public UIConfig(T onBuild) {
        super(onBuild);
    }

    public UIConfig<T> setTheme(@StyleRes int theme) {
        this.theme = theme;
        return this;
    }

    public UIConfig<T> setImageCroppingEnabled(boolean enabled) {
        this.imageCroppingEnabled = enabled;
        return this;
    }

    public UIConfig<T> setResetPasswordEnabled(boolean resetPasswordEnabled) {
        this.resetPasswordEnabled = resetPasswordEnabled;
        return this;
    }

    public UIConfig<T> setPublicRoomCreationEnabled(boolean value) {
        this.publicRoomCreationEnabled = value;
        return this;
    }

//    public UIConfig<T> unreadMessagesCountForPublicChatRoomsEnabled(boolean value) {
//        this.unreadMessagesCountForPublicChatRoomsEnabled = value;
//        return this;
//    }

    public UIConfig<T> setImageMessagesEnabled(boolean value) {
        this.imageMessagesEnabled = value;
        return this;
    }

    public UIConfig<T> setLocationMessagesEnabled(boolean value) {
        this.locationMessagesEnabled = value;
        return this;
    }

    public UIConfig<T> setGroupsEnabled(boolean value) {
        this.groupsEnabled = value;
        return this;
    }

    public UIConfig<T> setThreadDetailsEnabled(boolean value) {
        this.threadDetailsEnabled = value;
        return this;
    }

    public UIConfig<T> setSaveImagesToDirectoryEnabled(boolean value) {
        this.saveImagesToDirectory = value;
        return this;
    }

    public UIConfig<T> setDateFormat(String format) {
        this.dateFormat = format;
        return this;
    }

    public UIConfig<T> setSetDefaultProfileImage(@DrawableRes int res) {
        this.defaultProfileImage = res;
        return this;
    }

    public UIConfig<T> setProfileHeaderImage(int profileHeaderImage) {
        this.profileHeaderImage = profileHeaderImage;
        return this;
    }

    public UIConfig<T> setDefaultProfileImage(int defaultProfileImage) {
        this.defaultProfileImage = defaultProfileImage;
        return this;
    }

    public UIConfig<T> setSaveImagesToDirectory(boolean saveImagesToDirectory) {
        this.saveImagesToDirectory = saveImagesToDirectory;
        return this;
    }

    public UIConfig<T> setUsernameHint(String usernameHint) {
        this.usernameHint = usernameHint;
        return this;
    }

}
