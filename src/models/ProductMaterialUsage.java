package models;

public class ProductMaterialUsage {
    private int productId;
    private String productName;
    private double materialQuantityNeeded;

    public ProductMaterialUsage(int productId, String productName, double materialQuantityNeeded) {
        this.productId = productId;
        this.productName = productName;
        this.materialQuantityNeeded = materialQuantityNeeded;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getMaterialQuantityNeeded() {
        return materialQuantityNeeded;
    }

    @Override
    public String toString() {
        return productName + " (требуется: " + String.format("%.2f", materialQuantityNeeded) + " ед.)";
    }
}
