import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton selectedButton;
    private Tema tema;
    
    // Warna dari Tema
    private Color colorUtama;
    private Color colorUtamaLembut;
    private Color colorIsi;
    private Color colorPelengkap;
    
    public AdminDashboard() {
        tema = new Tema();
        
        // Parse warna dari String hex ke Color
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        colorIsi = Color.decode(tema.warna_isi);
        colorPelengkap = Color.decode(tema.warna_pelengkap);
        
        setTitle("Admin Dashboard - Futsal Booking System");
        setSize(tema.lebar, tema.tinggi);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(colorUtamaLembut);
        
        // Sidebar
        JPanel sidebar = createSidebar();
        
        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(colorUtamaLembut);
        
        // Add panels
        contentPanel.add(new DashboardPanel(tema), "dashboard");
        contentPanel.add(new ManageFieldsPanel(tema), "fields");
        contentPanel.add(new ManageBookingsPanel(tema), "bookings");
        contentPanel.add(new UsersPanel(tema), "users");
        contentPanel.add(new TransactionsPanel(tema), "transactions");
        contentPanel.add(new SettingsPanel(tema), "settings");
        
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(colorUtama);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Logo/Title
        JLabel titleLabel = new JLabel("ADMIN PANEL");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(titleLabel);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel subtitleLabel = new JLabel("Futsal Booking System");
        subtitleLabel.setFont(new Font(tema.font, Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(subtitleLabel);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Menu buttons
        String[] menuItems = {"Dashboard", "Manage Fields", "Manage Bookings", "Users", "Transactions", "Settings"};
        String[] menuIcons = {"📊", "⚽", "📅", "👥", "💰", "⚙️"};
        String[] menuKeys = {"dashboard", "fields", "bookings", "users", "transactions", "settings"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuBtn = createMenuButton(menuIcons[i] + "  " + menuItems[i], menuKeys[i]);
            sidebar.add(menuBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
            
            if (i == 0) {
                menuBtn.setBackground(colorIsi);
                selectedButton = menuBtn;
            }
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        // Logout button
        JButton logoutBtn = new JButton("🔒  Logout");
        logoutBtn.setFont(new Font(tema.font, Font.PLAIN, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setMaximumSize(new Dimension(220, 45));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin logout?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
        
        sidebar.add(logoutBtn);
        
        return sidebar;
    }
    
    private JButton createMenuButton(String text, String panelKey) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(tema.font, Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorUtama);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, panelKey);
            if (selectedButton != null) {
                selectedButton.setBackground(colorUtama);
            }
            btn.setBackground(colorIsi);
            selectedButton = btn;
        });
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != selectedButton) {
                    btn.setBackground(Color.decode(tema.warna2_utama_lembut));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != selectedButton) {
                    btn.setBackground(colorUtama);
                }
            }
        });
        
        return btn;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard().setVisible(true);
        });
    }
}

// Dashboard Panel
class DashboardPanel extends JPanel {
    private Tema tema;
    private Color colorUtama, colorUtamaLembut, colorIsi, colorPelengkap;
    
