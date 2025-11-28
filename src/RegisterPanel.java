import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RegisterPanel extends JPanel {
   private JTextField usernameField, emailField;
   private JPasswordField passwordField, confirmPasswordField;
   private JButton registerButton, backButton;
   private DatabaseConfig userService = new DatabaseConfig();
   private Tema ini = new Tema();
   private App app;

   public RegisterPanel(App app) {
      this.app = app;

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBackground(Color.decode(ini.warna_pelengkap));
      mainPanel.setPreferredSize(new Dimension(ini.lebar, ini.tinggi)); 

      add(mainPanel);

      // HEADER SAMA DENGAN LOGIN
      JLayeredPane headerLayeredPane = new JLayeredPane();
      headerLayeredPane.setPreferredSize(new Dimension(ini.tinggi, 200));

      JLabel backgroundLabel = new JLabel(new ImageIcon(
               new ImageIcon("assets/images/img-header.png")
                     .getImage()
                     .getScaledInstance(ini.lebar, 200, Image.SCALE_SMOOTH)
      ));
      backgroundLabel.setBounds(0, 0, ini.lebar, 200);

      // teks Header
      JLabel headerText = new JLabel("Futsal-GO");
      headerText.setFont(new Font("Arial", Font.BOLD, 70));
      headerText.setForeground(Color.WHITE);
      headerText.setBounds(500, 50, 1000, 100);

      headerLayeredPane.add(backgroundLabel, Integer.valueOf(0));
      headerLayeredPane.add(headerText, Integer.valueOf(1));
      mainPanel.add(headerLayeredPane, BorderLayout.NORTH);

      // PANEL REGISTER BOX
      JPanel registerBox = new JPanel();
      registerBox.setLayout(new BoxLayout(registerBox, BoxLayout.Y_AXIS));
      registerBox.setOpaque(false);
      registerBox.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

      JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
      titleLabel.setFont(new Font(ini.font, Font.BOLD, 32));
      titleLabel.setForeground(Color.decode(ini.warna_isi));
      titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

      // FORM PANEL
      JPanel formPanel = new JPanel();
      formPanel.setOpaque(false);
      formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
      formPanel.setMaximumSize(new Dimension(460, 300));

      // Username 
      JPanel usernameRow = new JPanel();
      usernameRow.setOpaque(false);
      usernameRow.setLayout(new BoxLayout(usernameRow, BoxLayout.X_AXIS));

      JLabel usernameLabel = new JLabel("Username:");
      usernameLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      usernameLabel.setForeground(Color.white);
      usernameLabel.setPreferredSize(new Dimension(150, 30)); // Width biar rata

      usernameField = new JTextField();
      usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      usernameField.setFont(new Font(ini.font, Font.BOLD, 14));
      usernameField.setForeground(Color.WHITE);
      usernameField.setBackground(Color.decode(ini.warna_utama_lembut));
      usernameField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      usernameField.setCaretColor(Color.decode(ini.warna_isi));

      usernameRow.add(usernameLabel);
      usernameRow.add(Box.createRigidArea(new Dimension(15, 0))); // X Gap
      usernameRow.add(usernameField);

      // Email 
      JPanel emailRow = new JPanel();
      emailRow.setOpaque(false);
      emailRow.setLayout(new BoxLayout(emailRow, BoxLayout.X_AXIS));

      JLabel emailLabel = new JLabel("Email:");
      emailLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      emailLabel.setForeground(Color.white);
      emailLabel.setPreferredSize(new Dimension(150, 30));

      emailField = new JTextField();
      emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      emailField.setFont(new Font(ini.font, Font.BOLD, 14));
      emailField.setForeground(Color.WHITE);
      emailField.setBackground(Color.decode(ini.warna_utama_lembut));
      emailField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      emailField.setCaretColor(Color.decode(ini.warna_isi));

      emailRow.add(emailLabel);
      emailRow.add(Box.createRigidArea(new Dimension(15, 0)));
      emailRow.add(emailField);

      // Password
      JPanel passwordRow = new JPanel();
      passwordRow.setOpaque(false);
      passwordRow.setLayout(new BoxLayout(passwordRow, BoxLayout.X_AXIS));

      JLabel passwordLabel = new JLabel("Password:");
      passwordLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      passwordLabel.setForeground(Color.white);
      passwordLabel.setPreferredSize(new Dimension(150, 30));

      passwordField = new JPasswordField();
      passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      passwordField.setFont(new Font(ini.font, Font.BOLD, 14));
      passwordField.setForeground(Color.WHITE);
      passwordField.setBackground(Color.decode(ini.warna_utama_lembut));
      passwordField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      passwordField.setCaretColor(Color.decode(ini.warna_isi));

      passwordRow.add(passwordLabel);
      passwordRow.add(Box.createRigidArea(new Dimension(15, 0)));
      passwordRow.add(passwordField);

      // Confirm Password 
      JPanel confirmPasswordRow = new JPanel();
      confirmPasswordRow.setOpaque(false);
      confirmPasswordRow.setLayout(new BoxLayout(confirmPasswordRow, BoxLayout.X_AXIS));

      JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
      confirmPasswordLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      confirmPasswordLabel.setForeground(Color.white);
      confirmPasswordLabel.setPreferredSize(new Dimension(150, 30));

      confirmPasswordField = new JPasswordField();
      confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
      confirmPasswordField.setFont(new Font(ini.font, Font.BOLD, 14));
      confirmPasswordField.setForeground(Color.WHITE);
      confirmPasswordField.setBackground(Color.decode(ini.warna_utama_lembut));
      confirmPasswordField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      confirmPasswordField.setCaretColor(Color.decode(ini.warna_isi));

      confirmPasswordRow.add(confirmPasswordLabel);
      confirmPasswordRow.add(Box.createRigidArea(new Dimension(15, 0)));
      confirmPasswordRow.add(confirmPasswordField);

      formPanel.add(usernameRow);
      formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
      formPanel.add(emailRow);
      formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
      formPanel.add(passwordRow);
      formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
      formPanel.add(confirmPasswordRow);

      // BUTTON 
      JPanel buttonPanel = new JPanel(new FlowLayout());
      buttonPanel.setOpaque(false);
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

      registerButton = new JButton("Register");
      registerButton.setFont(new Font(ini.font, Font.BOLD, 20));
      registerButton.setBackground(Color.decode(ini.warna_isi));
      registerButton.setForeground(Color.black);
      registerButton.setFocusPainted(false);
      registerButton.setOpaque(true);
      registerButton.setBorderPainted(false);

      backButton = new JButton("Back to Login");
      backButton.setFont(new Font(ini.font, Font.BOLD, 20));
      backButton.setBackground(Color.decode(ini.warna_isi));
      backButton.setForeground(Color.black);
      backButton.setFocusPainted(false);
      backButton.setOpaque(true);
      backButton.setBorderPainted(false);

      buttonPanel.add(registerButton);
      buttonPanel.add(backButton);

      registerBox.add(titleLabel);
      registerBox.add(formPanel);
      registerBox.add(buttonPanel);

      mainPanel.add(registerBox, BorderLayout.CENTER);

      setupEventListeners();
   }

   private void setupEventListeners() {
      registerButton.addActionListener(e -> registerUser());
      backButton.addActionListener(e -> {
         app.showLogin();
      });
   }

   private void registerUser() {
      String username = usernameField.getText().trim();
      String email = emailField.getText().trim();
      String password = new String(passwordField.getPassword());
      String confirmPassword = new String(confirmPasswordField.getPassword());
      String userLevel = "user";

      if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
         JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      if (!password.equals(confirmPassword)) {
         JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      if (password.length() < 6) {
         JOptionPane.showMessageDialog(this, "Password must be at least 6 characters", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      new Thread(() -> {
         if (userService.isUsernameExists(username)) {
            SwingUtilities.invokeLater(() ->
               JOptionPane.showMessageDialog(RegisterPanel.this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE)
            );
            return;
         }

         if (userService.isEmailExists(email)) {
            SwingUtilities.invokeLater(() ->
               JOptionPane.showMessageDialog(RegisterPanel.this, "Email already exists", "Error", JOptionPane.ERROR_MESSAGE)
            );
            return;
         }

         boolean success = userService.registerUser(username, password, email, userLevel);

         SwingUtilities.invokeLater(() -> {
            if (success) {
               JOptionPane.showMessageDialog(RegisterPanel.this, "Registration Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
               app.showLogin();
            } else {
               JOptionPane.showMessageDialog(RegisterPanel.this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
         });
      }).start();
   }
}
