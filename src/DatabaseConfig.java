import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConfig {

   // Database Config
   private static final String URL = "jdbc:mysql://localhost:3306/futsal_management";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "";

   private static String getUrl() {
      return URL;
   }

   private static String getUsername() {
      return USERNAME;
   }

   private static String getPassword() {
      return PASSWORD;
   }

   // Insert / Register User
   public boolean registerUser(String username, String password, String email, String userLevel) {
      String sql = "INSERT INTO users (username, password, email, user_level) VALUES (?, ?, ?, ?)";

      try (Connection conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
           PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, username);
         pstmt.setString(2, password);
         pstmt.setString(3, email);
         pstmt.setString(4, userLevel);

         int affectedRows = pstmt.executeUpdate();
         return affectedRows > 0;

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   // Login 
   public User authenticateUser(String username, String password) {
      String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

      try (Connection conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
           PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, username);
         pstmt.setString(2, password);

         ResultSet rs = pstmt.executeQuery();

         if (rs.next()) {
            return new User(
                  rs.getInt("id"),
                  rs.getString("username"),
                  rs.getString("password"),
                  rs.getString("email"),
                  rs.getString("user_level"),
                  rs.getString("created_at"));
         }

      } catch (SQLException e) {
         e.printStackTrace();
      }

      return null;
   }

   // Check username sudah ada atau belum
   public boolean isUsernameExists(String username) {
      String sql = "SELECT id FROM users WHERE username = ?";

      try (Connection conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
           PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, username);
         ResultSet rs = pstmt.executeQuery();
         return rs.next();

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   // Check email sudah ada atau belum
   public boolean isEmailExists(String email) {
      String sql = "SELECT id FROM users WHERE email = ?";

      try (Connection conn = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
           PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, email);
         ResultSet rs = pstmt.executeQuery();
         return rs.next();

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   
    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, field_id, booking_date, start_time, end_time, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword());
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, booking.getUserId());
            pstmt.setInt(2, booking.getFieldId());
            pstmt.setString(3, booking.getBookingDate());
            pstmt.setString(4, booking.getStartTime());
            pstmt.setString(5, booking.getEndTime());
            pstmt.setDouble(6, booking.getTotalPrice());
            pstmt.setString(7, "PENDING");

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC, start_time DESC";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword());
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("field_id"),
                        rs.getString("booking_date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getString("created_at"));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public boolean checkAvailability(int fieldId, String date, String startTime, String endTime) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE field_id = ? AND booking_date = ? AND status != 'CANCELLED' AND (start_time < ? AND end_time > ?)";

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUsername(),
                DatabaseConfig.getPassword());
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fieldId);
            pstmt.setString(2, date);
            pstmt.setString(3, endTime);
            pstmt.setString(4, startTime);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

   public boolean addField(FutsalField field) {
      String sql = "INSERT INTO futsal_fields (field_name, open_time, close_time, price_per_session) VALUES (?, ?, ?, ?)";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, field.getFieldName());
         pstmt.setString(2, field.getOpenTime());
         pstmt.setString(3, field.getCloseTime());
         pstmt.setDouble(4, field.getPricePerSession());

         int affectedRows = pstmt.executeUpdate();
         return affectedRows > 0;

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public List<FutsalField> getAllFields() {
      List<FutsalField> fields = new ArrayList<>();
      String sql = "SELECT * FROM futsal_fields ORDER BY field_name";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

         while (rs.next()) {
            FutsalField field = new FutsalField(
                  rs.getInt("id"),
                  rs.getString("field_name"),
                  rs.getString("open_time"),
                  rs.getString("close_time"),
                  rs.getDouble("price_per_session"),
                  rs.getBoolean("is_active"),
                  rs.getString("created_at"),
                  rs.getString("updated_at"));
            fields.add(field);
         }

      } catch (SQLException e) {
         e.printStackTrace();
      }

      return fields;
   }

   public FutsalField getFieldById(int id) {
      String sql = "SELECT * FROM futsal_fields WHERE id = ?";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setInt(1, id);
         ResultSet rs = pstmt.executeQuery();

         if (rs.next()) {
            return new FutsalField(
                  rs.getInt("id"),
                  rs.getString("field_name"),
                  rs.getString("open_time"),
                  rs.getString("close_time"),
                  rs.getDouble("price_per_session"),
                  rs.getBoolean("is_active"),
                  rs.getString("created_at"),
                  rs.getString("updated_at"));
         }

      } catch (SQLException e) {
         e.printStackTrace();
      }

      return null;
   }

   public boolean updateField(FutsalField field) {
      String sql = "UPDATE futsal_fields SET field_name = ?, open_time = ?, close_time = ?, " +
            "price_per_session = ?, is_active = ? WHERE id = ?";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, field.getFieldName());
         pstmt.setString(2, field.getOpenTime());
         pstmt.setString(3, field.getCloseTime());
         pstmt.setDouble(4, field.getPricePerSession());
         pstmt.setBoolean(5, field.isActive());
         pstmt.setInt(6, field.getId());

         int affectedRows = pstmt.executeUpdate();
         return affectedRows > 0;

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public boolean deleteField(int id) {
      String sql = "DELETE FROM futsal_fields WHERE id = ?";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setInt(1, id);
         int affectedRows = pstmt.executeUpdate();
         return affectedRows > 0;

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }


}
