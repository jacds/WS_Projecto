import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.tdb.lib.StringAbbrev;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class QueryManager {
    private Dataset dataset;
    private Model model;
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";

    QueryManager(){
        try{
            dataset = TDBFactory.createDataset("/Users/Rocha/Documents/WS_Projecto/tdb"); //Directorio Rocha
        }catch (Exception e){
            dataset = TDBFactory.createDataset("C:/Users/Asus/WebApplication/tdb"); //Directorio Silva
        }
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
        String gender = getSingleInfo(name, "hasName", "hasGender");
        result.add(gender);

        //  Dates
        String beginDate, endDate;
        if(gender != null){
            beginDate = getSingleInfo(name, "hasName", "hasBornDate");
            endDate = getSingleInfo(name, "hasName", "hasDeathDate");
        }
        else{
            beginDate = getSingleInfo(name, "hasName", "hasBeginDate");
            endDate = getSingleInfo(name, "hasName", "hasEndDate");
        }
        result.add(beginDate);
        result.add(endDate);

        //  Location
        String location = getSingleInfo(name, "hasName", "hasLocation");
        result.add(location);

        //  Description
        String description = getSingleInfo(name, "hasName", "hasDescription");
        int index = (description.contains("<a"))? description.indexOf("<a") : 0;
        description = description.substring(0, index) + " ...";
        result.add(description);

        //  LastFM Page
        String lastFM = getSingleInfo(name, "hasName", "hasLastFMPage");
        result.add(lastFM);

        // result = {name, gender, beginDate, endDate, location, description, lastFM};

        ArrayList<ArrayList<String>> results = new ArrayList<>();
        results.add(result);

        //  result = {album1, album2, ...}

        result = getArtistAlbums(name);
        results.add(result);

        return results;
    }

    public String getSingleInfo(String name, String identifier, String attribute){
        String sparlQuery = "SELECT ?attribute " +
                "WHERE" +
                "   { ?x <" + nameSpace + identifier + "> \"" + name + "\" ." +
                "     ?x <" + nameSpace + attribute + "> ?attribute ." +
                "   }";

        try{
            return executeQuery(sparlQuery, "attribute").get(0);
        }catch (Exception e){
            return null;
        }
    }

    public ArrayList<String> getArtistAlbums(String name){
        String artistID = getSingleInfo(name, "hasName", "hasID");
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
        //hasImage
        //hasDescription
        //hasLastFMPage
        //hasTrack
            //hasTitle
            //hasNumber
            //hasLength
    public ArrayList<ArrayList<String>> getAlbumInfo(String title){
        ArrayList<ArrayList<String>> results = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();

        //  Title
        result.add(title);

        //  Artist
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT ?artist WHERE { ?x :isAlbumOf ?y. ?x :hasTitle \"" + title +"\" . ?y :hasName ?artist}";
        String artist = executeQuery(sparqlQuery, "artist").get(0);
        result.add(artist);

        //  Image
        String image = getSingleInfo(title, "hasTitle", "hasImage");
        result.add(image);

        //  Description
        String description = getSingleInfo(title, "hasTitle", "hasDescription");
        int index = (description.contains("<a"))? description.indexOf("<a") : 0;
        description = description.substring(0, index) + " ...";
        result.add(description);

        //  LastFMPage
        String lastFM = getSingleInfo(title, "hasTitle", "hasLastFMPage");
        result.add(lastFM);

        //result = {title, artist, image, description, lastfm}
        results.add(result);

        //  Tracks
        ArrayList<ArrayList<String>> tracks = getAlbumTracks(title);
        for (ArrayList<String> list: tracks ) {
            results.add(list);
        }

        //result = {number1, title1, length1, number2, title2, length2, ...}
        results.add(result);

        return results;
    }

    private ArrayList<ArrayList<String>> getAlbumTracks(String title){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?track ?number ?length WHERE { ?x :isTrackOf ?y. ?y :hasTitle \"" + title + "\". ?x :hasTitle ?track . ?x :hasNumber ?number. ?x :hasLength ?length}";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        ArrayList<Integer> sortingArray = new ArrayList<>();
        ArrayList<String> aux = new ArrayList<>();

        while(results.hasNext()){
            QuerySolution qs = results.nextSolution();
            // Track Number
            RDFNode temp = qs.get("number");
            if(aux.contains(temp.toString())){
                continue;
            }
            aux.add(temp.toString());
            sortingArray.add(Integer.parseInt(temp.toString()));
            //  Track Title
            temp = qs.get("track");
            aux.add(temp.toString());
            // Track Length
            temp = qs.get("length");
            aux.add(temp.toString());
        }




        Collections.sort(sortingArray.subList(0, sortingArray.size()));
        int index;
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> length = new ArrayList<>();

        for (Integer var: sortingArray) {
            index = aux.indexOf(String.valueOf(var));
            number.add(aux.get(index));
            name.add(aux.get(index+1));
            length.add(aux.get(index+2));
            System.out.println(aux.get(index) + aux.get(index+1) + aux.get(index+2));
        }

        qe.close();

        result.add(number);
        result.add(name);
        result.add(length);

        return result;
    }

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
