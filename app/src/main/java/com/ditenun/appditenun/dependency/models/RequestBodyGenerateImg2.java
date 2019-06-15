package com.ditenun.appditenun.dependency.models;

import com.google.gson.annotations.SerializedName;

public class RequestBodyGenerateImg2 {
    public static String ALGO_IMG_QUILTING = "img_quilting";
    //public static String ALGO_IMG_WARPING = "img_warping";
    public static String ALGO_NON_PARAMETRIC_SAMPLING = "img_nps";

    //TODO : CEK THEN GO FOR IT (PUT ENUM IT)

    @SerializedName("sourceFile")
    private String sourceFile;
    @SerializedName("algoritma")
    private String algoritma;
    //private String algoritma2;

    @SerializedName("model")
    private String model;

    @SerializedName("warna")
    private String warna;

    @SerializedName("idMotif")
    private String idMotif;

    public RequestBodyGenerateImg2(String sourceFile) {
        this.sourceFile = sourceFile;
        this.algoritma = ALGO_IMG_QUILTING;
    }

    public RequestBodyGenerateImg2(String algoritma, String sourceFile, String model, String warna, String idMotif) {
        this.sourceFile = sourceFile;
        this.algoritma = algoritma;
        this.model= model;
        this.warna= warna;
        this.idMotif= idMotif;
    }

    public String getIdMotif() {
        return idMotif;
    }

    public void setIdMotif(String idMotif) {
        this.idMotif = idMotif;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getAlgoritma() {
        return algoritma;
    }

    public void setAlgoritma(String algoritma) {
        this.algoritma = algoritma;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model= model;
    }


    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna= warna;
    }


}
