package ui;

import models.Material;
import services.MaterialService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import ui.forms.MaterialFormScreen;
import ui.forms.ProductAnalysisScreen; // Добавьте этот импорт
import services.ProductService; // Добавьте этот импорт

public class MainFrame extends JFrame {
    private final MaterialService materialService;
    private ProductService productService;
    private JPanel cardsPanel;

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


    public MainFrame(MaterialService materialService,
                     ProductService productService) {
        this.materialService = materialService;
        this.productService = productService;

        initializeFonts();
        initializeUI();
        loadMaterials();
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
        setTitle("Образ плюс");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PRIMARY_BG);

        //меню
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(SECONDARY_BG);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        //логотип
        try {
            URL imageUrl = getClass().getResource("/images/obraz_logo.png");

            if (imageUrl == null) {
                System.err.println("Ошибка: Изображение '/images/obraz_logo.png' не найдено на classpath.");
                throw new RuntimeException("Ресурс изображения не найден.");
            }

            ImageIcon logoIcon = new ImageIcon(imageUrl);

            if (logoIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                System.err.println("Ошибка при полной загрузке пикселей изображения: " + imageUrl);
                throw new RuntimeException("Изображение загружено не полностью.");
            }

            Image image = logoIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(image));
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            menuBar.add(logoLabel);
        } catch (Exception e) {
            JLabel logoLabel = new JLabel("Лого (ошибка загрузки)");
            logoLabel.setFont(constantiaBold);
            logoLabel.setForeground(ACCENT_COLOR);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            menuBar.add(logoLabel);
            System.err.println("Произошла ошибка при отображении логотипа: " + e.getMessage());
            e.printStackTrace();
        }

        menuBar.add(Box.createHorizontalGlue());

        //меню
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setForeground(TEXT_COLOR);
        fileMenu.setFont(constantiaPlain);

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setForeground(TEXT_COLOR);
        exitItem.setFont(constantiaPlain);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        //панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(SECONDARY_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton addButton = createStyledButton("Добавить материал");
        JButton analysisButton = createStyledButton("Анализ Производства");

        addButton.addActionListener(this::handleAddMaterial);
        analysisButton.addActionListener(e -> showProductionAnalysisScreen());

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(analysisButton);

        //основная панель с карточками
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(PRIMARY_BG);

        JScrollPane scrollPane = new JScrollPane(cardsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PRIMARY_BG);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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

    private void loadMaterials() {
        try {
            cardsPanel.removeAll();

            List<Material> materials = materialService.getAllMaterials();

            if (materials.isEmpty()) {
                JLabel emptyLabel = new JLabel("Нет материалов для отображения");
                emptyLabel.setForeground(TEXT_COLOR);
                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                emptyLabel.setFont(constantiaPlain);
                cardsPanel.add(emptyLabel);
            } else {
                for (Material material : materials) {
                    cardsPanel.add(createMaterialCard(material));
                    cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }

            cardsPanel.revalidate();
            cardsPanel.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки материалов: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    //создание карточек
    private JPanel createMaterialCard(Material material) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setBackground(PRIMARY_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PRIMARY_BG);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(PRIMARY_BG);

        JLabel typeAndNameLabel = new JLabel(material.getMaterialTypeName() + " | " + material.getName());
        typeAndNameLabel.setFont(new Font(constantiaBoldLarge.getFontName(), Font.BOLD, 16));
        typeAndNameLabel.setForeground(ACCENT_COLOR);
        headerPanel.add(typeAndNameLabel);
        leftPanel.add(headerPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 0, 3));
        detailsPanel.setBackground(PRIMARY_BG);

        detailsPanel.add(createDetailLabel("Мин. запас: " + String.format("%.2f", material.getMinQuantity()) +
                " " + material.getUnitOfMeasure(), 13));

        detailsPanel.add(createDetailLabel("Количество: " + String.format("%.2f", material.getCurrentQuantity()) +
                " " + material.getUnitOfMeasure(), 13));

        String priceAndPackageQtyText = "Цена: " + String.format("%.2f", material.getCostPerUnit()) + " руб. | " +
                "В упаковке: " + String.format("%.2f", material.getPackageQuantity()) +
                " " + material.getUnitOfMeasure();
        detailsPanel.add(createDetailLabel(priceAndPackageQtyText, 13));

        leftPanel.add(detailsPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(PRIMARY_BG);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JLabel requiredQuantityLabel = createDetailLabel("Требуется: " + String.format("%.2f", material.getRequiredQuantity()) + " " + material.getUnitOfMeasure(), 15);
        requiredQuantityLabel.setFont(constantiaBold);
        requiredQuantityLabel.setForeground(ACCENT_COLOR);
        requiredQuantityLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(requiredQuantityLabel);
        rightPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(PRIMARY_BG);

        JButton editButton = createSmallButton("Изменить", 13);
        editButton.addActionListener(e -> handleEditMaterial(material));

        JButton deleteButton = createSmallButton("Удалить", 13);
        deleteButton.addActionListener(e -> handleDeleteMaterial(material));

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightPanel.add(buttonPanel);

        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JLabel createDetailLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(constantiaPlain.getFontName(), Font.PLAIN, fontSize));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JButton createSmallButton(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setBackground(SECONDARY_BG);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font(constantiaPlain.getFontName(), Font.PLAIN, fontSize));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    private void handleAddMaterial(ActionEvent e) {
        MaterialFormScreen formScreen = new MaterialFormScreen(
                materialService,
                null,
                new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        loadMaterials();
                    }
                }
        );
        formScreen.setVisible(true);
    }

    private void showProductionAnalysisScreen() {
        if (productService == null) {
            JOptionPane.showMessageDialog(this, "Ошибка: ProductService не инициализирован.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ProductAnalysisScreen analysisScreen = new ProductAnalysisScreen(
                this,
                materialService,
                productService,
                new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        loadMaterials();
                    }
                }
        );
        analysisScreen.setVisible(true);
    }

    private void handleEditMaterial(Material material) {
        try {
            Material loadedMaterial = materialService.getMaterialById(material.getId());
            if (loadedMaterial != null) {
                MaterialFormScreen formScreen = new MaterialFormScreen(
                        materialService,
                        loadedMaterial,
                        new Consumer<Void>() {
                            @Override
                            public void accept(Void aVoid) {
                                loadMaterials();
                            }
                        }
                );
                formScreen.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при загрузке материала для редактирования: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMaterial(Material material) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите удалить материал: " + material.getName() + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (materialService.deleteMaterial(material.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "Материал успешно удален",
                            "Успех",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadMaterials();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Не удалось удалить материал",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при удалении: " + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
