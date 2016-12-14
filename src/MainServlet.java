import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Asus on 13-12-2016.
 */
@WebServlet(name = "MainServlet")
public class MainServlet extends HttpServlet {
    private String search_info;
    private String type;

    /*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }*/

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        search_info = request.getParameter("search_info");
        type = request.getParameter("type");

        if(type.equals("artist")){
            
        }
        else if(type.equals("album")){

        }
        else if(type.equals("track")){

        }
    }


    public void init() throws ServletException{
        System.out.println("Server running...");
    }
}
