/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "UserVerification", urlPatterns = {"/UserVerification"})
public class UserVerification extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();

        try {
            String code = req.getParameter("code");
            System.out.println(code);
            String email = (String) httpSession.getAttribute("email");

            if (code.isEmpty()) {
                responseObject.addProperty("message", "Please eneter verification code");
            } else if (!Validations.isInteger(code)) {
                responseObject.addProperty("message", "Invalid verification code please check again");
            } else {

                //find user
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", email));

                //check user
                if (criteria.list().isEmpty()) {
                    // user not found
                    responseObject.addProperty("message", "Try again later");
                } else {
                    //user found
                    User user = (User) criteria.list().get(0);

                    // check already verified or not
                    if (user.getVerification() != 1111) {
                        
                        System.out.println(code);
                        System.out.println(user.getVerification());
                        
                        if (user.getVerification()==Integer.parseInt(code)) {
                            //user verified
                            //create user dto
                            UserDTO userDTO = new UserDTO();
                            userDTO.setFirst_name(user.getFirst_name());
                            userDTO.setLast_name(user.getLast_name());
                            userDTO.setEmail(user.getEmail());
                            userDTO.setDatetime(user.getDatetime());

                            //update user verification
                            user.setVerification(1111);
                            session.update(user);
                            session.beginTransaction().commit();

                            httpSession.removeAttribute("email");
                            httpSession.setAttribute("user", userDTO);

                            responseObject.addProperty("success", true);
                            responseObject.addProperty("message", "Verification successfully complete");

                        } else {
                            // unverified
                            responseObject.addProperty("message", "Verification process failed. Try again!");
                        }
                        
                    }

                }

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
