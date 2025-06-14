package models;

public class Product {
    private int id;
    private int productTypeId;
    private String name;
    private String articleNumber;
    private double minPartnerPrice;
    private String productTypeName;
    private double productTypeCoefficient;

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public double getMinPartnerPrice() {
        return minPartnerPrice;
    }

    public void setMinPartnerPrice(double minPartnerPrice) {
        this.minPartnerPrice = minPartnerPrice;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public double getProductTypeCoefficient() {
        return productTypeCoefficient;
    }

    public void setProductTypeCoefficient(double productTypeCoefficient) {
        this.productTypeCoefficient = productTypeCoefficient;
    }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}