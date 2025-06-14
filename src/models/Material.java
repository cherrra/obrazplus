package models;

public class Material {
    private int id;
    private String name;
    private int materialTypeId;
    private String materialTypeName;
    private double currentQuantity;
    private String unitOfMeasure;
    private double packageQuantity;
    private double minQuantity;
    private double costPerUnit;
    private double requiredQuantity;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaterialTypeId() { return materialTypeId; }
    public void setMaterialTypeId(int materialTypeId) { this.materialTypeId = materialTypeId; }

    public String getMaterialTypeName() { return materialTypeName; }
    public void setMaterialTypeName(String materialTypeName) { this.materialTypeName = materialTypeName; }

    public double getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(double currentQuantity) { this.currentQuantity = currentQuantity; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public double getPackageQuantity() { return packageQuantity; }
    public void setPackageQuantity(double packageQuantity) { this.packageQuantity = packageQuantity; }

    public double getMinQuantity() { return minQuantity; }
    public void setMinQuantity(double minQuantity) { this.minQuantity = minQuantity; }

    public double getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(double costPerUnit) { this.costPerUnit = costPerUnit; }

    public double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(double requiredQuantity) { this.requiredQuantity = requiredQuantity; }
}
