package ua.softserveinc.tc.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.softserveinc.tc.constants.ModelConstants.MyKidsConst;
import ua.softserveinc.tc.entity.Child;
import ua.softserveinc.tc.service.ChildService;
import ua.softserveinc.tc.service.UserService;
import ua.softserveinc.tc.validator.ChildValidator;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nestor on 07.05.2016.
 * Controller handles kid registration
 */
@Controller
public class UserRegisterNewKidController {
    @Autowired
    private ChildService childService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChildValidator childValidator;

    /**
     * Handles HTTP GET request to display registration form
     *
     * @param model
     * @return view name
     */
    @RequestMapping(value = "/registerkid", method = RequestMethod.GET)
    public String registerKid(Model model){
        if(!model.containsAttribute(MyKidsConst.KID_ATTRIBUTE)) {
            model.addAttribute(MyKidsConst.KID_ATTRIBUTE, new Child());
        }
        return MyKidsConst.KID_REGISTRATION_VIEW;
    }


    /**
     * Handles HTTP POST request from user after form submitting
     * Validates the invariants of the Child object and prepares it
     * for persistance into the database
     *
     * @param child A Child object to be persisted into the database
     * @param principal A Spring Security interface implementation
     *                  that represents currently logged in account
     * @param bindingResult A result holder for object binding
     * @return redirects to My Kids view if successful
     *         or current view if failed
     */
    @RequestMapping(value = "/registerkid", method = RequestMethod.POST)
    public String submit(
            @ModelAttribute(value = MyKidsConst.KID_ATTRIBUTE) Child child,
            Principal principal,
            BindingResult bindingResult) {

        child.setParentId(
                userService.getUserByEmail(
                        principal.getName()));

        childValidator.validate(child, bindingResult);

        if(bindingResult.hasErrors()) {
            return MyKidsConst.KID_REGISTRATION_VIEW;
        }

        childService.create(child);
        return "redirect:/" + MyKidsConst.MY_KIDS_VIEW;
    }

    /**
     * Registers custom date format
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}