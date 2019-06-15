package com.ditenun.appditenun.dependency.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Motif extends RealmObject {
    @PrimaryKey
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("bytes")
    private byte[] bytes;

    public Motif() {
    }

    public Motif(int id, String name, byte[] bytes) {
        this.id = id;
        this.name = name;
        this.bytes = bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
