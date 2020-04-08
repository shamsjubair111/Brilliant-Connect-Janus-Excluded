package co.chatsdk.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import co.chatsdk.core.dao.Keys;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.R;
import co.chatsdk.ui.fragments.ProfileFragment;
import co.chatsdk.ui.utils.ToastHelper;

/**
 * Created by ben on 8/23/17.
 */

public class ProfileActivity extends BaseActivity {

    protected User user;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userEntityID = getIntent().getStringExtra(Keys.IntentKeyUserEntityID);

        if (userEntityID != null && !userEntityID.isEmpty()) {
            user =  ChatSDK.db().fetchUserWithEntityID(userEntityID);
            if (user != null) {
                ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.profileFragment);
                fragment.setUser(user);
                return;
            }
        }

        ToastHelper.show(this, R.string.user_entity_id_not_set);
        finish();

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_profile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}
