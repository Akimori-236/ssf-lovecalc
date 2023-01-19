package sg.edu.nus.iss.app.ssflovecalc.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class LoveResult {
    private String id;

    private String fname;
    private String sname;
    private Integer percentage;
    private String resultMsg;
    private Boolean compatible;

    public LoveResult() {
        this.id = generateId(8);
    }

    private synchronized String generateId(int numChars) {
        Random r = new Random();
        StringBuilder strBuilder = new StringBuilder();
        while (strBuilder.length() < numChars) {
            strBuilder.append(Integer.toHexString(r.nextInt()));
        }
        return strBuilder.toString().substring(0, numChars);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Boolean isCompatible() {
        return compatible;
    }

    public void setCompatible(Boolean compatible) {
        this.compatible = compatible;
    }

    public static LoveResult create(String json) throws IOException {
        LoveResult lr = new LoveResult();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader jReader = Json.createReader(is);
            JsonObject jObj = jReader.readObject();
            lr.setFname(jObj.getString("fname"));
            lr.setSname(jObj.getString("sname"));
            lr.setPercentage(Integer.parseInt(jObj.getString("percentage")));
            lr.setResultMsg(jObj.getString("result"));
            if (lr.getPercentage() >= 75) {
                lr.setCompatible(true);
            } else {
                lr.setCompatible(false);
            }
        }
        return lr;
    }

    @Override
    public String toString() {
        return "Object - " + this.id;
    }
}