    public DashboardPanel(Tema tema) {
        this.tema = tema;
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        colorIsi = Color.decode(tema.warna_isi);
        colorPelengkap = Color.decode(tema.warna_pelengkap);
        
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(colorUtamaLembut);
        
        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel("Kamis, 27 November 2025");
        dateLabel.setFont(new Font(tema.font, Font.PLAIN, 14));
        dateLabel.setForeground(colorIsi);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(colorUtamaLembut);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        statsPanel.add(createStatCard("Total Bookings", "248", "📅", new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Total Users", "1,234", "👥", new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Active Fields", "8", "⚽", new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Revenue Today", "Rp 2.450.000", "💰", new Color(241, 196, 15)));
        
        add(statsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(tema.font, Font.PLAIN, 13));
        titleLabel.setForeground(new Color(120, 120, 120));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        valueLabel.setForeground(colorUtama);
        
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
}

// Manage Fields Panel
class ManageFieldsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private Tema tema;
    private Color colorUtama, colorUtamaLembut, colorIsi;
    
    public ManageFieldsPanel(Tema tema) {
        this.tema = tema;
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        colorIsi = Color.decode(tema.warna_isi);
        
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(colorUtamaLembut);
        
        JLabel titleLabel = new JLabel("Manage Fields");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addBtn = new JButton("+ Add New Field");
        addBtn.setFont(new Font(tema.font, Font.PLAIN, 14));
        addBtn.setBackground(colorIsi);
        addBtn.setForeground(colorUtama);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(160, 40));
        
        addBtn.addActionListener(e -> {
            showAddFieldDialog();
        });
        
        headerPanel.add(addBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Field Name", "Open Time", "Close Time", "Price/Hour", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Sample data
        tableModel.addRow(new Object[]{"1", "Lapangan A", "08:00", "22:00", "Rp 150.000", "Active"});
        tableModel.addRow(new Object[]{"2", "Lapangan B", "08:00", "22:00", "Rp 150.000", "Active"});
        tableModel.addRow(new Object[]{"3", "Lapangan C", "08:00", "22:00", "Rp 200.000", "Maintenance"});
        
        table = new JTable(tableModel);
        table.setFont(new Font(tema.font, Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(colorIsi.getRed(), colorIsi.getGreen(), colorIsi.getBlue(), 50));
        table.setBackground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(tema.font, Font.BOLD, 13));
        header.setBackground(colorUtama);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void showAddFieldDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Field", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JTextField nameField = new JTextField();
        JTextField openField = new JTextField("08:00");
        JTextField closeField = new JTextField("22:00");
        JTextField priceField = new JTextField("150000");
        
        panel.add(new JLabel("Field Name:"));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Open Time:"));
        panel.add(openField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Close Time:"));
        panel.add(closeField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(new JLabel("Price per Hour:"));
        panel.add(priceField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(colorIsi);
        saveBtn.setForeground(colorUtama);
        saveBtn.setFocusPainted(false);
        
        saveBtn.addActionListener(e -> {
            String name = nameField.getText();
            String open = openField.getText();
            String close = closeField.getText();
            String price = "Rp " + priceField.getText();
            
            if (!name.isEmpty()) {
                int newId = tableModel.getRowCount() + 1;
                tableModel.addRow(new Object[]{String.valueOf(newId), name, open, close, price, "Active"});
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Field name cannot be empty!");
            }
        });
        
        panel.add(saveBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}

// Manage Bookings Panel
class ManageBookingsPanel extends JPanel {
    private Tema tema;
    private Color colorUtama, colorUtamaLembut, colorIsi;
    
    public ManageBookingsPanel(Tema tema) {
        this.tema = tema;
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        colorIsi = Color.decode(tema.warna_isi);
        
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Manage Bookings");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Booking ID", "User", "Field", "Date", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        model.addRow(new Object[]{"BK001", "John Doe", "Lapangan A", "27/11/2025", "14:00-16:00", "Confirmed"});
        model.addRow(new Object[]{"BK002", "Jane Smith", "Lapangan B", "27/11/2025", "16:00-18:00", "Pending"});
        model.addRow(new Object[]{"BK003", "Bob Wilson", "Lapangan A", "28/11/2025", "10:00-12:00", "Confirmed"});
        
        JTable table = new JTable(model);
        table.setFont(new Font(tema.font, Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font(tema.font, Font.BOLD, 13));
        table.getTableHeader().setBackground(colorUtama);
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }
}

// Users Panel
class UsersPanel extends JPanel {
    private Tema tema;
    private Color colorUtama, colorUtamaLembut;
    
    public UsersPanel(Tema tema) {
        this.tema = tema;
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"User ID", "Name", "Email", "Phone", "Member Level", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        model.addRow(new Object[]{"U001", "John Doe", "john@email.com", "081234567890", "Premium", "Active"});
        model.addRow(new Object[]{"U002", "Jane Smith", "jane@email.com", "081234567891", "Regular", "Active"});
        
        JTable table = new JTable(model);
        table.setFont(new Font(tema.font, Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font(tema.font, Font.BOLD, 13));
        table.getTableHeader().setBackground(colorUtama);
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }
}

// Transactions Panel
class TransactionsPanel extends JPanel {
    private Tema tema;
    private Color colorUtama, colorUtamaLembut;
    
    public TransactionsPanel(Tema tema) {
        this.tema = tema;
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Transaction ID", "User", "Booking ID", "Amount", "Payment Method", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        model.addRow(new Object[]{"TRX001", "John Doe", "BK001", "Rp 300.000", "Transfer", "27/11/2025", "Success"});
        model.addRow(new Object[]{"TRX002", "Jane Smith", "BK002", "Rp 300.000", "E-Wallet", "27/11/2025", "Pending"});
        
        JTable table = new JTable(model);
        table.setFont(new Font(tema.font, Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font(tema.font, Font.BOLD, 13));
        table.getTableHeader().setBackground(colorUtama);
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }
}

// Settings Panel
class SettingsPanel extends JPanel {
    private Tema tema;
    private Color colorUtama, colorUtamaLembut, colorIsi;
    
    public SettingsPanel(Tema tema) {
        this.tema = tema;
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        colorIsi = Color.decode(tema.warna_isi);
        
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        settingsPanel.add(createSettingItem("System Name", "Futsal Booking System"));
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsPanel.add(createSettingItem("Operating Hours", "08:00 - 22:00"));
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsPanel.add(createSettingItem("Booking Lead Time", "2 hours"));
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsPanel.add(createSettingItem("Cancellation Policy", "24 hours before"));
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(colorUtamaLembut);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        wrapper.add(settingsPanel, BorderLayout.NORTH);
        
        add(wrapper, BorderLayout.CENTER);
    }
    
    private JPanel createSettingItem(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font(tema.font, Font.BOLD, 14));
        labelComp.setForeground(colorUtama);
        
        JTextField valueField = new JTextField(value);
        valueField.setFont(new Font(tema.font, Font.PLAIN, 14));
        valueField.setPreferredSize(new Dimension(300, 35));
        
        panel.add(labelComp, BorderLayout.WEST);
        panel.add(valueField, BorderLayout.EAST);
        
        return panel;
    }
}