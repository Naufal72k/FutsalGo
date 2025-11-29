import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DashboardAdmin extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton selectedButton;
    private Tema tema;
    private App app;

    // Services
    private DatabaseConfig dbService = new DatabaseConfig();

    // Warna dari Tema
    private Color colorUtama;
    private Color colorUtamaLembut;
    private Color colorIsi;

    // Components for Dashboard (Live Update)
    private JLabel lblValBookings, lblValUsers, lblValFields, lblValRevenue;
    private DefaultTableModel recentTransModel;

    // Components for Manage Bookings
    private DefaultTableModel bookingsModel;
    private JTable bookingsTable;

    // Components for Manual Booking Dialog
    private JPanel adminTimeGridPanel;
    private List<String> adminSelectedSlots = new ArrayList<>();
    private JComboBox<FutsalField> adminCmbField;
    private JSpinner adminSpinDate;

    // Components for Manage Fields
    private DefaultTableModel fieldsModel;
    private JTable fieldsTable;

    // Components for Transactions
    private DefaultTableModel transModel;
    private JTable transTable;
    private JSpinner dateStartSpinner;
    private JSpinner dateEndSpinner;
    private JLabel lblTotalRevenue;

    // Components for Settings
    private JToggleButton btnToggleShop;
    private JLabel lblShopStatus;

    public DashboardAdmin(App app) {
        this.app = app;
        tema = new Tema();

        // Parse warna dari String hex ke Color
        colorUtama = Color.decode(tema.warna_utama);
        colorUtamaLembut = Color.decode(tema.warna_utama_lembut);
        colorIsi = Color.decode(tema.warna_isi);

        // Setup panel
        setLayout(new BorderLayout());
        setBackground(colorUtamaLembut);

        // Sidebar
        JPanel sidebar = createSidebar();

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(colorUtamaLembut);

        // Add panels
        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(createManageFieldsPanel(), "fields");
        contentPanel.add(createManageBookingsPanel(), "bookings");
        contentPanel.add(createTransactionsPanel(), "transactions");
        contentPanel.add(createSettingsPanel(), "settings");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Load data awal dashboard
        refreshDashboardStats();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(colorUtama);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

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

        String[] menuItems = { "Dashboard", "Manage Fields", "Manage Bookings", "Transactions", "Settings" };
        String[] menuIcons = { "📊", "⚽", "📅", "💰", "⚙️" };
        String[] menuKeys = { "dashboard", "fields", "bookings", "transactions", "settings" };

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
            int choice = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION)
                app.showLogin();
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
            if (selectedButton != null)
                selectedButton.setBackground(colorUtama);
            btn.setBackground(colorIsi);
            selectedButton = btn;

            // Auto refresh logic based on panel
            if (panelKey.equals("dashboard"))
                refreshDashboardStats(); // UPDATE: Realtime refresh stats
            if (panelKey.equals("bookings"))
                refreshBookingsTable();
            if (panelKey.equals("fields"))
                refreshFieldsTable();
            if (panelKey.equals("transactions"))
                loadTransactionData();
            if (panelKey.equals("settings"))
                loadSettings();
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != selectedButton)
                    btn.setBackground(Color.decode(tema.warna2_utama_lembut));
            }

            public void mouseExited(MouseEvent e) {
                if (btn != selectedButton)
                    btn.setBackground(colorUtama);
            }
        });

        return btn;
    }

    // --- PANEL DASHBOARD UTAMA (RE-DESIGNED) ---
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorUtamaLembut);

        // ScrollPane agar responsif jika layar kecil
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(colorUtamaLembut);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // 1. Header Besar
        JLabel welcomeLabel = new JLabel("Welcome Back, Administrator");
        welcomeLabel.setFont(new Font(tema.font, Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        dateLabel.setFont(new Font(tema.font, Font.PLAIN, 14));
        dateLabel.setForeground(new Color(200, 200, 200));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainContent.add(welcomeLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 5)));
        mainContent.add(dateLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. Stats Grid (Kartu Besar)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0)); // 1 Baris 4 Kolom
        statsGrid.setBackground(colorUtamaLembut);
        statsGrid.setMaximumSize(new Dimension(2000, 140)); // Tinggi tetap
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Init Labels (akan diisi via refreshDashboardStats)
        lblValBookings = new JLabel("0");
        lblValUsers = new JLabel("0");
        lblValFields = new JLabel("0");
        lblValRevenue = new JLabel("Rp 0");

        statsGrid.add(createBigStatCard("Total Paid Bookings", lblValBookings, "📅", new Color(52, 152, 219)));
        statsGrid.add(createBigStatCard("Registered Users", lblValUsers, "👥", new Color(46, 204, 113)));
        statsGrid.add(createBigStatCard("Active Fields", lblValFields, "⚽", new Color(155, 89, 182)));
        statsGrid.add(createBigStatCard("Revenue (Today)", lblValRevenue, "💰", new Color(241, 196, 15)));

        mainContent.add(statsGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 40)));

        // 3. Recent Transactions Table (Preview)
        JLabel tableTitle = new JLabel("Recent Transactions");
        tableTitle.setFont(new Font(tema.font, Font.BOLD, 18));
        tableTitle.setForeground(Color.WHITE);
        tableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainContent.add(tableTitle);
        mainContent.add(Box.createRigidArea(new Dimension(0, 15)));

        String[] cols = { "Date", "User ID", "Field", "Time", "Price", "Status" };
        recentTransModel = new DefaultTableModel(cols, 0);
        JTable recentTable = new JTable(recentTransModel);
        recentTable.setRowHeight(35);
        recentTable.setEnabled(false); // Read only

        JScrollPane scrollTable = new JScrollPane(recentTable);
        scrollTable.setPreferredSize(new Dimension(0, 250));
        scrollTable.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainContent.add(scrollTable);

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    // UPDATE: Method untuk refresh realtime statistik
    private void refreshDashboardStats() {
        // Fetch data
        Map<String, Object> stats = dbService.getDashboardStats();

        // Update Labels
        if (lblValBookings != null)
            lblValBookings.setText(stats.get("TotalBookings").toString());
        if (lblValUsers != null)
            lblValUsers.setText(stats.get("TotalUsers").toString());
        if (lblValFields != null)
            lblValFields.setText(stats.get("ActiveFields").toString());
        if (lblValRevenue != null)
            lblValRevenue.setText(String.format("Rp %,.0f", (Double) stats.get("RevenueToday")));

        // Update Recent Table (Ambil 5 teratas)
        if (recentTransModel != null) {
            recentTransModel.setRowCount(0);
            List<Booking> all = dbService.getAllBookings(); // Sudah sort DESC
            int limit = Math.min(all.size(), 5);
            for (int i = 0; i < limit; i++) {
                Booking b = all.get(i);
                recentTransModel.addRow(new Object[] {
                        b.getBookingDate(), b.getUserId(), b.getFieldId(),
                        b.getStartTime().substring(0, 5),
                        String.format("Rp %,.0f", b.getTotalPrice()),
                        b.getStatus()
                });
            }
        }
    }

    // --- MANAGE FIELDS PANEL ---
    private JPanel createManageFieldsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorUtamaLembut);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(colorUtamaLembut);
        JLabel title = new JLabel("Manage Fields");
        title.setFont(new Font(tema.font, Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton btnAdd = new JButton("+ Add Field");
        btnAdd.setBackground(colorIsi);
        btnAdd.addActionListener(e -> showFieldDialog(null));

        JButton btnEdit = new JButton("Edit");
        btnEdit.setBackground(new Color(241, 196, 15));
        btnEdit.addActionListener(e -> editSelectedField());

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedField());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        header.add(actionPanel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = { "ID", "Name", "Open", "Close", "Price", "Status" };
        fieldsModel = new DefaultTableModel(cols, 0);
        fieldsTable = new JTable(fieldsModel);
        fieldsTable.setRowHeight(35);

        panel.add(new JScrollPane(fieldsTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshFieldsTable() {
        fieldsModel.setRowCount(0);
        List<FutsalField> fields = dbService.getAllFields();
        for (FutsalField f : fields) {
            fieldsModel.addRow(new Object[] {
                    f.getId(), f.getFieldName(), f.getOpenTime(), f.getCloseTime(),
                    String.format("Rp %,.0f", f.getPricePerSession()),
                    f.isActive() ? "Active" : "Inactive"
            });
        }
    }

    private void showFieldDialog(FutsalField fieldToEdit) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                fieldToEdit == null ? "Add Field" : "Edit Field", true);
        d.setSize(400, 350);
        d.setLocationRelativeTo(this);

        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtName = new JTextField(fieldToEdit != null ? fieldToEdit.getFieldName() : "");
        JTextField txtOpen = new JTextField(fieldToEdit != null ? fieldToEdit.getOpenTime() : "08:00:00");
        JTextField txtClose = new JTextField(fieldToEdit != null ? fieldToEdit.getCloseTime() : "22:00:00");
        JTextField txtPrice = new JTextField(
                fieldToEdit != null ? String.valueOf((int) fieldToEdit.getPricePerSession()) : "");
        JCheckBox chkActive = new JCheckBox("Active", fieldToEdit != null ? fieldToEdit.isActive() : true);

        p.add(new JLabel("Field Name:"));
        p.add(txtName);
        p.add(new JLabel("Open Time (HH:mm:ss):"));
        p.add(txtOpen);
        p.add(new JLabel("Close Time (HH:mm:ss):"));
        p.add(txtClose);
        p.add(new JLabel("Price per Hour:"));
        p.add(txtPrice);
        p.add(new JLabel("Status:"));
        p.add(chkActive);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText());

                // Validasi nama kembar
                int currentId = (fieldToEdit != null) ? fieldToEdit.getId() : -1;
                if (dbService.isFieldNameExists(name, currentId)) {
                    JOptionPane.showMessageDialog(d, "Nama lapangan sudah ada!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FutsalField f = new FutsalField();
                f.setFieldName(name);
                f.setOpenTime(txtOpen.getText());
                f.setCloseTime(txtClose.getText());
                f.setPricePerSession(price);
                f.setActive(chkActive.isSelected());

                boolean success;
                if (fieldToEdit == null) {
                    success = dbService.addField(f);
                } else {
                    f.setId(fieldToEdit.getId());
                    success = dbService.updateField(f);
                }

                if (success) {
                    refreshFieldsTable();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Gagal menyimpan data.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Input tidak valid: " + ex.getMessage());
            }
        });

        d.add(p, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void editSelectedField() {
        int row = fieldsTable.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) fieldsTable.getValueAt(row, 0);

        List<FutsalField> list = dbService.getAllFields();
        for (FutsalField f : list) {
            if (f.getId() == id) {
                showFieldDialog(f);
                return;
            }
        }
    }

    private void deleteSelectedField() {
        int row = fieldsTable.getSelectedRow();
        if (row == -1)
            return;
        int id = (int) fieldsTable.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus lapangan ini? History booking mungkin hilang.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dbService.deleteField(id))
                refreshFieldsTable();
        }
    }

    // --- MANAGE BOOKINGS PANEL (UPDATE: ACCEPT BUTTON) ---
    private JPanel createManageBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorUtamaLembut);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(colorUtamaLembut);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Manage Bookings");
        titleLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Tombol Action Container
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        // Tombol APPROVE (Baru)
        JButton btnApprove = new JButton("✓ Accept / Approve");
        btnApprove.setBackground(new Color(46, 204, 113)); // Hijau
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFocusPainted(false);
        btnApprove.addActionListener(e -> approveSelectedBooking());

        // Tombol REJECT
        JButton btnCancel = new JButton("✕ Reject / Cancel");
        btnCancel.setBackground(new Color(231, 76, 60)); // Merah
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> cancelSelectedBooking());

        // Tombol MANUAL BOOKING
        JButton btnManual = new JButton("+ Manual Booking");
        btnManual.setBackground(colorIsi);
        btnManual.setForeground(Color.BLACK);
        btnManual.setFocusPainted(false);
        btnManual.addActionListener(e -> showManualBookingDialog());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshBookingsTable());

        actionPanel.add(btnApprove); // Tambah tombol approve
        actionPanel.add(btnCancel);
        actionPanel.add(btnManual);
        actionPanel.add(btnRefresh);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Tabel Data
        String[] columns = { "Booking ID", "User ID", "Field ID", "Date", "Start Time", "Total Price", "Status" };
        bookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsModel);
        bookingsTable.setFont(new Font(tema.font, Font.PLAIN, 13));
        bookingsTable.setRowHeight(40);
        bookingsTable.getTableHeader().setFont(new Font(tema.font, Font.BOLD, 13));
        bookingsTable.getTableHeader().setBackground(colorUtama);
        bookingsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshBookingsTable(); // Load data awal
        return panel;
    }

    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        List<Booking> bookings = dbService.getAllBookings();
        for (Booking b : bookings) {
            bookingsModel.addRow(new Object[] {
                    b.getId(),
                    b.getUserId(),
                    b.getFieldId(),
                    b.getBookingDate(),
                    b.getStartTime().substring(0, 5),
                    String.format("Rp %,.0f", b.getTotalPrice()),
                    b.getStatus()
            });
        }
    }

    // LOGIC: APPROVE (Ubah status ke PAID)
    private void approveSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang ingin di-approve!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingsModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) bookingsModel.getValueAt(selectedRow, 6);

        if ("PAID".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Booking sudah lunas (PAID).", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if ("CANCELLED".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Tidak bisa approve booking yang sudah dibatalkan.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Terima pembayaran dan set status ke PAID?\nPendapatan akan bertambah.",
                "Konfirmasi Approve", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dbService.updateBookingStatus(bookingId, "PAID")) {
                JOptionPane.showMessageDialog(this, "Booking berhasil di-approve!");
                refreshBookingsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // LOGIC: REJECT (Ubah status ke CANCELLED)
    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang ingin dibatalkan!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingsModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) bookingsModel.getValueAt(selectedRow, 6);

        if ("CANCELLED".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Booking ini sudah dibatalkan sebelumnya.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menolak/membatalkan booking ini?\nSlot jam akan kembali kosong.",
                "Konfirmasi Pembatalan", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dbService.updateBookingStatus(bookingId, "CANCELLED")) {
                JOptionPane.showMessageDialog(this, "Booking berhasil dibatalkan!");
                refreshBookingsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- MANUAL BOOKING DIALOG (FIXED STATUS PAID) ---
    private void showManualBookingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Manual Booking", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Pilih Field
        adminCmbField = new JComboBox<>();
        List<FutsalField> fields = dbService.getAllFields();
        for (FutsalField f : fields)
            if (f.isActive())
                adminCmbField.addItem(f);

        // 2. Pilih Tanggal
        adminSpinDate = new JSpinner(new SpinnerDateModel());
        adminSpinDate.setEditor(new JSpinner.DateEditor(adminSpinDate, "yyyy-MM-dd"));

        // 3. Admin Grid Waktu
        adminTimeGridPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        // Listener Update Grid
        ActionListener updateGridListener = e -> updateAdminTimeSlots();
        adminCmbField.addActionListener(updateGridListener);
        adminSpinDate.addChangeListener(e -> updateAdminTimeSlots());

        formPanel.add(new JLabel("Select Field:"));
        formPanel.add(adminCmbField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(new JLabel("Select Date:"));
        formPanel.add(adminSpinDate);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(new JLabel("Select Time Slots (Green = Available):"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(adminTimeGridPanel);

        // Tombol Save
        JButton btnSave = new JButton("Save Booking (Direct PAID)");
        btnSave.setBackground(colorIsi);
        btnSave.addActionListener(e -> {
            saveManualBooking(dialog);
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);

        // Init Grid Awal
        updateAdminTimeSlots();

        dialog.setVisible(true);
    }

    private void updateAdminTimeSlots() {
        FutsalField selectedField = (FutsalField) adminCmbField.getSelectedItem();
        if (selectedField == null)
            return;

        Date dateVal = (Date) adminSpinDate.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(dateVal);

        adminTimeGridPanel.removeAll();
        adminSelectedSlots.clear();

        List<String> occupiedTimes = dbService.getOccupiedTimeSlots(selectedField.getId(), dateStr);

        for (int i = 8; i <= 22; i++) {
            String timeLabel = String.format("%02d:00", i);
            JToggleButton btn = new JToggleButton(timeLabel);

            if (occupiedTimes.contains(timeLabel)) {
                btn.setBackground(new Color(255, 100, 100)); // Merah
                btn.setEnabled(false);
            } else {
                btn.setBackground(new Color(150, 255, 150)); // Hijau
                btn.addActionListener(e -> {
                    if (btn.isSelected()) {
                        adminSelectedSlots.add(timeLabel);
                        btn.setBackground(Color.decode(tema.warna_isi));
                    } else {
                        adminSelectedSlots.remove(timeLabel);
                        btn.setBackground(new Color(150, 255, 150));
                    }
                });
            }
            adminTimeGridPanel.add(btn);
        }
        adminTimeGridPanel.revalidate();
        adminTimeGridPanel.repaint();
    }

    private void saveManualBooking(JDialog dialog) {
        if (adminSelectedSlots.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Pilih minimal satu slot jam!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        FutsalField selectedField = (FutsalField) adminCmbField.getSelectedItem();
        Date selectedDate = (Date) adminSpinDate.getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(selectedDate);

        boolean allSuccess = true;

        for (String startTime : adminSelectedSlots) {
            int startH = Integer.parseInt(startTime.substring(0, 2));
            String endTimeStr = String.format("%02d:00:00", startH + 1);
            String startTimeStr = startTime + ":00";

            Booking b = new Booking();
            b.setUserId(1); // Admin ID
            b.setFieldId(selectedField.getId());
            b.setBookingDate(dateStr);
            b.setStartTime(startTimeStr);
            b.setEndTime(endTimeStr);
            b.setTotalPrice(selectedField.getPricePerSession());

            // UPDATE: Manual Booking langsung PAID agar masuk revenue
            b.setStatus("PAID");

            if (!dbService.createBooking(b)) {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            JOptionPane.showMessageDialog(dialog, "Manual Booking Berhasil Disimpan (Status: PAID)!");
            dialog.dispose();
            refreshBookingsTable();
        } else {
            JOptionPane.showMessageDialog(dialog, "Gagal menyimpan beberapa slot (Mungkin bentrok).", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- TRANSACTIONS PANEL ---
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorUtamaLembut);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);

        dateStartSpinner = new JSpinner(new SpinnerDateModel());
        dateStartSpinner.setEditor(new JSpinner.DateEditor(dateStartSpinner, "yyyy-MM-dd"));

        dateEndSpinner = new JSpinner(new SpinnerDateModel());
        dateEndSpinner.setEditor(new JSpinner.DateEditor(dateEndSpinner, "yyyy-MM-dd"));

        JButton btnFilter = new JButton("Filter Data");
        btnFilter.setBackground(colorIsi);
        btnFilter.addActionListener(e -> loadTransactionData());

        filterPanel.add(new JLabel("From: "));
        filterPanel.add(dateStartSpinner);
        filterPanel.add(new JLabel(" To: "));
        filterPanel.add(dateEndSpinner);
        filterPanel.add(btnFilter);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] cols = { "Date", "Customer ID", "Field", "Time", "Price", "Status" };
        transModel = new DefaultTableModel(cols, 0);
        transTable = new JTable(transModel);
        transTable.setRowHeight(30);
        panel.add(new JScrollPane(transTable), BorderLayout.CENTER);

        // Footer Total
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        lblTotalRevenue = new JLabel("Total Revenue: Rp 0");
        lblTotalRevenue.setFont(new Font("Arial", Font.BOLD, 18));
        footer.add(lblTotalRevenue);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private void loadTransactionData() {
        Date start = (Date) dateStartSpinner.getValue();
        Date end = (Date) dateEndSpinner.getValue();

        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        end = c.getTime();

        List<Booking> list = dbService.getTransactionsByDate(start, end);
        transModel.setRowCount(0);
        double total = 0;

        for (Booking b : list) {
            if ("PAID".equals(b.getStatus())) {
                total += b.getTotalPrice();
            }
            transModel.addRow(new Object[] {
                    b.getBookingDate(), b.getUserId(), b.getFieldId(),
                    b.getStartTime().substring(0, 5),
                    String.format("Rp %,.0f", b.getTotalPrice()),
                    b.getStatus()
            });
        }
        lblTotalRevenue.setText(String.format("Total Revenue: Rp %,.0f", total));
    }

    // --- SETTINGS PANEL ---
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(colorUtamaLembut);

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(400, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Status Operasional Lapangan", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        btnToggleShop = new JToggleButton("LOADING...");
        btnToggleShop.setFont(new Font("Arial", Font.BOLD, 24));
        btnToggleShop.setFocusPainted(false);

        btnToggleShop.addActionListener(e -> {
            boolean isOpen = btnToggleShop.isSelected();
            updateToggleVisual(isOpen);
            dbService.setFutsalStatus(isOpen);
        });

        lblShopStatus = new JLabel("Mengatur apakah user bisa booking atau tidak.", SwingConstants.CENTER);

        card.add(title, BorderLayout.NORTH);
        card.add(btnToggleShop, BorderLayout.CENTER);
        card.add(lblShopStatus, BorderLayout.SOUTH);

        panel.add(card);

        loadSettings();

        return panel;
    }

    private void loadSettings() {
        if (btnToggleShop != null) {
            boolean isOpen = dbService.isFutsalOpen();
            btnToggleShop.setSelected(isOpen);
            updateToggleVisual(isOpen);
        }
    }

    private void updateToggleVisual(boolean isOpen) {
        if (isOpen) {
            btnToggleShop.setText("TOKO BUKA (OPEN)");
            btnToggleShop.setBackground(new Color(46, 204, 113)); // Hijau
            btnToggleShop.setForeground(Color.WHITE);
        } else {
            btnToggleShop.setText("TOKO TUTUP (CLOSED)");
            btnToggleShop.setBackground(new Color(231, 76, 60)); // Merah
            btnToggleShop.setForeground(Color.WHITE);
        }
    }

    // --- HELPER UI: BIG STAT CARD ---
    private JPanel createBigStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, accentColor),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Icon Area
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        // Text Area
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(tema.font, Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font(tema.font, Font.BOLD, 24));
        valueLabel.setForeground(Color.DARK_GRAY);

        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }
}