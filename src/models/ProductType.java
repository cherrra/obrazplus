package models;

public class ProductType {
    private int id;
    private String name;
    private double coefficient;


    public ProductType(int id, String name, double coefficient) {
        this.id = id;
        this.name = name;
        this.coefficient = coefficient;
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

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return name;
    }
};