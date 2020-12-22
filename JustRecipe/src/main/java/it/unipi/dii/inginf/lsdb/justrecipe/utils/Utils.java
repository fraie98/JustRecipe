package it.unipi.dii.inginf.lsdb.justrecipe.utils;

import com.thoughtworks.xstream.XStream;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.CommentController;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.RecipeSnapshotController;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.UserSnapshotController;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import it.unipi.dii.inginf.lsdb.justrecipe.model.User;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.management.jfr.RecordingInfo;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class that contains some useful method
 */
public class Utils {

    /**
     * Snippet of code for jumping in the next scene
     * Every scene has associated its specific controller
     * @param fileName      The name of the file in which i can obtain the GUI (.fxml)
     * @param event         The event that leads to change the scene
     * @return The new controller, because I need to pass some parameters
     */
    public static Object changeScene (String fileName, Event event)
    {
        Scene scene = null;
        FXMLLoader loader = null;
        try {
            loader=new FXMLLoader(Utils.class.getResource(fileName));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function is used to read the config.xml file
     * @return  ConfigurationParameters instance
     */
    public static ConfigurationParameters readConfigurationParameters ()
    {
        if (validConfigurationParameters())
        {
            XStream xs = new XStream();

            String text = null;
            try {
                text = new String(Files.readAllBytes(Paths.get("./config.xml")));
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }

            return (ConfigurationParameters) xs.fromXML(text);
        }
        else
        {
            System.exit(1); //If i can't read the configuration file I can't continue with the program
        }
        return null;
    }

    /**
     * This function is used to validate the config.xml with the config.xsd
     * @return  true if config.xml is well formatted, otherwise false
     */
    private static boolean validConfigurationParameters()
    {
        try
        {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document document = documentBuilder.parse("./config.xml");
            Schema schema = schemaFactory.newSchema(new StreamSource("./config.xsd"));
            schema.newValidator().validate(new DOMSource(document));
        }
        catch (Exception e)
        {
            if (e instanceof SAXException)
                System.out.println("Validation Error: " + e.getMessage());
            else
                System.out.println(e.getMessage());

            return false;
        }
        return true;
    }

    /**
     * Function used to get the string representation of a List<String>, with every element separated by comma
     * @param list  the list to transform
     * @return      the string representation
     */
    public static String fromListToString (List<String> list)
    {
        String string = "";
        if (list != null)
        {
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext())
            {
                string = string.concat(iterator.next());
                if (iterator.hasNext())
                {
                    string = string.concat(", ");
                }
            }
        }
        return string;
    }

    /**
     * Function used to transform a string in an list of string, divided by comma
     * @param string        String to consider
     * @return              A list of the substring
     */
    public static List<String> fromStringToList (String string)
    {
        List<String> list = new ArrayList<>();
        list = Arrays.asList(string.split(","));
        return list;
    }

    /**
     * Function that transform a Date object in the standard format chosen for the application
     * @param date
     * @return
     */
    public static String fromDateToString (Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * Function that shows an error alert
     * @param text  Text to be shown
     */
    public static void showErrorAlert (String text)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.setHeaderText("Ops.. Something went wrong..");
        alert.setTitle("Error");
        ImageView imageView = new ImageView(new Image("/img/emoticon-cry.png"));
        alert.setGraphic(imageView);
        alert.show();
    }

    /**
     * Function that shows an information windows
     * @param text  Text to be shown
     */
    public static void showInfoAlert (String text)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(text);
        alert.setHeaderText("Important Message");
        alert.setTitle("Information");
        ImageView imageView = new ImageView(new Image("/img/info.png"));
        imageView.setFitHeight(45);
        imageView.setFitWidth(45);
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);
        alert.show();
    }

    /**
     * This function create a pane that contains a recipe snapshot
     * @param recipe    recipe to display in the snapshot
     * @return
     */
    private static Pane createRecipeSnapshot(Recipe recipe)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/recipeSnap.fxml"));
            pane = (Pane) loader.load();
            RecipeSnapshotController recipeSnapshotController =
                    (RecipeSnapshotController) loader.getController();
            recipeSnapshotController.setRecipe(recipe);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    /**
     * Function that adds the snapshots of the recipes, 2 for each row
     * @param vBox      VBox in which I have to show the snapshots
     * @param recipes   Recipes to show
     */
    public static void addRecipesSnap(VBox vBox, List<Recipe> recipes) {
        Iterator<Recipe> iterator = recipes.iterator();
        while (iterator.hasNext())
        {
            HBox row = new HBox();
            row.setStyle("-fx-padding: 10px");
            row.setSpacing(20);
            Recipe recipe1 = iterator.next();
            Pane rec1 = createRecipeSnapshot(recipe1);
            row.getChildren().add(rec1);
            if (iterator.hasNext())
            {
                Recipe recipe2 = iterator.next();
                Pane rec2 = createRecipeSnapshot(recipe2);
                row.getChildren().add(rec2);
            }
            vBox.getChildren().add(row);;
        }
    }

    /**
     * Function used to show the comments
     * @param vBox      VBox in which I have to show the comments
     * @param comments  Comments to show
     */
    public static void showComments(VBox vBox, List<Comment> comments, Recipe recipe) {
        Iterator<Comment> iterator = comments.iterator();
        vBox.setSpacing(20);
        while (iterator.hasNext())
        {
            Comment comment = iterator.next();
            Pane commentPane = loadComment(comment, recipe);
            vBox.getChildren().add(commentPane);
        }
    }

    /**
     * Function used to show one comment
     * @param pane      Pane in which I have to show the comments
     * @param comment  Comments to show
     */
    public static void showComment(Pane pane, Comment comment, Recipe recipe){
        Pane commentPane = loadComment(comment, recipe);
        pane.getChildren().add(commentPane);
    }

    /**
     * Function used to load the .fxml for the comment
     * @param comment   Comment to show
     * @return          The pane in which I have showed the comment
     */
    private static Pane loadComment (Comment comment, Recipe recipe)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/comment.fxml"));
            pane = (Pane) loader.load();
            CommentController commentController =
                    (CommentController) loader.getController();
            commentController.setComment(comment, recipe);
