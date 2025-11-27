import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

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
}