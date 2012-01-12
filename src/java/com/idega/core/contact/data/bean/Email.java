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

import com.idega.core.contact.data.EmailDataView;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

@Entity
@Table(name = Email.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "email.findAll", query = "select e from Email e"),
	@NamedQuery(name = "email.findAllByUser", query = "select e from Email e join e.users u where u.userID = :userID"),
	@NamedQuery(name = "email.findByUserAndType", query = "select e from Email e join e.users u join e.emailType t where t.uniqueName = :uniqueName and u.userID = :userID"),
	@NamedQuery(name = "email.findAllByGroup", query = "select e from Email e join e.groups g where g.groupID = :groupID"),
	@NamedQuery(name = "email.findByGroupAndType", query = "select e from Email e join e.groups g join e.emailType t where t.uniqueName = :uniqueName and g.groupID = :groupID")
})
public class Email implements Serializable, EmailDataView {

	private static final long serialVersionUID = 2434545685633792727L;

	public static final String ENTITY_NAME = "ic_email";
	public static final String COLUMN_EMAIL_ID = "ic_email_id";
	private static final String COLUMN_ADDRESS = "address";
	private static final String COLUMN_EMAIL_TYPE_ID = "ic_email_type_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_EMAIL_ID)
	private Integer emailID;
	
	@Column(name = COLUMN_ADDRESS)
	private String address;
	
	@ManyToOne
	@JoinColumn(name = COLUMN_EMAIL_TYPE_ID)
	private EmailType emailType;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = User.class)
	@JoinTable(name = "ic_user_email", joinColumns = { @JoinColumn(name = COLUMN_EMAIL_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) }) 
	private List<User> users;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = Group.class)
	@JoinTable(name = "ic_group_email", joinColumns = { @JoinColumn(name = COLUMN_EMAIL_ID) }, inverseJoinColumns = { @JoinColumn(name = Group.COLUMN_GROUP_ID) }) 
	private List<Group> groups;

	/**
	 * @return the emailID
	 */
	public Integer getId() {
		return this.emailID;
	}

	/**
	 * @param emailID the emailID to set
	 */
	public void setId(Integer emailID) {
		this.emailID = emailID;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.contact.data.EmailDataView#getEmailAddress()
	 */
	public String getEmailAddress() {
		return getAddress();
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the emailType
	 */
	public EmailType getEmailType() {
		return this.emailType;
	}

	/**
	 * @param emailType the emailType to set
	 */
	public void setEmailType(EmailType emailType) {
		this.emailType = emailType;
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