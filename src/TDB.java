import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import info.debatty.java.stringsimilarity.*;

//TODO GET ARTIST PHOTO FROM LASTFM API
public class TDB {
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";

    public static void createTDB(){
        // open TDB dataset
        String directory = "./tdb";
        Dataset dataset = TDBFactory.createDataset(directory);

        Model tdb = dataset.getDefaultModel();

// read the input file
        String source = "data/PopulatedProject.owl";
        FileManager.get().readModel( tdb, source);

        tdb.close();
        dataset.close();
    }

    public static void main(String[] args) {

        createTDB();
    }

}
