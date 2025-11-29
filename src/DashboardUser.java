import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardUser extends JPanel {

    // GLOBAL VARIABLES
    private User currentUser;
    private DatabaseConfig bookingService = new DatabaseConfig();
    private DatabaseConfig fieldService = new DatabaseConfig();
    private Tema ini = new Tema();
    private App app;

    // Layout Manager
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Menu Buttons
    private ModernMenuButton btnBooking;
    private ModernMenuButton btnHistory;

    // Table Model
    private BookingsTableModel historyTableModel;

    // Input Komponen
    private JComboBox<FutsalField> cmbField;
    private JSpinner spinDate;
    private JPanel timeGridPanel;

    // Variabel Logic Multi-Select
    private List<String> selectedTimeSlots = new ArrayList<>();
    private List<JToggleButton> timeButtons = new ArrayList<>();

    // Komponen Stats (Untuk Update Realtime)
    private JLabel lblTotalPaidBookings;

    public DashboardUser(App app, User user) {
        this.currentUser = user;
        this.app = app;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 720));

        // Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Konten Area Utama
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.decode(ini.warna_pelengkap));

        // HAPUS HEADER LAMA (createHeader) AGAR LEBIH BERSIH
        // Kita akan memasukkan header besar langsung ke dalam halaman Booking

        // Body Dinamis
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);
        // Padding lebih besar biar lega
        mainContentPanel.setBorder(new EmptyBorder(10, 30, 30, 30));

        mainContentPanel.add(createBookingPage(), "BOOKING");
        mainContentPanel.add(createHistoryPage(), "HISTORY");

        contentArea.add(mainContentPanel, BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);

        setActiveButton(btnBooking);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 720));
        sidebar.setBackground(Color.decode(ini.warna_utama));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel logoLabel = new JLabel("Futsal-GO");
        logoLabel.setFont(new Font(ini.font, Font.BOLD, 28));
        logoLabel.setForeground(Color.decode(ini.warna_isi));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(50, 0, 0, 0));

        btnBooking = new ModernMenuButton("Book Field", true);
        btnHistory = new ModernMenuButton("My History", false);

        // Tombol Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font(ini.font, Font.BOLD, 16));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(231, 76, 60)); // Merah
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        // Pastikan dimensi maksimum cukup lebar tapi tidak memenuhi layar
        btnLogout.setMaximumSize(new Dimension(200, 45));
        // ALIGNMENT CENTER PENTING DI SINI
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(231, 76, 60));
            }
        });

        btnBooking.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "BOOKING");
            setActiveButton(btnBooking);
            refreshUserStats(); // Refresh stats saat balik ke home
            updateTimeSlots();
        });

        btnHistory.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "HISTORY");
            loadHistoryData();
            setActiveButton(btnHistory);
        });

        btnLogout.addActionListener(e -> app.showLogin());

        // Susun Menu
        menuContainer.add(btnBooking);
        menuContainer.add(Box.createVerticalStrut(10));
        menuContainer.add(btnHistory);

        sidebar.add(logoLabel);
        sidebar.add(menuContainer);
        sidebar.add(Box.createVerticalGlue()); // Dorong logout ke bawah
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(20)); // Sedikit jarak dari bawah

        return sidebar;
    }

    // --- RE-DESIGNED BOOKING PAGE (MIRIP DASHBOARD ADMIN) ---
    private JPanel createBookingPage() {
        // Gunakan Panel wrapper dengan BoxLayout Y_AXIS agar bisa scroll jika perlu
        JPanel pagePanel = new JPanel();
        pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.Y_AXIS));
        pagePanel.setOpaque(false);

        // 1. HEADER BESAR (Welcome Text + Date)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel welcomeLabel = new JLabel("Welcome Back, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font(ini.font, Font.BOLD, 28));
        welcomeLabel.setForeground(Color.DARK_GRAY);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        dateLabel.setFont(new Font(ini.font, Font.PLAIN, 14));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(dateLabel);

        // 2. STATS GRID (KARTU BESAR)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 120)); // Tinggi fix
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Init label stat
        lblTotalPaidBookings = new JLabel("Loading...");

        // Panggil helper createBigStatCard (Gaya Admin)
        statsPanel.add(createBigStatCard("Total Paid Bookings", lblTotalPaidBookings, "📅", new Color(52, 152, 219)));
        statsPanel.add(createBigStatCard("Active Fields", new JLabel("3"), "⚽", new Color(46, 204, 113)));
        statsPanel.add(createBigStatCard("Your Level", new JLabel("Member"), "⭐", new Color(241, 196, 15)));

        // 3. BOOKING FORM AREA
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.setBorder(new EmptyBorder(30, 0, 0, 0));

        JLabel formTitle = new JLabel("Book a Field Now");
        formTitle.setFont(new Font(ini.font, Font.BOLD, 20));
        formTitle.setForeground(Color.DARK_GRAY);
        formTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel formCard = createModernCardPanel();
        formCard.setLayout(new GridBagLayout()); // Kembali ke GridBag untuk form rapi

        // Isi Form
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // ... Komponen Form (Sama seperti logika sebelumnya) ...
        JLabel lblField = createLabel("Select Field");
        cmbField = new JComboBox<>();
        styleComboBox(cmbField);
        List<FutsalField> fields = fieldService.getAllFields();
        for (FutsalField f : fields)
            if (f.isActive())
                cmbField.addItem(f);

        JLabel lblDate = createLabel("Date");
        spinDate = new JSpinner(new SpinnerDateModel());
        spinDate.setEditor(new JSpinner.DateEditor(spinDate, "yyyy-MM-dd"));
        styleSpinner(spinDate);

        JLabel lblTime = createLabel("Select Time Slots (Multi-select)");
        timeGridPanel = new JPanel(new GridLayout(3, 5, 8, 8));
        timeGridPanel.setOpaque(false);

        JLabel lblPrice = createLabel("Total Price");
        JLabel lblPriceValue = new JLabel("Rp 0");
        lblPriceValue.setFont(new Font(ini.font, Font.BOLD, 20));
        lblPriceValue.setForeground(Color.decode(ini.warna_isi));

        // Listeners
        cmbField.addActionListener(e -> {
            updateTimeSlots();
            updatePrice(lblPriceValue);
        });
        spinDate.addChangeListener(e -> updateTimeSlots());

        // Layout Form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formContent.add(lblField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formContent.add(cmbField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formContent.add(lblDate, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formContent.add(spinDate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formContent.add(lblTime, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formContent.add(timeGridPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formContent.add(lblPrice, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        formContent.add(lblPriceValue, gbc);

        JButton btnSubmit = new JButton("Book Now");
        styleActionButton(btnSubmit);
        btnSubmit.addActionListener(e -> processBookingAttempt(lblPriceValue));

        // Masukkan formContent ke card
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.weightx = 1.0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        formCard.add(formContent, cardGbc);

        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(20, 0, 0, 0);
        cardGbc.fill = GridBagConstraints.NONE;
        cardGbc.anchor = GridBagConstraints.EAST; // Tombol di kanan
        formCard.add(btnSubmit, cardGbc);

        formWrapper.add(formTitle, BorderLayout.NORTH);
        formWrapper.add(formCard, BorderLayout.CENTER);

        // RAKIT SEMUA KE PAGE PANEL
        pagePanel.add(headerPanel);
        pagePanel.add(statsPanel);
        pagePanel.add(formWrapper);

        SwingUtilities.invokeLater(this::updateTimeSlots);
        refreshUserStats(); // Load stats awal

        return pagePanel;
    }

    // --- HELPER: UPDATE REAL STATS ---
    private void refreshUserStats() {
        new Thread(() -> {
            // Panggil method baru countUserPaidBookings
            int paidCount = bookingService.countUserPaidBookings(currentUser.getId());
            SwingUtilities.invokeLater(() -> {
                lblTotalPaidBookings.setText(String.valueOf(paidCount));
            });
        }).start();
    }

    // --- LOGIC: VISUAL GRID TOMBOL ---
    private void updateTimeSlots() {
        FutsalField selectedField = (FutsalField) cmbField.getSelectedItem();
        if (selectedField == null)
            return;

        // Reset Panel
        timeGridPanel.removeAll();
        timeButtons.clear();
        selectedTimeSlots.clear();

        // 1. CEK STATUS TOKO DARI SETTINGS
        if (!bookingService.isFutsalOpen()) {
            timeGridPanel.setLayout(new BorderLayout());

            JLabel lblClosed = new JLabel("<html><center>MOHON MAAF<br>LAPANGAN SEDANG TUTUP / LIBUR</center></html>",
                    SwingConstants.CENTER);
            lblClosed.setFont(new Font("Arial", Font.BOLD, 18));
            lblClosed.setForeground(new Color(231, 76, 60)); // Merah

            timeGridPanel.add(lblClosed, BorderLayout.CENTER);

            JLabel lblPrice = getPriceLabelFromPanel();
            if (lblPrice != null)
                lblPrice.setText("Rp 0 (CLOSED)");

            timeGridPanel.revalidate();
            timeGridPanel.repaint();
            return;
        }

        // Jika BUKA
        timeGridPanel.setLayout(new GridLayout(3, 5, 8, 8));

        Date dateVal = (Date) spinDate.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(dateVal);

        // Ambil data slot terisi dari DB (Hanya PENDING & PAID)
        List<String> occupiedTimes = bookingService.getOccupiedTimeSlots(selectedField.getId(), dateStr);

        // Generate Tombol 08:00 - 22:00
        for (int i = 8; i <= 22; i++) {
            String timeLabel = String.format("%02d:00", i);
            JToggleButton btn = new JToggleButton(timeLabel);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(80, 35));

            if (occupiedTimes.contains(timeLabel)) {
                // SLOT PENUH (MERAH)
                btn.setBackground(new Color(255, 100, 100));
                btn.setForeground(Color.WHITE);
                btn.setEnabled(false);
            } else {
                // SLOT KOSONG (HIJAU)
                btn.setBackground(new Color(150, 255, 150));
                btn.setForeground(Color.DARK_GRAY);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Action Klik
                btn.addActionListener(e -> {
                    if (btn.isSelected()) {
                        selectedTimeSlots.add(timeLabel);
                        btn.setBackground(Color.decode(ini.warna_isi));
                    } else {
                        selectedTimeSlots.remove(timeLabel);
                        btn.setBackground(new Color(150, 255, 150));
                    }
                    updatePrice(getPriceLabelFromPanel());
                });
            }
            timeButtons.add(btn);
            timeGridPanel.add(btn);
        }

        updatePrice(getPriceLabelFromPanel());
        timeGridPanel.revalidate();
        timeGridPanel.repaint();
    }

    private JLabel getPriceLabelFromPanel() {
        try {
            // Karena hierarki berubah (GridBag), kita cari manual agak tricky
            // Cara aman: simpan referensi lblPriceValue di class level jika perlu
            // Tapi untuk sekarang kita cari parent dari gridPanel -> formContent ->
            // komponen index terakhir
            Container formContent = timeGridPanel.getParent();
            // index terakhir di formContent adalah lblPriceValue (lihat layouting di atas)
            return (JLabel) formContent.getComponent(formContent.getComponentCount() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    private void updatePrice(JLabel priceLabel) {
        if (priceLabel == null)
            return;

        FutsalField f = (FutsalField) cmbField.getSelectedItem();
        if (f != null) {
            double total = f.getPricePerSession() * selectedTimeSlots.size();
            priceLabel.setText(String.format("Rp %,d", (int) total));
        }
    }

    // --- LOGIC: BOOKING & QR POPUP ---
    private void processBookingAttempt(JLabel priceLbl) {
        if (!bookingService.isFutsalOpen()) {
            JOptionPane.showMessageDialog(this, "Maaf, toko sedang tutup.", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedTimeSlots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu slot jam!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FutsalField selectedField = (FutsalField) cmbField.getSelectedItem();
        double totalPrice = selectedField.getPricePerSession() * selectedTimeSlots.size();

        showPaymentDialog(totalPrice);
    }

    private void showPaymentDialog(double amount) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pembayaran QRIS", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JLabel lblTotal = new JLabel("Total Bayar: Rp " + (int) amount, SwingConstants.CENTER);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        ImageIcon qrIcon = new ImageIcon(new ImageIcon("assets/images/qris_dummy.png")
                .getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH));
        JLabel lblQr = new JLabel(qrIcon);
        lblQr.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblInfo = new JLabel(
                "<html><center>Scan QR Code di atas.<br>Klik tombol di bawah JIKA sudah transfer.</center></html>",
                SwingConstants.CENTER);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnConfirm = new JButton("Saya Sudah Transfer");
        btnConfirm.setBackground(Color.decode(ini.warna_isi));
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirm.addActionListener(e -> {
            dialog.dispose();
            saveBookingsToDatabase(); // PROSES PENYIMPANAN
        });

        dialog.add(lblTotal, BorderLayout.NORTH);
        dialog.add(lblQr, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(lblInfo, BorderLayout.NORTH);
        bottomPanel.add(btnConfirm, BorderLayout.SOUTH);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void saveBookingsToDatabase() {
        FutsalField selectedField = (FutsalField) cmbField.getSelectedItem();
        Date selectedDate = (Date) spinDate.getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(selectedDate);

        new Thread(() -> {
            boolean allSuccess = true;
            List<String> failedSlots = new ArrayList<>();

            for (String startTime : selectedTimeSlots) {
                int startH = Integer.parseInt(startTime.substring(0, 2));
                String endTimeStr = String.format("%02d:00:00", startH + 1);
                String startTimeStr = startTime + ":00";

                Booking booking = new Booking();
                booking.setUserId(currentUser.getId());
                booking.setFieldId(selectedField.getId());
                booking.setBookingDate(dateStr);
                booking.setStartTime(startTimeStr);
                booking.setEndTime(endTimeStr);
                booking.setTotalPrice(selectedField.getPricePerSession());

                // STATUS PENDING
                booking.setStatus("PENDING");

                boolean success = bookingService.createBooking(booking);

                if (!success) {
                    allSuccess = false;
                    failedSlots.add(startTime);
                }
            }

            boolean finalSuccess = allSuccess;
            SwingUtilities.invokeLater(() -> {
                if (finalSuccess) {
                    JOptionPane.showMessageDialog(this,
                            "Booking berhasil! Menunggu konfirmasi Admin.",
                            "Status Pending",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadHistoryData();
                    refreshUserStats(); // UPDATE STATS SETELAH BOOKING
                    cardLayout.show(mainContentPanel, "HISTORY");
                    setActiveButton(btnHistory);
                    updateTimeSlots();
                } else {
                    String msg = "Sebagian booking GAGAL disimpan karena slot berikut baru saja diambil orang lain:\n"
                            + String.join(", ", failedSlots)
                            + "\n\nSilakan pilih jam lain.";
                    JOptionPane.showMessageDialog(this, msg, "Gagal Sebagian", JOptionPane.WARNING_MESSAGE);
                    loadHistoryData();
                    updateTimeSlots();
                }
            });
        }).start();
    }

    // --- Helper UI Methods ---
    private JPanel createHistoryPage() {
        // Halaman History dibungkus panel agar rapi
        JPanel pagePanel = new JPanel(new BorderLayout());
        pagePanel.setOpaque(false);

        // Header simple untuk history
        JLabel lblTitle = new JLabel("Transaction History");
        lblTitle.setFont(new Font(ini.font, Font.BOLD, 24));
        lblTitle.setForeground(Color.DARK_GRAY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        pagePanel.add(lblTitle, BorderLayout.NORTH);

        JPanel card = createModernCardPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        historyTableModel = new BookingsTableModel();
        JTable table = new JTable(historyTableModel);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setFont(new Font(ini.font, Font.PLAIN, 14));
        table.setSelectionBackground(Color.decode(ini.warna_pelengkap));

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font(ini.font, Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh Data");
        styleActionButton(btnRefresh);
        btnRefresh.setPreferredSize(new Dimension(150, 40));
        btnRefresh.addActionListener(e -> loadHistoryData());

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(btnRefresh, BorderLayout.SOUTH);

        pagePanel.add(card, BorderLayout.CENTER);

        loadHistoryData();
        return pagePanel;
    }

    private void loadHistoryData() {
        new Thread(() -> {
            List<Booking> bookings = bookingService.getUserBookings(currentUser.getId());
            SwingUtilities.invokeLater(() -> {
                if (historyTableModel != null) {
                    historyTableModel.setBookings(bookings);
                    historyTableModel.fireTableDataChanged();
                }
            });
        }).start();
    }

    private void setActiveButton(ModernMenuButton activeBtn) {
        btnBooking.setActive(false);
        btnHistory.setActive(false);
        activeBtn.setActive(true);
        repaint();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(ini.font, Font.BOLD, 14));
        l.setForeground(Color.GRAY);
        return l;
    }

    private void styleActionButton(JButton btn) {
        btn.setBackground(Color.decode(ini.warna_isi));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font(ini.font, Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField()
                    .setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(Color.WHITE);
        }
        spinner.setBorder(BorderFactory.createEmptyBorder());
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
    }

    private JPanel createModernCardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200, 100));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                super.paintComponent(g);
            }
        };
    }

    // METHOD BARU: STYLE CARD SEPERTI ADMIN
    private JPanel createBigStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Garis accent di bawah
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
        lblTitle.setFont(new Font(ini.font, Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font(ini.font, Font.BOLD, 24));
        valueLabel.setForeground(Color.DARK_GRAY);

        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }
}