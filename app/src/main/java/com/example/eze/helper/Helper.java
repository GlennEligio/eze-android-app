package com.example.eze.helper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.eze.dtos.RequestDto;
import com.example.eze.model.Item;
import com.example.eze.model.Request;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Request> asListOfRequest(List<RequestDto> requestDtos){
        List<Request> requests = new ArrayList<Request>();



        for (RequestDto requestDto : requestDtos) {
            Request request = new Request(requestDto.getId(),
                    combineItemIds(requestDto.items, false),
                    requestDto.getItems().size(),
                    requestDto.getCreatedDate(),
                    requestDto.getStudentName(),
                    requestDto.getProfessorName(),
                    requestDto.getCode(),
                    requestDto.getStatus());

            requests.add(request);
        }
        return requests;
    }

    public static String combineItemIds (List<Item> items, Boolean withSpace){
        String itemIds = "";
        for (Item item : items) {
            if(withSpace){
                if(itemIds.equals("")){
                    itemIds = itemIds.concat(item.getId());
                }else{
                    itemIds = itemIds.concat(", " + item.getId());
                }
            }else{
                if(itemIds.equals("")){
                    itemIds = itemIds.concat(item.getId());
                }else{
                    itemIds = itemIds.concat("," + item.getId());
                }
            }
        }
        return itemIds;
    }

    public static String combineItemNames (List<Item> items, Boolean withSpace){
        String itemNames = "";
        for (Item item : items) {
            if(withSpace){
                if(itemNames.equals("")){
                    itemNames = itemNames.concat(item.getName());
                }else{
                    itemNames = itemNames.concat(", " + item.getName());
                }
            }else{
                if(itemNames.equals("")){
                    itemNames = itemNames.concat(item.getName());
                }else{
                    itemNames = itemNames.concat("," + item.getName());
                }
            }
        }
        return itemNames;
    }
}
