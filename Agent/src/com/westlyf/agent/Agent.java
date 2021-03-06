package com.westlyf.agent;

import com.westlyf.controller.BackgroundProcess;
import com.westlyf.database.ExamDatabase;
import com.westlyf.database.ExerciseDatabase;
import com.westlyf.database.LessonDatabase;
import com.westlyf.database.UserDatabase;
import com.westlyf.domain.exercise.mix.VideoPracticalExercise;
import com.westlyf.domain.exercise.practical.PracticalExercise;
import com.westlyf.domain.exercise.practical.PracticalPrintExercise;
import com.westlyf.domain.exercise.quiz.Exam;
import com.westlyf.domain.lesson.TextLesson;
import com.westlyf.user.ExamGrade;
import com.westlyf.user.UserExercise;
import com.westlyf.user.Users;
import com.westlyf.utils.RuntimeUtil;
import com.westlyf.utils.StringUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by robertoguazon on 05/09/2016.
 */
public class Agent {

    private static Users loggedUser;
    private static boolean isExerciseCleared;
    private static int tries;
    private static String response;
    private static String output;
    private static ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    private static UserExercise userExercise = new UserExercise();
    private static TextLesson lesson = new TextLesson();
    private static VideoPracticalExercise exercise = new VideoPracticalExercise();
    private static PracticalPrintExercise examExercise = new PracticalPrintExercise();
    private static Exam exam = new Exam();
    private static ExamGrade examGrade = new ExamGrade();

    private static ArrayList<UserExercise> userExercises = new ArrayList<>();
    private static ArrayList<TextLesson> lessonsInModule = new ArrayList<>();
    private static ArrayList<TextLesson> textLessons = new ArrayList<>();
    private static ArrayList<VideoPracticalExercise> videoPracticalExercises = new ArrayList<>();
    private static ArrayList<PracticalExercise> challenges = new ArrayList<>();
    private static ArrayList<PracticalPrintExercise> examExercises = new ArrayList<>();
    private static ArrayList<Exam> exams = new ArrayList<>();
    private static ArrayList<ExamGrade> examGrades = new ArrayList<>();

    private static BackgroundProcess backgroundProcess = new BackgroundProcess();
    private static final String fer = "http://localhost/emotion-detection/";

    public Agent(Users user) {
        setLoggedUser(user);
        loadAll();
    }

    public static void loadAll(){
        String s = "module";
        int i = 1;
        //extract data from db
        while (!Agent.getLoggedUser().getCurrentModuleId().equals(s)) {
            s = "module" + i++;
            load(LoadType.LESSON, s);
            load(LoadType.EXERCISE, s);
        }
        if (getLoggedUser().getCurrentExamId() != null) {
            if (getLoggedUser().getCurrentExamId().equals("challenge")){
                load(LoadType.CHALLENGE, "challenge");
            }else{
                System.out.println("exam");
                load(LoadType.EXAM, s);
                System.out.println("exam exercise");
                load(LoadType.EXAM_EXERCISE, s);
            }
        }else {
            System.out.println("exam empty.");
        }
        load(LoadType.USER_EXERCISE);
        load(LoadType.GRADE);
        //prints out the arraylists
        print(LoadType.LESSON);
        print(LoadType.EXERCISE);
        print(LoadType.EXAM);
        print(LoadType.EXAM_EXERCISE);
        print(LoadType.CHALLENGE);
        print(LoadType.USER_EXERCISE);
        print(LoadType.GRADE);
    }

    public static void load(LoadType loadType){
        load(loadType, null);
    }

