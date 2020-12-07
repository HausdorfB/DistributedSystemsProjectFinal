package com.example.filmportapp;


import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filmportapp.utils.Constants;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ClientActivity extends YouTubeBaseActivity {
    YouTubePlayerView mYoutubePlayerView;
    Button btnSync, btnSend;
    TextView tvMSG;
    EditText etMSG;
    YouTubePlayer.OnInitializedListener mOnInitialListener;
    String videoL = "87F2d89GatA";
    String videoL2 = "1JPxRU4y19w";
    String videoS = "Qgduhk26sIw";
    String videoS2 = "RnqAXuLZlaE";

    String allMSG = "";
    String inputMSG = "";

    MqttAndroidClient client;
    YouTubePlayer youTubePlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        btnSync = (Button)findViewById(R.id.btnSync);

        btnSend = (Button)findViewById(R.id.btnSend);
        tvMSG = (TextView)findViewById(R.id.tvMSG);
        tvMSG.setMovementMethod(new ScrollingMovementMethod());
        tvMSG.setText(allMSG);
        etMSG = (EditText)findViewById(R.id.etMSG);

        etMSG.setHint("Enter message...");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputMSG = Constants.KEY_EMAIL+ ": " + etMSG.getText().toString();
                pubMSG(inputMSG, (Constants.ROOM_ID+"convo"));
                etMSG.getText().clear();
                etMSG.onEditorAction(EditorInfo.IME_ACTION_DONE);
                // allMSG += "\n"+inputMSG;
                //tvMSG.append("\n"+inputMSG);
            }
        });


        //broker
        String host1 = "tcp://broker.hivemq.com:1883";
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(ClientActivity.this, host1, clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(ClientActivity.this , "Connected", Toast.LENGTH_SHORT).show();
                    //  client.close();
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.

                    subscription();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(ClientActivity.this , "Failed to Connect", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //tvMSG.append("\n"+message.toString());

                if (topic.equalsIgnoreCase("video/sync/update/"+ Constants.ROOM_ID + "start")){
                    Toast.makeText(ClientActivity.this , "MSG RECEIVED: START", Toast.LENGTH_SHORT).show();
                    mYoutubePlayerView.initialize("AIzaSyBMkd0FZt260WBoQGT2vCJk4qQFbixHzTE", mOnInitialListener);
                }
                else if (topic.equalsIgnoreCase("video/sync/update/"+ Constants.ROOM_ID+"convo")){
                    Toast.makeText(ClientActivity.this , "MSG RECEIVED: NEW CHAT", Toast.LENGTH_SHORT).show();
                    tvMSG.append("\n"+message.toString());
                }
                else if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+"movie")){
                    Toast.makeText(ClientActivity.this , "MSG RECEIVED: MOV RECVD", Toast.LENGTH_SHORT).show();
                    videoS2 = message.toString();
                }



            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


        mYoutubePlayerView = (YouTubePlayerView)findViewById(R.id.YTvid);

        mOnInitialListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(videoS2, 4000);

                btnSync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pubMSG("SYNC REQ",  Constants.ROOM_ID+ "syncreq");
                        //CODE TO SYNC HERE
                    }
                });
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etMSG = (EditText)findViewById(R.id.etMSG);
                        inputMSG = Constants.KEY_EMAIL+ ": " + etMSG.getText().toString();
                        pubMSG(inputMSG, (Constants.ROOM_ID+"convo"));
                        etMSG.getText().clear();
                        etMSG.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        // allMSG += "\n"+inputMSG;
                        //tvMSG.append("\n"+inputMSG);
                    }
                });

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {


                        if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+ "play")){
                            Toast.makeText(ClientActivity.this , "MSG RECEIVED: PLAY", Toast.LENGTH_SHORT).show();
                            youTubePlayer.play();
                        }
                        else if (topic.equalsIgnoreCase("video/sync/update/" +  Constants.ROOM_ID+ "skip")){
                            Toast.makeText(ClientActivity.this , "MSG RECEIVED: SKIP", Toast.LENGTH_SHORT).show();
                            //mYoutubePlayerView.initialize("AIzaSyBMkd0FZt260WBoQGT2vCJk4qQFbixHzTE", mOnInitialListener);
                            youTubePlayer.seekToMillis(youTubePlayer.getCurrentTimeMillis()+ 10000);
                        }
                        else if (topic.equalsIgnoreCase("video/sync/update/" +   Constants.ROOM_ID+ "rwnd")){
                            Toast.makeText(ClientActivity.this , "MSG RECEIVED: RWND", Toast.LENGTH_SHORT).show();
                            //mYoutubePlayerView.initialize("AIzaSyBMkd0FZt260WBoQGT2vCJk4qQFbixHzTE", mOnInitialListener);
                            youTubePlayer.seekToMillis(youTubePlayer.getCurrentTimeMillis()- 10000);
                        }

                        else if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+ "pause")){
                            Toast.makeText(ClientActivity.this , "MSG RECEIVED: PAUSE", Toast.LENGTH_SHORT).show();
                            //mYoutubePlayerView.initialize("AIzaSyBMkd0FZt260WBoQGT2vCJk4qQFbixHzTE", mOnInitialListener);
                            youTubePlayer.pause();
                        }
                        else if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+ "syncnow")){
                            Toast.makeText(ClientActivity.this , "MSG RECEIVED: SYNCING", Toast.LENGTH_SHORT).show();
                            //mYoutubePlayerView.initialize("AIzaSyBMkd0FZt260WBoQGT2vCJk4qQFbixHzTE", mOnInitialListener);


                            int timeSync = Integer.parseInt(String.valueOf(message));

                            youTubePlayer.seekToMillis(timeSync+1000);
                        }
                        else if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+"convo")){
                            Toast.makeText(ClientActivity.this , "MSG RECEIVED: NEW CHAT", Toast.LENGTH_SHORT).show();
                            tvMSG.append("\n"+message.toString());
                        }





                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

    }

    private void pubMSG(String msg, String topicid){
        String topic = "video/sync/update/"+topicid;
        String payload = msg;
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            // MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, payload.getBytes(), 0, false);
            Toast.makeText(this , "MSG Sent", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private void subscription (){
        String topic = "video/sync/update/#";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
