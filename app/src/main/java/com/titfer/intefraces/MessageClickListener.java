package com.titfer.intefraces;


import com.titfer.internal_db.Mess_tabel;

/**
 * Created by sotra on 3/21/2017.
 */
public interface MessageClickListener {
    void send_message(Mess_tabel mess_tabel);
    void StartImageViewer(String image) ;
}
