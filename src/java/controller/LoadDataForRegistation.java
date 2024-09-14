package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Cart;
import entity.Category;
import entity.SubCategory;
import entity.Unit;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "LoadDataForRegistation", urlPatterns = {"/LoadDataForRegistation"})
public class LoadDataForRegistation extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // checked user
            if (req.getSession().getAttribute("user") != null) {

                // get categories
                List<Cart> categoryList = session.createCriteria(Category.class).list();
                // get sub categories
                List<SubCategory> subCategoryList = session.createCriteria(SubCategory.class).list();
                //get units
                List<Unit> unitList = session.createCriteria(Unit.class).list();

                responseObject.addProperty("success", true);
                responseObject.add("categoryList", gson.toJsonTree(categoryList));
                responseObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
                responseObject.add("unitList", gson.toJsonTree(unitList));

            } else {
                responseObject.addProperty("message", "Something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
