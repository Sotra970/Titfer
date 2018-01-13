package com.titfer.Models;

public class Room_model
{

    // This is the unique id given by the server
    public String room_id;
        public String consumer_key , designer_key;

    public Room_model(String room_id, String consumer_key , String designer_key) {
        this.room_id = room_id;
        this.consumer_key = consumer_key;
        this.designer_key = designer_key;
    }

    public Room_model() {

    }
}