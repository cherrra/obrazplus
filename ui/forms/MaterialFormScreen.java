package ui.forms;

import models.Material;
import models.MaterialType;
import services.MaterialService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class MaterialFormScreen extends JDialog {
    private final MaterialService materialService;
    private Material currentMaterial;
    private final Consumer<Void> refreshCallback;

    //компоненты формы
    private JTextField nameField;
    private JComboBox<MaterialType> materialTypeComboBox;
    private JTextField currentQuantityField;
    private JTextField unitOfMeasureField;
    private JTextField packageQuantityField;
    private JTextField minQuantityField;
    private JTextField costPerUnitField;

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

    public MaterialFormScreen(MaterialService materialService, Material material, Consumer<Void> refreshCallback) {
        this.materialService = materialService;
        this.currentMaterial = material;
        this.refreshCallback = refreshCallback;

        initializeFonts();
        initializeUI();
        loadMaterialTypes();
        populateForm();
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
        String title = currentMaterial == null ? "Добавление материала" : "Редактирование материала";
        setTitle(title);
        setSize(1200, 800);
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PRIMARY_BG);

        //панель заголовка
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(constantiaBoldLarge);
        titleLabel.setForeground(ACCENT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        //основная форма
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PRIMARY_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Название:", constantiaPlain), gbc);
        gbc.gridx = 1;
        nameField = createTextField();
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Тип материала:", constantiaPlain), gbc);
        gbc.gridx = 1;
        materialTypeComboBox = new JComboBox<>();
        materialTypeComboBox.setFont(constantiaPlain);
        materialTypeComboBox.setBackground(Color.WHITE);
        materialTypeComboBox.setForeground(TEXT_COLOR);
        formPanel.add(materialTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Текущее количество:", constantiaPlain), gbc);
        gbc.gridx = 1;
        currentQuantityField = createTextField();
        formPanel.add(currentQuantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(createLabel("Единица измерения:", constantiaPlain), gbc);
        gbc.gridx = 1;
        unitOfMeasureField = createTextField();
        formPanel.add(unitOfMeasureField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(createLabel("Количество в упаковке:", constantiaPlain), gbc);
        gbc.gridx = 1;
        packageQuantityField = createTextField();
        formPanel.add(packageQuantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(createLabel("Минимальный запас:", constantiaPlain), gbc);
        gbc.gridx = 1;
        minQuantityField = createTextField();
        formPanel.add(minQuantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(createLabel("Стоимость за единицу:", constantiaPlain), gbc);
        gbc.gridx = 1;
        costPerUnitField = createTextField();
        formPanel.add(costPerUnitField, gbc);

        add(formPanel, BorderLayout.CENTER);

        //панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(SECONDARY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> saveMaterial());
        JButton cancelButton = createStyledButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
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
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void loadMaterialTypes() {
        try {
            List<MaterialType> types = materialService.getAllMaterialTypes();
            DefaultComboBoxModel<MaterialType> model = new DefaultComboBoxModel<>();
            for (MaterialType type : types) {
                model.addElement(type);
            }
            materialTypeComboBox.setModel(model);

            materialTypeComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof MaterialType) {
                        setText(((MaterialType) value).getName());
                    }
                    return this;
                }
            });

            if (currentMaterial != null) {
                for (int i = 0; i < materialTypeComboBox.getItemCount(); i++) {
                    MaterialType type = materialTypeComboBox.getItemAt(i);
                    if (type.getId() == currentMaterial.getMaterialTypeId()) {
                        materialTypeComboBox.setSelectedItem(type);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки типов материалов: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        if (currentMaterial != null) {
            nameField.setText(currentMaterial.getName());
            currentQuantityField.setText(String.valueOf(currentMaterial.getCurrentQuantity()));
            unitOfMeasureField.setText(currentMaterial.getUnitOfMeasure());
            packageQuantityField.setText(String.valueOf(currentMaterial.getPackageQuantity()));
            minQuantityField.setText(String.valueOf(currentMaterial.getMinQuantity()));
            costPerUnitField.setText(String.valueOf(currentMaterial.getCostPerUnit()));
        } else {
            currentQuantityField.setText("0.0");
            packageQuantityField.setText("0.0");
            minQuantityField.setText("0.0");
            costPerUnitField.setText("0.0");
        }
    }

    private void saveMaterial() {
        try {
            String name = nameField.getText().trim();
            MaterialType selectedType = (MaterialType) materialTypeComboBox.getSelectedItem();
            double currentQuantity = Double.parseDouble(currentQuantityField.getText());
            String unitOfMeasure = unitOfMeasureField.getText().trim();
            double packageQuantity = Double.parseDouble(packageQuantityField.getText());
            double minQuantity = Double.parseDouble(minQuantityField.getText());
            double costPerUnit = Double.parseDouble(costPerUnitField.getText());

            if (name.isEmpty() || selectedType == null || unitOfMeasure.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Пожалуйста, заполните все обязательные поля (Название, Тип материала, Единица измерения).",
                        "Ошибка ввода",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (currentMaterial == null) {
                currentMaterial = new Material();
            }

            currentMaterial.setName(name);
            currentMaterial.setMaterialTypeId(selectedType.getId());
            currentMaterial.setCurrentQuantity(currentQuantity);
            currentMaterial.setUnitOfMeasure(unitOfMeasure);
            currentMaterial.setPackageQuantity(packageQuantity);
            currentMaterial.setMinQuantity(minQuantity);
            currentMaterial.setCostPerUnit(costPerUnit);

            if (materialService.saveMaterial(currentMaterial)) {
                JOptionPane.showMessageDialog(this,
                        "Материал успешно сохранен!",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                refreshCallback.accept(null);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Не удалось сохранить материал.",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Пожалуйста, введите корректные числовые значения для количества и стоимости.",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка базы данных при сохранении материала: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}