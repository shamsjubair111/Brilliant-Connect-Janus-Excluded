package notification;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.room.Message;
import com.flashphoner.fpwcsapi.room.Participant;
import com.flashphoner.fpwcsapi.room.Room;
import com.flashphoner.fpwcsapi.room.RoomEvent;
import com.flashphoner.fpwcsapi.room.RoomManager;
import com.flashphoner.fpwcsapi.room.RoomManagerEvent;
import com.flashphoner.fpwcsapi.room.RoomManagerOptions;
import com.flashphoner.fpwcsapi.room.RoomOptions;

import org.webrtc.SurfaceViewRenderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import sdk.chat.core.session.ChatSDK;

public class NotificationCancelActivity extends AppCompatActivity {
    private RoomManager roomManager;
    private Room room;
    private Queue<ParticipantView> freeViews = new LinkedList<>();
    private Map<String, ParticipantView> busyViews = new ConcurrentHashMap<>();

    private class ParticipantView {

        SurfaceViewRenderer surfaceViewRenderer;
        TextView login;

        public ParticipantView(SurfaceViewRenderer surfaceViewRenderer, TextView login) {
            this.surfaceViewRenderer = surfaceViewRenderer;
            this.login = login;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
        }
        String roomName = getIntent().getStringExtra("senderNumber").toString();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(100001);
        ChatSDK.mediaStop();
        Flashphoner.init(this);
        String currentUserId = ChatSDK.auth().currentUser().getName();
        RoomManagerOptions roomManagerOptions = new RoomManagerOptions("wss://tb.intercloud.com.bd:8443", "" + currentUserId);
        roomManager = Flashphoner.createRoomManager(roomManagerOptions);
        roomManager.on(new RoomManagerEvent() {
            @Override
            public void onConnected(final Connection connection) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (connection.getStatus().equals("ESTABLISHED")) {

                            /**
                             * Room name is set with method RoomOptions.setName().
                             */
                            RoomOptions roomOptions = new RoomOptions();
                            roomOptions.setName(roomName);

                            /**
                             * The participant joins a video chat room with method RoomManager.join().
                             * RoomOptions object is passed to the method.
                             * Room object is created and returned by the method.
                             */
                            room = roomManager.join(roomOptions);

                            /**
                             * Callback functions for events occurring in video chat room are added.
                             * If the event is related to actions performed by one of the other participants, Participant object with data of that participant is passed to the corresponding function.
                             */
                            room.on(new RoomEvent() {

                                @Override
                                public void onState(final Room room) {
//
//                                    Toast.makeText(ReceiverActivity.this, "Start Ringing" + room.getParticipants().size(), Toast.LENGTH_SHORT).show();
//
//
//                                    mediaPlayer.start();
//                                    mediaPlayer.setLooping(true);
//                                                final MediaPlayer mediaPlayer = new
                                    /**
                                     * After joining, Room object with data of the room is received.
                                     * Method Room.getParticipants() is used to check the number of already connected participants.
                                     * The method returns collection of Participant objects.
                                     * The collection size is determined, and, if the maximum allowed number (in this case, three) has already been reached, the user leaves the room with method Room.leave().
                                     */

                                    room.leave(null);
                                    finishAndRemoveTask();
//                                        room.leave();


                                }

                                @Override
                                public void onJoined(Participant participant) {

                                }

                                @Override
                                public void onLeft(Participant participant) {

                                }

                                @Override
                                public void onPublished(Participant participant) {

                                }

                                @Override
                                public void onFailed(Room room, String s) {

                                }

                                @Override
                                public void onMessage(Message message) {

                                }

                            });


                        }
                    }
                });
            }

            @Override
            public void onDisconnection(final Connection connection) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<Map.Entry<String, ParticipantView>> i = busyViews.entrySet().iterator();
                        while (i.hasNext()) {
                            Map.Entry<String, ParticipantView> e = i.next();
                            e.getValue().login.setText("NONE");
                            e.getValue().surfaceViewRenderer.release();
                            i.remove();
                            freeViews.add(e.getValue());
                        }

                    }
                });
                finishAndRemoveTask();
            }

        });
//        finishAndRemoveTask();
//        this.finish();
    }
}