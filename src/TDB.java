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

        //String name = "Muse";
        /*String queryString = "SELECT ?gender " +
                "WHERE" +
                "   { ?x <" + nameSpace + "hasName> \"" + name + "\" ." +
                "     ?x <" + nameSpace + "hasGender> ?gender ." +
                "   }";*/

        //QueryManager qm = new QueryManager();
        //String album = "Christmas Hits";

        //String description = qm.getSingleInfo("elvis presley", "hasName", "hasDescription");
        //System.out.println(description);

        //String queryString = "PREFIX : <" + nameSpace + "> SELECT ?track ?number ?length WHERE { ?x :isTrackOf ?y. ?y :hasTitle \"" + album + "\". ?x :hasTitle ?track . ?x :hasNumber ?number. ?x :hasLength ?length}";
        /*String album = "ChangesBowie";
        String queryString = "PREFIX : <" + nameSpace + "> SELECT ?artist WHERE { ?x :isAlbumOf ?y. ?x :hasTitle \"" + album +"\" . ?y :hasName ?artist}";

        /*String sparlQuery = "SELECT ?attribute " +
                "WHERE" +
                "   { ?x <" + nameSpace + "hasID> \"" + name + "\" ." +
                "     ?x <" + nameSpace + attribute + "> ?attribute ." +
                "   }";

        /*QueryManager qm = new QueryManager();
        String asd = qm.getArtistSingleInfo("Elvis Presley", "hasID");
        asd = asd.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
        System.out.println(Integer.parseInt(asd));*/

        //SELECT ?subject ?object
        //WHERE { ?subject :hasCity ?object }
        String queryString = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?number ?length ?artist ?album WHERE { ?x :isTrackOf ?y. ?x :hasTitle \"" + "Hysteria" + "\". ?y :hasTitle ?album . ?y :isAlbumOf ?z . ?z :hasName ?artist .?x :hasNumber ?number. ?x :hasLength ?length}";


        Query query = QueryFactory.create(queryString) ;
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect() ;
            results = ResultSetFactory.copyResults(results);
            ResultSetFormatter.out(System.out, results, query) ;
        }

        /*QueryManager qm = new QueryManager();
        String result = qm.getArtistSingleInfo("Elvis Presley", "hasGender");
        qm.closeConnections();
        System.out.println(result);*/

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
