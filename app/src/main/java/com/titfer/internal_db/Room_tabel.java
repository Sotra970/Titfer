package com.titfer.internal_db;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;


@Table(name = "room")
public class Room_tabel extends  Model
{

    // This is the unique id given by the server
    @Column(name = "room_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String room_id;
    @Column(name = "name")
    public String name ;

    @Column(name = "img")
    public String img ;

    @Column(name = "count")
    public int count ;



    @Column(name = "type")
    public String type ;

    @Column(name = "consumer_id")
    public String consumer_id ;

    @Column(name = "designer_id")
    public String designer_id ;








    // Make sure to have a default constructor for every ActiveAndroid model
    public Room_tabel(){
        super();
    }


    // Used to return items from another table based on the foreign key
    public List<Room_tabel> data(String type) {
          return new Select()
                .from(Room_tabel.class)
                  .where("type = ?" , type)
                .execute();
    }
    public int unseenCount(String room_id) {
        List<Room_tabel> data =  new Select()
                .from(Room_tabel.class)
                .where("room_id = ?" , room_id)
                .execute();
        if (data.size() != 0  && data.get(0).count > 0 )
            return data.get(0).count;


        return  0;
    }
    public void clear(String room_id) {
        new Update(Room_tabel.class).set("count = ? " , 0).where("room_id = ?" , room_id).execute(); ;

    }
    public void update_count(String room_id) {
        Log.e("update_count" , "update " + room_id ) ;
        new Update(Room_tabel.class).set("count = ?" , (unseenCount(room_id)+1) ).where("room_id = ?" , room_id).execute(); ;

    }

    public boolean check(String room_id) {
       List list =   new Select()
                .from(Room_tabel.class)
                .where("room_id = ?", room_id)
                .execute();
            if (list.size() >0){
                return true;
            }else return false ;

    }

    public Room_tabel get(String room_id) {
        List<Room_tabel> list =   new Select()
                .from(Room_tabel.class)
                .where("room_id = ?", room_id)
                .execute();
        return list.get(0);
    }

//    public  void  deletefav(String ID ){
//        new Delete().from(Mess_tabel.class).where("_id = ?", ID).execute();
//    }

}