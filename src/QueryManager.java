import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.log4j.varia.NullAppender;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 14-12-2016.
 */
public class QueryManager {
    private Dataset dataset;
    private Model model;
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";

    QueryManager(){
        dataset = TDBFactory.createDataset("./tdb");
        model = dataset.getDefaultModel();
    }

    public void closeConnections(){
        model.close();
        dataset.close();
    }

    //getArtists
    public ArrayList<String> getArtists(){
        ArrayList<String> result = new ArrayList<>();

        String sparqlQuery = "SELECT ?name WHERE {?x <" + nameSpace + "hasName> ?name}";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while(results.hasNext()){
            QuerySolution qs = results.nextSolution();
            RDFNode temp = qs.get("name");
            result.add(temp.asNode().getLocalName());
        }

        qe.close();

        return result;
    }


    //getArtistInfo


    //getAlbums


    //getAlbumInfo


    //getValueFromTable(search_info, type)
    //search_info = band -> compare to None







}
