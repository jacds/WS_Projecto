import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;

import org.apache.jena.rdf.model.RDFNode;

import java.util.ArrayList;


public class QueryManager {
    private Dataset dataset;
    private Model model;
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";

    QueryManager(){
        //dataset = TDBFactory.createDataset("/Users/Rocha/Documents/WS_Projecto/tdb"); //Directorio Rocha
        dataset = TDBFactory.createDataset("C:/Users/Asus/WebApplication/tdb"); //Directorio Silva
        model = dataset.getDefaultModel();
    }

    public void closeConnections(){
        model.close();
        dataset.close();
    }

    //getArtists
    public ArrayList<String> getArtists(){
        String sparqlQuery = "SELECT ?name WHERE {?x <" + nameSpace + "hasName> ?name}";

        return executeQuery(sparqlQuery, "name");
    }


    //getArtistInfo
        //hasName
        //hasGender (if person)
        //hasBirthDate (if person)
        //hasBeginDate (if group)
        //hasDeathDate (if person)
        //hasEndDate (if group)
        //hasLocation
        //hasDescription
        //hasLastFMPage
        //hasAlbum
            //hasTitle
    public ArrayList<ArrayList<String>> getArtistInfo(String name) {
        ArrayList<String> result = new ArrayList<>();
        result.add(name);

        //  Gender
        String gender = getArtistSingleInfo(name, "hasGender");
        result.add(gender);

        //  Dates
        String beginDate, endDate;
        if(gender != null){
            beginDate = getArtistSingleInfo(name, "hasBornDate");
            endDate = getArtistSingleInfo(name, "hasDeathDate");
        }
        else{
            beginDate = getArtistSingleInfo(name, "hasBeginDate");
            endDate = getArtistSingleInfo(name, "hasEndDate");
        }
        result.add(beginDate);
        result.add(endDate);

        //  Location
        String location = getArtistSingleInfo(name, "hasLocation");
        result.add(location);

        //  Description
        String description = getArtistSingleInfo(name, "hasDescription");
        result.add(description);

        //  LastFM Page
        String lastFM = getArtistSingleInfo(name, "hastLastFMPage");
        result.add(lastFM);

        // result = {name, gender, beginDate, endDate, location, description, lastFM};

        ArrayList<ArrayList<String>> results = new ArrayList<>();
        results.add(result);

        //  result = {album1, album2, ...}

        result = getArtistAlbums(name);
        results.add(result);

        return results;
    }

    public String getArtistSingleInfo(String name, String attribute){
        String sparlQuery = "SELECT ?attribute " +
                "WHERE" +
                "   { ?x <" + nameSpace + "hasName> \"" + name + "\" ." +
                "     ?x <" + nameSpace + attribute + "> ?attribute ." +
                "   }";

        try{
            return executeQuery(sparlQuery, "attribute").get(0);
        }catch (Exception e){
            return null;
        }
    }

    public ArrayList<String> getArtistAlbums(String name){
        String artistID = getArtistSingleInfo(name, "hasID");
        artistID = artistID.replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT ?album WHERE { ?x :isAlbumOf :"+ artistID +". ?x :hasTitle ?album}";

        return executeQuery(sparqlQuery, "album");
    }

    //getAlbums
    public ArrayList<String> getAlbums(){
        ArrayList<String> result = new ArrayList<>();
        String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "SELECT DISTINCT ?title\n" +
                "WHERE {\n" +
                "  ?s rdf:type <"+nameSpace+"Album>.\n" +
                " ?s <"+nameSpace+"hasTitle> ?title "+
                "}";

        return executeQuery(sparqlQuery, "title");
    }


    //getAlbumInfo
        //hasTitle
        //isAlbumOf
        //hasTrack
            //hasTitle
            //hasNumber
            //hasLength
            //hasLastFMPage
        //hasImage
        //hasLastFMPage


    //getValueFromTable(search_info, type)
    //search_info = band -> compare to None

    private ArrayList<String> executeQuery(String sparqlQuery, String parameter){
        ArrayList<String> result = new ArrayList<>();

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while(results.hasNext()){
            QuerySolution qs = results.nextSolution();
            RDFNode temp = qs.get(parameter);
            if(!temp.toString().equals("(null)"))
                result.add(temp.toString());
        }

        qe.close();

        return result;
    }
}