    public static void load(LoadType loadType, String s){
        switch (loadType){
            case LESSON:
                getTextLessons().addAll(LessonDatabase.getTextLessonsUsingTagsContains(s));
                break;
            case EXERCISE:
                getVideoPracticalExercises().addAll(ExerciseDatabase.getVideoPracticalExercisesUsingTagsContains(s, "lesson"));
                break;
            case EXAM_EXERCISE:
                ArrayList<PracticalExercise> temp = ExerciseDatabase.getPracticalExercisesUsingTagsContains(s, "exam");
                for (PracticalExercise aTemp : temp) {
                    getExamExercises().add((PracticalPrintExercise) aTemp);
                }
                break;
            case EXAM:
                getExams().addAll(ExamDatabase.getExamsUsingTagsContains(s));
                break;
            case USER_EXERCISE:
                getUserExercises().addAll(UserDatabase.getUserExercisesUsingUserId(getLoggedUser().getUserId()));
                break;
            case GRADE:
                getExamGrades().addAll(UserDatabase.getExamGradesUsingUserId(getLoggedUser().getUserId()));
                break;
            case CHALLENGE:
                getChallenges().addAll(ExerciseDatabase.getPracticalExercisesUsingTagsContains(s));
                break;
            default:
                System.out.println("empty.");
                break;
        }
    }

    public static void print(LoadType loadType){
        System.out.println("\nContents of " + loadType);
        switch (loadType){
            case LESSON:
                getTextLessons().forEach((a)->System.out.println(a));
                break;
            case EXERCISE:
                getVideoPracticalExercises().forEach((a)->System.out.println(a));
                break;
            case EXAM_EXERCISE:
                getExamExercises().forEach((a)->System.out.println(a));
                break;
            case EXAM:
                getExams().forEach((a)->System.out.println(a));
                break;
            case USER_EXERCISE:
                getUserExercises().forEach((a)->System.out.println(a));
                break;
            case GRADE:
                getExamGrades().forEach((a)->System.out.println(a));
                break;
            case CHALLENGE:
                getChallenges().forEach((a)->System.out.println(a));
                break;
            default:
                System.out.println("empty.");
                break;
        }
    }

    public static void removeLoggedUser(){
        if (getLoggedUser() != null) {
            if (updateUser() > 0) {
                setLoggedUser(null);
                setUserExercise(null);
                setLesson(null);
                setExercise(null);
                setExamExercise(null);
                setExam(null);
                setExamGrade(null);
                getLessonsInModule().clear();
                getTextLessons().clear();
                getChallenges().clear();
                getExamExercises().clear();
                getVideoPracticalExercises().clear();
                getExams().clear();
                getUserExercises().clear();
                getExamGrades().clear();
                setOutput("");
                setResponse("");
                setTries(0);
            }
        }
    }

    public static TextLesson loadLesson(String tag1, String tag2){
        TextLesson match = null;
        for (int i = 0; i < getTextLessons().size(); i++) {
            match = getTextLessons().get(i);
            if (match.getTagsString().contains(tag1) && match.getTagsString().contains(tag2)){
                break;
            }else {match = null;}
        }
        setLesson(match);
        System.out.println("\nRetrieved Lesson:\n" + getLesson());
        return match;
    }

    public static PracticalPrintExercise loadExamExercise(String tag1, String tag2){
        PracticalPrintExercise match = null;
        for (int i=0; i<getExamExercises().size(); i++){
            match = getExamExercises().get(i);
            if (match.getTagsString().contains(tag1) && match.getTagsString().contains(tag2)){
                break;
            }else {match = null;}
        }
        setExamExercise(match);
        System.out.println("\nRetrieved Exam Exercise:\n" + getExamExercise());
        return match;
    }

    public static VideoPracticalExercise loadExercise(String tag1, String tag2){
        VideoPracticalExercise match = null;
        for (int i = 0; i < getVideoPracticalExercises().size(); i++) {
            match = getVideoPracticalExercises().get(i);
            if (match.getTagsString().contains(tag1) && match.getTagsString().contains(tag2)){
                break;
            }else {match = null;}
        }
        setExercise(match);
        System.out.println("\nRetrieved Exercise:\n" + getExercise());
        return match;
    }

    public static Exam loadExam(){
        Exam match = null;
        if (!getLoggedUser().getCurrentExamId().contains("exam")){
            match = getExams().get(tries%3);
            getLoggedUser().setCurrentExamId(match.getTagsString());
        }else {
            for (int i = 0; i < getExams().size(); i++){
                match = getExams().get(i);
                if (getLoggedUser().getCurrentExamId().contains(match.getTagsString())){
                    break;
                }else {match = null;}
            }
        }
        setExam(match);
        System.out.println("\nRetrieved Exam:\n" + getExam());
        return match;
    }

