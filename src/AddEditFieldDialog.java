import javax.swing.*;
import java.awt.*;

public class AddEditFieldDialog extends JDialog {
   private FutsalField field;
   private FieldService fieldService;
   private JPanel parentPanel;

   private JTextField nameField;
   private JSpinner openTimeSpinner, closeTimeSpinner;
   private JSpinner priceSpinner;
   private JCheckBox activeCheckBox;
   private JButton saveButton, cancelButton;

   public AddEditFieldDialog(FutsalField field, FieldService fieldService, JPanel parentPanel) {
      this.field = field;
      this.fieldService = fieldService;
      this.parentPanel = parentPanel;
      initializeUI();
   }

   private void initializeUI() {
      setTitle(field == null ? "Add New Field" : "Edit Field");
      setModal(true);
      setSize(400, 300);
      setLocationRelativeTo(null);
      setResizable(false);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      // Form panel
      JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

      JLabel nameLabel = new JLabel("Field Name:");
      nameField = new JTextField();

      JLabel openTimeLabel = new JLabel("Open Time:");
      openTimeSpinner = new JSpinner(new SpinnerDateModel());
      JSpinner.DateEditor openTimeEditor = new JSpinner.DateEditor(openTimeSpinner, "HH:mm");
      openTimeSpinner.setEditor(openTimeEditor);

      JLabel closeTimeLabel = new JLabel("Close Time:");
      closeTimeSpinner = new JSpinner(new SpinnerDateModel());
      JSpinner.DateEditor closeTimeEditor = new JSpinner.DateEditor(closeTimeSpinner, "HH:mm");
      closeTimeSpinner.setEditor(closeTimeEditor);

      JLabel priceLabel = new JLabel("Price per Session:");
      priceSpinner = new JSpinner(new SpinnerNumberModel(100000.0, 0.0, 1000000.0, 50000.0));

      JLabel activeLabel = new JLabel("Active:");
      activeCheckBox = new JCheckBox();
      activeCheckBox.setSelected(true);

      formPanel.add(nameLabel);
      formPanel.add(nameField);
      formPanel.add(openTimeLabel);
      formPanel.add(openTimeSpinner);
      formPanel.add(closeTimeLabel);
      formPanel.add(closeTimeSpinner);
      formPanel.add(priceLabel);
      formPanel.add(priceSpinner);
      formPanel.add(activeLabel);
      formPanel.add(activeCheckBox);

      // Button panel
      JPanel buttonPanel = new JPanel(new FlowLayout());
      saveButton = new JButton("Save");
      cancelButton = new JButton("Cancel");

      saveButton.setBackground(new Color(46, 139, 87));
      saveButton.setForeground(Color.WHITE);
      cancelButton.setBackground(new Color(220, 20, 60));
      cancelButton.setForeground(Color.WHITE);

      cancelButton.setOpaque(true);
      cancelButton.setBorderPainted(false);

      saveButton.setOpaque(true);
      saveButton.setBorderPainted(false);

      buttonPanel.add(saveButton);
      buttonPanel.add(cancelButton);

      mainPanel.add(formPanel, BorderLayout.CENTER);
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);

      add(mainPanel);

      if (field != null) {
         loadFieldData();
      }

      saveButton.addActionListener(e -> saveField());
      cancelButton.addActionListener(e -> dispose());
   }

   private void loadFieldData() {
      nameField.setText(field.getFieldName());

      try {
         java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
         java.util.Date openTime = sdf.parse(field.getOpenTime());
         java.util.Date closeTime = sdf.parse(field.getCloseTime());

         openTimeSpinner.setValue(openTime);
         closeTimeSpinner.setValue(closeTime);
      } catch (Exception e) {
         e.printStackTrace();
      }

      priceSpinner.setValue(field.getPricePerSession());
      activeCheckBox.setSelected(field.isActive());
   }

   private void saveField() {
      String fieldName = nameField.getText().trim();

      if (fieldName.isEmpty()) {
         JOptionPane.showMessageDialog(this,
               "Field name cannot be empty",
               "Error",
               JOptionPane.ERROR_MESSAGE);
         return;
      }

      java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
      String openTime = sdf.format(openTimeSpinner.getValue());
      String closeTime = sdf.format(closeTimeSpinner.getValue());
      double price = (Double) priceSpinner.getValue();
      boolean isActive = activeCheckBox.isSelected();

      FutsalField newField = new FutsalField();
      newField.setFieldName(fieldName);
      newField.setOpenTime(openTime);
      newField.setCloseTime(closeTime);
      newField.setPricePerSession(price);
      newField.setActive(isActive);

      if (field != null) {
         newField.setId(field.getId());
      }

      new Thread(() -> {
         boolean success;
         if (field == null) {
            success = fieldService.addField(newField);
         } else {
            success = fieldService.updateField(newField);
         }

         SwingUtilities.invokeLater(() -> {
            if (success) {
               JOptionPane.showMessageDialog(this,
                     "Field saved successfully",
                     "Success",
                     JOptionPane.INFORMATION_MESSAGE);

               // Refresh panel utama
               // if (parentPanel instanceof AdminDashboard.ManageFieldsPanel) {
               //    ((AdminDashboard.ManageFieldsPanel) parentPanel).loadFieldsData();
               // }

               dispose();
            } else {
               JOptionPane.showMessageDialog(this,
                     "Failed to save field",
                     "Error",
                     JOptionPane.ERROR_MESSAGE);
            }
         });
      }).start();
   }
}