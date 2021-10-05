package com.eze.helper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.eze.dtos.AccountWithTokens;
import com.eze.dtos.RequestDto;
import com.eze.model.Account;
import com.eze.model.Item;
import com.eze.model.Request;
import com.eze.retrofit.UserClient;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String convertOffSetDateTimeToString(OffsetDateTime offsetDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, LLL dd, yyyy - KK:mm:ss a");

        return offsetDateTime.toLocalDateTime().format(formatter);
    }

    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static Account asAccount(AccountWithTokens accountWithTokens){
        return new Account(accountWithTokens.getId(),
                accountWithTokens.getName(),
                accountWithTokens.getUsername(),
                accountWithTokens.getPassword(),
                accountWithTokens.getRole(),
                accountWithTokens.getAccessToken(),
                accountWithTokens.getRefreshToken());
    }
}
