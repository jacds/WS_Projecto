import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.rdf.model.RDFNode;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryManager {
    private Dataset dataset;
    private Model model;
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";
    private static final String sparqlPrefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX my: <http://www.semanticweb.org/rocha/ontologies/SemanticMusic#>\n" +
            "\n";

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

        String image = getSingleInfo(id, "hasID", "hasImage");
        result.add(image);

        String genres = getSingleInfo(id, "hasID", "hasGenres");
        result.add(genres.substring(0, genres.length()-1));

        // result = {name, gender, beginDate, endDate, location, description, lastFM, Image};

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

        //result = {title, artist, image, description, lastfm, id, date}
        String date = getSingleInfo(id, "hasID", "hasYear");
        result.add(date);
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

        /*for(int i=0; i<ids.size(); i++){
            System.out.println(name.get(i) + " " + ids.get(i));
        }*/

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
        String sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?number ?length ?artist ?artistID ?album ?albumID WHERE {:"+id + " :isTrackOf ?y. ?y :hasID ?albumID. ?y :hasTitle ?album . ?y :isAlbumOf ?z . ?z :hasID ?artistID. ?z :hasName ?artist .:" + id + " :hasNumber ?number. :" + id + " :hasLength ?length}";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet resultset = qe.execSelect();

        ArrayList<String> result = new ArrayList<>();
        result.add(title);
        ArrayList<ArrayList<String>> results = new ArrayList<>();
        results.add(result);

        ArrayList<String> album = new ArrayList<>();
        ArrayList<String> albumID = new ArrayList<>();
        ArrayList<String> artist = new ArrayList<>();
        ArrayList<String> artistID = new ArrayList<>();
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

            //  AlbumID
            temp = qs.get("albumID");
            albumID.add(temp.toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", ""));

            //  ArtistID
            temp = qs.get("artistID");
            artistID.add(temp.toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", ""));
        }

        results.add(album);
        results.add(artist);
        results.add(number);
        results.add(length);
        results.add(albumID);
        results.add(artistID);

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
            if(!temp.toString().equals("(null)") && !result.contains(temp.toString())) {
                result.add(temp.toString());
                ids.add(temp_id.toString().replace("^^http://www.w3.org/2001/XMLSchema#integer", ""));
            }
        }

        /*System.out.println("IDS: " + ids.size());
        for(int i=0; i<ids.size(); i++){
            System.out.println(result.get(i) + " " + ids.get(i));
        }*/

        qe.close();

        return_result.add(result);
        return_result.add(ids);

        return return_result;
    }


    //  SEMANTIC SEARCH PART
    public ArrayList<ArrayList<String>> getSemanticResults(String search){
        try {
            ArrayList<ArrayList<String>> results;
            ArrayList<String> searchClasses = new ArrayList<>();
            String temp1, temp2;

            //  1 - Parse
            List<String> words = parseSearch(search);
            ArrayList<String> searchValues = new ArrayList<>(words);
            //System.out.println(words);

            //  2- Get Classes
            NormalizedLevenshtein similarity = new NormalizedLevenshtein();
            List<String> temp_classes = getClasses();
            for (String word : words) {
                temp1 = word.toLowerCase();
                for (String cl : temp_classes) {
                    temp2 = cl.toLowerCase();
                    //System.out.println("DISTANCE: " + similarity.distance(temp1, temp2));
                    if (similarity.distance(temp1, temp2) <= 0.4) {
                        searchClasses.add(cl);
                        if (searchValues.contains(word)) {
                            searchValues.remove(word);
                        }
                        break;
                    }
                }
            }

            if (searchClasses.isEmpty()) {
                return null;
            }

            //  3 - Get properties
            HashMap<String, String> listClassProperties = new HashMap<>();
            List<String> searchProperty = new ArrayList<>();

            for (String temp_class : searchClasses) {
                searchProperty.clear();
                List<String> temp_properties;
                HashMap<String, ArrayList<String>> availableValues;
                if (temp_class.equals("Artist")) {
                    temp_properties = getProperties("Group");
                    temp_properties.addAll(getProperties("Person"));
                    Set<String> hs = new HashSet<>();
                    hs.addAll(temp_properties);
                    temp_properties.clear();
                    temp_properties.addAll(hs);
                } else {
                    temp_properties = getProperties(temp_class);
                }

                availableValues = getAvailableValues(temp_properties, temp_class);
                //System.out.println(availableValues);
                //System.out.println(words);
                for (String word : words) {
                    temp1 = word.toLowerCase();
                    //System.out.println(temp1);
                    for (Map.Entry<String, ArrayList<String>> entry : availableValues.entrySet()) {
                        //System.out.println(entry.getKey());
                        if (entry.getValue().contains(temp1)) {
                            listClassProperties.put(entry.getKey(), temp1);
                            if (!(word.equals("female") || word.equals("male"))) {
                                System.out.println("REMOVED: " + word);
                                searchValues.remove(word);
                            }
                        }
                        else if(entry.getKey().equals("hasGenres")){
                            if(new NormalizedLevenshtein().distance(word, "Artist") <= 0.4){
                                searchValues.remove(word);
                            }
                            else{
                                if(!listClassProperties.containsKey("hasGenres")){
                                    listClassProperties.put(entry.getKey(), word);
                                }
                            }
                        }
                    }
                }
            }
            //System.out.println(listClassProperties.entrySet());
            System.out.println(searchValues);

            String property = listClassProperties.entrySet().iterator().next().getKey();
            System.out.println(property);
            String searchParameter;
            try{
                searchParameter = searchValues.get(0).toLowerCase();
            }catch (Exception e){
                searchParameter = "\"\"";
            }
            String sparqlQuery = null;

            String parameter1, parameter2;
            ArrayList<String> type = new ArrayList<>();
            if (searchClasses.contains("Album") || searchClasses.contains("Track")) {
                parameter1 = "hasTitle";
                if(property.equals("hasYear")){
                    parameter2 = "FILTER( regex(str(?y), \"" + searchParameter + "\"))}";
                }
                else {
                    parameter2 = (searchClasses.contains("Album")) ? "hasName" : "hasTitle";
                    parameter2 = "?y :" + parameter2 + "?name FILTER(lcase(str(?name)) =" + searchParameter + ")}";
                }
            }
            else {
                parameter1 = "hasName";
                parameter2 = listClassProperties.containsKey("hasGender") ? "FILTER( lcase(str(?y))=\"" + searchParameter + "\")}" : "FILTER( regex(lcase(str(?y))," + searchParameter + "))}";

                if(searchParameter.equals("\"\"")){
                    parameter2 = "FILTER( str(?y)!=\"None\")}";
                }
            }

            if (listClassProperties.containsKey("artist") && searchClasses.contains("Track")) {
                sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?parameter ?parameterID WHERE {?x :hasName ?name. ?x :hasID ?artistID. ?y :isAlbumOf ?x. ?z :isTrackOf ?y. ?z :hasID ?parameterID. ?z :hasTitle ?parameter. FILTER(lcase(str(?name)) =" + searchParameter + ")}";
            } else {
                sparqlQuery = "PREFIX : <" + nameSpace + "> SELECT DISTINCT ?parameter ?parameterID WHERE {?x :" + property + " ?y. ?x :" + parameter1 + "?parameter . ?x :hasID ?parameterID. " + parameter2;
            }

            type.add(searchClasses.get(0));
            System.out.println(sparqlQuery);
            results = executeIDQuery(sparqlQuery, "parameter", "parameterID");
            //System.out.println(type);
            results.add(type);


            return results;
        }catch (Exception e){
            return null;
        }
    }

    private List<String> parseSearch(String search){
        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(search);
        while (m.find())
            list.add(m.group(1)); // Add .replace("\"", "") to remove surrounding quotes.
        return list;
    }

    private List<String> getClasses() {
        List<String> result = new ArrayList<String>();

        String sparqlQuery = sparqlPrefix +
                "SELECT DISTINCT ?class\n" +
                "WHERE {\n" +
                "\t?class a owl:Class.\n" +
                "\t\tFILTER(REGEX(STR(?class), \"^"+nameSpace+"\")).\n" +
                "}";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.nextSolution();
            RDFNode temp = qs.get("class");
            result.add(temp.asNode().getLocalName());
        }

        qe.close();

        return result;
    }

    private List<String> getProperties(String cl) {
        List<String> result = new ArrayList<String>();

        String sparqlQuery = sparqlPrefix +
                "SELECT DISTINCT ?property\n" +
                "\tWHERE {\n" +
                "\t\t?instance a my:"+cl+".\n" +
                "\t\t?instance ?property ?obj.\n" +
                "\t}";

        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.nextSolution();
            RDFNode temp = qs.get("property");

            if(!result.contains(temp.asNode().getLocalName())) {
                result.add(temp.asNode().getLocalName());
            }
        }
        if(cl.equals("Track")){
            result.add("artist");
        }

        qe.close();

        return result;
    }

    private HashMap<String, ArrayList<String>> getAvailableValues(List<String> properties, String cl) {
        ArrayList<String> temp = new ArrayList<>();
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        if(cl.equals("Track")){
            for(String prop: properties){
                temp.clear();
                if(prop.equals("isTrackOf")){
                    temp.add("from");
                }
                else if(prop.equals("artist")){
                    temp.add("by");
                    temp.add("of");
                }
                result.put(prop, new ArrayList<>(temp));
            }
        }
        else if(cl.equals("Album")){
            for(String prop: properties){
                temp.clear();
                if(prop.equals("isAlbumOf")){
                    temp.add("of");
                    temp.add("by");
                }
                else if(prop.equals("hasYear")){
                    temp.add("from");
                }
                result.put(prop, new ArrayList<>(temp));
            }
        }
        else if(cl.equals("Artist")){
            for(String prop: properties){
                temp.clear();
                if(prop.equals("hasLocation")){
                    temp.add("from");
                }
                else if(prop.equals("hasBeginDate")){
                    temp.add("created");
                    temp.add("formed");
                    temp.add("founded");
                    temp.add("active");
                    temp.add("begin");
                }
                else if(prop.equals("hasBornDate")){
                    temp.add("born");
                }
                else if(prop.equals("hasEndDate")){
                    temp.add("shut");
                    temp.add("canceled");
                    temp.add("finished");
                    temp.add("inactive");
                    temp.add("end");
                }
                else if(prop.equals("hasDeathDate")){
                    temp.add("died");
                    temp.add("dead");
                }
                else if(prop.equals("hasGender")){
                    temp.add("female");
                    temp.add("male");
                }
                else if(prop.equals("hasGenres")){
                    temp.add("");
                }
                result.put(prop, new ArrayList<>(temp));
            }
        }
        return result;
    }
}
