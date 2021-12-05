package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public Label lblBanner;
    public Label lblUserID;
    public AnchorPane root;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public TextField txtSelectedToDo;
    public Button btnUpdate;
    public Button btnDelete;

    public String selectedID = null;

    public void initialize(){
        lblBanner.setText("Hi...! "+LoginFormController.loginUserName + " Welcome to To-Do List");
        lblUserID.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                setDisableCommon(false);

                subRoot.setVisible(false);

                ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();

                if(selectedItem == null){
                    return;
                }

                txtSelectedToDo.setText(selectedItem.getDescription());

                selectedID = selectedItem.getId();
            }
        });
    }

    public void setDisableCommon(boolean isDisable){
        txtSelectedToDo.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do You Want to Log Out...?", ButtonType.YES,ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){

            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));

            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {

        lstToDo.getSelectionModel().clearSelection();

        setDisableCommon(true);

        txtDescription.requestFocus();

        subRoot.setVisible(true);
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        String id = autoGenerateID();
        String description = txtDescription.getText();
        String user_id = lblUserID.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values(?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_id);

            preparedStatement.executeUpdate();

            txtDescription.clear();
            subRoot.setVisible(false);

            loadList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        String todoID = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");
            boolean isExist = resultSet.next();
            if(isExist){
                todoID = resultSet.getString(1);
                todoID = todoID.substring(1,todoID.length());
                int intID = Integer.parseInt(todoID);
                intID++;
                if(intID <10){
                    todoID = "T00" +intID;
                }
                else if(intID < 100){
                    todoID = "T0" +intID;
                }
                else{
                    todoID = "T" +intID;
                }
            }
            else{
                todoID = "T001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return todoID;
    }

    public void loadList(){
        ObservableList<ToDoTM> todos = lstToDo.getItems();
        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where userId = ?");
            preparedStatement.setObject(1,LoginFormController.loginUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM object = new ToDoTM(id,description,user_id);

                todos.add(object);
            }
            lstToDo.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String selectedText = txtSelectedToDo.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,selectedText);
            preparedStatement.setObject(2,selectedID);

            preparedStatement.executeUpdate();

            txtSelectedToDo.clear();

            loadList();

            setDisableCommon(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do You Want to Delete This Yo Do...?",ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1,selectedID);

                preparedStatement.executeUpdate();

                loadList();

                txtSelectedToDo.clear();

                setDisableCommon(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
