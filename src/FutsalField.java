public class FutsalField {
   private int id;
   private String fieldName;
   private String openTime;
   private String closeTime;
   private double pricePerSession;
   private boolean isActive;
   private String createdAt;
   private String updatedAt;

   public FutsalField() {
   }

   public FutsalField(int id, String fieldName, String openTime, String closeTime,
         double pricePerSession, boolean isActive, String createdAt, String updatedAt) {
      this.id = id;
      this.fieldName = fieldName;
      this.openTime = openTime;
      this.closeTime = closeTime;
      this.pricePerSession = pricePerSession;
      this.isActive = isActive;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public String getOpenTime() {
      return openTime;
   }

   public void setOpenTime(String openTime) {
      this.openTime = openTime;
   }

   public String getCloseTime() {
      return closeTime;
   }

   public void setCloseTime(String closeTime) {
      this.closeTime = closeTime;
   }

   public double getPricePerSession() {
      return pricePerSession;
   }

   public void setPricePerSession(double pricePerSession) {
      this.pricePerSession = pricePerSession;
   }

   public boolean isActive() {
      return isActive;
   }

   public void setActive(boolean active) {
      isActive = active;
   }

   public String getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }

   public String getUpdatedAt() {
      return updatedAt;
   }

   public void setUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
   }

   @Override
   public String toString() {
      return fieldName;
   }
}