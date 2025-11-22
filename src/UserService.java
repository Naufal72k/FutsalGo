import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {

   public boolean registerUser(String username, String password, String email, String userLevel) {
      String sql = "INSERT INTO users (username, password, email, user_level) VALUES (?, ?, ?, ?)";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, username);
         pstmt.setString(2, hashPassword(password)); // Hash password
         pstmt.setString(3, email);
         pstmt.setString(4, userLevel);

         int affectedRows = pstmt.executeUpdate();
         return affectedRows > 0;

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public User authenticateUser(String username, String password) {
      String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, username);
         pstmt.setString(2, hashPassword(password));

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

   public boolean isUsernameExists(String username) {
      String sql = "SELECT id FROM users WHERE username = ?";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, username);
         ResultSet rs = pstmt.executeQuery();

         return rs.next();

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   public boolean isEmailExists(String email) {
      String sql = "SELECT id FROM users WHERE email = ?";

      try (Connection conn = DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword());
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, email);
         ResultSet rs = pstmt.executeQuery();

         return rs.next();

      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

   private String hashPassword(String password) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[] hash = md.digest(password.getBytes());
         StringBuilder hexString = new StringBuilder();

         for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
               hexString.append('0');
            hexString.append(hex);
         }

         return hexString.toString();

      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e);
      }
   }
}