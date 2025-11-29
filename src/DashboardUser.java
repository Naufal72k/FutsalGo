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
    private JLabel pageTitleLabel;

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

        // Konten Area
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.decode(ini.warna_pelengkap));
        contentArea.add(createHeader(), BorderLayout.NORTH);

        // Body Dinamis
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

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

        // UPDATE: Custom Style untuk Tombol Logout (Merah Kotak)
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font(ini.font, Font.BOLD, 16));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(231, 76, 60)); // Merah
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setMaximumSize(new Dimension(220, 45));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tambahkan sedikit efek hover sederhana untuk logout
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(192, 57, 43)); // Merah lebih gelap
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(231, 76, 60)); // Kembali merah cerah
            }
        });

        btnBooking.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "BOOKING");
            pageTitleLabel.setText("Book a Field");
            setActiveButton(btnBooking);
            updateTimeSlots(); // Refresh grid saat pindah tab
        });

        btnHistory.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "HISTORY");
            pageTitleLabel.setText("Transaction History");
            loadHistoryData();
            setActiveButton(btnHistory);
        });

        btnLogout.addActionListener(e -> app.showLogin());

        menuContainer.add(btnBooking);
        menuContainer.add(Box.createVerticalStrut(10));
        menuContainer.add(btnHistory);

        sidebar.add(logoLabel);
        sidebar.add(menuContainer);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout); // Tambahkan tombol logout yang baru

        return sidebar;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.decode(ini.warna_pelengkap));
        header.setBorder(new EmptyBorder(20, 30, 0, 30));
        header.setPreferredSize(new Dimension(1020, 70));

        pageTitleLabel = new JLabel("Book a Field");
        pageTitleLabel.setFont(new Font(ini.font, Font.BOLD, 24));
        pageTitleLabel.setForeground(Color.DARK_GRAY);

        JLabel userLabel = new JLabel("Hi, " + currentUser.getUsername());
        userLabel.setFont(new Font(ini.font, Font.PLAIN, 16));
        userLabel.setForeground(Color.GRAY);

        header.add(pageTitleLabel, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createBookingPage() {
        JPanel pagePanel = new JPanel(new BorderLayout(20, 20));
        pagePanel.setOpaque(false);

        // Info Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(1020, 120));

        statsPanel.add(createInfoCard("Total Bookings", "0", Color.decode("#4e73df")));
        statsPanel.add(createInfoCard("Active Fields", "3", Color.decode("#1cc88a")));
        statsPanel.add(createInfoCard("Your Level", "Member", Color.decode("#36b9cc")));

        // Booking Form Area
        JPanel formCard = createModernCardPanel();
        formCard.setLayout(new GridBagLayout());

        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Pilih Lapangan
        JLabel lblField = createLabel("Select Field");
        cmbField = new JComboBox<>();
        styleComboBox(cmbField);
        List<FutsalField> fields = fieldService.getAllFields();
        for (FutsalField f : fields)
            if (f.isActive())
                cmbField.addItem(f);

        // 2. Pilih Tanggal
        JLabel lblDate = createLabel("Date");
        spinDate = new JSpinner(new SpinnerDateModel());
        spinDate.setEditor(new JSpinner.DateEditor(spinDate, "yyyy-MM-dd"));
        styleSpinner(spinDate);

        // 3. Grid Jadwal
        JLabel lblTime = createLabel("Select Time Slots (Multi-select allowed)");
        timeGridPanel = new JPanel(new GridLayout(3, 5, 8, 8)); // 3 Baris x 5 Kolom (15 Jam)
        timeGridPanel.setOpaque(false);

        // 4. Total Harga
        JLabel lblPrice = createLabel("Total Price");
        JLabel lblPriceValue = new JLabel("Rp 0");
        lblPriceValue.setFont(new Font(ini.font, Font.BOLD, 20));
        lblPriceValue.setForeground(Color.decode(ini.warna_isi));

        // --- Logic Listener ---
        cmbField.addActionListener(e -> {
            updateTimeSlots();
            updatePrice(lblPriceValue);
        });
        spinDate.addChangeListener(e -> updateTimeSlots());

        // Layouting Komponen
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

        // Tombol Book Now
        JButton btnSubmit = new JButton("Book Now");
        styleActionButton(btnSubmit);
        btnSubmit.addActionListener(e -> processBookingAttempt(lblPriceValue));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        formCard.add(formContent, cardGbc);
        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(20, 0, 0, 0);
        formCard.add(btnSubmit, cardGbc);

        pagePanel.add(statsPanel, BorderLayout.NORTH);
        pagePanel.add(formCard, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::updateTimeSlots);

        return pagePanel;
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
            Container parent = timeGridPanel.getParent();
            return (JLabel) parent.getComponent(7);
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

                // UPDATE: SET STATUS MENJADI PENDING (Menunggu Admin)
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
                    // UPDATE: Pesan Sukses Menunggu Admin
                    JOptionPane.showMessageDialog(this,
                            "Booking berhasil! Menunggu konfirmasi Admin.",
                            "Status Pending",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadHistoryData();
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
        JPanel card = createModernCardPanel();
        card.setLayout(new BorderLayout(20, 20));
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

        card.add(new JLabel("Your Previous Matches"), BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        card.add(btnRefresh, BorderLayout.SOUTH);

        loadHistoryData();
        return card;
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

    private JPanel createInfoCard(String title, String value, Color accent) {
        JPanel card = createModernCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(ini.font, Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font(ini.font, Font.BOLD, 24));
        lblValue.setForeground(Color.DARK_GRAY);
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(5, 40));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(bar, BorderLayout.WEST);
        return card;
    }
}