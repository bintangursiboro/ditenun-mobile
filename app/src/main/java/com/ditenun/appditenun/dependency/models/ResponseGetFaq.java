package com.ditenun.appditenun.dependency.models;

import java.util.List;

/**
 * Created by TROJAN-016 on 2/12/2019.
 */

public class ResponseGetFaq {
    private boolean error;
    private String message;
    private List<Faq> data;
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

    public void setData(List<Faq> data) {
        this.data = data;
    }

    public List<Faq> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
