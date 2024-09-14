/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
@WebServlet(name = "LoadIndexData", urlPatterns = {"/LoadIndexData"})
public class LoadIndexData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();
        try {

            Criteria criteria = session.createCriteria(Product.class);
            criteria.addOrder(Order.desc("id"));
            criteria.setFirstResult(0);
            criteria.setMaxResults(12);
            List<Product> ProductList = criteria.list();

            responseObject.addProperty("success", true);
            responseObject.addProperty("message", "Product Loading");
            responseObject.add("productList", gson.toJsonTree(ProductList));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
