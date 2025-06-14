package services;

import models.Material;
import models.MaterialType;
import models.Product; // Добавьте этот импорт
import models.ProductMaterialUsage; // Добавьте этот импорт
import models.ProductType; // Добавьте этот импорт

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final DatabaseService dbService;

    public ProductService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public ProductType getProductTypeById(int productTypeId) throws SQLException {
        String sql = "SELECT product_type_id, type_name, type_coefficient FROM ProductTypes WHERE product_type_id = ?";
        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productTypeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductType(
                            rs.getInt("product_type_id"),
                            rs.getString("type_name"),
                            rs.getDouble("type_coefficient")
                    );
                }
            }
        }
        return null;
    }

    public MaterialType getMaterialTypeById(int materialTypeId) throws SQLException {
        String sql = "SELECT material_type_id, type_name, loss_percentage FROM MaterialTypes WHERE material_type_id = ?";
        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, materialTypeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MaterialType type = new MaterialType();
                    type.setId(rs.getInt("material_type_id"));
                    type.setName(rs.getString("type_name"));
                    type.setLossPercentage(rs.getDouble("loss_percentage"));
                    return type;
                }
            }
        }
        return null;
    }


    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_type_id, p.name, p.article_number, p.min_partner_price, " +
                "pt.type_name, pt.type_coefficient " +
                "FROM Products p JOIN ProductTypes pt ON p.product_type_id = pt.product_type_id";

        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setProductTypeId(rs.getInt("product_type_id"));
                product.setName(rs.getString("name"));
                product.setArticleNumber(rs.getString("article_number"));
                product.setMinPartnerPrice(rs.getDouble("min_partner_price"));
                product.setProductTypeName(rs.getString("type_name"));
                product.setProductTypeCoefficient(rs.getDouble("type_coefficient"));
                products.add(product);
            }
        }
        return products;
    }

    public List<ProductType> getAllProductTypes() throws SQLException {
        List<ProductType> types = new ArrayList<>();
        String sql = "SELECT product_type_id, type_name, type_coefficient FROM ProductTypes";
        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                types.add(new ProductType(
                        rs.getInt("product_type_id"),
                        rs.getString("type_name"),
                        rs.getDouble("type_coefficient")
                ));
            }
        }
        return types;
    }


    //список продукции, в производстве которой используется указанный материал
    public List<ProductMaterialUsage> getProductsUsingMaterial(int materialId) throws SQLException {
        List<ProductMaterialUsage> usages = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, pc.material_quantity " +
                "FROM Products p " +
                "JOIN ProductComposition pc ON p.product_id = pc.product_id " +
                "WHERE pc.material_id = ?";

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, materialId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usages.add(new ProductMaterialUsage(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("material_quantity")
                    ));
                }
            }
        }
        return usages;
    }

    //расчет целого кол-ва продукции из заданного кол-ва сырья
    public int calculateProductQuantity(
            int productTypeId,
            int materialTypeId,
            double rawMaterialQuantity,
            double productParam1,
            double productParam2) throws SQLException {

        //проверка на положительные параметры продукции
        if (productParam1 <= 0 || productParam2 <= 0 || rawMaterialQuantity <= 0) {
            return -1; //неподходящие данные
        }

        //коэффициент типа продукции
        ProductType productType = getProductTypeById(productTypeId);
        if (productType == null) {
            return -1; //несуществующий тип продукции
        }
        double typeCoefficient = productType.getCoefficient();

        //процент потери сырья указанного типа
        MaterialType materialType = getMaterialTypeById(materialTypeId);
        if (materialType == null) {
            return -1; //несуществующий тип материала
        }
        double lossPercentage = materialType.getLossPercentage();

        // 1. Количество необходимого сырья на одну единицу продукции
        double materialNeededPerProduct = productParam1 * productParam2 * typeCoefficient;
        if (materialNeededPerProduct <= 0) {
            return -1; //некорректные параметры для расчета потребности
        }

        // 2. Учет процента потери сырья
        double effectiveRawMaterialQuantity = rawMaterialQuantity * (1 - lossPercentage / 100.0);
        if (effectiveRawMaterialQuantity < 0) {
            return -1; //некорректный процент потерь
        }

        // 3. Расчет кол-ва получаемой продукции
        int quantityProduced = (int) Math.floor(effectiveRawMaterialQuantity / materialNeededPerProduct);

        return quantityProduced;
    }
}
