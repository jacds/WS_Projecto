import org.apache.jena.base.Sys;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Asus on 14-12-2016.
 */
@WebServlet(name = "/Artist")
public class Artist extends HttpServlet {
    /*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }*/

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QueryManager qm = new QueryManager();
        ArrayList<String> result = qm.getArtists();
        qm.closeConnections();

        request.setAttribute("result", result);
        RequestDispatcher view=request.getRequestDispatcher("artistList.jsp");
        view.forward(request,response);
    }

    public void init(){
        System.out.println("Artists page");
    }
}