    public static int countTries(){
        tries = 0;
        for (ExamGrade aGradesList : getExamGrades()) {
            if (aGradesList.getExam_title().contains("Module " +
                    getLoggedUser().getCurrentExamId().charAt(getLoggedUser().getCurrentExamId().length()-1))) {
                tries++;
            }
        }
        System.out.println("No. of Tries: " + tries);
        return tries;
    }

    public static PracticalExercise loadChallenge(String title){
        PracticalExercise match = null;
        for (int i = 0; i < getChallenges().size(); i++) {
            match = getChallenges().get(i);
            if (match.getTitle().contains(title)){
                break;
            }else {match = null;}
        }
        System.out.println("\nRetrieved Challenge:\n" + match);
        return match;
    }

    public static ArrayList<TextLesson> getLessonsInModule(String currentModule) {
        for (int i = 0; i < getTextLessons().size(); i++) {
            TextLesson match = getTextLessons().get(i);
            if (match.getTagsString().contains(currentModule)){
                getLessonsInModule().add(match);
            }
        }
        //System.out.println("Contents of lessons in module:");
        //getLessonsInModule().forEach((a)->System.out.println(a));
        return getLessonsInModule();
    }

    public static void clearLessonsInModule(){
        getLessonsInModule().clear();
    }

    public static void clearExams() { getExams().clear(); }

    public static void clearExamExercises(){ getExamExercises().clear(); }

    public static boolean containsPracticalExercise(PracticalExercise practicalExercise){
        UserExercise match;
        for (int i = 0; i < getUserExercises().size(); i++){
            match = getUserExercises().get(i);
            if (practicalExercise.getTitle().equals(match.getExercise_title())){
                setUserExercise(match);
                return true;
            }
        }
        return false;
    }

    //Run and Compile
    public static boolean runCode(PracticalPrintExercise practicalPrintExercise){
        if (practicalPrintExercise != null) {
            compileCode(practicalPrintExercise);
            setOutput(RuntimeUtil.CONSOLE_OUTPUT.toString());

            String errorString = RuntimeUtil.CONSOLE_ERR_OUTPUT.toString();
            errorString = StringUtil.replaceLineMatch(errorString, RuntimeUtil.LOGGER_SLF4J, ""); //remove log

            if (!errorString.isEmpty()) {
                setOutput("Error: " + errorString + "\n"); //output errors
                setResponse("Error: Compilation");
                return false;
            }

            // -1 means no error; returns index of CString
            int errorCStringIndex = practicalPrintExercise.checkCGroup(practicalPrintExercise.codeProperty());
            boolean correctOutput = practicalPrintExercise.evaluate(RuntimeUtil.CONSOLE_OUTPUT.toString());
            if (errorCStringIndex == -1 && correctOutput) {
                setResponse("Correct");
                return true;
            } else if (errorCStringIndex != -1) {
                setResponse(practicalPrintExercise.getCStringTip(errorCStringIndex));
                return false;
            } else if (!correctOutput) {
                setResponse("Output not match: follow instructions");
                return false;
            }
        }
        setResponse("practicalPrintExercise is null");
        return false;
    }

    private static void compileCode(PracticalPrintExercise practicalPrintExercise) {
        try {
            RuntimeUtil.setOutStream(RuntimeUtil.CONSOLE_STRING_STREAM);
            RuntimeUtil.setErrStream(RuntimeUtil.CONSOLE_ERR_STRING_STREAM);
            RuntimeUtil.reset(RuntimeUtil.CONSOLE_OUTPUT);
            RuntimeUtil.reset(RuntimeUtil.CONSOLE_ERR_OUTPUT);

            RuntimeUtil.setOutStream(RuntimeUtil.CONSOLE_STRING_STREAM);
            RuntimeUtil.reset(RuntimeUtil.CONSOLE_OUTPUT);

            RuntimeUtil.compile(practicalPrintExercise);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            RuntimeUtil.setOutStream(RuntimeUtil.CONSOLE_STREAM);
            RuntimeUtil.setErrStream(RuntimeUtil.CONSOLE_ERR_STREAM);
        }
    }

