package sdk.chat.ui;

import static sdk.chat.ui.utils.ValidPhoneNumberUtil.validPhoneNumber;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import sdk.chat.core.dao.User;
import sdk.chat.core.session.ChatSDK;
import sdk.chat.ui.utils.ToastHelper;
import sdk.guru.common.DisposableMap;
import sdk.guru.common.RX;

public class ContactProfile extends AppCompatActivity implements Consumer<Throwable>, CompletableObserver {

    DisposableMap dm = new DisposableMap();
    private TextView userNameTextView;
    private TextView textView9;
    private ImageView backImage;
    private ImageView userPicture;
    private ImageView videoCall;
    private ImageView imageView3;
    private ImageView chatIcon;
    private ImageView appAudioCall;
    private String receiverNumber;
    private String receiverName;

    private LinearLayout directCall;
    private LinearLayout apptoappVideoCall;
    private LinearLayout apptoappMessage;
    private LinearLayout apptonumberSms;
    private LinearLayout AppToAppCallCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_profile);

        userNameTextView = findViewById(R.id.userNameTextView);
        textView9 = findViewById(R.id.textView9);
        backImage = findViewById(R.id.backImage);
        directCall = findViewById(R.id.directCall);
        AppToAppCallCall = findViewById(R.id.AppToAppCallCall);
        apptoappVideoCall = findViewById(R.id.apptoappVideoCall);
        apptoappMessage = findViewById(R.id.apptoappMessage);
        apptonumberSms = findViewById(R.id.apptonumberSms);

        receiverName = getIntent().getStringExtra("contactName");
        userNameTextView.setText(receiverName);
        int imageResId = getIntent().getIntExtra("imageResId", -1);


        textView9.setText(getIntent().getStringExtra("contactNumber"));


        userPicture = findViewById(R.id.userPicture);
        String contactImage = getIntent().getStringExtra("contactImage");
        if(!Objects.equals(contactImage, "")){
            Uri contactImageUri = Uri.parse(contactImage);
            userPicture.setImageURI(contactImageUri);
        }

//        Glide.with(this)
//                .load(imageList.get(imageResId % imageList.size()))
//                .apply(RequestOptions.circleCropTransform())
//                .into(userPicture);


        receiverNumber = validPhoneNumber(getIntent().getStringExtra("contactNumber"));


//        apptoappVideoCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (getIntent().getStringExtra("registered").equals("yes")) {
//
//                    if(ChatSDK.auth().getCurrentUserEntityID()!=null) {
//                        Intent intent = new Intent(getApplicationContext(), AppToAppCall.class);
//                        intent.putExtra("receiverNumber", receiverNumber);
//                        intent.putExtra("type", "video");
//                        intent.putExtra("contactName", receiverName);
//                        intent.putExtra("photo", contactImage);
//                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    }
//                    else{
//                        Toast.makeText(ContactProfile.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(ContactProfile.this, "Number Not Registered", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        apptoappMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ChatSDK.auth().getCurrentUserEntityID()!=null) {
                    User user = ChatSDK.db().fetchUserWithEntityID(receiverNumber + "@localhost");

                    dm.add(ChatSDK.thread().createThread("", User.convertIfPossible(Arrays.asList(user))).observeOn(RX.main()).subscribe(thread -> {
                        ChatSDK.ui().startChatActivityForID(ContactProfile.this, thread.getEntityID());
                        finish();
                    }, ContactProfile.this));
                }
                else{
                    Toast.makeText(ContactProfile.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        AppToAppCallCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getIntent().getStringExtra("registered").equals("yes")) {
//
//                    if(ChatSDK.auth().getCurrentUserEntityID()!=null) {
//                        Intent intent = new Intent(getApplicationContext(), AppToAppCall.class);
//                        intent.putExtra("receiverNumber", receiverNumber);
//                        intent.putExtra("type", "audio");
//                        intent.putExtra("contactName", receiverName);
//                        intent.putExtra("photo", contactImage);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.setAction(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//                    }
//                    else{
//                        Toast.makeText(ContactProfile.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ContactProfile.this, "Number Not Registered", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


//        directCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(ChatSDK.auth().getCurrentUserEntityID()!=null) {
//                    Intent intent = new Intent(ContactProfile.this, OutgoingCall.class);
//                    intent.putExtra("receiverNumber", receiverNumber);
//                    intent.putExtra("activityName", "ContactProfile");
//                    intent.putExtra("contactName", receiverName);
//                    intent.putExtra("photo", contactImage);
//                    startActivity(intent);
//                }
//                else{
//                    Toast.makeText(ContactProfile.this, "Please try again later", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void accept(Throwable t) {
        onError(t);
    }

    public void onSubscribe(@NonNull Disposable d) {
        dm.add(d);
    }

    /**
     * Called once the deferred computation completes normally.
     */
    public void onComplete() {

    }

    /**
     * Called once if the deferred computation 'throws' an exception.
     *
     * @param e the exception, not null.
     */
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        System.out.println("SHOW TOAST" + e.getLocalizedMessage());

        ToastHelper.show(this, e.getLocalizedMessage());
    }
}