public class User {
   private int id;
   private String username;
   private String password;
   private String email;
   private String userLevel;
   private String createdAt;

   public User() {
   }

   public User(int id, String username, String password, String email, String userLevel, String createdAt) {
      this.id = id;
      this.username = username;
      this.password = password;
      this.email = email;
      this.userLevel = userLevel;
      this.createdAt = createdAt;
   }

   // Getters and Setters
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getUserLevel() {
      return userLevel;
   }

   public void setUserLevel(String userLevel) {
      this.userLevel = userLevel;
   }

   public String getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }
}