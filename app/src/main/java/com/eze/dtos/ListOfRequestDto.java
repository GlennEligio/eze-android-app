package com.eze.dtos;

import java.util.List;

public class ListOfRequestDto {
    private List<RequestDto> requestDtos;

    public ListOfRequestDto(List<RequestDto> requestDtos) {
        this.requestDtos = requestDtos;
    }

    public List<RequestDto> getRequestDtos() {
        return requestDtos;
    }

    public void setRequestDtos(List<RequestDto> requestDtos) {
        this.requestDtos = requestDtos;
    }
}
