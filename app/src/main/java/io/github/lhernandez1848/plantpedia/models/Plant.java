package io.github.lhernandez1848.plantpedia.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Plant {

    private String name;
    private int typeId;
    private int sunlightId;
    private int dayTemp;
    private int nightTemp;
    private int startHumidity;
    private int endHumidity;
    private String dateWatered;
    private int waterFrequency;
    private String comment;
    private String image;

    public Plant(String name, int typeId, int sunlightId,
                 int dayTemp, int nightTemp, int startHumidity, int endHumidity,
                 String dateWatered, int waterFrequency, String comment, String image){
        this.name = name;
        this.typeId = typeId;
        this.sunlightId = sunlightId;
        this.dayTemp = dayTemp;
        this.nightTemp = nightTemp;
        this.startHumidity = startHumidity;
        this.endHumidity = endHumidity;
        this.dateWatered = dateWatered;
        this.waterFrequency = waterFrequency;
        this.comment = comment;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getSunlightId() {
        return sunlightId;
    }

    public void setSunlightId(int sunlightId) {
        this.sunlightId = sunlightId;
    }

    public int getDayTemp() {
        return dayTemp;
    }

    public void setDayTemp(int dayTemp) {
        this.dayTemp = dayTemp;
    }

    public int getNightTemp() {
        return nightTemp;
    }

    public void setNightTemp(int nightTemp) {
        this.nightTemp = nightTemp;
    }

    public int getStartHumidity() {
        return startHumidity;
    }

    public void setStartHumidity(int startHumidity) {
        this.startHumidity = startHumidity;
    }

    public int getEndHumidity() {
        return endHumidity;
    }

    public void setEndHumidity(int endHumidity) {
        this.endHumidity = endHumidity;
    }

    public String getDateWatered() {
        return dateWatered;
    }

    public void setDateWatered(String dateWatered) {
        this.dateWatered = dateWatered;
    }

    public int getWaterFrequency() {
        return waterFrequency;
    }

    public void setWaterFrequency(int waterFrequency) {
        this.waterFrequency = waterFrequency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public Map<String, Object> toPlantDetails() {
        HashMap<String, Object> details = new HashMap<>();
        details.put("name", name);
        details.put("typeId", typeId);
        details.put("sunlightId", sunlightId);
        details.put("dayTemp", dayTemp);
        details.put("nightTemp", nightTemp);
        details.put("startHumidity", startHumidity);
        details.put("endHumidity", endHumidity);
        details.put("dateWatered", dateWatered);
        details.put("waterFrequency", waterFrequency);
        details.put("comment", comment);
        details.put("image", image);

        return details;
    }
}
