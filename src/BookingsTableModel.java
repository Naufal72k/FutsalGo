import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookingsTableModel extends AbstractTableModel {
    private List<Booking> bookings;
    private String[] columnNames = { "ID", "Date", "Field ID", "Start Time", "End Time", "Total Price", "Status" };

    public BookingsTableModel() {
        this.bookings = new ArrayList<>();
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public int getRowCount() {
        return bookings.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Booking booking = bookings.get(row);

        switch (column) {
            case 0:
                return booking.getId();
            case 1:
                return booking.getBookingDate();
            case 2:
                return booking.getFieldId();
            case 3:
                return booking.getStartTime().substring(0, 5);
            case 4:
                return booking.getEndTime().substring(0, 5);
            case 5:
                return String.format("Rp %,d", (int) booking.getTotalPrice());
            case 6:
                return booking.getStatus();
            default:
                return null;
        }
    }
}