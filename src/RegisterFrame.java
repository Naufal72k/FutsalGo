import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
   private JTextField usernameField, emailField;
   private JPasswordField passwordField, confirmPasswordField;
   private JButton registerButton, backButton;
   private UserService userService;
   private LoginFrame loginFrame;

   public RegisterFrame(LoginFrame loginFrame) {
      this.loginFrame = loginFrame;
      userService = new UserService();
      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal Management - Register");
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setSize(450, 350);
      setLocationRelativeTo(null);
      setResizable(false);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      JLabel titleLabel = new JLabel("Register New Account", JLabel.CENTER);
      titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
      titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

      JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

      JLabel usernameLabel = new JLabel("Username:");
      usernameField = new JTextField();

      JLabel emailLabel = new JLabel("Email:");
      emailField = new JTextField();

      JLabel passwordLabel = new JLabel("Password:");
      passwordField = new JPasswordField();

      JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
      confirmPasswordField = new JPasswordField();

      formPanel.add(usernameLabel);
      formPanel.add(usernameField);
      formPanel.add(emailLabel);
      formPanel.add(emailField);
      formPanel.add(passwordLabel);
      formPanel.add(passwordField);
      formPanel.add(confirmPasswordLabel);
      formPanel.add(confirmPasswordField);

      JPanel buttonPanel = new JPanel(new FlowLayout());
      registerButton = new JButton("Register");
      backButton = new JButton("Back to Login");

      registerButton.setBackground(new Color(46, 139, 87));
      registerButton.setForeground(Color.WHITE);
      registerButton.setOpaque(true);
      registerButton.setBorderPainted(false);
      backButton.setBackground(new Color(220, 20, 60));
      backButton.setForeground(Color.WHITE);
      backButton.setOpaque(true);
      backButton.setBorderPainted(false);

      buttonPanel.add(registerButton);
      buttonPanel.add(backButton);

      mainPanel.add(titleLabel, BorderLayout.NORTH);
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);

      add(mainPanel);

      setupEventListeners();
   }

   private void setupEventListeners() {
      registerButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            registerUser();
         }
      });

      backButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            dispose();
            loginFrame.setVisible(true);
         }
      });
   }

   private void registerUser() {
      String username = usernameField.getText().trim();
      String email = emailField.getText().trim();
      String password = new String(passwordField.getPassword());
      String confirmPassword = new String(confirmPasswordField.getPassword());
      String userLevel = "user";

      // Validation
      if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
         JOptionPane.showMessageDialog(this,
               "Please fill in all fields",
               "Error",
               JOptionPane.ERROR_MESSAGE);
         return;
      }

      if (!password.equals(confirmPassword)) {
         JOptionPane.showMessageDialog(this,
               "Passwords do not match",
               "Error",
               JOptionPane.ERROR_MESSAGE);
         return;
      }

      if (password.length() < 6) {
         JOptionPane.showMessageDialog(this,
               "Password must be at least 6 characters long",
               "Error",
               JOptionPane.ERROR_MESSAGE);
         return;
      }

      // Pakai thread utk register agar terhindar dri UI freezing
      new Thread(new Runnable() {
         @Override
         public void run() {
            // Check username/email 
            if (userService.isUsernameExists(username)) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     JOptionPane.showMessageDialog(RegisterFrame.this,
                           "Username already exists",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                  }
               });
               return;
            }

            if (userService.isEmailExists(email)) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     JOptionPane.showMessageDialog(RegisterFrame.this,
                           "Email already exists",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                  }
               });
               return;
            }

            // Register user
            boolean success = userService.registerUser(username, password, email, userLevel);

            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  if (success) {
                     JOptionPane.showMessageDialog(RegisterFrame.this,
                           "Registration successful! Please login.",
                           "Success",
                           JOptionPane.INFORMATION_MESSAGE);

                     dispose();
                     loginFrame.setVisible(true);
                  } else {
                     JOptionPane.showMessageDialog(RegisterFrame.this,
                           "Registration failed. Please try again.",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                  }
               }
            });
         }
      }).start();
   }
}