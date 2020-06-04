package com.example.accessingdatamysql.controllers;

import com.example.accessingdatamysql.auth.UserDetailsImp;
import com.example.accessingdatamysql.dao.*;
import com.example.accessingdatamysql.models.*;
import com.example.accessingdatamysql.models.enums.OrderStatus;
import com.example.accessingdatamysql.models.enums.ProductStatus;
import com.example.accessingdatamysql.storage.StorageService;
import com.example.accessingdatamysql.thirdparty.PaypalPayment;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@RequestMapping("/order")
@Api(value = "order", description = "Controller about order")
public class OrderController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private ShippingInfoRepository shippingInfoRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private CartRepository cartRepository;


    private PaypalPayment paypal = PaypalPayment.getInstance();

    @ApiOperation("Confirm Order")
    @PostMapping(path="/confirmOrder")
    @Transactional(rollbackFor=Exception.class)
    public @ResponseBody String confirmOrder(@RequestParam Integer addressID, @RequestParam Integer shippingCompanyID,
                                            @RequestBody CardInfo card, HttpServletResponse response){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = ((UserDetailsImp) auth.getPrincipal()).getUserID();

        User user = userRepository.findUserByUserID(userID).get();
        Address address = addressRepository.findAddressByAddressID(addressID).get();
        ShippingCompany shippingCompany = shippingRepository.findShippingCompanyByShippingCompanyID(shippingCompanyID).get();


        Set<CartItem> cart = user.getCart();
        LocalDateTime datetime = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHoursMinutes(3,0));
        String formattedDatetime = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(datetime);




        if(isCartValid(cart)){


                /*validate card if validated return true*/
            System.out.println("Hello");
            if(paypal.validateCard(card)) {

                System.out.println("Hello");
                card = cardRepository.save(card);



                System.out.println(user.getUserID());
                System.out.println(card.getCardNumber());


                /*Set<User> userSet = new HashSet<User>();
                userSet.add(user);

                Set<CardInfo> cardSet = new HashSet<CardInfo>();
                cardSet.add(card);*/

                Order order = new Order();
                order.setBuyer(user);
                order.setCardNumber(card);
                order.setDatetime(formattedDatetime);
                order.setReceiverAddressID(address);

                orderRepository.save(order);

                for (CartItem item : cart) {

                    ShippingInfo shippingInfo = new ShippingInfo();
                    shippingInfo.setShippingCompanyID(shippingCompany);
                    shippingInfo.setTrackingNumber("0000");

                    shippingInfo = shippingInfoRepository.save(shippingInfo);

                    Sells sells = item.getSells();
                    Product product = sells.getProduct();
                    Integer newQuantity = sells.getQuantity() - item.getAmount();

                    if (product.getStatus() != ProductStatus.ACTIVE) {
                        throw new RuntimeException("Product is not active,  transaction rollback");
                    } else if (newQuantity < 0) {
                        throw new RuntimeException("Not enough stock, transaction rollback");
                    }

                    /* Product Soldout */
                    if (newQuantity == 0) {
                        product.setStatus(ProductStatus.DEACTIVE);
                    }

                    /* Update the product quantity */
                    sells.setQuantity(newQuantity);

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setQuantity(item.getAmount());
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(product);
                    orderDetail.setUser(sells.getUser());
                    orderDetail.setShippingInfo(shippingInfo);
                    orderDetail.setStatus(OrderStatus.ACTIVE);



                    /*Change refund to enum
                     * throw new RuntimeException("throwing exception to test transaction rollback");*/
                    /*orderDetail.setRefund(0);*/

                    orderDetailRepository.save(orderDetail);
                    productRepository.save(product);
                    sellerRepository.save(sells);
                    cartRepository.deleteByUserIDAndSellID(userID, item.getCartItemID().getSellID());



                }

                return "confirmed";
            }
            else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return "Card could not be confirmed";
            }






        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "Cart is not valid.";

    }

    public boolean isCartValid(Set<CartItem> cart){

        for(CartItem item : cart){

            Sells sells = item.getSells();
            Integer amount = item.getAmount();

            if(sells.getProduct().getStatus() != ProductStatus.ACTIVE){
                return false;
            }
            else if (sells.getQuantity() < amount)
            {
                return false;
            }

        }
        return true;
    }

    @ApiOperation("Get Order Details. Lists details for every product")
    @GetMapping(path = "/getOrderDetails")
    public @ResponseBody List<OrderDetail> getOrderDetails(@RequestParam Integer orderID)
    {
        return orderDetailRepository.findOrderDetailsByOrderID(orderID);
    }


    @ApiOperation("Get list of given orders by user")
    @GetMapping(path="/givenOrders")
    public @ResponseBody List<Order> givenOrders()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = ((UserDetailsImp) auth.getPrincipal()).getUserID();
        return orderRepository.findOrderByUserID(userID);
    }

    @ApiOperation("Get list of taken orders by seller")
    @GetMapping(path="/takenOrders")
    public @ResponseBody List<Order> getTakenOrders()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = ((UserDetailsImp) auth.getPrincipal()).getUserID();
        return  orderDetailRepository.findOrdersBySellerID(userID);
    }

    @ApiOperation("Cancel Order Not implemented")
    @PostMapping(path="/cancel")
    public @ResponseBody String cancelOrder(@RequestParam Integer orderID, @RequestParam Integer productID, @RequestParam Integer sellerID, HttpServletResponse response)
    {
        OrderDetail orderDetail = orderDetailRepository.findOrderDetailByOrderIDAndProductIDAndSellerID(orderID,productID,sellerID);
        if(orderDetail.getStatus() == OrderStatus.ACTIVE)
            orderDetail.setStatus(OrderStatus.CANCELLED);
        else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "You can't cancel an order after beginning it's shipment process. Look for refunding.";
        }
        return "Your order has canceled";
    }

    @ApiOperation("Change Address Not implemented")
    @PostMapping(path="/updateAddress")
    public @ResponseBody String updateAddress(@RequestParam Integer orderID, Address address)
    {
        throw new NotImplementedException();
    }

    @Transactional
    @ApiOperation("Refund Request")
    @PostMapping(path="/refundRequest")
    public @ResponseBody String refundRequest(@RequestParam Integer orderID,@RequestParam Integer productId, @RequestParam Integer sellerID,@RequestBody(required = false) List<MultipartFile> file,@RequestParam String message)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = ((UserDetailsImp) auth.getPrincipal()).getUserID();

        User user = userRepository.findUserByUserID(userID).get();

        LocalDateTime datetime = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.ofHoursMinutes(3,0));
        String formattedDatetime = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(datetime);

        OrderDetail orderDetail = orderDetailRepository.findOrderDetailByOrderIDAndProductIDAndSellerID(orderID,productId,sellerID);
        orderDetail.setStatus(OrderStatus.REFUNDREQUEST);
        RefundRequest refundRequest = new RefundRequest(orderDetail,formattedDatetime,message);
        refundRequest.setUser(user);

        refundRequest = refundRepository.save(refundRequest);
        if(file != null)
        {
            refundRequest.setPath(storageService.storeAll(file,refundRequest.getRefundID()));
            refundRepository.save(refundRequest);
        }
        return "refund request created with id " + refundRequest.getRefundID().toString();
    }

    @ApiOperation("List Refund Requests")
    @GetMapping(path = "/refundRequests")
    public @ResponseBody List<RefundRequest> getRefundRequests()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userID = ((UserDetailsImp) auth.getPrincipal()).getUserID();

        List<RefundRequest> ref = refundRepository.findRefundRequestsBySellerID(userID);
        Collections.sort(ref, Comparator.comparing(RefundRequest::getDatetime));
        Collections.reverse(ref);
        return ref;
    }

    @ApiOperation("Return Request Not implemented")
    @PostMapping(path="/returnConfirm")
    public @ResponseBody String confirmReturnRequest(@RequestParam Integer orderID, String message, boolean confirm)
    {
        throw new NotImplementedException();
    }
}