import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserDashboardFrame extends JFrame {

   // =========================================
   // GLOBAL VARIABLES
   // =========================================
   private User currentUser;
   private BookingService bookingService;
   private FieldService fieldService;
   private Tema ini = new Tema(); // Mengambil warna dari file Tema.java

   // Layout Manager
   private CardLayout cardLayout;
   private JPanel mainContentPanel;
   private JLabel pageTitleLabel;

   // Menu Buttons (untuk update status aktif)
   private ModernMenuButton btnBooking;
   private ModernMenuButton btnHistory;

   // Table Model
   private BookingsTableModel historyTableModel;

   public UserDashboardFrame(User user) {
      this.currentUser = user;
      this.bookingService = new BookingService();
      this.fieldService = new FieldService();

      initializeUI();
   }

   private void initializeUI() {
      setTitle("Futsal-GO - User Dashboard");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1280, 720); // Ukuran lebih lebar ala dashboard modern
      setLocationRelativeTo(null);

      // 1. Setup Layout Utama
      setLayout(new BorderLayout());

      // 2. Sidebar (Kiri)
      JPanel sidebar = createSidebar();
      add(sidebar, BorderLayout.WEST);

      // 3. Konten Area (Tengah)
      JPanel contentArea = new JPanel(new BorderLayout());
      contentArea.setBackground(Color.decode(ini.warna_pelengkap)); // Warna abu muda background

      // 3a. Header
      JPanel header = createHeader();
      contentArea.add(header, BorderLayout.NORTH);

      // 3b. Body Dinamis (Card Layout)
      cardLayout = new CardLayout();
      mainContentPanel = new JPanel(cardLayout);
      mainContentPanel.setOpaque(false); // Transparan agar warna background terlihat
      mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Jarak pinggir

      // --- Tambahkan Halaman ---
      mainContentPanel.add(createBookingPage(), "BOOKING");
      mainContentPanel.add(createHistoryPage(), "HISTORY");

      contentArea.add(mainContentPanel, BorderLayout.CENTER);
      add(contentArea, BorderLayout.CENTER);

      // Set halaman awal active
      setActiveButton(btnBooking);
   }

   // =========================================
   // BUILDER METHODS (UI COMPONENTS)
   // =========================================

   private JPanel createSidebar() {
      JPanel sidebar = new JPanel();
      sidebar.setPreferredSize(new Dimension(260, getHeight()));
      sidebar.setBackground(Color.decode(ini.warna_utama)); // Warna Gelap
      sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
      sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

      // Logo
      JLabel logoLabel = new JLabel("Futsal-GO");
      logoLabel.setFont(new Font(ini.font, Font.BOLD, 28));
      logoLabel.setForeground(Color.decode(ini.warna_isi)); // Warna Neon/Emas
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
         // new LoginFrame().setVisible(true); // Uncomment jika LoginFrame ada
         dispose();
      });

      menuContainer.add(btnBooking);
      menuContainer.add(Box.createVerticalStrut(10));
      menuContainer.add(btnHistory);

      // Spacer agar logout di bawah
      sidebar.add(logoLabel);
      sidebar.add(menuContainer);
      sidebar.add(Box.createVerticalGlue()); // Push logout to bottom
      sidebar.add(btnLogout);

      return sidebar;
   }

   private JPanel createHeader() {
      JPanel header = new JPanel(new BorderLayout());
      header.setBackground(Color.decode(ini.warna_pelengkap)); // Sama dengan background
      header.setBorder(new EmptyBorder(20, 30, 0, 30));
      header.setPreferredSize(new Dimension(getWidth(), 70));

      // Judul Halaman
      pageTitleLabel = new JLabel("Book a Field");
      pageTitleLabel.setFont(new Font(ini.font, Font.BOLD, 24));
      pageTitleLabel.setForeground(Color.DARK_GRAY);

      // Profil User
      JLabel userLabel = new JLabel("Hi, " + currentUser.getUsername());
      userLabel.setFont(new Font(ini.font, Font.PLAIN, 16));
      userLabel.setForeground(Color.GRAY);
      userLabel.setIcon(new ImageIcon("assets/user_icon.png")); // Opsional jika ada icon

      header.add(pageTitleLabel, BorderLayout.WEST);
      header.add(userLabel, BorderLayout.EAST);

      return header;
   }

   private JPanel createBookingPage() {
      // Panel utama transparan
      JPanel pagePanel = new JPanel(new BorderLayout(20, 20));
      pagePanel.setOpaque(false);

      // 1. Info Cards (Atas)
      JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
      statsPanel.setOpaque(false);
      statsPanel.setPreferredSize(new Dimension(getWidth(), 120));

      statsPanel.add(new InfoCard("Total Bookings", "0", Color.decode("#4e73df")));
      statsPanel.add(new InfoCard("Active Fields", "3", Color.decode("#1cc88a")));
      statsPanel.add(new InfoCard("Your Level", "Member", Color.decode("#36b9cc")));

      // 2. Booking Form (Card Besar)
      ModernCardPanel formCard = new ModernCardPanel();
      formCard.setLayout(new GridBagLayout()); // Gunakan GBC agar rapi di tengah

      // Komponen Form
      JPanel formContent = new JPanel(new GridLayout(6, 2, 20, 20));
      formContent.setOpaque(false);
      formContent.setPreferredSize(new Dimension(500, 350));

      JLabel lblField = createLabel("Select Field");
      JComboBox<FutsalField> cmbField = new JComboBox<>();
      styleComboBox(cmbField);
      // Load Fields
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
      // Set default time to 08:00
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 8);
      cal.set(Calendar.MINUTE, 0);
      spinTime.setValue(cal.getTime());

      JLabel lblDuration = createLabel("Duration (Hours)");
      Integer[] durations = { 1, 2, 3, 4, 5 };
      JComboBox<Integer> cmbDuration = new JComboBox<>(durations);
      styleComboBox(cmbDuration);

      JLabel lblPrice = createLabel("Total Price");
      JLabel lblPriceValue = new JLabel("Rp 0");
      lblPriceValue.setFont(new Font(ini.font, Font.BOLD, 20));
      lblPriceValue.setForeground(Color.decode(ini.warna_isi));

      // Logic Harga
      cmbDuration.addActionListener(e -> updatePrice(cmbField, cmbDuration, lblPriceValue));
      cmbField.addActionListener(e -> updatePrice(cmbField, cmbDuration, lblPriceValue));

      // Trigger calc awal
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
      ModernCardPanel card = new ModernCardPanel();
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

      loadHistoryData(); // Load awal
      return card;
   }

   // =========================================
   // HELPER LOGIC
   // =========================================

   private void setActiveButton(ModernMenuButton activeBtn) {
      btnBooking.setActive(false);
      btnHistory.setActive(false);
      activeBtn.setActive(true);
      repaint();
   }

   private void updatePrice(JComboBox fieldBox, JComboBox durBox, JLabel priceLabel) {
      FutsalField f = (FutsalField) fieldBox.getSelectedItem();
      int d = (Integer) durBox.getSelectedItem();
      if (f != null) {
         double total = f.getPricePerSession() * d;
         priceLabel.setText(String.format("Rp %,d", (int) total));
      }
   }

   private void processBooking(JComboBox fieldBox, JSpinner dateSp, JSpinner timeSp, JComboBox durBox,
         JLabel priceLbl) {
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

   // --- STYLING HELPERS ---
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

   private void styleComboBox(JComboBox box) {
      box.setBackground(Color.WHITE);
      box.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
   }

   // =========================================
   // INNER CLASSES (CUSTOM COMPONENTS)
   // =========================================

   // 1. Tombol Menu Flat Modern
   class ModernMenuButton extends JButton {
      private boolean isActive = false;
      private Color hoverColor = new Color(255, 255, 255, 20);

      public ModernMenuButton(String text, boolean isActive) {
         super(text);
         this.isActive = isActive;
         setFont(new Font(ini.font, Font.PLAIN, 16));
         setForeground(Color.WHITE);
         setBackground(Color.decode(ini.warna_utama));
         setBorder(new EmptyBorder(10, 30, 10, 10)); // Padding kiri lega
         setHorizontalAlignment(SwingConstants.LEFT);
         setFocusPainted(false);
         setContentAreaFilled(false);
         setCursor(new Cursor(Cursor.HAND_CURSOR));
         setMaximumSize(new Dimension(260, 50));

         addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
               setForeground(Color.decode(ini.warna_isi));
            }

            public void mouseExited(MouseEvent e) {
               setForeground(isActive ? Color.decode(ini.warna_isi) : Color.WHITE);
            }
         });
      }

      public void setActive(boolean active) {
         this.isActive = active;
         setForeground(active ? Color.decode(ini.warna_isi) : Color.WHITE);
         repaint();
      }

      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         if (isActive) {
            // Gambar indikator di kiri
            g2.setColor(Color.decode(ini.warna_isi));
            g2.fillRect(0, 5, 5, getHeight() - 10);

            // Background halus
            g2.setColor(hoverColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
         }
         super.paintComponent(g);
      }
   }

   // 2. Card Panel (Kotak Putih dengan Shadow)
   class ModernCardPanel extends JPanel {
      public ModernCardPanel() {
         setOpaque(false);
      }

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
   }

   // 3. Info Widget Kecil
   class InfoCard extends ModernCardPanel {
      public InfoCard(String title, String value, Color accent) {
         setLayout(new BorderLayout());
         setBorder(new EmptyBorder(15, 20, 15, 20));

         JLabel lblTitle = new JLabel(title);
         lblTitle.setFont(new Font(ini.font, Font.PLAIN, 14));
         lblTitle.setForeground(Color.GRAY);

         JLabel lblValue = new JLabel(value);
         lblValue.setFont(new Font(ini.font, Font.BOLD, 24));
         lblValue.setForeground(Color.DARK_GRAY);

         JPanel bar = new JPanel();
         bar.setBackground(accent);
         bar.setPreferredSize(new Dimension(5, 40));

         add(lblTitle, BorderLayout.NORTH);
         add(lblValue, BorderLayout.CENTER);
         add(bar, BorderLayout.WEST);
      }
   }
}