//            commentController.setRecipeName(recipeName);                                                              //DEBUG
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    /**
     * Function used to load the .fxml for the snapshot of the user
     * @param user      User to load
     * @return          The pane in which the snapshot has been loaded
     */
    private static Pane loadUserSnapshot (User user)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/userSnap.fxml"));
            pane = (Pane) loader.load();
            UserSnapshotController userSnapshotController =
                    (UserSnapshotController) loader.getController();
            userSnapshotController.setUserSnap(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }

    /**
     * Function that load the snapshot of the users in the pane
     * There will be four snapshot for each row (if possible)
     * @param vBox          VBox in which the snapshot has to be inserted
     * @param users         The users to show
     */
    public static void addUsersSnap(VBox vBox, List<User> users) {
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext())
        {
            HBox row = new HBox();
            row.setStyle("-fx-padding: 10px");
            row.setSpacing(10);
            User user1 = iterator.next();
            Pane pane1 = loadUserSnapshot (user1);
            row.getChildren().add(pane1);
            if (iterator.hasNext())
            {
                User user2 = iterator.next();
                Pane pane2 = loadUserSnapshot (user2);
                row.getChildren().add(pane2);

                if (iterator.hasNext())
                {
                    User user3 = iterator.next();
                    Pane pane3 = loadUserSnapshot (user3);
                    row.getChildren().add(pane3);

                    if (iterator.hasNext())
                    {
                        User user4 = iterator.next();
                        Pane pane4 = loadUserSnapshot (user4);
                        row.getChildren().add(pane4);
                    }
                }
            }
            vBox.getChildren().add(row);
        }
    }

    /**
     * Function used to remove all element on a pane
     * @param pane      Pane to free
     */
    public static void removeAllFromPane (Pane pane)
    {
        pane.getChildren().remove(0, pane.getChildren().size());
    }

}
