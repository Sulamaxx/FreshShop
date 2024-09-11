/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Product;
import entity.SubCategory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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

        if (!Validations.isInteger(id)) {
            responseObject.addProperty("message", "product not found");
        } else {
            try {
                Product product = (Product) session.get(Product.class, Integer.parseInt(id));
                if (product == null) {
                    responseObject.addProperty("message", "Something went wrong");
                } else {
                    product.setUser(null);

                    Criteria criteria = session.createCriteria(SubCategory.class);
                    criteria.add(Restrictions.eq("category", product.getSubCategory().getCategory()));
                    List<SubCategory> subCategoryList = criteria.list();

                    Criteria criteria1 = session.createCriteria(Product.class);
                    criteria1.add(Restrictions.in("subCategory", subCategoryList));
                    criteria1.add(Restrictions.ne("id", product.getId()));
                    criteria.addOrder(Order.desc("id"));
                    criteria1.setMaxResults(12);
                    List<Product> productList = criteria1.list();

                    for (Product product1 : productList) {
                        product1.setUser(null);
                    }

                    responseObject.addProperty("success", true);
                    responseObject.add("product", gson.toJsonTree(product));
                    responseObject.add("productList", gson.toJsonTree(productList));

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
