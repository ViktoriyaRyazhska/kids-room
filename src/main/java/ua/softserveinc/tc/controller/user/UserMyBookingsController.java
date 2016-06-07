package ua.softserveinc.tc.controller.user;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ua.softserveinc.tc.constants.UserConstants;
import ua.softserveinc.tc.dto.BookingDTO;
import ua.softserveinc.tc.entity.Booking;
import ua.softserveinc.tc.entity.Role;
import ua.softserveinc.tc.entity.User;
import ua.softserveinc.tc.server.exception.ResourceNotFoundException;
import ua.softserveinc.tc.service.BookingService;
import ua.softserveinc.tc.service.RoomService;
import ua.softserveinc.tc.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nestor on 14.05.2016.
 * Controller handles reports on User's bookings
 */

@Controller
public class UserMyBookingsController {
    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    /**
     * Renders a view
     * @param principal
     * @return required view
     * @throws AccessDeniedException
     * if requesting user has no permissions
     * to access this page
     * @throws ResourceNotFoundException
     * if any of the requesting resources were not found
     */
    @RequestMapping(value = "/mybookings", method = RequestMethod.GET)
    public ModelAndView getMyBookings(Principal principal)
    throws AccessDeniedException{
        User current = userService.getUserByEmail(principal.getName());
        if(current.getRole() != Role.USER){
            throw new AccessDeniedException("Have to be a User");
        }

        ModelAndView model = new ModelAndView();
        model.setViewName(UserConstants.MY_BOOKINGS_VIEW);
        ModelMap modelMap = model.getModelMap();
        return model;
    }


    /**
     * Handles HTTP GET request for bookings in custom range of time
     *
     * @param dateLo time range lower limit
     * @param dateHi time range upper limit
     * @param principal User principal
     *
     * @return A list of DTOs containing all valuable info in JSON
     * @throws AccessDeniedException
     * if requesting user has no permissions
     * to access this page
     * @throws ResourceNotFoundException
     * if any of the requesting resources were not found
     */
    @RequestMapping(value = "mybookings/getbookings", method = RequestMethod.GET)
    public @ResponseBody String getBookings(
                       @RequestParam(value = "dateLo") String dateLo,
                       @RequestParam(value = "dateHi") String dateHi,
                       Principal principal)
    throws AccessDeniedException, ResourceNotFoundException{

        User currentUser = userService.getUserByEmail(principal.getName());
        if(currentUser.getRole() != Role.USER){
            throw new AccessDeniedException("Have to be a User");
        }
        List<Booking> myBookings = bookingService.getBookings(currentUser, dateLo, dateHi);
        List<BookingDTO> dtos = new ArrayList<>();
        myBookings.forEach((booking -> dtos.add(new BookingDTO(booking))));

        Gson gson = new Gson();
        return gson.toJson(dtos);
    }
}