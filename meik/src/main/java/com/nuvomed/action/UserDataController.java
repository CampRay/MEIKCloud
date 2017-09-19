/**   
 * @Title: UserController.java 
 * @Package com.uswop.action 
 * @Description: TODO
 * @author Phills Li    
 * @date Sep 2, 2014 7:25:22 PM 
 * @version V1.0   
 */
package com.nuvomed.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nuvomed.service.UserDataService;
import com.nuvomed.service.UserService;
@Controller
@RequestMapping(value="userdata")
public class UserDataController extends BaseController {

	private Logger logger = Logger.getLogger(UserDataController.class);
	@Autowired
	private UserDataService userDataService;
	
    @Autowired
	private UserService userService;
    
    
}
