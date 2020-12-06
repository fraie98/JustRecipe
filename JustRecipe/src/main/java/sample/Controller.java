package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {
    @FXML
    private JFXTextField usernameLoginTextField;
    @FXML
    private JFXPasswordField passwordLoginTextField;
    @FXML
    private JFXTextField firstNameRegistrationTextField;
    @FXML
    private JFXTextField lastNameRegistrationTextField;
    @FXML
    private JFXTextField usernameRegistrationTextField;
    @FXML
    private JFXPasswordField passwordRegistrationTextField;
    @FXML
    private JFXPasswordField confirmPasswordRegistrationTextField;
    @FXML
    private Label labelLoginFailed;
    @FXML
    private Label labelRegistrationFailed;
    @FXML
    private Button loginButton;
    @FXML
    private Button registrationButton;

    public void initialize()
    {
        labelLoginFailed.setVisible(false);
        labelRegistrationFailed.setVisible(false);
        loginButton.setOnAction(actionEvent -> handleLoginButtonAction(actionEvent));
        registrationButton.setOnAction(actionEvent -> handleRegisterButtonAction(actionEvent));
    }

    private void handleLoginButtonAction(ActionEvent actionEvent) {
        if (usernameLoginTextField.getText().equals("") || passwordLoginTextField.getText().equals(""))
        {
            labelLoginFailed.setVisible(true);
            return;
        }

        login(usernameLoginTextField.getText(), passwordLoginTextField.getText());
    }

    private void handleRegisterButtonAction(ActionEvent actionEvent) {
        if ((firstNameRegistrationTextField.getText().equals("") ||
                lastNameRegistrationTextField.getText().equals("") ||
                usernameRegistrationTextField.getText().equals("") ||
                passwordRegistrationTextField.getText().equals("") ||
                confirmPasswordRegistrationTextField.getText().equals(""))
            || (!passwordRegistrationTextField.getText().equals(confirmPasswordRegistrationTextField.getText())))
        {
            labelRegistrationFailed.setVisible(true);
            return;
        }

        register(firstNameRegistrationTextField.getText(), lastNameRegistrationTextField.getText(),
                usernameRegistrationTextField.getText(), passwordRegistrationTextField.getText());
    }

    private void login (final String username, final String password)
    {

    }

    private void register (final String firsName, final String lastName, final String username,
                           final String password)
    {

    }
}
