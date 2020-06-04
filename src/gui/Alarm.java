package gui;

import javafx.scene.control.Alert;

/**
 *
 * @author Admin
 */
public class Alarm {
    
    /**
    * Метод отображения на отображения на экране окна предупреждения
    * @param alType тип сообщения (NONE, INFORMATION, WARNING, CONFIRMATION, ERROR)
    * @param title название окна
    * @param headerText название предупреждения
    * @param contentText описание
    */
    public static void showAlert (Alert.AlertType alType, String title, String headerText, String contentText) {
        
        Alert alert = new Alert(alType);
        alert.initOwner(null);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
        
    }

    @Override
    public String toString() {
        return super.toString();
    }
    
}
