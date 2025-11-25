import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
   private User currentUser;
   private JTabbedPane tabbedPane;

   public AdminDashboardFrame(User user) {
      this.currentUser = user;
      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal Management - Admin Dashboard");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1000, 700);
      setLocationRelativeTo(null);

      // Main panel
      JPanel mainPanel = new JPanel(new BorderLayout());

      // Header
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setBackground(new Color(70, 130, 180));
      headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (Admin)");
      welcomeLabel.setForeground(Color.WHITE);
      welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

      JButton logoutButton = new JButton("Logout");
      logoutButton.setBackground(Color.RED);
      logoutButton.setForeground(Color.WHITE);
      logoutButton.setOpaque(true);
      logoutButton.setBorderPainted(false);

      logoutButton.addActionListener(e -> {
         // new LoginFrame().setVisible(true);
         dispose();
      });

      headerPanel.add(welcomeLabel, BorderLayout.WEST);
      headerPanel.add(logoutButton, BorderLayout.EAST);

      tabbedPane = new JTabbedPane();

      tabbedPane.addTab("Dashboard", createDashboardPanel());
      tabbedPane.addTab("Manage Fields", new ManageFieldsPanel());

      mainPanel.add(headerPanel, BorderLayout.NORTH);
      mainPanel.add(tabbedPane, BorderLayout.CENTER);

      add(mainPanel);
   }

   private JPanel createDashboardPanel() {
      JPanel dashboardPanel = new JPanel(new BorderLayout());
      dashboardPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

      JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
      titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
      titleLabel.setForeground(new Color(70, 130, 180));

      JLabel featuresLabel = new JLabel("<html><center>"
            + "<h2>Futsal Management System</h2>"
            + "<ul style='text-align: left; margin-left: 100px;'>"
            + "<li><b>Manage Fields:</b> Add, edit, and delete futsal fields</li>"
            + "</ul>"
            + "<p>tambahin lgi fitur-fitur buat adminnya cuy, yg skrng baru buat/edit/delete lapangan doang</p>"
            + "</center></html>");
      featuresLabel.setFont(new Font("Arial", Font.PLAIN, 16));
      featuresLabel.setHorizontalAlignment(SwingConstants.CENTER);

      dashboardPanel.add(titleLabel, BorderLayout.NORTH);
      dashboardPanel.add(featuresLabel, BorderLayout.CENTER);

      return dashboardPanel;
   }

   public class ManageFieldsPanel extends JPanel {
      private FieldService fieldService;
      private JTable fieldsTable;
      private FieldsTableModel tableModel;

      public ManageFieldsPanel() {
         fieldService = new FieldService();
         initializeUI();
         loadFieldsData();
      }

      private void initializeUI() {
         setLayout(new BorderLayout());
         setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

         // Title
         JLabel titleLabel = new JLabel("Manage Futsal Fields", JLabel.CENTER);
         titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
         titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

         // Table
         tableModel = new FieldsTableModel();
         fieldsTable = new JTable(tableModel);
         fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         fieldsTable.getTableHeader().setReorderingAllowed(false);

         JScrollPane tableScrollPane = new JScrollPane(fieldsTable);
         tableScrollPane.setPreferredSize(new Dimension(800, 300));

         // Buttons
         JPanel buttonPanel = new JPanel(new FlowLayout());

         JButton addButton = new JButton("Add Field");
         JButton editButton = new JButton("Edit Field");
         JButton deleteButton = new JButton("Delete Field");
         JButton refreshButton = new JButton("Refresh");

         addButton.setBackground(new Color(46, 139, 87));
         editButton.setBackground(new Color(70, 130, 180));
         deleteButton.setBackground(new Color(220, 20, 60));
         refreshButton.setBackground(new Color(169, 169, 169));

         addButton.setForeground(Color.WHITE);
         editButton.setForeground(Color.WHITE);
         deleteButton.setForeground(Color.WHITE);
         refreshButton.setForeground(Color.WHITE);

         addButton.setOpaque(true);
         addButton.setBorderPainted(false);

         editButton.setOpaque(true);
         editButton.setBorderPainted(false);

         deleteButton.setOpaque(true);
         deleteButton.setBorderPainted(false);

         refreshButton.setOpaque(true);
         refreshButton.setBorderPainted(false);

         buttonPanel.add(addButton);
         buttonPanel.add(editButton);
         buttonPanel.add(deleteButton);
         buttonPanel.add(refreshButton);

         add(titleLabel, BorderLayout.NORTH);
         add(tableScrollPane, BorderLayout.CENTER);
         add(buttonPanel, BorderLayout.SOUTH);

         addButton.addActionListener(e -> showAddFieldDialog());
         editButton.addActionListener(e -> showEditFieldDialog());
         deleteButton.addActionListener(e -> deleteSelectedField());
         refreshButton.addActionListener(e -> loadFieldsData());
      }

      public void loadFieldsData() {
         new Thread(() -> {
            List<FutsalField> fields = fieldService.getAllFields();
            SwingUtilities.invokeLater(() -> {
               tableModel.setFields(fields);
               tableModel.fireTableDataChanged();
            });
         }).start();
      }

      private void showAddFieldDialog() {
         new AddEditFieldDialog(null, fieldService, this).setVisible(true);
      }

      private void showEditFieldDialog() {
         int selectedRow = fieldsTable.getSelectedRow();
         if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                  "Please select a field to edit",
                  "Error",
                  JOptionPane.WARNING_MESSAGE);
            return;
         }

         FutsalField field = tableModel.getFieldAt(selectedRow);
         new AddEditFieldDialog(field, fieldService, this).setVisible(true);
      }

      private void deleteSelectedField() {
         int selectedRow = fieldsTable.getSelectedRow();
         if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                  "Please select a field to delete",
                  "Error",
                  JOptionPane.WARNING_MESSAGE);
            return;
         }

         FutsalField field = tableModel.getFieldAt(selectedRow);
         int confirm = JOptionPane.showConfirmDialog(this,
               "Are you sure you want to delete field: " + field.getFieldName() + "?",
               "Confirm Delete",
               JOptionPane.YES_NO_OPTION);

         if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
               boolean success = fieldService.deleteField(field.getId());
               SwingUtilities.invokeLater(() -> {
                  if (success) {
                     JOptionPane.showMessageDialog(this,
                           "Field deleted successfully",
                           "Success",
                           JOptionPane.INFORMATION_MESSAGE);
                     loadFieldsData();
                  } else {
                     JOptionPane.showMessageDialog(this,
                           "Failed to delete field",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                  }
               });
            }).start();
         }
      }
   }
}