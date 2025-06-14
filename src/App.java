import javax.swing.JFrame;

import services.DatabaseService;
import services.MaterialService;
import services.ProductService;
import ui.MainFrame;

public class App {
    private static DatabaseService dbService;
    private static MaterialService materialService;
    private static ProductService productService;

    public static void main(String[] args) {
        try {
            //инициализация сервисов
            dbService = DatabaseService.getInstance();
            materialService = new MaterialService(dbService);
            productService = new ProductService(dbService);

            //главное окно
            MainFrame mainFrame = new MainFrame(materialService, productService);
            mainFrame.setVisible(true);

            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    shutdown();
                }
            });

        } catch (Exception e) {
            System.err.println("Ошибка запуска приложения: " + e.getMessage());
            e.printStackTrace();
            shutdown();
        }
    }

    private static void shutdown() {
        try {
            if (dbService != null) {
                dbService.close();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при завершении работы DatabaseService: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
