/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "ResendVerificationCode", urlPatterns = {"/ResendVerificationCode"})
public class ResendVerificationCode extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            if (req.getSession().getAttribute("email") != null) {
                // get user
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", req.getSession().getAttribute("email")));

                // check user
                if (criteria.list().isEmpty()) {
                    responseObject.addProperty("message", "Something went wrong");
                } else {
                    // user exist
                    User user = (User) criteria.list().get(0);
                    int code = (int) (Math.random() * 1000000);

                    //update user
                    user.setVerification(code);
                    session.update(user);

                    //  code send
                    new Thread(() -> {
                        Mail.sendMail(user.getEmail(), "Account Verification", "<h1>verification Code :" + user.getVerification() + "</h1>");
                    }).start();
                    
                    session.beginTransaction().commit();
                    
                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "Verification code resent. Please check your inbox");
                    
                }
                
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
