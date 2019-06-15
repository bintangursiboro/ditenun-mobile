package com.ditenun.appditenun.dependency.models;

import java.util.List;

/**
 * Created by TROJAN-016 on 2/8/2019.
 */

public class ResponseGetFeedback {
    private boolean error;
    private String message;
    private List<Feedback> data;
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

    public void setData(List<Feedback> data) {
        this.data = data;
    }

    public List<Feedback> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
