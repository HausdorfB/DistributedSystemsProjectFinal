package com.example.filmportapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filmportapp.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RoomActivity  extends AppCompatActivity {
    MqttAndroidClient client;
    String connType = "";
    String roomID = "";
    int count = 0;
    int totalCount = 0;

    TextView roomInfo, jointMem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        //get intent parameters
        Intent intent = getIntent();
        connType = intent.getStringExtra("CONN_TYPE");
        roomID = intent.getStringExtra("ROOM_ID");
        totalCount = intent.getIntExtra("COUNT",0);

        //init text view
        roomInfo = (TextView) findViewById(R.id.tvRoomID);
        roomInfo.setText("Room ID: " + roomID);
        jointMem = (TextView) findViewById(R.id.tvMembers);

        Constants.ROOM_ID = roomID;

        if (connType.equals("host")){
            count++;
        }

        //broker
        String host1 = "tcp://broker.hivemq.com:1883";
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(RoomActivity.this, host1, clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {


                    //subscription();
                    // We are connected

                    //pubMSG(videoS2, "movie");
                    //  client.close();
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    if (connType.equalsIgnoreCase("host")){
                        Toast.makeText(RoomActivity.this , "Host Connected", Toast.LENGTH_SHORT).show();
                       // count++;
                        hostSub();
                        informServer();
                        jointMem.setText(count+"");
                    }

                    else if (connType.equalsIgnoreCase("client")){
                        Toast.makeText(RoomActivity.this , "Viewer Connected", Toast.LENGTH_SHORT).show();
                        clientSub();
                        clientPub();
                    }



                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(RoomActivity.this , "Failed to Connect", Toast.LENGTH_SHORT).show();

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
            if (topic.equalsIgnoreCase(("filmport/rooms/"+roomID+"/host"))){
                count++;
                jointMem.setText( count + "");

                if (count == totalCount){
                    //pub start game
                    hostPub();
                    quizReady();
                }
            }
            else if (topic.equalsIgnoreCase(("filmport/rooms/"+roomID+"/clients"))){
                //start game
                quizReady();

            }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });






    }

    private void quizReady(){
        Intent intent = new Intent(RoomActivity.this, QuizReadyActivity.class);
        intent.putExtra("CONN_TYPE", connType);
        startActivity(intent);
    }

    private void informServer(){
        String topic = "filmport/trivia/lobby";
        String payload = totalCount+"&"+Constants.ROOM_ID;
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            // MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, payload.getBytes(), 0, false);
            Toast.makeText(this , "MSG Sent", Toast.LENGTH_SHORT).show();
            Toast.makeText(this , "Host Pubd", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void hostPub(){
        String topic = "filmport/rooms/"+roomID+"/clients";
        String payload = "startgame";
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            // MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, payload.getBytes(), 0, false);
            Toast.makeText(this , "MSG Sent", Toast.LENGTH_SHORT).show();
            Toast.makeText(this , "Host Pubd", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void clientPub(){
        String topic = "filmport/rooms/"+roomID+"/host";
        String payload = "joint";
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            // MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, payload.getBytes(), 0, false);
            Toast.makeText(this , "Client Pubd", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
     private void hostSub(){
             String topic = "filmport/rooms/"+roomID+"/host";
             int qos = 1;
             try {
                 IMqttToken subToken = client.subscribe(topic, qos);
                 subToken.setActionCallback(new IMqttActionListener() {
                     @Override
                     public void onSuccess(IMqttToken asyncActionToken) {
                         // The message was published
                         Toast.makeText(RoomActivity.this , "Host Subd", Toast.LENGTH_SHORT).show();
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

     private void clientSub(){
         String topic = "filmport/rooms/"+roomID+"/clients";
         int qos = 1;
         try {
             IMqttToken subToken = client.subscribe(topic, qos);
             subToken.setActionCallback(new IMqttActionListener() {
                 @Override
                 public void onSuccess(IMqttToken asyncActionToken) {
                     // The message was published
                     Toast.makeText(RoomActivity.this , "Client Subd", Toast.LENGTH_SHORT).show();
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
