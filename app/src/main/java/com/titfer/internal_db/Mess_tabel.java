package com.titfer.internal_db;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;


@Table(name = "mess")
public class Mess_tabel extends  Model implements Serializable
{





    // This is the unique id given by the server
    @Column(name = "mess_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String mess_id;

    @Column(name = "user_id")
    public String user_id ;

    @Column(name = "user_name")
    public String user_name ;

    @Column(name = "message")
    public String message ;


    @Column(name = "timeStamp")
    public String timeStamp ;

    @Column(name = "dir")
    public int dir = 2 ;

    @Column(name = "room_id")
    public String room_id ;


    String action = "down" ;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    // Make sure to have a default constructor for every ActiveAndroid model
    public Mess_tabel(){
        super();
    }


    // Used to return items from another table based on the foreign key
    public List<Mess_tabel> data() {
          return new Select()
                .from(Mess_tabel.class)
                .execute();
    }

    public Mess_tabel last_mess(String room_id) {
       List<Mess_tabel> data =   new Select()
                .from(Mess_tabel.class)
                .where("room_id = ?", room_id)
                .execute();
        if (data.size() > 0 )
        return  data.get((data.size()-1)) ;

        return  new Mess_tabel() ;
    }
    public List<Mess_tabel> list_of_room_data(String room_id) {
        List<Mess_tabel> data =   new Select()
                .from(Mess_tabel.class)
                .where("room_id = ?", room_id)
                .execute() ;

        return  data ;
    }
//    public int unseenCount(String room_id) {
//        List<Mess_tabel> data =  new Select()
//                .from(Mess_tabel.class)
//                .where("user_id =? false")
//                .execute();
//        return  data.size();
//    }

    public boolean check(String ID) {
       List list =   new Select()
                .from(Mess_tabel.class)
                .where("mess_id = ?", ID)
                .execute();
            if (list.size() >0){
                return true;
            }else return false ;

    }

//    public  void  deletefav(String ID ){
//        new Delete().from(Mess_tabel.class).where("mess_id = ?", ID).execute();
//    }

}