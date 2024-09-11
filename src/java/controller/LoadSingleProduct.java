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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Session;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        String id = req.getParameter("id");
        System.out.println("");
        System.out.println(id);
        
        if (!Validations.isInteger(id)) {
            responseObject.addProperty("message", "product not found");
        } else {
            try {
                Product product = (Product) session.get(Product.class, Integer.parseInt(id));
                if (product == null) {
                    responseObject.addProperty("message", "Something went wrong");
                } else {
                    product.setUser(null);
                    responseObject.addProperty("success", true);
                    responseObject.add("product", gson.toJsonTree(product));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                session.close();
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }

}
