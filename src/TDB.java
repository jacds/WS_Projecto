import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

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

        //createTDB();
        //LOAD TRIPLE STORE
        Dataset dataset = TDBFactory.createDataset("./tdb");
        //model can be queried with SPARQL
        Model model = dataset.getDefaultModel();


        String queryString = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?number ?length ?artist ?album WHERE { ?x :isTrackOf ?y. ?x :hasTitle \"" + "Hysteria" + "\". ?y :hasTitle ?album . ?y :isAlbumOf ?z . ?z :hasName ?artist .?x :hasNumber ?number. ?x :hasLength ?length}";


        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            results = ResultSetFactory.copyResults(results);
            ResultSetFormatter.out(System.out, results, query) ;
        }

        model.close();
        dataset.close();
    }

}
