import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
   private JTextField usernameField;
   private JPasswordField passwordField;
   private JButton loginButton, registerButton;
   private UserService userService;

   public LoginFrame() {
      userService = new UserService();
      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal Management - Login");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(400, 300);
      setLocationRelativeTo(null);
      setResizable(false);

      // Main panel
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      // Title
      JLabel titleLabel = new JLabel("Login to Futsal Management", JLabel.CENTER);
      titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
      titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

      // Form panel
      JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

      JLabel usernameLabel = new JLabel("Username:");
      usernameField = new JTextField();

      JLabel passwordLabel = new JLabel("Password:");
      passwordField = new JPasswordField();

      formPanel.add(usernameLabel);
      formPanel.add(usernameField);
      formPanel.add(passwordLabel);
      formPanel.add(passwordField);

      // Button panel
      JPanel buttonPanel = new JPanel(new FlowLayout());
      loginButton = new JButton("Login");
      registerButton = new JButton("Register");

      loginButton.setBackground(new Color(70, 130, 180));
      loginButton.setForeground(Color.WHITE);
      loginButton.setOpaque(true);
      loginButton.setBorderPainted(false);
      registerButton.setBackground(new Color(46, 139, 87));
      registerButton.setForeground(Color.WHITE);
      registerButton.setOpaque(true);
      registerButton.setBorderPainted(false);

      buttonPanel.add(loginButton);
      buttonPanel.add(registerButton);

      mainPanel.add(titleLabel, BorderLayout.NORTH);
      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);

      add(mainPanel);

      setupEventListeners();
   }

   private void setupEventListeners() {
      loginButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            loginUser();
         }
      });

      registerButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            openRegisterFrame();
         }
      });

      // Enter key listener utk login (tombol enter)
      passwordField.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            loginUser();
         }
      });
   }

   private void loginUser() {
      String username = usernameField.getText().trim();
      String password = new String(passwordField.getPassword());

      if (username.isEmpty() || password.isEmpty()) {
         JOptionPane.showMessageDialog(this,
               "Please fill in all fields",
               "Error",
               JOptionPane.ERROR_MESSAGE);
         return;
      }

      // Pakai thread saat authentikasi agar tidak mengalami UI freezing
      new Thread(new Runnable() {
         @Override
         public void run() {
            User user = userService.authenticateUser(username, password);

            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  if (user != null) {
                     JOptionPane.showMessageDialog(LoginFrame.this,
                           "Login successful!",
                           "Success",
                           JOptionPane.INFORMATION_MESSAGE);

                     // Redirect berdasarkan user level
                     if ("admin".equals(user.getUserLevel())) {
                        new AdminDashboardFrame(user).setVisible(true);
                     } else {
                        new UserDashboardFrame(user).setVisible(true);
                     }

                     dispose(); // Close login window
                  } else {
                     JOptionPane.showMessageDialog(LoginFrame.this,
                           "Invalid username or password",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                  }
               }
            });
         }
      }).start();
   }

   private void openRegisterFrame() {
      new RegisterFrame(this).setVisible(true);
   }
}