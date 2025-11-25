import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
   private JTextField usernameField;
   private JPasswordField passwordField;
   private JButton loginButton, registerButton;
   private UserService userService;
   private Tema ini = new Tema();

   public LoginFrame() {
      userService = new UserService();
      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal Management - Login");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1280, 720);
      setLocationRelativeTo(null);
      setResizable(false);
      
      // Panel utama
      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBackground(Color.BLACK);
      add(mainPanel);

      // pake layered pane biar bisa di tumpuk
      JLayeredPane headerLayeredPane = new JLayeredPane();
      headerLayeredPane.setPreferredSize(new Dimension(ini.tinggi, 200));

      // gambar Header
      JLabel backgroundLabel = new JLabel(new ImageIcon(
               new ImageIcon("assets/images/img-header.png")
                     .getImage()
                     .getScaledInstance(ini.lebar, 200, Image.SCALE_SMOOTH)
      ));
      backgroundLabel.setBounds(0, 0, ini.lebar, 200);

      // teks Header
      JLabel headerText = new JLabel("RENTAL PS RIJAL");
      headerText.setFont(new Font("Arial", Font.BOLD, 70));
      headerText.setForeground(Color.WHITE);
      headerText.setBounds(450, 50, 1000, 100);

      // tumpuk dua komponen di layered pane
      headerLayeredPane.add(backgroundLabel, Integer.valueOf(0));
      headerLayeredPane.add(headerText, Integer.valueOf(1));

      mainPanel.add(headerLayeredPane, BorderLayout.NORTH);

      JPanel loginBox = new JPanel();
      loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
      loginBox.setOpaque(false);
      loginBox.setBorder(BorderFactory.createEmptyBorder(150, 0, 100, 0));

      // Title
      JLabel titleLabel = new JLabel("Login to Futsal Management", JLabel.CENTER);
      titleLabel.setFont(new Font(ini.font, Font.BOLD, 32));
      titleLabel.setForeground(Color.decode(ini.warna_isi));
      titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

      // Form Panel
      JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
      formPanel.setOpaque(false);
      formPanel.setMaximumSize(new Dimension(380, 100));

      JLabel usernameLabel = new JLabel("Username:");
      usernameLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      usernameLabel.setForeground(Color.white);

      usernameField = new JTextField();
      usernameField.setFont(new Font(ini.font, Font.BOLD, 14));
      usernameField.setForeground(Color.white);
      usernameField.setBackground(Color.decode(ini.warna_utama_lembut));
      usernameField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      usernameField.setCaretColor(Color.decode(ini.warna_isi));

      JLabel passwordLabel = new JLabel("Password:");
      passwordLabel.setFont(new Font(ini.font, Font.BOLD, 14));
      passwordLabel.setForeground(Color.white);

      passwordField = new JPasswordField();
      passwordField.setFont(new Font(ini.font, Font.BOLD, 14));
      passwordField.setForeground(Color.white);
      passwordField.setBackground(Color.decode(ini.warna_utama_lembut));
      passwordField.setBorder(new LineBorder(Color.decode(ini.warna_isi), 1));
      passwordField.setCaretColor(Color.decode(ini.warna_isi));

      formPanel.add(usernameLabel);
      formPanel.add(usernameField);
      formPanel.add(passwordLabel);
      formPanel.add(passwordField);

      // Button Panel
      JPanel buttonPanel = new JPanel(new FlowLayout());
      buttonPanel.setOpaque(false);
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

      loginButton = new JButton();
      loginButton.setText("Login");
      loginButton.setFont(new Font(ini.font, Font.BOLD, 20));
      loginButton.setBackground(Color.decode(ini.warna_isi));
      loginButton.setForeground(Color.black);
      loginButton.setFocusPainted(false);
      loginButton.setOpaque(true);
      loginButton.setBorderPainted(false);

      registerButton = new JButton();
      registerButton.setText("Register");
      registerButton.setFont(new Font(ini.font, Font.BOLD, 20));
      registerButton.setBackground(Color.decode(ini.warna_isi));
      registerButton.setForeground(Color.black);
      registerButton.setFocusPainted(false);
      registerButton.setOpaque(true);
      registerButton.setBorderPainted(false);

      buttonPanel.add(loginButton);
      buttonPanel.add(registerButton);

      // Assemble panels
      loginBox.add(titleLabel);
      loginBox.add(formPanel);
      loginBox.add(buttonPanel);

      mainPanel.add(loginBox, BorderLayout.CENTER);

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