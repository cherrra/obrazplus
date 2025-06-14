package ui.forms;

import models.Material;
import models.Product;
import models.ProductMaterialUsage;
import models.ProductType;
import services.MaterialService;
import services.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class ProductAnalysisScreen extends JDialog {
    private final MaterialService materialService;
    private final ProductService productService;
    private final Consumer<Void> refreshCallback;

    //компоненты для списка продукции по материалу
    private JComboBox<Material> materialComboBox;
    private DefaultListModel<ProductMaterialUsage> productUsageListModel;
    private JList<ProductMaterialUsage> productUsageList;

    //компоненты для расчета количества продукции
    private JComboBox<ProductType> productTypeCalculationComboBox;
    private JTextField materialTypeCalculationIdField;
    private JTextField rawMaterialQuantityField;
    private JTextField productParam1Field;
    private JTextField productParam2Field;
    private JLabel calculationResultLabel;

    //цвета
    private static final Color PRIMARY_BG = new Color(255, 255, 255);
    private static final Color SECONDARY_BG = new Color(191, 214, 246);
    private static final Color ACCENT_COLOR = new Color(64, 92, 115);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);

    //шрифты
    private Font constantiaPlain;
    private Font constantiaBold;
    private Font constantiaBoldLarge;

    public ProductAnalysisScreen(JFrame parent, MaterialService materialService, ProductService productService, Consumer<Void> refreshCallback) {
        super(parent, "Анализ Производства", true);
        this.materialService = materialService;
        this.productService = productService;
        this.refreshCallback = refreshCallback;

        initializeFonts();
        initializeUI();
        loadMaterialsAndProductTypesForCalculation();
    }

    private void initializeFonts() {
        try {
            constantiaPlain = new Font("Constantia", Font.PLAIN, 12);
            constantiaBold = new Font("Constantia", Font.BOLD, 12);
            constantiaBoldLarge = new Font("Constantia", Font.BOLD, 16);

            if (!constantiaPlain.getFamily().equals("Constantia")) {
                constantiaPlain = new Font("Serif", Font.PLAIN, 12);
                constantiaBold = new Font("Serif", Font.BOLD, 12);
                constantiaBoldLarge = new Font("Serif", Font.BOLD, 16);
            }
        } catch (Exception e) {
            constantiaPlain = new Font("Serif", Font.PLAIN, 12);
            constantiaBold = new Font("Serif", Font.BOLD, 12);
            constantiaBoldLarge = new Font("Serif", Font.BOLD, 16);
        }
    }

    private void initializeUI() {
        setSize(1200, 800);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(PRIMARY_BG);

        //вкладки для двух разделов
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(constantiaBold);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBackground(SECONDARY_BG);

        //панель 1
        JPanel productsByMaterialPanel = createProductsByMaterialPanel();
        tabbedPane.addTab("Продукция по материалу", productsByMaterialPanel);

        //панель 2
        JPanel calculationPanel = createCalculationPanel();
        tabbedPane.addTab("Расчет продукции", calculationPanel);

        add(tabbedPane, BorderLayout.CENTER);

        //кнопка
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(SECONDARY_BG);
        JButton closeButton = createStyledButton("Закрыть");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createProductsByMaterialPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        //выбор материала
        JPanel materialSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        materialSelectionPanel.setBackground(PRIMARY_BG);
        materialSelectionPanel.add(createLabel("Выберите материал:", constantiaPlain));

        materialComboBox = new JComboBox<>();
        materialComboBox.setFont(constantiaPlain);
        materialComboBox.setPreferredSize(new Dimension(250, 30));
        materialComboBox.setBackground(Color.WHITE);
        materialComboBox.setForeground(TEXT_COLOR);
        materialComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Material) {
                    setText(((Material) value).getName());
                }
                return this;
            }
        });
        materialComboBox.addActionListener(e -> loadProductsForSelectedMaterial());
        materialSelectionPanel.add(materialComboBox);
        panel.add(materialSelectionPanel, BorderLayout.NORTH);

        //список продукции
        productUsageListModel = new DefaultListModel<>();
        productUsageList = new JList<>(productUsageListModel);
        productUsageList.setFont(constantiaPlain);
        productUsageList.setBackground(Color.WHITE);
        productUsageList.setForeground(TEXT_COLOR);
        productUsageList.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        JScrollPane scrollPane = new JScrollPane(productUsageList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        //выбор типа продукции
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Тип продукции:", constantiaPlain), gbc);
        gbc.gridx = 1;
        productTypeCalculationComboBox = new JComboBox<>();
        productTypeCalculationComboBox.setFont(constantiaPlain);
        productTypeCalculationComboBox.setBackground(Color.WHITE);
        productTypeCalculationComboBox.setForeground(TEXT_COLOR);
        productTypeCalculationComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProductType) {
                    setText(((ProductType) value).getName());
                }
                return this;
            }
        });
        panel.add(productTypeCalculationComboBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("ID типа материала:", constantiaPlain), gbc);
        gbc.gridx = 1;
        materialTypeCalculationIdField = createTextField();
        panel.add(materialTypeCalculationIdField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Количество сырья:", constantiaPlain), gbc);
        gbc.gridx = 1;
        rawMaterialQuantityField = createTextField();
        panel.add(rawMaterialQuantityField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Параметр 1 продукции:", constantiaPlain), gbc);
        gbc.gridx = 1;
        productParam1Field = createTextField();
        panel.add(productParam1Field, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Параметр 2 продукции:", constantiaPlain), gbc);
        gbc.gridx = 1;
        productParam2Field = createTextField();
        panel.add(productParam2Field, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton calculateButton = createStyledButton("Рассчитать количество продукции");
        calculateButton.addActionListener(e -> performCalculation());
        panel.add(calculateButton, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        calculationResultLabel = createLabel("Результат: ",constantiaBoldLarge);
        calculationResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(calculationResultLabel, gbc);

        return panel;
    }

    private void loadMaterialsAndProductTypesForCalculation() {
        try {
            List<Material> materials = materialService.getAllMaterials();
            materialComboBox.removeAllItems();
            for (Material material : materials) {
                materialComboBox.addItem(material);
            }
            if (!materials.isEmpty()) {
                materialComboBox.setSelectedIndex(0);
            }

            List<ProductType> allProductTypes = productService.getAllProductTypes();
            productTypeCalculationComboBox.removeAllItems();
            for (ProductType type : allProductTypes) {
                productTypeCalculationComboBox.addItem(type);
            }
            if (!allProductTypes.isEmpty()) {
                productTypeCalculationComboBox.setSelectedIndex(0);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки данных для ComboBox'ов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void loadProductsForSelectedMaterial() {
        Material selectedMaterial = (Material) materialComboBox.getSelectedItem();
        if (selectedMaterial != null) {
            try {
                List<ProductMaterialUsage> usages = productService.getProductsUsingMaterial(selectedMaterial.getId());
                productUsageListModel.clear();
                if (usages.isEmpty()) {
                    productUsageListModel.addElement(new ProductMaterialUsage(0, "Нет продукции, использующей этот материал", 0.0));
                } else {
                    for (ProductMaterialUsage usage : usages) {
                        productUsageListModel.addElement(usage);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки продукции для материала: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            productUsageListModel.clear();
            productUsageListModel.addElement(new ProductMaterialUsage(0, "Выберите материал", 0.0));
        }
    }

    private void performCalculation() {
        try {
            ProductType selectedProductType = (ProductType) productTypeCalculationComboBox.getSelectedItem();
            if (selectedProductType == null) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, выберите тип продукции.", "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productTypeId = selectedProductType.getId();

            int materialTypeId = Integer.parseInt(materialTypeCalculationIdField.getText().trim());
            double rawMaterialQuantity = Double.parseDouble(rawMaterialQuantityField.getText().trim());
            double productParam1 = Double.parseDouble(productParam1Field.getText().trim());
            double productParam2 = Double.parseDouble(productParam2Field.getText().trim());

            int result = productService.calculateProductQuantity(
                    productTypeId, materialTypeId, rawMaterialQuantity, productParam1, productParam2
            );

            if (result == -1) {
                calculationResultLabel.setText("Результат: Некорректные данные или параметры.");
                calculationResultLabel.setForeground(Color.RED);
            } else {
                calculationResultLabel.setText("Результат: " + result + " ед. продукции");
                calculationResultLabel.setForeground(ACCENT_COLOR);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите корректные числовые значения для количества и параметров.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            calculationResultLabel.setText("Результат: Ошибка ввода.");
            calculationResultLabel.setForeground(Color.RED);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка базы данных при расчете: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            calculationResultLabel.setText("Результат: Ошибка БД.");
            calculationResultLabel.setForeground(Color.RED);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Неизвестная ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            calculationResultLabel.setText("Результат: Неизвестная ошибка.");
            calculationResultLabel.setForeground(Color.RED);
        }
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(constantiaPlain);
        textField.setForeground(TEXT_COLOR);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(constantiaBold);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return button;
    }
}
