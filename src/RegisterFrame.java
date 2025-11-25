import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
   private JTextField usernameField, emailField;
   private JPasswordField passwordField, confirmPasswordField;
   private JButton registerButton, backButton;
   private UserService userService;
   private LoginFrame loginFrame;
   private Tema ini = new Tema();

   public RegisterFrame(LoginFrame loginFrame) {
      this.loginFrame = loginFrame;
      userService = new UserService();
      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal Management - Register");
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setSize(1280, 720);
      setLocationRelativeTo(null);
      setResizable(false);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBackground(Color.BLACK);
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

      JLabel headerText = new JLabel("RENTAL PS RIJAL");
      headerText.setFont(new Font("Arial", Font.BOLD, 70));
      headerText.setForeground(Color.WHITE);
      headerText.setBounds(450, 50, 1000, 100);

      headerLayeredPane.add(backgroundLabel, Integer.valueOf(0));
      headerLayeredPane.add(headerText, Integer.valueOf(1));
      mainPanel.add(headerLayeredPane, BorderLayout.NORTH);

      // PANEL REGISTER BOX
      JPanel registerBox = new JPanel();
      registerBox.setLayout(new BoxLayout(registerBox, BoxLayout.Y_AXIS));
      registerBox.setOpaque(false);
      registerBox.setBorder(BorderFactory.createEmptyBorder(80, 0, 50, 0));

      JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
      titleLabel.setFont(new Font(ini.font, Font.BOLD, 32));
      titleLabel.setForeground(Color.decode(ini.warna_isi));
      titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

      JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
      formPanel.setOpaque(false);
      formPanel.setMaximumSize(new Dimension(460, 150));

      JLabel usernameLabel = new JLabel("Username:");
      usernameLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      usernameLabel.setForeground(Color.white);

      usernameField = new JTextField();
      usernameField.setFont(new Font(ini.font, Font.BOLD, 14));
      usernameField.setForeground(Color.white);
      usernameField.setBackground(Color.decode(ini.warna_utama_lembut));
      usernameField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      usernameField.setCaretColor(Color.decode(ini.warna_isi));

      JLabel emailLabel = new JLabel("Email:");
      emailLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      emailLabel.setForeground(Color.white);

      emailField = new JTextField();
      emailField.setFont(new Font(ini.font, Font.BOLD, 14));
      emailField.setForeground(Color.white);
      emailField.setBackground(Color.decode(ini.warna_utama_lembut));
      emailField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      emailField.setCaretColor(Color.decode(ini.warna_isi));

      JLabel passwordLabel = new JLabel("Password:");
      passwordLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      passwordLabel.setForeground(Color.white);

      passwordField = new JPasswordField();
      passwordField.setFont(new Font(ini.font, Font.BOLD, 14));
      passwordField.setForeground(Color.white);
      passwordField.setBackground(Color.decode(ini.warna_utama_lembut));
      passwordField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      passwordField.setCaretColor(Color.decode(ini.warna_isi));

      JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
      confirmPasswordLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      confirmPasswordLabel.setForeground(Color.white);

      confirmPasswordField = new JPasswordField();
      confirmPasswordField.setFont(new Font(ini.font, Font.BOLD, 14));
      confirmPasswordField.setForeground(Color.white);
      confirmPasswordField.setBackground(Color.decode(ini.warna_utama_lembut));
      confirmPasswordField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      confirmPasswordField.setCaretColor(Color.decode(ini.warna_isi));

      formPanel.add(usernameLabel); formPanel.add(usernameField);
      formPanel.add(emailLabel); formPanel.add(emailField);
      formPanel.add(passwordLabel); formPanel.add(passwordField);
      formPanel.add(confirmPasswordLabel); formPanel.add(confirmPasswordField);

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
         dispose();
         loginFrame.setVisible(true);
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
               JOptionPane.showMessageDialog(RegisterFrame.this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE)
            );
            return;
         }

         if (userService.isEmailExists(email)) {
            SwingUtilities.invokeLater(() ->
               JOptionPane.showMessageDialog(RegisterFrame.this, "Email already exists", "Error", JOptionPane.ERROR_MESSAGE)
            );
            return;
         }

         boolean success = userService.registerUser(username, password, email, userLevel);

         SwingUtilities.invokeLater(() -> {
            if (success) {
               JOptionPane.showMessageDialog(RegisterFrame.this, "Registration Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
               dispose();
               loginFrame.setVisible(true);
            } else {
               JOptionPane.showMessageDialog(RegisterFrame.this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
         });
      }).start();
   }
}
