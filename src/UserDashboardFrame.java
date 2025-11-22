import javax.swing.*;
import java.awt.*;

public class UserDashboardFrame extends JFrame {
   private User currentUser;

   public UserDashboardFrame(User user) {
      this.currentUser = user;
      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal Management - User Dashboard");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(800, 600);
      setLocationRelativeTo(null);

      // Main panel
      JPanel mainPanel = new JPanel(new BorderLayout());

      // Header
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(new Color(46, 139, 87));
      headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
      welcomeLabel.setForeground(Color.WHITE);
      welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

      JButton logoutButton = new JButton("Logout");
      logoutButton.setBackground(Color.RED);
      logoutButton.setForeground(Color.WHITE);
      logoutButton.setOpaque(true);
      logoutButton.setBorderPainted(false);

      logoutButton.addActionListener(e -> {
         new LoginFrame().setVisible(true);
         dispose();
      });

      headerPanel.add(welcomeLabel, BorderLayout.WEST);
      headerPanel.add(logoutButton, BorderLayout.EAST);

      // Content
      JPanel contentPanel = new JPanel(new GridBagLayout());
      contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

      JLabel dashboardLabel = new JLabel("User View");
      dashboardLabel.setFont(new Font("Arial", Font.BOLD, 24));
      dashboardLabel.setForeground(new Color(46, 139, 87));

      JLabel featuresLabel = new JLabel("<html><center>"
            + "Future Features:<br>"
            + "- Book Fields<br>"
            + "- View Booking History<br>"
            + "- Make Payments<br>"
            + "- View Available Slots<br>"
            + "</center></html>");
      featuresLabel.setFont(new Font("Arial", Font.PLAIN, 16));
      featuresLabel.setHorizontalAlignment(SwingConstants.CENTER);

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.anchor = GridBagConstraints.CENTER;
      gbc.insets = new Insets(10, 10, 30, 10);

      contentPanel.add(dashboardLabel, gbc);
      contentPanel.add(featuresLabel, gbc);

      mainPanel.add(headerPanel, BorderLayout.NORTH);
      mainPanel.add(contentPanel, BorderLayout.CENTER);

      add(mainPanel);
   }
}