package sample.controller;

import com.westlyf.agent.Agent;
import com.westlyf.domain.exercise.quiz.Exam;
import com.westlyf.user.ExamGrade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Yves on 10/13/2016.
 */
public class GradesController implements Initializable{

    private ObservableList<ExamGrade> gradesList;
    @FXML private TableView<ExamGrade> tableView;
    @FXML private TableColumn<ExamGrade, String> examTitleColumn;
    @FXML private TableColumn<ExamGrade, Integer> rawGradeColumn;
    @FXML private TableColumn<ExamGrade, Integer> totalItemsColumn;
    @FXML private TableColumn<ExamGrade, String> statusColumn;
    @FXML private TableColumn<ExamGrade, String> percentGradeColumn;

    @FXML private Button backToMenu;
    @FXML private Label overallLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gradesList = FXCollections.observableArrayList();
        gradesList.addAll(Agent.getExamGrades());
        examTitleColumn.setCellValueFactory(new PropertyValueFactory<>("exam_title"));
        rawGradeColumn.setCellValueFactory(new PropertyValueFactory<>("rawGrade"));
        totalItemsColumn.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        percentGradeColumn.setCellValueFactory(new PropertyValueFactory<>("percentGrade"));
        tableView.setItems(gradesList);
    }

    @FXML
    private void handleChangeSceneAction(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;
        if (event.getSource() == backToMenu){
            stage = (Stage) backToMenu.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("../view/user.fxml"));
        }else {return;}
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
