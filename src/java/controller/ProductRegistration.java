/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Cart;
import entity.Product;
import entity.ProductStatus;
import entity.SubCategory;
import entity.Unit;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@MultipartConfig
@WebServlet(name = "ProductRegistration", urlPatterns = {"/ProductRegistration"})
public class ProductRegistration extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String qty = req.getParameter("qty");
        String price = req.getParameter("price");
        String discount = req.getParameter("discount");
        String category = req.getParameter("category");
        String sub_category = req.getParameter("sub_category");
        String unit = req.getParameter("unit");
        Part image1 = req.getPart("image1");
        Part image2 = req.getPart("image2");
        Part image3 = req.getPart("image3");

        //validations
        if (title.isEmpty()) {
            responseObject.addProperty("message", "Please enter the product title");
        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Please enter the product description");
        } else if (qty.isEmpty()) {
            responseObject.addProperty("message", "Please enter the product quantity");
        } else if (!Validations.isDouble(qty)) {
            responseObject.addProperty("message", "Invalid quantity");
        } else if (price.isEmpty()) {
            responseObject.addProperty("message", "Please enter the product price");
        } else if (!Validations.isDouble(price)) {
            responseObject.addProperty("message", "Invalid price");
        } else if (discount.isEmpty()) {
            responseObject.addProperty("message", "Please enter the product discount or 0");
        } else if (!Validations.isDouble(discount)) {
            responseObject.addProperty("message", "Invalid discount");
        } else if (!Validations.isInteger(category)) {
            responseObject.addProperty("message", "Please select category");
        } else if (!Validations.isInteger(sub_category)) {
            responseObject.addProperty("message", "Please select sub category");
        } else if (!Validations.isInteger(unit)) {
            responseObject.addProperty("message", "Please select unit");
        } else if (image1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Please upload image 1");
        } else if (image2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Please upload image 2");
        } else if (image3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Please upload image 3");
        } else {

            try {
                // check category exist
                Cart categoryObject = (Cart) session.get(Cart.class, Integer.parseInt(category));
                if (categoryObject == null) {
                    responseObject.addProperty("message", "Invalid category");
                } else {
                    //check sub category exist
                    SubCategory subCategoryObject = (SubCategory) session.get(SubCategory.class, Integer.parseInt(sub_category));

                    if (subCategoryObject == null) {
                        responseObject.addProperty("message", "Invalid sub category");
                    } else {
                        //check sub category and category relationship
                        if (subCategoryObject.getCategory().getId() == categoryObject.getId()) {

                            Unit unitObject = (Unit) session.get(Unit.class, Integer.parseInt(unit));

                            if (unitObject == null) {
                                responseObject.addProperty("message", "Invalid unit");
                            } else {

                                //get user db
                                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
                                User user = (User) session.createCriteria(User.class).add(Restrictions.eq("email", userDTO.getEmail())).uniqueResult();

                                //save product
                                Product product = new Product();
                                product.setTitle(title);
                                product.setDescription(description);
                                product.setQty(Double.parseDouble(qty));
                                product.setPrice(Double.parseDouble(price));
                                product.setDiscount(Double.parseDouble(discount));
                                product.setSubCategory(subCategoryObject);
                                product.setProductStatus((ProductStatus) session.get(ProductStatus.class, 1));
                                product.setUnit(unitObject);
                                product.setDatetime(new Date());
                                product.setUser(user);

                                int product_id = (int) session.save(product);
                                session.beginTransaction().commit();

                                // get application real path and change as web
                                String newApplicationPath = req.getServletContext().getRealPath("").replace("build" + File.separator + "web", "web");

                                // create folder
                                File folder = new File(newApplicationPath + File.separator + "product-images" + File.separator + product_id);
                                if (!folder.exists()) {
                                    folder.mkdirs();
                                }

                                //image save
                                File file = new File(folder, "image1.png");
                                InputStream inputStream = image1.getInputStream();
                                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                File file1 = new File(folder, "image2.png");
                                InputStream inputStream1 = image2.getInputStream();
                                Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                File file2 = new File(folder, "image3.png");
                                InputStream inputStream2 = image3.getInputStream();
                                Files.copy(inputStream2, file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                responseObject.addProperty("success", true);
                                responseObject.addProperty("message", "Product registered successfully");

                            }

                        } else {
                            responseObject.addProperty("message", "Invalid category or sub category");
                        }

                    }

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
