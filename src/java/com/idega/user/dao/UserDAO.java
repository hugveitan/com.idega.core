/**
 *
 */
package com.idega.user.dao;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.Gender;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

@SpringBeanName("userDAO")
public interface UserDAO extends GenericDao {

	@Transactional(readOnly = false)
	public User createUser(String firstName, String middleName, String lastName, String displayName, String personalID, String description, Gender gender, Date dateOfBirth, Group primaryGroup);

	public User getUser(Integer userID);

	public User getUser(String personalID);

	public User getUserByUUID(String uniqueID);

	public List<User> getUsersByNames(String firstName, String middleName, String lastName);

	public Email getUsersMainEmail(User user);

	public Email updateUserMainEmail(User user, String address);

	public Gender getGender(String name);

	public Gender getMaleGender();

	public Gender getFemaleGender();

}