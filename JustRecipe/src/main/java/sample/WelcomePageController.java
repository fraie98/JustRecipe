package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Welcome page
 */
public class WelcomePageController {
    @FXML private JFXTextField usernameLoginTextField;
    @FXML private JFXPasswordField passwordLoginTextField;
    @FXML private JFXTextField firstNameRegistrationTextField;
    @FXML private JFXTextField lastNameRegistrationTextField;
    @FXML private JFXTextField usernameRegistrationTextField;
    @FXML private JFXPasswordField passwordRegistrationTextField;
    @FXML private JFXPasswordField confirmPasswordRegistrationTextField;
    @FXML private Label labelLoginFailed;
    @FXML private Label labelRegistrationFailed;
    @FXML private Button loginButton;
    @FXML private Button registrationButton;

    private Neo4jDriver neo4jDriver;

    /**
     * Method called when the controller is initialized
     */
    public void initialize()
    {
        labelLoginFailed.setVisible(false);
        labelRegistrationFailed.setVisible(false);
        loginButton.setOnAction(actionEvent -> handleLoginButtonAction(actionEvent));
        registrationButton.setOnAction(actionEvent -> handleRegisterButtonAction(actionEvent));
        neo4jDriver = new Neo4jDriver("localhost", 7687, "neo4j", "justrecipe");
        neo4jDriver.initConnection();
    }

    /**
     * Method used to handle the Login button click event
     * @param actionEvent   The event that occurs when the user click the Login button
     */
    private void handleLoginButtonAction(ActionEvent actionEvent) {
        labelLoginFailed.setVisible(false);
        if (usernameLoginTextField.getText().equals("") || passwordLoginTextField.getText().equals(""))
        {
            labelLoginFailed.setVisible(true);
        }
        else
        {
            if (login(usernameLoginTextField.getText(), passwordLoginTextField.getText()))
            {
                HomePageController homePageController = (HomePageController)
                        Utils.changeScene("/homepage.fxml", actionEvent);
                homePageController.transferNeo4jDriver(neo4jDriver);
            }
            else
            {
                labelLoginFailed.setVisible(true);
            }
        }
    }

    /**
     * Method used to handle the Register button click event
     * @param actionEvent   The event that occurs when the user click the Register button
     */
    private void handleRegisterButtonAction(ActionEvent actionEvent) {
        labelLoginFailed.setVisible(false);
        if ((firstNameRegistrationTextField.getText().equals("") ||
                lastNameRegistrationTextField.getText().equals("") ||
                usernameRegistrationTextField.getText().equals("") ||
                passwordRegistrationTextField.getText().equals("") ||
                confirmPasswordRegistrationTextField.getText().equals(""))
            || (!passwordRegistrationTextField.getText().equals(confirmPasswordRegistrationTextField.getText())))
        {
            labelRegistrationFailed.setVisible(true);
        }
        else
        {
            if (register(firstNameRegistrationTextField.getText(), lastNameRegistrationTextField.getText(),
                    usernameRegistrationTextField.getText(), passwordRegistrationTextField.getText()))
            {
                HomePageController homePageController = (HomePageController)
                        Utils.changeScene("/homepage.fxml", actionEvent);
                homePageController.transferNeo4jDriver(neo4jDriver);
            }
            else
            {
                labelRegistrationFailed.setVisible(true);
            }
        }
    }

    /**
     * Function used to perform the operations needed to login a user
     * @param username  username of the user
     * @param password  password of the user
     * @return          true if the login was successful, otherwise false
     */
    private boolean login (final String username, final String password)
    {
        if (neo4jDriver.checkUser(username, password))
            return true;
        else
            return false;
    }

    /**
     * Function used to perform the operations needed to register a user
     * @param firsName  first name of the user
     * @param lastName  last name of the user
     * @param username  username of the user
     * @param password  password of the user
     * @return          true if the registration was successful, otherwise false
     */
    private boolean register (final String firsName, final String lastName, final String username,
                           final String password)
    {
        // I need to check if it is possible to use this username
        if (!neo4jDriver.checkUsername(username)) //If it wasn't previously used
        {
            neo4jDriver.addUser(firsName, lastName, username, password);
            return true;
        }
        return false;
    }
}
