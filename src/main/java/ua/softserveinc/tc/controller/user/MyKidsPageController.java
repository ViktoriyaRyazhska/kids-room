package ua.softserveinc.tc.controller.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ua.softserveinc.tc.constants.ChildConstants;
import ua.softserveinc.tc.entity.Child;
import ua.softserveinc.tc.entity.Role;
import ua.softserveinc.tc.entity.User;
import ua.softserveinc.tc.service.UserService;

import java.security.Principal;
import java.util.List;


/**
 * Controller handles request for "My Kids" view
 */

@Controller
public class MyKidsPageController {
    @Autowired
    private UserService userService;

    /**
     * @param principal
     * @return "My Kids" view
     * @throws AccessDeniedException if requesting user has no permissions
     *                               to access this page
     */
    @RequestMapping(value = "/mykids", method = RequestMethod.GET)
    public ModelAndView myKids(Principal principal)
            throws AccessDeniedException {

        ModelAndView model = new ModelAndView();
        model.setViewName(ChildConstants.View.MY_KIDS);
        User current = userService.getUserByEmail(principal.getName());
        if (current.getRole() != Role.USER) {
            throw new AccessDeniedException("Only parents have access to this page");
        }

        List<Child> myKids = current.getEnabledChildren();
        ModelMap modelMap = model.getModelMap();

        if (!modelMap.containsAttribute(ChildConstants.View.MY_KIDS_LIST_ATTRIBUTE)) {
            modelMap.addAttribute(ChildConstants.View.MY_KIDS_LIST_ATTRIBUTE, myKids);
        }

        return model;
    }
}