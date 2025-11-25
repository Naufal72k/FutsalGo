import javax.swing.*;
import java.awt.*;

public class FutsalManagementApp extends JFrame {
   private CardLayout cardLayout;
   private JPanel mainPanel;
   private Tema ini = new Tema();
    
   private LoginFrame loginPanel;
   private RegisterFrame registerPanel;

   public static void main(String[] args) {
      SwingUtilities.invokeLater(FutsalManagementApp::new);
   }

   public FutsalManagementApp() {
      setTitle("Futsal-GO");
      setSize(ini.lebar, ini.tinggi);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);
      getContentPane().setBackground(Color.BLACK);
      
      cardLayout = new CardLayout();
      mainPanel = new JPanel(cardLayout);
      add(mainPanel);

      // Buat panel login dan main menu
      loginPanel = new LoginFrame(this);
      registerPanel = new RegisterFrame(this);

      mainPanel.add(loginPanel, "login");
      mainPanel.add(registerPanel, "register");

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

}