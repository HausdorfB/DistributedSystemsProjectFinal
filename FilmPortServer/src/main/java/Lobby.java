import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Lobby  implements MqttCallback {
    final IMqttClient client;
    int totalCount = 2;
    int count = 0;
    String[] uName = new String[10];
    int[] uScore = new int[10];
    String roomID = "";
    int winner = 0;
    Random rand = new Random();
    int gamestartlistener = 0;

    public static void main(String[] args) throws Exception {
        String broker = "tcp://broker.hivemq.com:1883";
        Lobby lobby = new Lobby(broker);
    }


    public Lobby(String broker) throws Exception {
        final String publisherId = UUID.randomUUID().toString();
        client = new MqttClient(broker, publisherId);
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.connect(options);
        client.setCallback(this);

        client.subscribe("filmport/trivia/#");

        //
        if (gamestartlistener == 1) {
           // new LobbyThread(client, totalCount, count, uName, uScore, roomID).start();
            gamestartlistener = 0;
        }

    }

   class LobbyThread extends Thread implements MqttCallback{

        int counter;
        int totalC;
        String[] Name = new String[10];
        int [] Score = new int[10];
       Random random = new Random();
        IMqttClient tClient;
        String room = "";
        String broker = "tcp://broker.hivemq.com:1883";
        public LobbyThread(IMqttClient client, int total, int count, String [] names, int [] scores, String roomid){
            this.counter = count;
            this.totalC = total;
            this.Name = names;
            this.Score = scores;
            //this.tClient = client;
            this.room = roomid;



        }

        public void run(){

            try {
                final String publisherId = UUID.randomUUID().toString();
                tClient = new MqttClient(broker, publisherId);
                final MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);
                options.setConnectionTimeout(10);
                tClient.connect(options);
                tClient.setCallback(this);

                tClient.subscribe("filmport/trivia/#");

            }
            catch(MqttException e){

                e.printStackTrace();
            }

            System.out.println("Thread started for " + room);
            tClient.setCallback(this);


        }
       public void checkWinner() throws MqttException {

           int hiScore = Score[0];
           for (int i = 0; i < totalC; i++){

               if (Score[i] >  hiScore){
                   hiScore = Score[i];
               }
           }

           List<String> winners = new ArrayList<>();

           //String [] winners = new String[totalCount];
           //int winCount = 0;
           for (int j = 0; j<totalC; j++){
               if (Score[j] == hiScore){
                   winners.add(Name[j]);
                   //winners[winCount] = uName[j];
                   //winCount++;
               }
           }

           int rando = rand.nextInt(winners.size());

           String finalWinner = winners.get(rando);

           for (int k = 0; k < totalC; k++){

               if (Name[k].equalsIgnoreCase(finalWinner)){
                   MqttMessage message = new MqttMessage();
                   message.setPayload("host".getBytes()); //convert to byte
                   message.setQos(1);//set level
                   client.publish("filmport/trivia/"+ room+ "/" + Name[k], message);
                   System.out.println("Host Message Sent to " + Name[k] + " in " + room);
               }
               else {
                   MqttMessage message = new MqttMessage();
                   message.setPayload("client".getBytes()); //convert to byte
                   message.setQos(1);//set level
                   client.publish("filmport/trivia/"+ room + "/" + Name[k], message);
                   System.out.println("Client Message Sent to " + Name[k]+ " in " + room);
               }
           }
           resetServer();









       }

       public  void resetServer(){
           this.counter = 0;
           this.totalC = 2;

       }
       @Override
       public void connectionLost(Throwable throwable) {

       }

       @Override
       public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
           if (s.equalsIgnoreCase("filmport/trivia/whowon/" + room)) {

               System.out.println("Current Count: " + counter);
               String[] iMSG = (mqttMessage.toString()).split("&", 2);
               uName[counter] = iMSG[0];
               uScore[counter] = Integer.parseInt(iMSG[1]);
               if ((counter+1) == totalC) {
                   checkWinner();
               }
               else {
                   counter++;
               }

           }
       }

       @Override
       public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

       }
   }







    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        if (s.equalsIgnoreCase("filmport/trivia/lobby")) {

            String[] iMSG = (mqttMessage.toString()).split("&", 2);

            this.totalCount = Integer.parseInt(iMSG[0]);
            roomID = iMSG[1];

           // Integer.parseInt(mqttMessage.toString());
            System.out.println("Total Count Set To: " + totalCount);
            System.out.println("Room: " + roomID);
            new LobbyThread(client, totalCount, count, uName, uScore, roomID).start();
 //public LobbyThread(IMqttClient client, int total, int count, String [] names, int [] scores, String roomid){


        }
        /*
        else if (s.equalsIgnoreCase("filmport/trivia/whowon")) {

            System.out.println("Current Count: " + count);
            String[] iMSG = (mqttMessage.toString()).split("&", 2);
            uName[count] = iMSG[0];
            uScore[count] = Integer.parseInt(iMSG[1]);
            if ((count+1) == totalCount) {
                checkWinner();
            }
            else {
                count++;
            }

        }
        */
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
