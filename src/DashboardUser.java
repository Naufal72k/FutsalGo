import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public DashboardUser(App app, User user) {
        this.currentUser = user;
        this.app = app;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1280, 720));

        // Sidebar (Kiri)
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Konten Area (Tengah)
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.decode(ini.warna_pelengkap));

        // Header
        JPanel header = createHeader();
        contentArea.add(header, BorderLayout.NORTH);

        // Body Dinamis (Card Layout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tambahkan Halaman
        mainContentPanel.add(createBookingPage(), "BOOKING");
        mainContentPanel.add(createHistoryPage(), "HISTORY");

        contentArea.add(mainContentPanel, BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);

        // Set halaman awal active
        setActiveButton(btnBooking);
    }

    // BUILDER METHODS (UI COMPONENTS)

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 720));
        sidebar.setBackground(Color.decode(ini.warna_utama));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Logo
        JLabel logoLabel = new JLabel("Futsal-GO");
        logoLabel.setFont(new Font(ini.font, Font.BOLD, 28));
        logoLabel.setForeground(Color.decode(ini.warna_isi));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Menu Container
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(50, 0, 0, 0));

        btnBooking = new ModernMenuButton("Book Field", true);
        btnHistory = new ModernMenuButton("My History", false);
        ModernMenuButton btnLogout = new ModernMenuButton("Logout", false);

        // Action Listeners
        btnBooking.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "BOOKING");
            pageTitleLabel.setText("Book a Field");
            setActiveButton(btnBooking);
        });

        btnHistory.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "HISTORY");
            pageTitleLabel.setText("Transaction History");
            loadHistoryData();
            setActiveButton(btnHistory);
        });

        btnLogout.addActionListener(e -> {
            // Trigger logout
            app.showLogin();
        });

        menuContainer.add(btnBooking);
        menuContainer.add(Box.createVerticalStrut(10));
        menuContainer.add(btnHistory);

        sidebar.add(logoLabel);
        sidebar.add(menuContainer);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.decode(ini.warna_pelengkap));
        header.setBorder(new EmptyBorder(20, 30, 0, 30));
        header.setPreferredSize(new Dimension(1020, 70));

        // Judul Halaman
        pageTitleLabel = new JLabel("Book a Field");
        pageTitleLabel.setFont(new Font(ini.font, Font.BOLD, 24));
        pageTitleLabel.setForeground(Color.DARK_GRAY);

        // Profil User
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

        // Info Cards (Atas)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(1020, 120));

        statsPanel.add(createInfoCard("Total Bookings", "0", Color.decode("#4e73df")));
        statsPanel.add(createInfoCard("Active Fields", "3", Color.decode("#1cc88a")));
        statsPanel.add(createInfoCard("Your Level", "Member", Color.decode("#36b9cc")));

        // Booking Form (Card Besar)
        JPanel formCard = createModernCardPanel();
        formCard.setLayout(new GridBagLayout());

        // Komponen Form
        JPanel formContent = new JPanel(new GridLayout(6, 2, 20, 20));
        formContent.setOpaque(false);
        formContent.setPreferredSize(new Dimension(500, 350));

        JLabel lblField = createLabel("Select Field");
        JComboBox<FutsalField> cmbField = new JComboBox<>();
        styleComboBox(cmbField);
        
        List<FutsalField> fields = fieldService.getAllFields();
        for (FutsalField f : fields)
            if (f.isActive())
                cmbField.addItem(f);

        JLabel lblDate = createLabel("Date");
        JSpinner spinDate = new JSpinner(new SpinnerDateModel());
        spinDate.setEditor(new JSpinner.DateEditor(spinDate, "yyyy-MM-dd"));
        styleSpinner(spinDate);

        JLabel lblTime = createLabel("Start Time");
        JSpinner spinTime = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinTime, "HH:mm");
        spinTime.setEditor(timeEditor);
        styleSpinner(spinTime);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        spinTime.setValue(cal.getTime());

        JLabel lblDuration = createLabel("Duration (Hours)");
        Integer[] durations = {1, 2, 3, 4, 5};
        JComboBox<Integer> cmbDuration = new JComboBox<>(durations);
        styleComboBox(cmbDuration);

        JLabel lblPrice = createLabel("Total Price");
        JLabel lblPriceValue = new JLabel("Rp 0");
        lblPriceValue.setFont(new Font(ini.font, Font.BOLD, 20));
        lblPriceValue.setForeground(Color.decode(ini.warna_isi));

        // Logic Harga
        cmbDuration.addActionListener(e -> updatePrice(cmbField, cmbDuration, lblPriceValue));
        cmbField.addActionListener(e -> updatePrice(cmbField, cmbDuration, lblPriceValue));

        if (cmbField.getItemCount() > 0)
            cmbField.setSelectedIndex(0);

        formContent.add(lblField);
        formContent.add(cmbField);
        formContent.add(lblDate);
        formContent.add(spinDate);
        formContent.add(lblTime);
        formContent.add(spinTime);
        formContent.add(lblDuration);
        formContent.add(cmbDuration);
        formContent.add(lblPrice);
        formContent.add(lblPriceValue);

        // Tombol Submit
        JButton btnSubmit = new JButton("Book Now");
        styleActionButton(btnSubmit);
        btnSubmit.addActionListener(e -> processBooking(cmbField, spinDate, spinTime, cmbDuration, lblPriceValue));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        formCard.add(formContent, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 0, 0);
        formCard.add(btnSubmit, gbc);

        pagePanel.add(statsPanel, BorderLayout.NORTH);
        pagePanel.add(formCard, BorderLayout.CENTER);

        return pagePanel;
    }

    private JPanel createHistoryPage() {
        JPanel card = createModernCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tabel
        historyTableModel = new BookingsTableModel();
        JTable table = new JTable(historyTableModel);

        // Styling Tabel Modern
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setFont(new Font(ini.font, Font.PLAIN, 14));
        table.setSelectionBackground(Color.decode(ini.warna_pelengkap));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font(ini.font, Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Refresh Button
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

    // HELPER LOGIC

    private void setActiveButton(ModernMenuButton activeBtn) {
        btnBooking.setActive(false);
        btnHistory.setActive(false);
        activeBtn.setActive(true);
        repaint();
    }

    private void updatePrice(JComboBox<FutsalField> fieldBox, JComboBox<Integer> durBox, JLabel priceLabel) {
        FutsalField f = (FutsalField) fieldBox.getSelectedItem();
        int d = (Integer) durBox.getSelectedItem();
        if (f != null) {
            double total = f.getPricePerSession() * d;
            priceLabel.setText(String.format("Rp %,d", (int) total));
        }
    }

    private void processBooking(JComboBox<FutsalField> fieldBox, JSpinner dateSp, JSpinner timeSp,
                                JComboBox<Integer> durBox, JLabel priceLbl) {
        FutsalField selectedField = (FutsalField) fieldBox.getSelectedItem();
        if (selectedField == null)
            return;

        Date selectedDate = (Date) dateSp.getValue();
        Date selectedTime = (Date) timeSp.getValue();
        int duration = (Integer) durBox.getSelectedItem();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(selectedDate);
        String startTimeStr = timeFormat.format(selectedTime);

        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedTime);
        cal.add(Calendar.HOUR_OF_DAY, duration);
        String endTimeStr = timeFormat.format(cal.getTime());

        double totalPrice = selectedField.getPricePerSession() * duration;

        new Thread(() -> {
            boolean isAvailable = bookingService.checkAvailability(selectedField.getId(), dateStr, startTimeStr,
                    endTimeStr);

            SwingUtilities.invokeLater(() -> {
                if (!isAvailable) {
                    JOptionPane.showMessageDialog(this, "Jadwal sudah terisi! Pilih jam lain.", "Gagal",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    Booking newBooking = new Booking();
                    newBooking.setUserId(currentUser.getId());
                    newBooking.setFieldId(selectedField.getId());
                    newBooking.setBookingDate(dateStr);
                    newBooking.setStartTime(startTimeStr);
                    newBooking.setEndTime(endTimeStr);
                    newBooking.setTotalPrice(totalPrice);
                    newBooking.setStatus("PENDING");

                    boolean success = bookingService.createBooking(newBooking);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Booking Berhasil! Silakan lakukan pembayaran.", "Sukses",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadHistoryData();
                        cardLayout.show(mainContentPanel, "HISTORY");
                        setActiveButton(btnHistory);
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal membuat booking.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }).start();
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

    // STYLING HELPERS

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

                // Shadow
                g2.setColor(new Color(200, 200, 200, 100));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);

                // Main Box (White)
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