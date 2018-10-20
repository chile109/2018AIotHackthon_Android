package app.kevin.com.speachrecognizationtest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {
    @SerializedName("alert")
    public int alert;
}
