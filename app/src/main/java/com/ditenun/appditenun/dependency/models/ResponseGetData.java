package com.ditenun.appditenun.dependency.models;

import java.util.List;

public class ResponseGetData {
    private boolean error;
    private String message;
    private List<Tenun> data;
    private Pagination pagination;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<Tenun> data) {
        this.data = data;
    }

    public List<Tenun> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
