import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.rdf.model.RDFNode;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;


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

    private ArrayList<ArrayList<String>> executeIDQuery(String sparqlQuery, String parameter, String id){
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<ArrayList<String>> return_result = new ArrayList<>();

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while(results.hasNext()){
            QuerySolution qs = results.nextSolution();
            RDFNode temp = qs.get(parameter);
            RDFNode temp_id = qs.get(id);
            if(!temp.toString().equals("(null)")) {
                result.add(temp.toString());
                ids.add(temp_id.toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", ""));
            }
        }

        for(int i=0; i<ids.size(); i++){
            System.out.println(result.get(i) + " " + ids.get(i));
        }

        qe.close();

        return_result.add(result);
        return_result.add(ids);

        return return_result;
    }

    //getArtists
    public ArrayList<ArrayList<String>> getArtists(){
        String sparqlQuery = "SELECT ?name ?id WHERE {?x <" + nameSpace + "hasName> ?name. ?x <" + nameSpace + "hasID> ?id} ORDER BY ?name";

        return executeIDQuery(sparqlQuery, "name", "id");
    }

    public ArrayList<ArrayList<String>> getArtistByName(String name){
        name = name.toLowerCase();
        String sparqlQuery = "SELECT ?name ?id WHERE {?x <" + nameSpace + "hasName> ?name . ?x <" + nameSpace + "hasID> ?id . FILTER(STRSTARTS(lcase(?name),\"" + name + "\"))}";

        return executeIDQuery(sparqlQuery, "name", "id");
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
    public ArrayList<ArrayList<String>> getArtistInfo(String name, String id) {
        ArrayList<String> result = new ArrayList<>();
        result.add(name);

        //  Gender
        String gender = getSingleInfo(id, "hasID", "hasGender");
        result.add(gender);

        //  Dates
        String beginDate, endDate;
        if(gender != null){
            beginDate = getSingleInfo(id, "hasID", "hasBornDate");
            endDate = getSingleInfo(id, "hasID", "hasDeathDate");
        }
        else{
            beginDate = getSingleInfo(id, "hasID", "hasBeginDate");
            endDate = getSingleInfo(id, "hasID", "hasEndDate");
        }
        result.add(beginDate);
        result.add(endDate);

        //  Location
        String location = getSingleInfo(id, "hasID", "hasLocation");
        result.add(location);

        //  Description
        String description = getSingleInfo(id, "hasID", "hasDescription");
        int index = (description.contains("<a"))? description.indexOf("<a") : 0;
        description = description.substring(0, index) + " (...)";
        result.add(description);

        //  LastFM Page
        String lastFM = getSingleInfo(id, "hasID", "hasLastFMPage");
        result.add(lastFM);

        // result = {name, gender, beginDate, endDate, location, description, lastFM};

        ArrayList<ArrayList<String>> results = new ArrayList<>();
        results.add(result);

        //  albums = {{album1, album2, ...},{id_album1, id_album2, ...}}
        ArrayList<ArrayList<String>> albums = getArtistAlbums(id);
        results.add(albums.get(0));
        results.add(albums.get(1));

        return results;
    }

    public String getSingleInfo(String name, String identifier, String attribute){
        name = name.toLowerCase();
        String sparlQuery = "SELECT ?attribute " +
                "WHERE" +
                "   { ?x <" + nameSpace + identifier + "> ?name ." +
                "     ?x <" + nameSpace + attribute + "> ?attribute ." +
                "   FILTER(lcase(str(?name)) = \"" + name + "\")}";

        try{
            return executeQuery(sparlQuery, "attribute").get(0);
        }catch (Exception e){
            return null;
        }
    }

    public ArrayList<ArrayList<String>> getArtistAlbums(String id){
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT ?album ?id WHERE { ?x :isAlbumOf :"+ id +". ?x :hasTitle ?album . ?x :hasID ?id} ORDER BY ?album";

        return executeIDQuery(sparqlQuery, "album", "id");
    }

    //getAlbums
    public ArrayList<ArrayList<String>> getAlbums(){
        String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "SELECT DISTINCT ?title ?id\n" +
                "WHERE {\n" +
                "  ?s rdf:type <"+nameSpace+"Album>.\n" +
                " ?s <"+nameSpace+"hasTitle> ?title ."+
                " ?s <"+nameSpace+"hasID> ?id}" +
                "ORDER BY ?title";

        return executeIDQuery(sparqlQuery, "title", "id");
    }

    public ArrayList<ArrayList<String>> getAlbumsByTitle(String title){
        title = title.toLowerCase();
        String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?title ?id WHERE {?x rdf:type <" + nameSpace + "Album>. ?x <" + nameSpace + "hasTitle> ?title . ?x <" + nameSpace + "hasID> ?id . FILTER(STRSTARTS(lcase(?title),\"" + title + "\"))}";

        return executeIDQuery(sparqlQuery, "title", "id");
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
    public ArrayList<ArrayList<String>> getAlbumInfo(String title, String id){
        ArrayList<ArrayList<String>> results = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();

        //  Title
        result.add(title);

        title = title.toLowerCase();

        //  Artist
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT ?artist ?id WHERE {:" + id + " :isAlbumOf ?y. ?y :hasName ?artist . ?y :hasID ?id}";
        ArrayList<ArrayList<String>> temp = executeIDQuery(sparqlQuery, "artist", "id");
        String artist = temp.get(0).get(0);
        String artistID = temp.get(1).get(0);
        result.add(artist);

        //  Image
        String image = getSingleInfo(id, "hasID", "hasImage");
        result.add(image);

        //  Description
        String description = getSingleInfo(id, "hasID", "hasDescription");
        int index = (description.contains("<a"))? description.indexOf("<a") : 0;
        description = description.substring(0, index) + " (...)";
        result.add(description);

        //  LastFMPage
        String lastFM = getSingleInfo(id, "hasID", "hasLastFMPage");
        result.add(lastFM);

        //  Add artist ID
        result.add(artistID);

        //result = {title, artist, image, description, lastfm, id}
        results.add(result);

        //  Tracks
        // {number1, title1, length1, id1, number2, title2, length2, id2, ...}
        ArrayList<ArrayList<String>> tracks = getAlbumTracks(id);
        for (ArrayList<String> list: tracks ) {
            results.add(list);
        }

        return results;
    }

    private ArrayList<ArrayList<String>> getAlbumTracks(String id){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?track ?number ?length ?id WHERE { ?id :isTrackOf :" + id + ". ?id :hasTitle ?track . ?id :hasNumber ?number. ?id :hasLength ?length}";

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
            // Track ID
            temp = qs.get("id");
            aux.add(temp.toString().replace("http://www.semanticweb.org/rocha/ontologies/SemanticMusic#", ""));
        }

        Collections.sort(sortingArray.subList(0, sortingArray.size()));
        int index;
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> length = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();

        for (Integer var: sortingArray) {
            index = aux.indexOf(String.valueOf(var));
            number.add(aux.get(index));
            name.add(aux.get(index+1));
            length.add(aux.get(index+2));
            ids.add(aux.get(index+3));
        }

        qe.close();

        for(int i=0; i<ids.size(); i++){
            System.out.println(name.get(i) + " " + ids.get(i));
        }

        result.add(number);
        result.add(name);
        result.add(length);
        result.add(ids);

        return result;
    }

    public ArrayList<ArrayList<String>> getTracksByTitle(String title){
        title = title.toLowerCase();
        String sparqlQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?title ?id WHERE {?x rdf:type <" + nameSpace + "Track>. ?x <" + nameSpace + "hasTitle> ?title . ?x <" + nameSpace + "hasID> ?id . FILTER(STRSTARTS(lcase(?title),\"" + title + "\"))}";

        return executeIDQuery(sparqlQuery, "title", "id");
    }

    public ArrayList<ArrayList<String>> getTrackInfo(String title, String id){
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?number ?length ?artist ?album WHERE {:"+id + " :isTrackOf ?y. ?y :hasTitle ?album . ?y :isAlbumOf ?z . ?z :hasName ?artist .:" + id + " :hasNumber ?number. :" + id + " :hasLength ?length}";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet resultset = qe.execSelect();

        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        ArrayList<ArrayList<String>> results = new ArrayList<>();
        results.add(result);

        ArrayList<String> album = new ArrayList<>();
        ArrayList<String> artist = new ArrayList<>();
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> length = new ArrayList<>();
        while(resultset.hasNext()) {
            QuerySolution qs = resultset.nextSolution();
            //  Album
            RDFNode temp = qs.get("album");
            album.add(temp.toString());

            //  Artist
            temp = qs.get("artist");
            artist.add(temp.toString());

            //  Number
            temp = qs.get("number");
            number.add(temp.toString());

            //  Length
            temp = qs.get("length");
            LocalTime timeOfDay = LocalTime.ofSecondOfDay(Integer.parseInt(temp.toString()));
            length.add(timeOfDay.toString());
        }

        results.add(album);
        results.add(artist);
        results.add(number);
        results.add(length);

        return results;
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
