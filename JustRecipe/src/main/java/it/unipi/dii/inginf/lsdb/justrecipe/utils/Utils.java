package it.unipi.dii.inginf.lsdb.justrecipe.utils;

import com.thoughtworks.xstream.XStream;
import it.unipi.dii.inginf.lsdb.justrecipe.config.ConfigurationParameters;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.CommentController;
import it.unipi.dii.inginf.lsdb.justrecipe.controller.RecipeSnapshotController;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Comment;
import it.unipi.dii.inginf.lsdb.justrecipe.model.Recipe;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext())
        {
            string = string.concat(iterator.next());
            if (iterator.hasNext())
            {
                string = string.concat(", ");
            }
        }
        return string;
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
     * @param pane      Pane in which I have to show the snapshots
     * @param recipes   Recipes to show
     */
    public static void addRecipesSnap(Pane pane, List<Recipe> recipes) {
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
            pane.getChildren().add(row);
        }
    }

    /**
     * Function used to show the comments
     * @param pane      Pane in which I have to show the comments
     * @param comments  Comments to show
     */
    public static void showComments(Pane pane, List<Comment> comments) {
        Iterator<Comment> iterator = comments.iterator();
        while (iterator.hasNext())
        {
            Comment comment = iterator.next();
            Pane commentPane = loadComment(comment);
            pane.getChildren().add(commentPane);
        }
    }

    /**
     * Function used to load the .fxml for the comment
     * @param comment   Comment to show
     * @return          The pane in which I have showed the comment
     */
    private static Pane loadComment (Comment comment)
    {
        Pane pane = null;
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource("/comment.fxml"));
            pane = (Pane) loader.load();
            CommentController commentController =
                    (CommentController) loader.getController();
            commentController.setComment(comment);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pane;
    }
}
