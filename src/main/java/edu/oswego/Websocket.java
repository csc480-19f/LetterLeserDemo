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
    HashMap<String, ArrayList<String>> userFavList = new HashMap<>();
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
                session.getBasicRemote().sendText(js.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("filter")){
            //JsonObject filter = jmessage.get("filter").getAsJsonObject();
            try {
                session.getBasicRemote().sendText(generateData().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("addfavorite")){

            JsonObject fav = jmessage.get("favorite").getAsJsonObject();
            String name = fav.get("favoritename").getAsString();
            ArrayList<String> favlist = userFavList.get(email);
            favlist.add(name);
            /*JsonObject filter = fav.get("filter").getAsJsonObject();
            favlist.put(name,filter.toString());*/
            userFavList.put(email,favlist);

            JsonObject js = new JsonObject();
            JsonArray ja = new JsonArray();
            for(int i=0;i<favlist.size();i++){ja.add(favlist.get(i));}
            js.addProperty("datatype","favoritenames");
            js.add("favoritenames",ja);

            try {
                session.getBasicRemote().sendText(js.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("callfavorite")){
            //String favoriteName = jmessage.get("favoriteName").toString();
            //JsonObject js = new JsonParser().parse(userFavList.get("email").get(favoriteName)).getAsJsonObject();
            try {
                session.getBasicRemote().sendText(generateData().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(messageType.equals("removefavorite")){
            String favorite = jmessage.get("favoritename").getAsString();
            ArrayList<String> favlist = userFavList.get(email);
            favlist.remove(favorite);

            JsonObject js = new JsonObject();
            JsonArray ja = new JsonArray();
            for(int i=0;i<favlist.size();i++){ja.add(favlist.get(i));}
            js.addProperty("datatype","favoritenames");
            js.add("favoritenames",ja);

            try {
                session.getBasicRemote().sendText(""+js.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageType.equals("login")){
            String pass = jmessage.get("pass").getAsString();
            Mailer mailer = new Mailer(email,pass);
            if(mailer.isConnected()){
                try {
                    JsonObject js = new JsonObject();
                    js.addProperty("messagetype","statusupdate");
                    js.addProperty("message","connected");
                    session.getBasicRemote().sendText(js.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JsonObject folders = new JsonObject();
                JsonArray ja = new JsonArray();
                ja.add("csc480");
                ja.add("work");
                ja.add("oswego");
                ja.add("spam");
                ja.add("important");
                folders.add("foldername", ja);
                try {
                    session.getBasicRemote().sendText(folders.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    JsonObject js = new JsonObject();
                    js.addProperty("messagetype","statusupdate");
                    js.addProperty("message","didnt connect");
                    session.getBasicRemote().sendText(js.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else{
            try {
                JsonObject js = new JsonObject();
                js.addProperty("messagetype","statusupdate");
                js.addProperty("message","invalid message type");
                session.getBasicRemote().sendText(js.toString());
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

        ArrayList<String> emails = listOfEmails();
        //email by domain
        HashMap<String,Integer> domains = new HashMap<>();





        //EmailsByFolder
        JsonArray byFolder = new JsonArray();
        ArrayList<String> fl = new ArrayList<>();
        fl.add("csc480");
        fl.add("work");
        fl.add("oswego");
        fl.add("spam");
        fl.add("important");
        for(int i=0;i<fl.size();i++){
            JsonObject temp = new JsonObject();
            temp.addProperty("folderobject",emails.get(i));
            temp.addProperty("folderparent",0.0);
            temp.addProperty("contribution", random.nextInt(200));
            byFolder.add(temp);
        }




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
        //js.add("emailbydomain",emailsByDomain);
        js.add("emailbyfolder",byFolder);

        return js;
    }

    private ArrayList<String> listOfEmails(){
        ArrayList<String> email = new ArrayList<>();
        email.add("jidugemmydd-0369@gmail.com");
        email.add("zisommame-6038@gmail.com");
        email.add("vonezatu-4361@gmail.com");
        email.add("jynnufurri-3590@gmail.com");
        email.add("issemawi-2110@yahoo.com");
        email.add("ygagazob-8832@yahoo.com");
        email.add("tocaffasab-8424@oswego.edu");
        email.add("lebattomaffe-0720@oswego.edu");
        email.add("ittodito-5049@oswego.edu");
        email.add("erupumiwe-6911@oswego.edu");
        email.add("attikoke-7524@oswego.edu");
        //dup here
        email.add("jidugemmydd-0369@gmail.com");
        email.add("zisommame-6038@gmail.com");
        email.add("vonezatu-4361@gmail.com");
        email.add("jynnufurri-3590@gmail.com");
        email.add("issemawi-2110@yahoo.com");
        email.add("ygagazob-8832@yahoo.com");
        email.add("tocaffasab-8424@oswego.edu");
        email.add("lebattomaffe-0720@oswego.edu");
        email.add("ittodito-5049@oswego.edu");
        email.add("erupumiwe-6911@oswego.edu");
        email.add("attikoke-7524@oswego.edu");

        return email;
    }




}
