package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String loginUserName;
    public static String loginUserID;

    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));

        Scene scene = new Scene(parent);

        Stage primaryStage = (Stage) root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Register Form");
        primaryStage.centerOnScreen();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        login();
    }

    public void txtPasswordOnAction(ActionEvent actionEvent) {
        login();
    }

    public void login(){

        if(txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        }
        else if(txtPassword.getText().trim().isEmpty()){
            txtPassword.requestFocus();
        }
        else{
            String useName = txtUserName.getText();
            String password = txtPassword.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from user where userName = ? and password = ?");
                preparedStatement.setObject(1,useName);
                preparedStatement.setObject(2,password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){

                    loginUserName = resultSet.getString(2);
                    loginUserID = resultSet.getString(1);
                    Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoForm.fxml"));

                    Scene scene = new Scene(parent);

                    Stage primaryStage = (Stage) root.getScene().getWindow();

                    primaryStage.setScene(scene);
                    primaryStage.setTitle("To Do Form");
                    primaryStage.centerOnScreen();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Username or Password doesn't Matched...!");
                    alert.showAndWait();

                    txtUserName.clear();
                    txtPassword.clear();

                    txtUserName.requestFocus();
                }

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
