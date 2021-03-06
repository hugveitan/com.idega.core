/**
 *
 */
package com.idega.core.accesscontrol.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.accesscontrol.data.bean.PermissionGroup;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.Group;

@SpringBeanName("permissionDAO")
public interface PermissionDAO extends GenericDao {

	@Transactional(readOnly = false)
	public PermissionGroup createPermissionGroup(String name, String description);

	public PermissionGroup findPermissionGroup(Object primaryKey);

	public PermissionGroup getPermissionGroup(String name);

	@Transactional(readOnly = false)
	public ICPermission createPermission(String contextType, String contextValue, Group group, String permissionString, boolean permissionValue);

	@Transactional(readOnly = false)
	public void removePermissions(String contextType, String contextValue, String permissionString, Collection<Group> groups);

	public ICPermission findPermission(String contextType, String contextValue, String permissionString, Group group);

	public List<ICPermission> findPermissions(String contextType);
	public List<ICPermission> findPermissions(String contextType, String contextValue);
	public List<ICPermission> findPermissions(String contextType, String contextValue, String permissionString);
	public List<ICPermission> findPermissions(String contextType, Collection<String> contextValues, Group group);
	public List<ICPermission> findPermissions(String contextType, String contextValue, String permissionString, String permissionValue);
	public List<ICPermission> findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(Group group, String permissionString, String contextType);
	public List<ICPermission> findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(Group group, Collection<String> permissionStrings, String contextType);
	public List<ICPermission> findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup(String contextType, String contextValue, Collection<String> permissionStrings, Group group);
	public List<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Group group);
	public List<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Collection<Group> groups);
	public List<ICPermission> findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection<Group> groups, String permissionString, String contextType);
	public List<ICPermission> findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection<Group> groups, Collection<String> permissionStrings, String contextType);
	public ICPermission findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue(Group group, String permissionString, String contextType, String contextValue);

	@Transactional(readOnly = false)
	public ICRole createRole(String roleKey, String roleDescriptionLocalizableKey, String roleNameLocalizableKey);

	public ICRole findRole(String roleKey);
	public List<ICRole> findAllRoles();
}