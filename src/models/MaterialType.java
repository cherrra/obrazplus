package models;

public class MaterialType {
    private int id;
    private String name;
    private double lossPercentage;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLossPercentage() { return lossPercentage; }
    public void setLossPercentage(double lossPercentage) { this.lossPercentage = lossPercentage; }
}