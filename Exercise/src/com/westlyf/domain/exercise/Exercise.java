package com.westlyf.domain.exercise;

import com.westlyf.domain.lesson.Lesson;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;

/**
 * Created by robertoguazon on 13/06/2016.
 */
public class Exercise extends Lesson implements Serializable {

    private IntegerProperty totalItems = new SimpleIntegerProperty();
    private IntegerProperty totalScore = new SimpleIntegerProperty();

    public Exercise() {
        super();
    }

    public int getTotalItems() {
        return totalItems.get();
    }

    public int getTotalScore() {
        return totalScore.get();
    }

    public void setTotalItems(int totalItems) {
        this.totalItems.set(totalItems);
    }

    public void setTotalScore(int totalScore) {
        this.totalScore.set(totalScore);
    }

    public String toString() {
        //TODO add the fields
        return super.toString();
    }

}
