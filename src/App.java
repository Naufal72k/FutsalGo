import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Tema ini = new Tema();
        
    // private User User;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardAdmin dashboardAdmin;
    private DashboardUser dashboardUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }

    public App() {
        setTitle("Futsal-GO");
        setSize(ini.lebar, ini.tinggi);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Buat panel masukan ke mainPanel card layout
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        dashboardAdmin = new DashboardAdmin(this);
        // dashboardUser = new DashboardUser(this, User);

        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        mainPanel.add(dashboardAdmin, "admin");
        // mainPanel.add(dashboardUser, "user");

        cardLayout.show(mainPanel, "login");

        setVisible(true);
    }

    // Pindah ke login
    public void showLogin() {                
        cardLayout.show(mainPanel, "login");

        mainPanel.revalidate();
        mainPanel.repaint();    
    }

    // Pindah ke register
    public void showRegister() {                
        cardLayout.show(mainPanel, "register");

        mainPanel.revalidate();
        mainPanel.repaint();    
    }

    public void showAdmin() {                
        cardLayout.show(mainPanel, "admin");

        mainPanel.revalidate();
        mainPanel.repaint();    
    }

    public void showUser(User currentUser) {            
        dashboardUser = new DashboardUser(this, currentUser);
        mainPanel.add(dashboardUser, "user");

        cardLayout.show(mainPanel, "user");

        mainPanel.revalidate();
        mainPanel.repaint();    
    }

}