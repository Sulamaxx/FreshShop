/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import entity.OrderItem;
import entity.OrderStatus;
import entity.Orders;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.PayHere;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "VerifyPayment", urlPatterns = {"/VerifyPayment"})
public class VerifyPayment extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String merchant_id = req.getParameter("merchant_id");
        String order_id = req.getParameter("order_id");
        String payhere_amount = req.getParameter("payhere_amount");
        String payhere_currency = req.getParameter("payhere_currency");
        String status_code = req.getParameter("status_code");
        String md5sig = req.getParameter("md5sig");

        String merchantSecret = "ODE4NDkwMjczMjUxOTE4OTcwNTI2MDE5Mjk2NzMwNDg1ODkwNzQ=";
        String merchantSecretHash = PayHere.generateMD5(merchantSecret);

        String generateMd5Hash = PayHere.generateMD5(merchant_id + order_id + payhere_amount + payhere_currency + status_code + merchantSecretHash);

        if (generateMd5Hash.equals(md5sig) && status_code.equals("2")) {
            System.out.println("Payment Complete of " + order_id);
            // update order status as paid
            Session session = HibernateUtil.getSessionFactory().openSession();
            Orders order = (Orders) session.get(Orders.class, order_id);

            Criteria criteria = session.createCriteria(OrderItem.class);
            criteria.add(Restrictions.eq("order", order));

            OrderStatus orderStatus = (OrderStatus) session.get(OrderStatus.class, 2);

            List<OrderItem> orderItemList = criteria.list();

            for (OrderItem orderItem : orderItemList) {
                orderItem.setOrderStatus(orderStatus);
            }

        }

    }

}