    //FER methods
    public static void startBrowser(){
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec( "rundll32 url.dll,FileProtocolHandler " + fer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startBackground(){
        Thread thread = new Thread(backgroundProcess, "thread");
        thread.start();
    }

    public static void stopBackground(){
        backgroundProcess.stop();
    }

    //database methods
    public static Users getUserUsingCredentials(String username, String password){
        return UserDatabase.getUserUsingCredentials(username, password);
    }

    public static int addUser(Users user){
        return UserDatabase.addUser(user);
    }

    public static int addUserExercise(UserExercise userExercise){
        setUserExercise(userExercise);
        getUserExercises().add(userExercise);
        return UserDatabase.addUserExercise(userExercise);
    }

    public static int addExamGrade(ExamGrade examGrade){
        setExamGrade(examGrade);
        getExamGrades().add(getExamGrade());
        return UserDatabase.addExamGrade(getExamGrade());
    }

    public static int updateUser(){
        return UserDatabase.updateUser(getLoggedUser());
    }

    public static int updateUserProfilePicture() {
        return UserDatabase.updateUserProfilePicture(loggedUser);
    }

    public static int updateUserExercise(String code){
        int i = getUserExercises().indexOf(getUserExercise());
        getUserExercises().get(i).setCode(code);
        setUserExercise(getUserExercises().get(i));
        return UserDatabase.updateUserExercise(getUserExercise());
    }

    //setters and getters
    public static Users getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(Users loggedUser) {
        Agent.loggedUser = loggedUser;
    }

    public static boolean isCleared() {
        return isExerciseCleared;
    }

    public static void setIsExerciseCleared(boolean isExerciseCleared) {
        Agent.isExerciseCleared = isExerciseCleared;
    }

    public static int getTries() {
        return tries;
    }

    public static void setTries(int tries) {
        Agent.tries = tries;
    }

    public static String getResponse() {
        return response;
    }

    public static void setResponse(String response) {
        Agent.response = response;
    }

    public static String getOutput() {
        return output;
    }

    public static void setOutput(String output) {
        Agent.output = output;
    }

    public static ObjectProperty<Image> imageProperty() {
        return imageProperty;
    }

    public static void setImageProperty(Image imageProperty) {
        Agent.imageProperty.set(imageProperty);
    }

    public static UserExercise getUserExercise() {
        return userExercise;
    }

    public static void setUserExercise(UserExercise userExercise) {
        Agent.userExercise = userExercise;
    }

    public static TextLesson getLesson() {
        return lesson;
    }

    public static void setLesson(TextLesson lesson) {
        Agent.lesson = lesson;
    }

    public static VideoPracticalExercise getExercise() {
        return exercise;
    }

    public static void setExercise(VideoPracticalExercise exercise) {
        Agent.exercise = exercise;
    }

    public static PracticalPrintExercise getExamExercise() {
        return examExercise;
    }

    public static void setExamExercise(PracticalPrintExercise examExercise) {
        Agent.examExercise = examExercise;
    }

    public static Exam getExam() {
        return exam;
    }

    public static void setExam(Exam exam) {
        Agent.exam = exam;
    }

    public static ExamGrade getExamGrade() {
        return examGrade;
    }

    public static void setExamGrade(ExamGrade examGrade) {
        Agent.examGrade = examGrade;
    }

    public static ArrayList<UserExercise> getUserExercises() {
        return userExercises;
    }

    public static ArrayList<TextLesson> getTextLessons() {
        return textLessons;
    }

    public static ArrayList<TextLesson> getLessonsInModule() {
        return lessonsInModule;
    }

    public static ArrayList<VideoPracticalExercise> getVideoPracticalExercises() {
        return videoPracticalExercises;
    }

    public static ArrayList<PracticalExercise> getChallenges() {
        return challenges;
    }

    public static ArrayList<PracticalPrintExercise> getExamExercises() {
        return examExercises;
    }

    public static ArrayList<Exam> getExams() {
        return exams;
    }

    public static ArrayList<ExamGrade> getExamGrades() {
        return examGrades;
    }

}
