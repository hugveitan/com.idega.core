/**
 * 
 */
package com.idega.core.contact.data.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

@Entity
@Table(name = Phone.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "phone.findAll", query = "select p from Phone p"),
	@NamedQuery(name = "phone.findUsersFaxPhone", query = "select p from Phone p join p.users u join p.phoneType t where t.uniqueName = " + PhoneType.UNIQUE_NAME_FAX_NUMBER + " and u.userID = :userID"),
	@NamedQuery(name = "phone.findUsersHomePhone", query = "select p from Phone p join p.users u join p.phoneType t where t.uniqueName = " + PhoneType.UNIQUE_NAME_HOME_PHONE + " and u.userID = :userID"),
	@NamedQuery(name = "phone.findUsersMobilePhone", query = "select p from Phone p join p.users u join p.phoneType t where t.uniqueName = " + PhoneType.UNIQUE_NAME_MOBILE_PHONE + " and u.userID = :userID"),
	@NamedQuery(name = "phone.findUsersWorkPhone", query = "select p from Phone p join p.users u join p.phoneType t where t.uniqueName = " + PhoneType.UNIQUE_NAME_WORK_PHONE + " and u.userID = :userID")
})
public class Phone implements Serializable {

	private static final long serialVersionUID = -7009150311403912036L;

	public static final String ENTITY_NAME = "ic_phone";
	public static final String COLUMN_PHONE_ID = "ic_phone_id";
	private static final String COLUMN_NUMBER = "phone_number";
	private static final String COLUMN_AREA_CODE_ID = "ic_area_code_id";
	private static final String COLUMN_PHONE_TYPE_ID = "ic_phone_type_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_PHONE_ID)
	private Integer phoneID;

	@Column(name = COLUMN_NUMBER)
	private String number;

	@ManyToOne
	@JoinColumn(name = COLUMN_AREA_CODE_ID)
	private AreaCode areaCode;

	@ManyToOne
	@JoinColumn(name = COLUMN_PHONE_TYPE_ID)
	private PhoneType phoneType;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Group.class)
	@JoinTable(name = "ic_group_phone", joinColumns = { @JoinColumn(name = COLUMN_PHONE_ID) }, inverseJoinColumns = { @JoinColumn(name = Group.COLUMN_GROUP_ID) })
	private List<Group> groups;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = User.class)
	@JoinTable(name = "ic_user_phone", joinColumns = { @JoinColumn(name = COLUMN_PHONE_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) })
	private List<User> users;

	/**
	 * @return the phoneID
	 */
	public Integer getId() {
		return this.phoneID;
	}

	/**
	 * @param phoneID
	 *          the phoneID to set
	 */
	public void setId(Integer phoneID) {
		this.phoneID = phoneID;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * @param number
	 *          the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the areaCode
	 */
	public AreaCode getAreaCode() {
		return this.areaCode;
	}

	/**
	 * @param areaCode
	 *          the areaCode to set
	 */
	public void setAreaCode(AreaCode areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 * @return the phoneType
	 */
	public PhoneType getPhoneType() {
		return this.phoneType;
	}

	/**
	 * @param phoneType
	 *          the phoneType to set
	 */
	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * @return the users
	 */
	public List<Group> getGroups() {
		return this.groups;
	}
}