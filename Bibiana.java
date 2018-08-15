// *******************************
// * Name: Joseph Weissig
// * Class: CS 162 Spring 2018
// * Class time: 1000
// * Date: June 12, 2018
// * Project: Final
// * Driver Name: Bibiana.java
// * Program Description: Pulls a random cocktail. "Bibiana" was the patron saint of the hungover.
// * Sorry it doesn't label which requirements it fulfills; I pulled an all-nighter to get it working!
// * The python script was to create the alcohol.json file.
// *******************************


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Bibiana extends Application {
    private ArrayList<Recipe> recipes;
    private BorderPane border;
    private VBox buttonBox;
    private VBox informationBox;
    private GridPane buttonPane;
    private GridPane informationPane;

    private Text name;
    private Text alcoholInfo;
    private Text mixerInfo;
    private Text specialInfo;
    private Text prepInfo;

    private Button random;
    private Button ageButton;
    private TextField ageField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CS162 Final: Cocktail Finder");
        
        recipes = getRecipes();
        border = new BorderPane();
        buttonBox = new VBox(100);
        buttonPane = new GridPane();
        informationBox = new VBox(100);
        informationPane = new GridPane();

        buttonBox.getChildren().add(buttonPane);
        informationBox.getChildren().add(informationPane);

        border.setLeft(buttonBox);
        border.setCenter(informationBox);
        border.setMargin(buttonPane, new Insets(40, 15, 40, 15));
        border.setMargin(informationPane, new Insets(30, 30, 30, 30));

        buttonPane.getColumnConstraints().add(new ColumnConstraints(100));
        buttonPane.setVgap(15);

        informationPane.getColumnConstraints().add(new ColumnConstraints(600));
        informationPane.setVgap(15);

        random = new Button("Get random recipe");
        random.setDisable(true);
        RandomBtnClicked rbc = new RandomBtnClicked();
        random.setOnAction(rbc);
        buttonPane.add(random, 0, 0, 2, 1);

        ageButton = new Button("Submit age");
        AgeBtnClicked abc = new AgeBtnClicked();
        ageButton.setOnAction(abc);
        buttonPane.add(ageButton, 1, 1);

        ageField = new TextField("Enter age");
        buttonPane.add(ageField, 0, 1);

        name = new Text("");
        informationPane.add(name, 0, 0);

        alcoholInfo = new Text("");
        informationPane.add(alcoholInfo, 0, 1);

        mixerInfo = new Text("");
        informationPane.add(mixerInfo, 0, 2);

        specialInfo = new Text("");
        informationPane.add(specialInfo, 0, 3);

        prepInfo = new Text("");
        informationPane.add(prepInfo, 0, 4);

        

        Scene scene = new Scene(border, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public class AgeBtnClicked implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            int ageInput = Integer.parseInt(ageField.getText());
            if (ageInput >= 21) {
                random.setDisable(false);
            } else {
                random.setDisable(true);
            }
        }
    }

    public class RandomBtnClicked implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Random random = new Random();
            int index = random.nextInt(recipes.size());
            Recipe r = recipes.get(index);
            ArrayList<String> information = r.display();
            name.setText(information.get(0));
            alcoholInfo.setText(information.get(1));
            mixerInfo.setText(information.get(2));
            specialInfo.setText(information.get(3));
            prepInfo.setText(information.get(4));
        }
    }

    public ArrayList<Recipe> getRecipes() {
        JSONParser parser = new JSONParser();
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();

        try {
            JSONArray recipeArrays = (JSONArray) parser.parse(new FileReader("iba-cocktails/recipes.json"));
            for (Object o : recipeArrays) {
                JSONObject recipe = (JSONObject) o;
                String name = (String) recipe.get("name");
                String prep = (String) recipe.get("preparation");
                JSONArray ingredients = (JSONArray) recipe.get("ingredients");
                recipes.add(new Recipe(name, prep, ingredients));
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        
        return recipes;
    }   
}