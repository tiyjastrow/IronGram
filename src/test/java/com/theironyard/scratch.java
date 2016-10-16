package com.theironyard;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by joe on 14/10/2016.
 */
public class scratch {

    public static void main(String[] args)throws Exception {

        String old =String.valueOf(LocalDateTime.now());
        System.out.println(old);

        String newTime = String.valueOf(LocalDateTime.now());

        //REGEX STUFF
        Pattern p = Pattern.compile("\\d{2}?\\d{2}?\\d{2}");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Thread.sleep(10000);

        if(newTime!=old){
            System.out.println("new time");
        }

    }

}
