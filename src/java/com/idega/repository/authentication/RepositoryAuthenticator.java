package com.idega.repository.authentication;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;

import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.FacesContextBuilder;
import com.idega.repository.RepositoryService;
import com.idega.servlet.filter.BaseFilter;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class RepositoryAuthenticator extends BaseFilter {

	private static final String REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME = Principal.class.getName() + "_user_principal",
								REPOSITORY_PASSWORD = "iw_repository_password",
								REPOSITORY_ROLES_UPDATED = "iw_repository_roles_updated",
								PROPERTY_UPDATE_ROLES = "repository.updateroles.enable";

	@Autowired
	private RepositoryService repository;
	@Autowired
	private AuthenticationBusiness authentication;

	private boolean defaultPermissionsApplied;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		doAuthentication(request, response, chain);
		
	}

	private boolean applyDefaultPermissionsToRepository() {
		try {
			if (!getRepositoryService().createFolderAsRoot(CoreConstants.CONTENT_PATH)) {
				return false;
			}
			//	TODO: use real access rights
			getRepositoryService().applyAccessControl(CoreConstants.CONTENT_PATH, new AccessControlPolicy[] {
					new IWAccessControlPolicy("content_editor", Arrays.asList(Privilege.JCR_ALL))
			});

			if (!getRepositoryService().createFolderAsRoot(CoreConstants.PUBLIC_PATH)) {
				return false;
			}

			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void doAuthentication(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		LoginBusinessBean loginBusiness = getLoginBusiness(req);
		
		try {
			//This attaches FacesContext to the thread and thereby makes it available, 
			//but this FacesContext must be released before the FacesServelt runs. 
			FacesContextBuilder.createFacesContext(request, response);
			
			if (loginBusiness.isLoggedOn(req)) {
				LoggedOnInfo lInfo = loginBusiness.getLoggedOnInfo(session);
				req = setAsAuthenticatedInRepository(req, lInfo.getLogin(), lInfo);
			} else {
				String[] loginAndPassword = loginBusiness.getLoginNameAndPasswordFromBasicAuthenticationRequest(req);
				String loggedInUser = getUserAuthenticatedByRepository(session);
				if (!ArrayUtil.isEmpty(loginAndPassword) && loginAndPassword.length >= 2) {
					String username = loginAndPassword[0];
					String password = loginAndPassword[1];
					LoggedOnInfo lInfo = loginBusiness.getLoggedOnInfo(session, username);
					if (loggedInUser == null) {
						if (isAuthenticated(req, lInfo, username, password)) {
							req = setAsAuthenticatedInRepository(req, username, lInfo);
						} else {
							setAsUnauthenticatedInRepository(session);
						}
					} else if (!username.equals(loggedInUser)) {
						if (isAuthenticated(req, lInfo, username, password)) {
							req = setAsAuthenticatedInRepository(req, username, lInfo);
						} else {
							setAsUnauthenticatedInRepository(session);
						}
					}

				} else if (loggedInUser != null) {
					setAsUnauthenticatedInRepository(session);
				}
			}
			
			if (!defaultPermissionsApplied) {
				defaultPermissionsApplied = true;
				defaultPermissionsApplied = applyDefaultPermissionsToRepository();

//				IWMainApplication iwma = IWMainApplication.getIWMainApplication((HttpServletRequest) request);
//				ELUtil.getInstance().publishEvent(new RepositoryStartedEvent(iwma));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return;
		} finally {
			FacesContextBuilder.removeFacesContext();
		}
		
		chain.doFilter(request, response);
	}

	private HttpServletRequest setAsAuthenticatedInRepository(HttpServletRequest request,String loginName, LoggedOnInfo lInfo)
		throws RemoteException, IOException, RepositoryException {

		String repositoryPrincipal = loginName;
		HttpSession session = request.getSession();
		LoginBusinessBean loginBusiness = getLoginBusiness(request);

		if (loginBusiness.isLoggedOn(request)) {
			LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
			if (loginSession.isSuperAdmin()) {
				String rootUserName = ((SimpleCredentials) getAuthenticationBusiness().getRootUserCredentials()).getUserID();
				request = new RepositoryAuthenticatedRequest(request, rootUserName, Collections.singleton(rootUserName));
				repositoryPrincipal = rootUserName;
			} else {
				if (request.getUserPrincipal() == null && lInfo != null) {
					request = new RepositoryAuthenticatedRequest(request, loginName, lInfo.getUserRoles());
				}
				updateRolesForUser(request, lInfo);
			}
		} else {
			String rootUserName = ((SimpleCredentials) getAuthenticationBusiness().getRootUserCredentials()).getUserID();
			if (loginName.equals(rootUserName)) {
				request = new RepositoryAuthenticatedRequest(request, rootUserName, Collections.singleton(rootUserName));
			} else {
				request = new RepositoryAuthenticatedRequest(request, loginName, lInfo.getUserRoles());
				updateRolesForUser(request, lInfo);
			}
		}

		session.setAttribute(REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME, repositoryPrincipal);
		return request;
	}

	private void updateRolesForUser(HttpServletRequest request, LoggedOnInfo lInfo) throws RepositoryException, RemoteException, IOException {
		generateUserFolders(request);

		IWMainApplication iwma = getIWMainApplication(request);
		boolean doUpdateRoles = iwma.getSettings().getBoolean(PROPERTY_UPDATE_ROLES, Boolean.TRUE);
		if (doUpdateRoles && lInfo != null && lInfo.getAttribute(REPOSITORY_ROLES_UPDATED) == null) {
			AuthenticationBusiness business = getAuthenticationBusiness();
			business.updateRoleMembershipForUser(lInfo.getLogin(), lInfo.getUserRoles(), null);
			lInfo.setAttribute(REPOSITORY_ROLES_UPDATED, Boolean.TRUE);
		}
	}

	private void generateUserFolders(HttpServletRequest request) throws RepositoryException {
		getRepositoryService().generateUserFolders(request.getRemoteUser());
	}

	private String getUserAuthenticatedByRepository(HttpSession session) {
		return (String) session.getAttribute(REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}

	private void setAsUnauthenticatedInRepository(HttpSession session) {
		session.removeAttribute(REPOSITORY_USER_PRINCIPAL_ATTRIBUTE_NAME);
	}

	private boolean isAuthenticated(HttpServletRequest request, LoggedOnInfo info, String login, String password) {
		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		if (loginBusiness.isLoggedOn(request)) {
			return true;
		} else {
			if (getAuthenticationBusiness().isRootUser(request)) {
				return true;
			}
			if (info != null) {
				String repositoryPassword = (String) info.getAttribute(REPOSITORY_PASSWORD);
				return !StringUtil.isEmpty(repositoryPassword) && repositoryPassword.equals(password);
			}
		}
		return false;
	}

	private RepositoryService getRepositoryService() {
		if (repository == null) {
			autowire();
		}
		return repository;
	}
	private AuthenticationBusiness getAuthenticationBusiness() {
		if (authentication == null) {
			autowire();
		}
		return authentication;
	}
	private void autowire() {
		ELUtil.getInstance().autowire(this);
	}

	@Override
	public void destroy() {}

}