package ems.service;

import java.util.List;

import ems.entity.AccessUrl;
import ems.entity.Designation;
import ems.entity.District;
import ems.entity.Employee;
import ems.entity.Gender;
import ems.entity.Ministry;
import ems.entity.ProfessionalDetails;
import ems.entity.State;
import ems.entity.SubUrl;
import ems.entity.UserRole;
import ems.entity.UserType;

public interface EmployeeService {

	public Employee getUser(String name);

	public int createEmployee(Employee theEmployee);

	public List<State> getEmpStateList();
	
	public List<District> getEmpDistrictList(State stateCode);

	public List<AccessUrl> getUrl(int userRole);

	public List<UserType> getUserTypeList();

	public List<SubUrl> getsubUrl(int urlId,int userRole);

	public List<UserRole> getUserRoleList();

	public List<Gender> getGenderList();

	public List<Employee> getEmployees();

	public int updateEmployee(Employee thEmployee);

	public int deleteEmployee(String emailId);

	public List<AccessUrl> getUrl();
	

	public List<SubUrl> getsubUrlByUrl(int getsubUrlByUrl);

	public int createRole(UserRole role);

	public int updateRole(UserRole role);

	public int deleteRole(String roleId);

	public List<Designation> getDesignationList();

	public int createDesignation(Designation designation);

	public int updateDesignation(Designation designation);

	public int deleteDesignation(String designationId);

	public int professionalDetails(Employee profdet);

	public List<Ministry> getMinistryList();

	public int createMinistry(Ministry ministry);

	public int updateMinistry(Ministry ministry);

	public int deleteMinistry(String ministryId);

	public int insertEmpUrl(String roleid, String urlid, String subUrlid);

}
