/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.gui.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import movie.registraction.dal.DALException;
import movie.registraction.gui.model.MainWindowModel;

/**
 * FXML Controller class
 *
 * @author B
 */
public class EditCategoriesController implements Initializable
{

    @FXML
    private TextField txtFieldCategory;
    MainWindowModel m;
    @FXML
    private ListView<String> lstViewAllCategories;
    @FXML
    private Button btnSave;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try {
            m = new MainWindowModel();
        } catch (DALException ex) {
            System.out.println(ex.getMessage());
        }

        lstViewAllCategories.setItems(m.getAllCategories());
    }

    @FXML
    private void btnAddCategory(ActionEvent event) throws SQLException
    {
        m.addChosenCategory(txtFieldCategory.getText());
    }

    @FXML
    private void btnRemoveCategory(ActionEvent event)
    {
        m.removeChosenCategory(lstViewAllCategories.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void btnSaveCategories(ActionEvent event)
    {

        m.saveCategories();
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

}
