package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Label lblPasswordNotMatched1;
    public Label lblPasswordNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblUserName;
    public Label lblEmail;
    public Label lblNewPassword;
    public Label lblConfirmPassword;
    public Label lblID;
    public AnchorPane root;

    public void initialize(){
        passwordNotMatched(false);

        setDisableCommon(true);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {

        if(txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        }
        else if(txtEmail.getText().trim().isEmpty()){
            txtEmail.requestFocus();
        }
        else if(txtNewPassword.getText().trim().isEmpty()){
            txtNewPassword.requestFocus();
        }
        else if(txtConfirmPassword.getText().trim().isEmpty()){
            txtConfirmPassword.requestFocus();
        }
        else{
            String newPassword = txtNewPassword.getText();
            String confirmPassword = txtConfirmPassword.getText();

            if(newPassword.equals(confirmPassword)){
                setBorderColor("transparent");
                passwordNotMatched(false);

                register();
            }
            else{
                setBorderColor("red");
                txtNewPassword.requestFocus();

                passwordNotMatched(true);
            }
        }
    }

    public void setBorderColor(String color){
        txtNewPassword.setStyle("-fx-border-color: "+color);
        txtConfirmPassword.setStyle("-fx-border-color: "+color);
    }

    public void passwordNotMatched(boolean isVisible){
        lblPasswordNotMatched1.setVisible(isVisible);
        lblPasswordNotMatched2.setVisible(isVisible);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);
        txtUserName.requestFocus();

        autoGenerateID();
    }

    public void setDisableCommon(boolean isDisable){
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
        lblUserName.setDisable(isDisable);
        lblEmail.setDisable(isDisable);
        lblNewPassword.setDisable(isDisable);
        lblConfirmPassword.setDisable(isDisable);
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");
            boolean isExist = resultSet.next();
            if(isExist){
                String userID = resultSet.getString(1);
                userID = userID.substring(1,userID.length());
                int intID = Integer.parseInt(userID);
                intID++;
                if(intID <10){
                    lblID.setText("U00" + intID);
                }
                else if(intID < 100){
                    lblID.setText("U0"+ intID);
                }
                else{
                    lblID.setText("U" + intID);
                }
            }
            else{
                lblID.setText("U001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void register(){
        Connection connection = DBConnection.getInstance().getConnection();

        String id = lblID.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,email);
            preparedStatement.setObject(4,password);

            int i = preparedStatement.executeUpdate();
            if(i !=0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Successfully Added...!");
                alert.showAndWait();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage primaryStage = (Stage) root.getScene().getWindow();

                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
