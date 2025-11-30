import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConfig {

   // Database Config
   private static final String URL = "jdbc:mysql://localhost:3306/futsal_management";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "";

   private Connection getConnection() throws SQLException {
      return DriverManager.getConnection(URL, USERNAME, PASSWORD);
   }

   // =========================================================================
   // 1. SETTINGS & TOKO
   // =========================================================================

   public boolean isFutsalOpen() {
      String sql = "SELECT setting_value FROM app_settings WHERE setting_key = 'shop_status'";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         ResultSet rs = pstmt.executeQuery();
         if (rs.next()) {
            return "OPEN".equalsIgnoreCase(rs.getString("setting_value"));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return true; // Default OPEN
   }

   public boolean setFutsalStatus(boolean isOpen) {
      String status = isOpen ? "OPEN" : "CLOSED";
      String sql = "INSERT INTO app_settings (setting_key, setting_value) VALUES ('shop_status', ?) " +
            "ON DUPLICATE KEY UPDATE setting_value = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, status);
         pstmt.setString(2, status);
         return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   // =========================================================================
   // 2. USER AUTHENTICATION
   // =========================================================================

   public boolean registerUser(String username, String password, String email, String userLevel) {
      String sql = "INSERT INTO users (username, password, email, user_level) VALUES (?, ?, ?, ?)";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, username);
         pstmt.setString(2, password);
         pstmt.setString(3, email);
         pstmt.setString(4, userLevel);
         return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public User authenticateUser(String username, String password) {
      String sql = "SELECT * FROM users WHERE username = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, username);
         ResultSet rs = pstmt.executeQuery();
         if (rs.next()) {
            if (rs.getString("password").equals(password)) {
               return new User(
                     rs.getInt("id"),
                     rs.getString("username"),
                     rs.getString("password"),
                     rs.getString("email"),
                     rs.getString("user_level"),
                     rs.getString("created_at"));
            }
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return null;
   }

   public boolean isUsernameExists(String username) {
      String sql = "SELECT id FROM users WHERE username = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, username);
         return pstmt.executeQuery().next();
      } catch (SQLException e) {
         return false;
      }
   }

   public boolean isEmailExists(String email) {
      String sql = "SELECT id FROM users WHERE email = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, email);
         return pstmt.executeQuery().next();
      } catch (SQLException e) {
         return false;
      }
   }

   // =========================================================================
   // 3. BOOKING CORE (UPDATED: TRANSACTION & RACE CONDITION FIX)
   // =========================================================================

   public List<String> getOccupiedTimeSlots(int fieldId, String date) {
      List<String> slots = new ArrayList<>();
      String sql = "SELECT DATE_FORMAT(start_time, '%H:%i') as slot_time FROM bookings " +
            "WHERE field_id = ? AND booking_date = ? AND status IN ('PENDING', 'PAID') " +
            "ORDER BY start_time";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setInt(1, fieldId);
         pstmt.setString(2, date);
         ResultSet rs = pstmt.executeQuery();
         while (rs.next()) {
            slots.add(rs.getString("slot_time"));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return slots;
   }

   /**
    * [UPDATE] Menggunakan TRANSAKSI DATABASE untuk mencegah Double Booking (Race
    * Condition).
    * Proses Cek Ketersediaan dan Insert Data dilakukan dalam satu koneksi yang
    * terkunci.
    */
   public boolean createBooking(Booking booking) {
      String startTimeFormatted = formatTimeStr(booking.getStartTime());
      String endTimeFormatted = formatTimeStr(booking.getEndTime());

      // Query Cek: Hitung booking yang overlap
      String checkSql = "SELECT COUNT(*) FROM bookings " +
            "WHERE field_id = ? AND booking_date = ? " +
            "AND status IN ('PAID', 'PENDING') " +
            "AND (start_time < ? AND end_time > ?)";

      String insertSql = "INSERT INTO bookings (user_id, field_id, booking_date, start_time, end_time, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

      Connection conn = null;
      PreparedStatement checkStmt = null;
      PreparedStatement insertStmt = null;

      try {
         conn = getConnection();
         // 1. MULAI TRANSAKSI (Matikan Auto-Commit)
         conn.setAutoCommit(false);

         // 2. CEK KETERSEDIAAN (Dalam Transaksi)
         checkStmt = conn.prepareStatement(checkSql);
         checkStmt.setInt(1, booking.getFieldId());
         checkStmt.setString(2, booking.getBookingDate());
         checkStmt.setString(3, endTimeFormatted);
         checkStmt.setString(4, startTimeFormatted);

         ResultSet rs = checkStmt.executeQuery();
         if (rs.next() && rs.getInt(1) > 0) {
            // JIKA SUDAH ADA YANG BOOKING DI DETIK YANG SAMA
            conn.rollback(); // Batalkan transaksi
            System.out.println("DEBUG: Slot Conflict Detected. Rollback transaction.");
            return false;
         }

         // 3. INSERT DATA (Jika Aman)
         insertStmt = conn.prepareStatement(insertSql);
         insertStmt.setInt(1, booking.getUserId());
         insertStmt.setInt(2, booking.getFieldId());
         insertStmt.setString(3, booking.getBookingDate());
         insertStmt.setString(4, startTimeFormatted);
         insertStmt.setString(5, endTimeFormatted);
         insertStmt.setDouble(6, booking.getTotalPrice());
         insertStmt.setString(7, booking.getStatus());

         int rows = insertStmt.executeUpdate();

         if (rows > 0) {
            // 4. KOMIT TRANSAKSI (Simpan Permanen)
            conn.commit();
            return true;
         } else {
            conn.rollback();
            return false;
         }

      } catch (SQLException e) {
         e.printStackTrace();
         if (conn != null) {
            try {
               conn.rollback(); // Rollback jika ada error SQL
            } catch (SQLException ex) {
               ex.printStackTrace();
            }
         }
         return false;
      } finally {
         // Tutup semua resource dengan rapi
         try {
            if (checkStmt != null)
               checkStmt.close();
            if (insertStmt != null)
               insertStmt.close();
            if (conn != null) {
               conn.setAutoCommit(true); // Kembalikan ke default
               conn.close();
            }
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
   }

   // Method checkAvailability biasa (untuk keperluan UI / non-transaksi)
   public boolean checkAvailability(int fieldId, String date, String startTime, String endTime) {
      String start = formatTimeStr(startTime);
      String end = formatTimeStr(endTime);

      String sql = "SELECT COUNT(*) FROM bookings " +
            "WHERE field_id = ? AND booking_date = ? " +
            "AND status IN ('PAID', 'PENDING') " +
            "AND (start_time < ? AND end_time > ?)";

      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setInt(1, fieldId);
         pstmt.setString(2, date);
         pstmt.setString(3, end);
         pstmt.setString(4, start);
         ResultSet rs = pstmt.executeQuery();
         if (rs.next()) {
            return rs.getInt(1) == 0;
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return false;
   }

   private String formatTimeStr(String time) {
      if (time.length() == 5) {
         return time + ":00";
      }
      return time;
   }

   public List<Booking> getUserBookings(int userId) {
      List<Booking> bookings = new ArrayList<>();
      String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC, start_time DESC";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setInt(1, userId);
         ResultSet rs = pstmt.executeQuery();
         while (rs.next()) {
            bookings.add(mapResultSetToBooking(rs));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return bookings;
   }

   public int countUserPaidBookings(int userId) {
      String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status = 'PAID'";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setInt(1, userId);
         ResultSet rs = pstmt.executeQuery();
         if (rs.next()) {
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return 0;
   }

   // =========================================================================
   // 4. ADMIN & REPORTING (UPDATED: TOTAL REVENUE ALL TIME)
   // =========================================================================

   public List<Booking> getAllBookings() {
      List<Booking> bookings = new ArrayList<>();
      String sql = "SELECT b.*, u.username, f.field_name " +
            "FROM bookings b " +
            "JOIN users u ON b.user_id = u.id " +
            "JOIN futsal_fields f ON b.field_id = f.id " +
            "ORDER BY b.booking_date DESC, b.start_time DESC";

      try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            Booking b = new Booking(
                  rs.getInt("id"),
                  rs.getInt("user_id"),
                  rs.getString("username"),
                  rs.getInt("field_id"),
                  rs.getString("field_name"),
                  rs.getString("booking_date"),
                  rs.getString("start_time"),
                  rs.getString("end_time"),
                  rs.getDouble("total_price"),
                  rs.getString("status"),
                  rs.getString("created_at"));
            bookings.add(b);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return bookings;
   }

   public List<Booking> getTransactionsByDate(java.util.Date startDate, java.util.Date endDate) {
      List<Booking> bookings = new ArrayList<>();
      java.sql.Date sqlStart = new java.sql.Date(startDate.getTime());
      java.sql.Date sqlEnd = new java.sql.Date(endDate.getTime());

      String sql = "SELECT * FROM bookings WHERE booking_date BETWEEN ? AND ? ORDER BY booking_date DESC, start_time ASC";

      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setDate(1, sqlStart);
         pstmt.setDate(2, sqlEnd);
         ResultSet rs = pstmt.executeQuery();
         while (rs.next()) {
            bookings.add(mapResultSetToBooking(rs));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return bookings;
   }

   public boolean updateBookingStatus(int bookingId, String status) {
      String sql = "UPDATE bookings SET status = ? WHERE id = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, status);
         pstmt.setInt(2, bookingId);
         return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public Map<String, Object> getDashboardStats() {
      Map<String, Object> stats = new HashMap<>();

      // 1. Revenue Hari Ini
      String sqlRevToday = "SELECT IFNULL(SUM(total_price), 0) FROM bookings WHERE status = 'PAID' AND DATE(created_at) = CURDATE()";

      // [BARU] 2. Revenue Total (Semua Waktu)
      String sqlRevTotal = "SELECT IFNULL(SUM(total_price), 0) FROM bookings WHERE status = 'PAID'";

      // 3. Total Booking (Paid)
      String sqlBook = "SELECT COUNT(*) FROM bookings WHERE status = 'PAID'";

      // 4. Total User
      String sqlUser = "SELECT COUNT(*) FROM users WHERE user_level = 'user'";

      // 5. Active Fields
      String sqlField = "SELECT COUNT(*) FROM futsal_fields WHERE is_active = TRUE";

      try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
         ResultSet rs = stmt.executeQuery(sqlBook);
         if (rs.next())
            stats.put("TotalBookings", rs.getInt(1));

         rs = stmt.executeQuery(sqlUser);
         if (rs.next())
            stats.put("TotalUsers", rs.getInt(1));

         rs = stmt.executeQuery(sqlField);
         if (rs.next())
            stats.put("ActiveFields", rs.getInt(1));

         rs = stmt.executeQuery(sqlRevToday);
         if (rs.next())
            stats.put("RevenueToday", rs.getDouble(1));

         // [BARU] Eksekusi Query Total Revenue All Time
         rs = stmt.executeQuery(sqlRevTotal);
         if (rs.next())
            stats.put("TotalRevenueAllTime", rs.getDouble(1));

      } catch (SQLException e) {
         e.printStackTrace();
      }
      return stats;
   }

   // =========================================================================
   // 5. CRUD LAPANGAN
   // =========================================================================

   public List<FutsalField> getAllFields() {
      List<FutsalField> fields = new ArrayList<>();
      String sql = "SELECT * FROM futsal_fields ORDER BY field_name";
      try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            fields.add(new FutsalField(
                  rs.getInt("id"), rs.getString("field_name"), rs.getString("open_time"),
                  rs.getString("close_time"), rs.getDouble("price_per_session"),
                  rs.getBoolean("is_active"), rs.getString("created_at"), rs.getString("updated_at")));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return fields;
   }

   public boolean isFieldNameExists(String fieldName, int excludeId) {
      String sql = "SELECT id FROM futsal_fields WHERE field_name = ? AND id != ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, fieldName);
         pstmt.setInt(2, excludeId);
         return pstmt.executeQuery().next();
      } catch (SQLException e) {
         return false;
      }
   }

   public boolean addField(FutsalField field) {
      String sql = "INSERT INTO futsal_fields (field_name, open_time, close_time, price_per_session) VALUES (?, ?, ?, ?)";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, field.getFieldName());
         pstmt.setString(2, field.getOpenTime());
         pstmt.setString(3, field.getCloseTime());
         pstmt.setDouble(4, field.getPricePerSession());
         return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public boolean updateField(FutsalField field) {
      String sql = "UPDATE futsal_fields SET field_name = ?, open_time = ?, close_time = ?, price_per_session = ?, is_active = ? WHERE id = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setString(1, field.getFieldName());
         pstmt.setString(2, field.getOpenTime());
         pstmt.setString(3, field.getCloseTime());
         pstmt.setDouble(4, field.getPricePerSession());
         pstmt.setBoolean(5, field.isActive());
         pstmt.setInt(6, field.getId());
         return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public boolean deleteField(int id) {
      String sql = "DELETE FROM futsal_fields WHERE id = ?";
      try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
         pstmt.setInt(1, id);
         return pstmt.executeUpdate() > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   // =========================================================================
   // HELPER
   // =========================================================================

   public List<User> getAllUsers() {
      List<User> users = new ArrayList<>();
      String sql = "SELECT * FROM users ORDER BY user_level DESC, username ASC";
      try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            users.add(new User(
                  rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                  rs.getString("email"), rs.getString("user_level"), rs.getString("created_at")));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return users;
   }

   private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
      return new Booking(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("field_id"),
            rs.getString("booking_date"),
            rs.getString("start_time"),
            rs.getString("end_time"),
            rs.getDouble("total_price"),
            rs.getString("status"),
            rs.getString("created_at"));
   }
}