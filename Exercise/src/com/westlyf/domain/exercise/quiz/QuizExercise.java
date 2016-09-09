package com.westlyf.domain.exercise.quiz;

import com.westlyf.domain.exercise.Exercise;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by robertoguazon on 13/06/2016.
 */
public class QuizExercise extends Exercise implements Serializable {

    protected ArrayList<QuizItem> quizItems = new ArrayList();

    public QuizExercise() {
        super();
    }

    public QuizExercise(QuizExerciseSerializable quizExerciseSerializable) {
        super();
        ArrayList<QuizItemSerializable> quizItemSerializables = quizExerciseSerializable.getQuizItems();
        for (int i = 0; i < quizItemSerializables.size(); i++) {
            quizItems.add(new QuizItem(quizItemSerializables.get(i)));
        }
    }

    public boolean addItem(QuizType type, String question, ArrayList<String>choices, ArrayList<String> validAnswers) {
        QuizItem quizItem = new QuizItem();
        quizItem.setType(type);
        quizItem.setQuestion(question);
        quizItem.setChoices(choices);
        quizItem.setValidAnswers(validAnswers);

        this.quizItems.add(quizItem);
        return true;
    }

    public void setQuizItems(ArrayList<QuizItem> quizItems) {
        this.quizItems = quizItems;
    }

    public boolean addItem(String question, ArrayList<String>choices, ArrayList<String> validAnswers) {
        QuizItem quizItem = new QuizItem();
        quizItem.setType(QuizType.RADIOBUTTON);
        quizItem.setQuestion(question);
        quizItem.setChoices(choices);
        quizItem.setValidAnswers(validAnswers);

        this.quizItems.add(quizItem);
        return true;
    }

    public ArrayList<QuizItem> getQuizItems() {
        return quizItems;
    }

    public ScrollPane getQuizExercise() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVisible(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox parentBox = new VBox();
        parentBox.setVisible(true);
        for (int i = 0; i < quizItems.size(); i++) {
            VBox childBox = quizItems.get(i).getItemBox();
            parentBox.getChildren().add(childBox);
            childBox.prefWidthProperty().bind(parentBox.prefWidthProperty());
        }

        parentBox.prefWidthProperty().bind(scrollPane.prefWidthProperty());
        scrollPane.setContent(parentBox);
        return scrollPane;
    }

    public void addItem(QuizItem quizItem) {
        this.quizItems.add(quizItem);
    }

    public void printQuiz() {
        System.out.println("Quiz: " + title.getValue());

        System.out.print("Tags: ");
        for (StringProperty tag: tags) {
            System.out.print(tag + " ");
        }
        System.out.println();

        for (QuizItem quizItem : quizItems) {
            quizItem.printItem();
        }

        System.out.println("---------");
    }

    @Override
    public String toString() {

        return super.toString() + "\n" + quizItems.toString();
    }

    public boolean isValidMaker() {
        if (quizItems == null || quizItems.isEmpty() || !super.isValid()) return false;

        for (QuizItem quizItem : quizItems) {
            if (!quizItem.isValidMaker()) return false;
        }

        return true;
    }

    public boolean isValidAnsweredFormat() {
        if (quizItems == null || quizItems.isEmpty() || !super.isValid()) return false;

        for (QuizItem quizItem : quizItems) {
            if (!quizItem.isValidAnsweredFormat()) return false;
        }

        return true;
    }

    public String check() {

        String quizItemsCheckString = "";
        for (QuizItem quizItem : quizItems) {
            quizItemsCheckString += quizItem.check() + "\n";
        }

        return super.check() + "\n" + quizItemsCheckString;
    }

    public String errorAnsweredFormat() {
        String error = "errors: ";
        int n = 0;
        for (QuizItem quizItem : quizItems) {
            if (!quizItem.isValidAnsweredFormat()) {
                n++;
            }
        }

        return error + n + " errors";
    }

    public String errorMaker() {
        String error = "errors: ";
        int n = 0;
        for (QuizItem quizItem : quizItems) {
            if (!quizItem.isValidMaker()) {
                n++;
            }
        }

        return error + n + " errors";
    }

    public void copy(QuizExercise quizExercise) {
        super.copy(quizExercise);

        this.quizItems = quizExercise.getQuizItems();
    }

    public QuizExercise clone() {
        QuizExercise clone = new QuizExercise();
        clone.copy(this);
        return clone;
    }


}
