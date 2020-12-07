package com.example.filmportapp;

import android.content.Intent;
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

public class HostActivity extends YouTubeBaseActivity {
    YouTubePlayerView mYoutubePlayerView;
    Button btnPlay, btnSkip, btnPause, btnRewind, btnSend;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Intent intent = getIntent();
        videoS2 = intent.getStringExtra("MOVIE_ID");

        //The key argument here must match that used in the other activity

        ////////////////////////////////////////////////






        //broker
        String host1 = "tcp://broker.hivemq.com:1883";
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(HostActivity.this, host1, clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    subscription();
                    // We are connected
                    Toast.makeText(HostActivity.this , "Connected", Toast.LENGTH_SHORT).show();
                    pubMSG(videoS2, (Constants.ROOM_ID+"movie"));
                    //  client.close();
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.




                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(HostActivity.this , "Failed to Connect", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }







        /////////////////////////////////////////////////////////

        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnSkip = (Button)findViewById(R.id.btnSkip);
        btnRewind = (Button)findViewById(R.id.btnRewind);
        btnSend = (Button)findViewById(R.id.btnSend);
        tvMSG = (TextView)findViewById(R.id.tvMSG);
        tvMSG.setMovementMethod(new ScrollingMovementMethod());
        tvMSG.setText(allMSG);

        etMSG = (EditText)findViewById(R.id.etMSG);

        etMSG.setHint("Enter message...");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputMSG = Constants.KEY_EMAIL+ ": " +etMSG.getText().toString();
                pubMSG(inputMSG, (Constants.ROOM_ID+"convo"));
                allMSG += "\n"+inputMSG;
                etMSG.getText().clear();
                etMSG.onEditorAction(EditorInfo.IME_ACTION_DONE);
                // tvMSG.append("\n"+inputMSG);
            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equalsIgnoreCase("video/sync/update/" +Constants.ROOM_ID+"convo")){
                    Toast.makeText(HostActivity.this , "MSG RECEIVED: NEW CHAT", Toast.LENGTH_SHORT).show();
                    tvMSG.append("\n"+message.toString());
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
                btnSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        youTubePlayer.seekToMillis(youTubePlayer.getCurrentTimeMillis()+10000);

                        pubMSG("SKIP", Constants.ROOM_ID+"skip");
                    }
                });
                btnRewind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        youTubePlayer.seekToMillis(youTubePlayer.getCurrentTimeMillis()-10000);

                        pubMSG("RWND", Constants.ROOM_ID+"rwnd");
                    }
                });
                btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        youTubePlayer.play();
                        pubMSG("PLAY", Constants.ROOM_ID+"play");
                    }
                });

                btnPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        youTubePlayer.pause();
                        pubMSG("PAUSE", Constants.ROOM_ID+"pause");
                    }
                });

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        //if message arrives
                        if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+"convo")){
                            Toast.makeText(HostActivity.this , "MSG RECEIVED: NEW CHAT", Toast.LENGTH_SHORT).show();
                            tvMSG.append("\n"+message.toString());
                        }

                        else  if (topic.equalsIgnoreCase("video/sync/update/" + Constants.ROOM_ID+"syncreq")){
                            Toast.makeText(HostActivity.this , "MSG RECEIVED: SYNC REQ", Toast.LENGTH_SHORT).show();
                            pubMSG((youTubePlayer.getCurrentTimeMillis()+""), Constants.ROOM_ID+"syncnow");

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

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pubMSG("START", Constants.ROOM_ID+"start");
                mYoutubePlayerView.initialize("AIzaSyBMkd0FZt260WBoQGT2vCJk4qQFbixHzTE", mOnInitialListener);

            }
        });






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



