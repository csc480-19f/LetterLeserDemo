package edu.oswego;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


@ServerEndpoint("/engine")
public class Websocket {
    HashMap<String, HashMap<String,String>> userFavList = new HashMap<>();
    HashMap<String, ArrayList<String>> userFolderList = new HashMap<>();
    @OnOpen
    public void onOpen(Session session) {

    }

    // This method allows you to message a specific user.
    /*
     * session.getBasicRemote().sendText(message);
     */
    @OnMessage // method that communicates with clients
    public void onMessage(String message, Session session) {

        JsonObject jmessage = new JsonParser().parse(message).getAsJsonObject();
        String messageType = jmessage.get("messagetype").getAsString();
        String email = jmessage.get("email").getAsString();
        if(messageType.equals("refresh")){
            try {
                JsonObject js = new JsonObject();
                js.addProperty("messageType","statusupdate");
                js.addProperty("message","refresh done");
                session.getBasicRemote().sendText(js.getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("filter")){
            //JsonObject filter = jmessage.get("filter").getAsJsonObject();
            try {
                session.getBasicRemote().sendText(generateData().getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("addfavorites")){

            JsonObject fav = jmessage.get("favorite").getAsJsonObject();
            String name = fav.get("name").getAsString();
            HashMap<String,String> favlist = userFavList.get(email);
            favlist.put(name,fav.getAsString());
            userFavList.put(email,favlist);
            ArrayList<String> l = new ArrayList<>(favlist.keySet());

            JsonObject js = new JsonObject();
            JsonArray ja = new JsonArray();
            for(int i=0;i<l.size();i++){ja.add(l.get(i));}
            js.addProperty("datatype","favoritenames");
            js.add("favoritenames",ja);

            try {
                session.getBasicRemote().sendText(js.getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("callfavorite")){
            //String favoriteName = jmessage.get("favoriteName").getAsString();
            //JsonObject js = new JsonParser().parse(userFavList.get("email").get(favoriteName)).getAsJsonObject();
            try {
                session.getBasicRemote().sendText(generateData().getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(messageType.equals("removefavorite")){
            String favorite = jmessage.get("favoritename").getAsString();
            HashMap favlist = userFavList.get(email);
            favlist.remove(favorite);
            ArrayList<String> l = new ArrayList<>(favlist.keySet());

            JsonObject js = new JsonObject();
            JsonArray ja = new JsonArray();
            for(int i=0;i<l.size();i++){ja.add(l.get(i));}
            js.addProperty("datatype","favoritenames");
            js.add("favoritenames",ja);

            try {
                session.getBasicRemote().sendText(js.getAsString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("login")){
            String pass = jmessage.get("pass").getAsString();

        }else{
            try {
                session.getBasicRemote().sendText("invalid messageType");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @OnClose // method to disconnect from a session : this also interrupts everything and
    // stops everything
    public void onClose(Session session) {

    }

    @OnError
    public void onError(Throwable t, Session session) {

    }

    private JsonObject generateData(){
        Random random = new Random();
        JsonObject js = new JsonObject();
        js.addProperty("messagetype","graphs");

        //setment
        JsonObject seniment = new JsonObject();
        seniment.addProperty("sentimentscore",(random.nextFloat()*100));


        //email by domain

        //EmailsByFolder

        //EmailsSentAndRecieved
        JsonArray jaa1 = new JsonArray();
        JsonArray jaa2 = new JsonArray();
        for(int i=0;i<7;i++){
            jaa1.add(random.nextInt(150));
            jaa2.add(random.nextInt(150));
        }
        JsonObject esnr = new JsonObject();
        esnr.add("sentemails",jaa1);
        esnr.add("recievedemails",jaa2);

        //NumberOfEmails
        JsonArray emailssnr = new JsonArray();
        for(int i=0;i<7;i++){
            JsonArray x = new JsonArray();
            for(int y = 0 ; y<6;y++){
                x.add(random.nextInt(200));
            }
            emailssnr.add(x);
        }

        //TimeBetweenReplies
        JsonArray ja1 = new JsonArray();
        JsonArray ja2 = new JsonArray();
        for(int i=0;i<7;i++){
            ja1.add(random.nextInt(200));
            ja2.add(random.nextInt(200));
        }
        JsonObject timeBetween = new JsonObject();
        timeBetween.add("sentemails",ja1);
        timeBetween.add("recievedemails",ja2);


        js.add("sentimentscore",seniment);
        js.add("emailssentandrecieved",esnr);
        js.add("numberofemails",emailssnr);
        js.add("timebetweenreplies",timeBetween);


        return js;
    }


}
