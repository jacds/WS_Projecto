import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.Property;
import org.apache.log4j.varia.NullAppender;
import org.json.*;

import java.io.*;
import java.util.Iterator;


public class CreatePopulation {
    private static final String nameSpace = "http://www.semanticweb.org/rocha/ontologies/SemanticMusic#";
    private static OntModel model;

    // Classes
    private static OntClass person;
    private static OntClass group;
    private static OntClass album;
    private static OntClass track;

    // Data Properties
    private static Property hasBeginDate;
    private static Property hasBornDate;
    private static Property hasDeathDate;
    private static Property hasEndDate;
    private static Property hasDescription;
    private static Property hasImage;
    private static Property hasLastFMPage;
    private static Property hasLength;
    private static Property hasName;
    private static Property hasNumber;
    private static Property hasTitle;
    private static Property hasYear;
    private static Property hasGender;
    private static Property hasLocation;
    private static Property hasID;
    private static Property hasGenres;
    // Object Properties
    private static Property isAlbumOf;
    private static Property isTrackOf;


    private static void create_artists(String filePath){
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(reader);
            String s, result = "";
            while((s=br.readLine()) != null){
                result += s;
            }
            reader.close();

            JSONObject obj = new JSONObject(result);
            JSONObject subObj;

            Iterator<?> iteKeys = obj.keys();

            String artistID;

            while(iteKeys.hasNext()){
                artistID = (String) iteKeys.next();
                subObj = (JSONObject) (obj.get(artistID));

                Individual newArtist = null;

                //System.out.println(subObj);

                if(subObj.get("Type").equals("Person")){
                    newArtist = model.createIndividual(nameSpace+artistID, person);
                    newArtist.addLiteral(hasBornDate, subObj.get("BeginDate"));
                    newArtist.addLiteral(hasDeathDate, subObj.get("EndDate"));
                    newArtist.addLiteral(hasGender, subObj.get("gender"));
                }
                //If Type==Group or Type==None
                else{
                    newArtist = model.createIndividual(nameSpace+artistID, group);
                    newArtist.addLiteral(hasBeginDate, subObj.get("BeginDate"));
                    newArtist.addLiteral(hasEndDate, subObj.get("EndDate"));
                }


                JSONArray genres = (JSONArray) subObj.get("Genres");
                String genresString = "";
                //System.out.println("GENRES " + genres.length());
                for(int i=0; i<genres.length(); i++){
                    genresString += genres.get(i);
                    genresString += ",";
                }

                //System.out.println(genresString.split(",").length);

                newArtist.addLiteral(hasGenres, genresString);
                newArtist.addLiteral(hasDescription, subObj.get("Description"));
                newArtist.addLiteral(hasLastFMPage, subObj.get("URL"));
                newArtist.addLiteral(hasName, subObj.get("Name"));
                newArtist.addLiteral(hasLocation, subObj.get("Location"));
                newArtist.addLiteral(hasID, subObj.get("ID"));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void create_albums(String filePath){
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(reader);
            String s, result = "";
            while((s=br.readLine()) != null){
                result += s;
            }
            reader.close();

            JSONObject obj = new JSONObject(result);
            JSONObject subObj;

            Iterator<?> iteKeys = obj.keys();

            String albumID;

            while(iteKeys.hasNext()){
                albumID = (String) iteKeys.next();
                subObj = (JSONObject) (obj.get(albumID));

                Individual newAlbum = model.createIndividual(nameSpace+albumID, album);

                // Data Properties
                newAlbum.addLiteral(hasDescription, subObj.get("Description"));
                newAlbum.addLiteral(hasImage, subObj.get("Image"));
                newAlbum.addLiteral(hasLastFMPage, subObj.get("URL"));
                newAlbum.addLiteral(hasTitle, subObj.get("Title"));
                newAlbum.addLiteral(hasYear, subObj.get("Date"));
                newAlbum.addLiteral(hasID, subObj.get("ID"));

                // Object Properties
                newAlbum.addProperty(isAlbumOf, model.getIndividual(nameSpace+subObj.get("ArtistID")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void create_tracks(String filepath){
        try{
            FileReader reader = new FileReader(filepath);
            BufferedReader br = new BufferedReader(reader);
            String s, result = "";
            while((s = br.readLine()) != null) {
                result += s;
            }
            reader.close();

            JSONObject obj = new JSONObject(result);
            JSONObject subObj;

            Iterator<?> iteKeys = obj.keys();

            String trackID;

            while(iteKeys.hasNext()){
                trackID = (String) iteKeys.next();
                subObj = (JSONObject) (obj.get(trackID));

                Individual newTrack = model.createIndividual(nameSpace+trackID, track);

                // Data Properties
                newTrack.addLiteral(hasTitle, subObj.get("Title"));
                newTrack.addLiteral(hasLength, subObj.get("Length"));
                newTrack.addLiteral(hasNumber, subObj.get("Number"));
                newTrack.addLiteral(hasID, trackID);

                // Object Properties
                newTrack.addProperty(isTrackOf, model.getIndividual(nameSpace+subObj.get("AlbumID")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public static void main(String[] args){
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        // JENA doesn't support OWL files
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.read("data/project.owl", "RDF/XML");

        // get classes
        person = model.getOntClass(nameSpace + "Person");
        group = model.getOntClass(nameSpace + "Group");
        album = model.getOntClass(nameSpace + "Album");
        track = model.getOntClass(nameSpace + "Track");

        // Get Properties
        // Data Properties
        hasBeginDate = model.createProperty(nameSpace, "hasBeginDate");
        hasBornDate = model.createProperty(nameSpace, "hasBornDate");
        hasDeathDate = model.createProperty(nameSpace, "hasDeathDate");
        hasEndDate = model.createProperty(nameSpace, "hasEndDate");
        hasDescription = model.createProperty(nameSpace, "hasDescription");
        hasImage = model.createProperty(nameSpace, "hasImage");
        hasLastFMPage = model.createProperty(nameSpace, "hasLastFMPage");
        hasLength = model.createProperty(nameSpace, "hasLength");
        hasName = model.createProperty(nameSpace, "hasName");
        hasNumber = model.createProperty(nameSpace, "hasNumber");
        hasTitle = model.createProperty(nameSpace, "hasTitle");
        hasYear = model.createProperty(nameSpace, "hasYear");
        hasGender = model.createProperty(nameSpace, "hasGender");
        hasLocation = model.createProperty(nameSpace, "hasLocation");
        hasID = model.createProperty(nameSpace, "hasID");
        hasGenres = model.createProperty(nameSpace, "hasGenres");
        // Object Properties
        isAlbumOf = model.createProperty(nameSpace, "isAlbumOf");
        isTrackOf = model.createProperty(nameSpace, "isTrackOf");

        // json files
        String dic_artists = "data/artists.json";
        String dic_albums = "data/albuns.json";
        String dic_tracks = "data/tracks.json";

        create_artists(dic_artists);
        create_albums(dic_albums);
        create_tracks(dic_tracks);

        // Save the changes
        OutputStream out;
        try {
            out = new FileOutputStream("data/PopulatedProject.owl");
            model.write(out, "RDF/XML");
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
