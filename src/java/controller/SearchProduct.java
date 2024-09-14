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
@WebServlet(name = "SearchProduct", urlPatterns = {"/SearchProduct"})
public class SearchProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        JsonObject requestObject = gson.fromJson(req.getReader(), JsonObject.class);

        try {

            String selectedSubCategoty = requestObject.get("selectedSubCategoty").getAsString();
            String search = requestObject.get("search").getAsString();
            String price_order = requestObject.get("price_order").getAsString();
            String min_price = requestObject.get("min_price").getAsString();
            String max_price = requestObject.get("max_price").getAsString();

            Criteria criteria = session.createCriteria(Product.class);

            // sub category
            if (Validations.isInteger(selectedSubCategoty)) {
                if (Integer.parseInt(selectedSubCategoty) > 0) {
                    if (session.get(SubCategory.class, Integer.parseInt(selectedSubCategoty)) != null) {
                        criteria.add(Restrictions.eq("subCategory", (SubCategory) session.get(SubCategory.class, Integer.parseInt(selectedSubCategoty))));
                    }
                }
            }

            // by title
            if (!search.isEmpty()) {
                criteria.add(Restrictions.ilike("title", search));
            }

            //price order
            if (Validations.isInteger(price_order)) {
                if (Integer.parseInt(price_order) == 1) {
                    criteria.addOrder(Order.asc("price"));
                } else if (Integer.parseInt(price_order) == 2) {
                    criteria.addOrder(Order.desc("price"));
                }
            }

            //price range
            criteria.add(Restrictions.ge("price", Double.parseDouble(min_price)));
            criteria.add(Restrictions.le("price", Double.parseDouble(max_price)));

            // all product count
            responseObject.addProperty("allProductCount", criteria.list().size());

            int firstResult = requestObject.get("firstResult").getAsInt();
            System.out.println(firstResult);
            criteria.setFirstResult(firstResult);
            criteria.setMaxResults(6);

            List<Product> productList = criteria.list();

            for (Product product : productList) {
                product.setUser(null);
            }
            responseObject.addProperty("success", true);
            responseObject.add("productList", gson.toJsonTree(productList));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }

}
