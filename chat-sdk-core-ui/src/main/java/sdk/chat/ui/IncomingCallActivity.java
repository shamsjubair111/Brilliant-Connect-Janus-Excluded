package sdk.chat.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



import notification.NotificationCancelActivity;
import sdk.chat.core.session.ChatSDK;

public class IncomingCallActivity extends AppCompatActivity   {

    private ImageButton activity_hang_up_button;
    private ImageButton activity_answer_call_button;

    private TextView caller_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        ChatSDK.callActivities.put("incomingCallActivity",this);
        activity_hang_up_button = findViewById(R.id.activity_hang_up_button);
        activity_answer_call_button = findViewById(R.id.activity_answer_call_button);
        caller_name = findViewById(R.id.caller_name);

        caller_name.setText(getIntent().getStringExtra("senderNumber"));

        activity_hang_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String senderNumber = getIntent().getStringExtra("senderNumber");
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(100001);
                Intent intent = new Intent(getApplicationContext(), NotificationCancelActivity.class);
                intent.putExtra("senderNumber", senderNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ChatSDK.mediaStop();
                finish();
                startActivity(intent);

            }
        });



        activity_answer_call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ChatSDK.mediaStop();
                String senderNumber = getIntent().getStringExtra("senderNumber");
                Intent intent = new Intent(getApplicationContext(), ReceiverActivity.class);
                intent.putExtra("senderNumber", senderNumber);
                String callType = getIntent().getStringExtra("type");
                intent.putExtra("type", callType);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}