public class Booking {
    private int id;
    private int userId;
    private int fieldId;

    // UPDATE: Variabel baru untuk menampung Nama User & Nama Lapangan
    // Ini berguna agar di tabel Admin kita bisa menampilkan "Budi" bukannya "User
    // ID: 2"
    private String userName;
    private String fieldName;

    private String bookingDate;
    private String startTime;
    private String endTime;
    private double totalPrice;

    // Default status
    private String status = "PAID";
    private String createdAt;

    public Booking() {
    }

    // Constructor Lama (Dipertahankan agar tidak error di DatabaseConfig yang belum
    // diupdate)
    public Booking(int id, int userId, int fieldId, String bookingDate, String startTime,
            String endTime, double totalPrice, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.fieldId = fieldId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor Baru (Opsional: Mencakup userName dan fieldName)
    public Booking(int id, int userId, String userName, int fieldId, String fieldName,
            String bookingDate, String startTime, String endTime, double totalPrice,
            String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- GETTERS & SETTERS BARU ---

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    // --- GETTERS & SETTERS LAMA ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}