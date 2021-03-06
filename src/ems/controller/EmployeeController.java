package ems.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import ems.entity.AccessUrl;
import ems.entity.Designation;
import ems.entity.District;
import ems.entity.Employee;
import ems.entity.Gender;
import ems.entity.Ministry;
import ems.entity.State;
import ems.entity.SubUrl;
import ems.entity.UserRole;
import ems.entity.UserType;
import ems.service.EmployeeService;
import ems.utils.PassEncryption;

@Controller
@SessionAttributes({"empsession","ipAddress","navUrl"}) 
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	
	
	@RequestMapping("/homepage11")
	public String showDashboard() {
		return "dashboard";
	}
	@RequestMapping("/home")
	public String showHome(Model model,HttpSession session,HttpServletRequest request) {
		String clientcookiesvalu="";
		Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cook : cookies)
            {
                if ("JSESSIONID".equalsIgnoreCase(cook.getName()))
                {
               	 //System.out.println("JSESSIONID  " +cook.getName());
               	 clientcookiesvalu = cook.getValue();
               	 //System.out.println("clientcookiesvalu  " +clientcookiesvalu);
                    break;
                }
            }
        }
       // System.out.println("session.getId  " +session.getId()); 
if(!(clientcookiesvalu.equalsIgnoreCase(session.getId())))
{
	return "redirect:/login";
}
	
		Employee emp=(Employee)session.getAttribute("empsession");
		List<AccessUrl>accList=employeeService.getUrl(emp.getUserRole());
		for (AccessUrl accessUrl : accList) {
			List<SubUrl> suburlList=new ArrayList<>();
			suburlList =employeeService.getsubUrl(accessUrl.getUrlId(),emp.getUserRole());
			accessUrl.setSubList(suburlList);
		}
		session.removeAttribute("navUrl");
		session.setAttribute("navUrl",accList);
	/*	model.addAttribute("navUrl",accList );*/
		return "homepage";
	}
	
	@RequestMapping("/registerNewForm")
	public String registerForm(/*@ModelAttribute("employee") Employee thEmployee,*/Model model) {
		Employee theEmployee = new Employee();
		model.addAttribute("employee", theEmployee);
		/*Get all states in drop down*/
		List<State> statelist=employeeService.getEmpStateList();
		model.addAttribute("allstate", statelist);
		List<UserType> typeList=employeeService.getUserTypeList();
		model.addAttribute("alltypes", typeList);
		List<UserRole> roleList=employeeService.getUserRoleList();
		model.addAttribute("allroles", roleList);
		List<Gender> genderList=employeeService.getGenderList();
		model.addAttribute("allgender", genderList);
		List<Designation> designationList=employeeService.getDesignationList();
		model.addAttribute("alldesignation", designationList);
		return "register";
	}
	
	@RequestMapping(value="/updateEmployee", method=RequestMethod.GET)
	public String updateEmployee(@RequestParam("emailId") String emailId,Model model,HttpSession session,HttpServletRequest request) {
		Employee theEmployee = employeeService.getUser(emailId);
		model.addAttribute("employee", theEmployee);
		System.out.println(theEmployee.getEmailId()+" bsdsdbb");
		List<State> statelist=employeeService.getEmpStateList();
		model.addAttribute("allstate", statelist);
		List<UserType> typeList=employeeService.getUserTypeList();
		model.addAttribute("alltypes", typeList);
		List<UserRole> roleList=employeeService.getUserRoleList();
		model.addAttribute("allroles", roleList);
		List<Gender> genderList=employeeService.getGenderList();
		model.addAttribute("allgender", genderList);
		List<Designation> designationList=employeeService.getDesignationList();
		model.addAttribute("alldesignation", designationList);
	//	employeeService.updateEmployee(theEmployee);
		return "updateUser";
	}
	
	@RequestMapping(value="/updateEmployee", method=RequestMethod.POST)
	public String updateEmployeeSave( @ModelAttribute("employee") Employee theEmployee, BindingResult bindingResult,@RequestParam(value="file", required=false) MultipartFile mfile, Model model,HttpSession session) {
		System.out.println("^^^^^^^^^^^^^^");
		if(bindingResult.hasErrors()) {
			System.out.println("tttttt "+bindingResult.getFieldErrors());
			model.addAttribute("errors", bindingResult.getFieldErrors());
			List<State> statelist=employeeService.getEmpStateList();
			model.addAttribute("allstate", statelist);
			List<UserType> typeList=employeeService.getUserTypeList();
			model.addAttribute("alltypes", typeList);
			List<UserRole> roleList=employeeService.getUserRoleList();
			model.addAttribute("allroles", roleList);
			List<Gender> genderList=employeeService.getGenderList();
			model.addAttribute("allgender", genderList);
			List<Designation> designationList=employeeService.getDesignationList();
			model.addAttribute("alldesignation", designationList);
			return "updateUser";
		}
		
		
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    Date date=new Date();
	    System.out.println(theEmployee.getDOB());
        try {
             date = formatter.parse(theEmployee.getDOB());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        theEmployee.setdDOB(date);
	int i=	employeeService.updateEmployee(theEmployee);
	System.out.println(theEmployee.getEmailId()+theEmployee.getDesignation());
		if(i>0) {
			return "redirect:/listEmployees";
		}
		return "error";
	}
	@RequestMapping(value="/createUser", method=RequestMethod.POST)
	public String createNewUser(@Valid @ModelAttribute("employee") Employee theEmployee, BindingResult bindingResult,@RequestParam(value="file", required=false) MultipartFile mfile, Model model,HttpSession session) throws IOException {
		System.out.println("in create user");
		if(bindingResult.hasErrors()) {
			System.out.println("gzbhc   "+bindingResult.getFieldErrors());
			model.addAttribute("errors", bindingResult.getFieldErrors());
			return "register";
		}
		
		Employee empsession=(Employee) session.getAttribute("empsession");
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    Date date=new Date();
	    System.out.println(theEmployee.getDOB());
        try {
             date = formatter.parse(theEmployee.getDOB());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	//	System.out.println("bfdj"+mfile.getBytes());
        try{theEmployee.setDistrictCode(Integer.parseInt(theEmployee.getDistrictCodeDesc().trim()));
		
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        
        theEmployee.setFile1(mfile.getBytes());
		theEmployee.setActivatedBy(empsession.getUserId());
		theEmployee.setActivatedClientIp(session.getAttribute("ipAddress").toString());
		theEmployee.setActiveStatus("Y");
		theEmployee.setdDOB(date);
		theEmployee.setUserPassword(new PassEncryption().encrypt("Pass@12345"));
		theEmployee.setUserName(theEmployee.getFirstName()+"  "+theEmployee.getLastName());
		int i=employeeService.createEmployee(theEmployee);
		theEmployee.setEnteredBy(empsession.getUserId());
		System.out.println(theEmployee.getUserName());
		if(i>0) {
		return "redirect:/home";
		}else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/alldistrict",method=RequestMethod.POST)
	@ResponseBody
	public List<District> showAllDistrict(@RequestParam("id") String stateCode ,Model model) {
		State state=new State();
		state.setStateCode(stateCode);
		return employeeService.getEmpDistrictList(state);				
}
	
	@RequestMapping("/listEmployees")
	public String listEmployees(Model model, HttpSession session) {
	//	Employee emp=(Employee)session.getAttribute("empsession");
		List<Employee> emplist = employeeService.getEmployees();
		model.addAttribute("employees", emplist);
		return "employeeList";
	}
	
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	@ResponseBody
	public List<Employee> delete(@RequestParam("emailId") String emailId, Model model, HttpSession session){
		int i= employeeService.deleteEmployee(emailId);
		 List<Employee> empList=new ArrayList<>();
		
		 if(i>0) {
			 empList = employeeService.getEmployees();
				model.addAttribute("employees", empList);
			 return empList;
		 }
		 return empList;	 
	}	
	@RequestMapping(value="/createRole", method=RequestMethod.GET)
	public String createRoleGET(HttpSession session, Model model  ) {
		model.addAttribute("role", new UserRole());
		List<UserRole> roleList = new ArrayList<>();
		roleList=employeeService.getUserRoleList();
	
		model.addAttribute("roles", roleList);
		return "createRole";
	}
	@RequestMapping(value="/createRole1", method=RequestMethod.POST)
	public String createRole(@ModelAttribute("role") UserRole role,HttpSession session, Model model  ) {
		Employee empSession = (Employee) session.getAttribute("empsession");
				role.setActiveStatus("Y");
		role.setClientIp(session.getAttribute("ipAddress").toString());
		role.setEnteredBy(empSession.getUserId());
		int i=0;
		if(role.getRoleId()==0) {
		i = employeeService.createRole(role);
		}
		else {
			i =	employeeService.updateRole(role);
			return "redirect:/createRole";
		}
		List<UserRole> roleList = new ArrayList<>();
		if(i>0) {
			
			roleList=employeeService.getUserRoleList();
			model.addAttribute("roles", roleList);
			return "createRole";
		}
		return "createRole";
	}
	
	@RequestMapping(value="/deleteRole",method=RequestMethod.POST)
	@ResponseBody
	public List<UserRole> deleteRole(@RequestParam("roleId") String roleId, Model model, HttpSession session){
		int i= employeeService.deleteRole(roleId);
		 List<UserRole> roleList=new ArrayList<>();
		
		 if(i>0) {
			 roleList = employeeService.getUserRoleList();
				model.addAttribute("role", roleList);
				 System.out.println(roleList);
				return roleList;
		 }
		 return roleList;	 
	}
	
	@RequestMapping(value="/createDesignation", method=RequestMethod.GET)
	public String createDesignationGET(HttpSession session, Model model  ) {
		model.addAttribute("designation", new Designation());
		List<Designation> designationList = new ArrayList<>();
		designationList=employeeService.getDesignationList();
	
		model.addAttribute("designationss", designationList);
		return "createDesignation";
}
	@RequestMapping(value="/createDesignation", method=RequestMethod.POST)
	public String createDesignation(@ModelAttribute("designation") Designation designation,HttpSession session, Model model  ) {
		Employee empSession = (Employee) session.getAttribute("empsession");
		designation.setActiveStatus("Y");
		designation.setClientIp(session.getAttribute("ipAddress").toString());
		designation.setEnteredBy(empSession.getUserId());
		int i=0;
		if(designation.getDesignationId()==0) {
		i = employeeService.createDesignation(designation);
		}
		else {
			i =	employeeService.updateDesignation(designation);
			return "redirect:/createDesignation";
		}
		List<Designation> designationList = new ArrayList<>();
		if(i>0) {
			
			designationList=employeeService.getDesignationList();
			model.addAttribute("designationss", designationList);
			return "createDesignation";
		}
		return "createDesignation";
	}
	
	@RequestMapping(value="/deleteDesignation",method=RequestMethod.POST)
	@ResponseBody
	public List<Designation> deleteDesignation(@RequestParam("designationId") String designationId, Model model, HttpSession session){
		int i= employeeService.deleteDesignation(designationId);
		 List<Designation> designationList=new ArrayList<>();
		
		 if(i>0) {
			 designationList = employeeService.getDesignationList();
				model.addAttribute("designation", designationList);
				 System.out.println(designationList);
				return designationList;
		 }
		 return designationList;	 
	}
	
	@RequestMapping(value="/createMinistry", method=RequestMethod.GET)
	public String createMinistryGET(HttpSession session, Model model  ) {
		model.addAttribute("ministry", new Ministry());
		List<Ministry> ministryList = new ArrayList<>();
		ministryList=employeeService.getMinistryList();
	
		model.addAttribute("ministries", ministryList);
		return "createMinistry";
}
	@RequestMapping(value="/createMinistry", method=RequestMethod.POST)
	public String createMinistry(@ModelAttribute("ministry") Ministry ministry,HttpSession session, Model model  ) {
		Employee empSession = (Employee) session.getAttribute("empsession");
		ministry.setActiveStatus("Y");
		ministry.setClientIp(session.getAttribute("ipAddress").toString());
		ministry.setEnteredBy(empSession.getUserId());
		int i=0;
		System.out.println(ministry.getMinistryDesc());
		System.out.println(ministry.getMinistryId());

		if(ministry.getMinistryId()==null) {
		i = employeeService.createMinistry(ministry);
		}
		else {
			i =	employeeService.updateMinistry(ministry);
			return "redirect:/createMinistry";
		}
		List<Ministry> ministryList = new ArrayList<>();
		if(i>0) {
			
			ministryList=employeeService.getMinistryList();
			model.addAttribute("ministries", ministryList);
			return "createMinistry";
		}
		return "createMinistry";
	}
	
	@RequestMapping(value="/deleteMinistry",method=RequestMethod.POST)
	@ResponseBody
	public List<Ministry> deleteMinistry(@RequestParam("ministryId") String ministryId, Model model, HttpSession session){
		int i= employeeService.deleteMinistry(ministryId);
		 List<Ministry> ministryList=new ArrayList<>();
		
		 if(i>0) {
			 ministryList = employeeService.getMinistryList();
				model.addAttribute("ministry", ministryList);
				 System.out.println(ministryList);
				return ministryList;
		 }
		 return ministryList;	 
	}
	

}
