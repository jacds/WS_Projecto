import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

public class TDB {
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";

    public void createTDB(){
        // open TDB dataset
        String directory = "./tdb";
        Dataset dataset = TDBFactory.createDataset(directory);

        Model tdb = dataset.getDefaultModel();

// read the input file
        String source = "PopulatedProject.owl";
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

        String name = "Muse";
        String queryString = "SELECT ?gender " +
                "WHERE" +
                "   { ?x <" + nameSpace + "hasName> \"" + name + "\" ." +
                "     ?x <" + nameSpace + "hasGender> ?gender ." +
                "   }";

        /*Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            results = ResultSetFactory.copyResults(results);
            ResultSetFormatter.out(System.out, results, query) ;
        }*/

        QueryManager qm = new QueryManager();
        String result = qm.getArtistSingleInfo("Elvis Presley", "hasGender");
        qm.closeConnections();
        System.out.println(result);

        /*ResIterator iter = model.listSubjects();

        try {
            while ( iter.hasNext() ) {
                Resource stmt = iter.next();

                System.out.println(stmt);
            }
        } finally {
            if ( iter != null ) iter.close();
        }*/

        model.close();
        dataset.close();
    }

}
