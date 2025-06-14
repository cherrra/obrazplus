package services;

import models.Material;
import models.MaterialType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialService {
    private final DatabaseService dbService;

    public MaterialService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    //все типы материалов из бд
    public List<MaterialType> getAllMaterialTypes() throws SQLException {
        List<MaterialType> types = new ArrayList<>();
        String sql = "SELECT material_type_id, type_name, loss_percentage FROM MaterialTypes";

        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MaterialType type = new MaterialType();
                type.setId(rs.getInt("material_type_id"));
                type.setName(rs.getString("type_name"));
                type.setLossPercentage(rs.getDouble("loss_percentage"));
                types.add(type);
            }
        }
        return types;
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

    //все материалы из бд
    public List<Material> getAllMaterials() throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT m.material_id, m.name, m.material_type_id, " +
                "m.current_quantity, m.unit_of_measure, m.package_quantity, " +
                "m.min_quantity, m.cost_per_unit, mt.type_name " +
                "FROM Materials m JOIN MaterialTypes mt ON m.material_type_id = mt.material_type_id";

        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Material material = mapResultSetToMaterial(rs);
                double requiredQty = getRequiredQuantityForMaterial(material.getId());
                material.setRequiredQuantity(requiredQty);
                materials.add(material);
            }
        }
        return materials;
    }

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setId(rs.getInt("material_id"));
        material.setName(rs.getString("name"));
        material.setMaterialTypeId(rs.getInt("material_type_id"));
        material.setMaterialTypeName(rs.getString("type_name"));
        material.setCurrentQuantity(rs.getDouble("current_quantity"));
        material.setUnitOfMeasure(rs.getString("unit_of_measure"));
        material.setPackageQuantity(rs.getDouble("package_quantity"));
        material.setMinQuantity(rs.getDouble("min_quantity"));
        material.setCostPerUnit(rs.getDouble("cost_per_unit"));
        return material;
    }

    //расчет требуемого кол-ва
    public double getRequiredQuantityForMaterial(int materialId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(pc.material_quantity), 0.0) AS total_required_quantity " +
                "FROM ProductComposition pc " +
                "WHERE pc.material_id = ?";
        double totalRequiredQuantity = 0.0;

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, materialId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalRequiredQuantity = rs.getDouble("total_required_quantity");
                }
            }
        }
        return totalRequiredQuantity;
    }

    public Material getMaterialById(int id) throws SQLException {
        String sql = "SELECT m.material_id, m.name, m.material_type_id, " +
                "m.current_quantity, m.unit_of_measure, m.package_quantity, " +
                "m.min_quantity, m.cost_per_unit, mt.type_name " +
                "FROM Materials m JOIN MaterialTypes mt ON m.material_type_id = mt.material_type_id " +
                "WHERE m.material_id = ?";

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Material material = mapResultSetToMaterial(rs);
                    double requiredQty = getRequiredQuantityForMaterial(material.getId());
                    material.setRequiredQuantity(requiredQty);
                    return material;
                }
            }
        }
        return null;
    }
    //сохранение с повторным подклюением для устранения ошибки
    public boolean saveMaterial(Material material) throws SQLException {
        Connection conn = null;
        try {
            conn = dbService.getConnection();
            conn.setAutoCommit(false);

            boolean result;
            if (material.getId() == 0) {
                result = insertMaterial(conn, material);
            } else {
                result = updateMaterial(conn, material);
            }

            conn.commit();
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private boolean insertMaterial(Connection conn, Material material) throws SQLException {
        String sql = "INSERT INTO Materials (name, material_type_id, " +
                "current_quantity, unit_of_measure, package_quantity, " +
                "min_quantity, cost_per_unit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setMaterialParameters(pstmt, material);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        material.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private boolean updateMaterial(Connection conn, Material material) throws SQLException {
        String sql = "UPDATE Materials SET name = ?, material_type_id = ?, " +
                "current_quantity = ?, unit_of_measure = ?, package_quantity = ?, " +
                "min_quantity = ?, cost_per_unit = ? WHERE material_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setMaterialParameters(pstmt, material);
            pstmt.setInt(8, material.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMaterial(int id) throws SQLException {
        String sql = "DELETE FROM Materials WHERE material_id = ?";

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private void setMaterialParameters(PreparedStatement pstmt, Material material) throws SQLException {
        pstmt.setString(1, material.getName());
        pstmt.setInt(2, material.getMaterialTypeId());
        pstmt.setDouble(3, material.getCurrentQuantity());
        pstmt.setString(4, material.getUnitOfMeasure());
        pstmt.setDouble(5, material.getPackageQuantity());
        pstmt.setDouble(6, material.getMinQuantity());
        pstmt.setDouble(7, material.getCostPerUnit());
    }
}