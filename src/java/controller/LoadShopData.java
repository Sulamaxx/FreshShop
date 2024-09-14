/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Product;
import entity.SubCategory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "LoadShopData", urlPatterns = {"/LoadShopData"})
public class LoadShopData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {

            Criteria criteria = session.createCriteria(Category.class);
            List<Category> categoryList = criteria.list();

            Criteria criteria1 = session.createCriteria(SubCategory.class);
            List<SubCategory> subCategoryList = criteria1.list();

            Criteria criteria2 = session.createCriteria(Product.class);
            criteria2.addOrder(Order.desc("id"));
            
            // all product count
            responseObject.addProperty("allProductCount", criteria2.list().size());
            
            criteria2.setFirstResult(0);
            criteria2.setMaxResults(6);
            List<Product> productList = criteria2.list();

            for (Product product : productList) {
                product.setUser(null);
            }

            responseObject.add("categoryList", gson.toJsonTree(categoryList));
            responseObject.add("subCategoryList", gson.toJsonTree(subCategoryList));
            responseObject.add("productList", gson.toJsonTree(productList));
            responseObject.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }

}
