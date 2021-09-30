package com.example.eze.room.typeconverter;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class OffSetDateTimeConverter {

    @TypeConverter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static OffsetDateTime toOffSetDateTime (String dateString){
        return dateString == null ? null : OffsetDateTime.parse(dateString);
    }

    @TypeConverter
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String dateString (OffsetDateTime offsetDateTime){
        return offsetDateTime == null ? null : offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
