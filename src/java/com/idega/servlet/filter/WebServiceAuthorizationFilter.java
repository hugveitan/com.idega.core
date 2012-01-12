/*
 * $Id: WebServiceAuthorizationFilter.java,v 1.2 2007/01/22 08:16:56 tryggvil Exp $
 * Created on Apr 4, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import sun.misc.BASE64Decoder;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.util.StringHandler;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * This Servlet filter sits by default in front of all (Axis) web services
 * and blocks unpriviliged access. <br/>
 * So access by clients that desire to use the services needs to be configured 
 * with the WS_DO_BASIC_AUTHENTICATION or WS_VALID_IP application properties.
 * </p>
 *  Last modified: $Date: 2007/01/22 08:16:56 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class WebServiceAuthorizationFilter implements Filter {
	
	private final String WEB_SERVICE_USER_ROLE = "web_service_user"; 
	
	private final String DO_BASIC_AUTHENTICATION = "WS_DO_BASIC_AUTHENTICATION";

	private final String VALID_IP = "WS_VALID_IP";

	LoginBusinessBean loginBusiness = null;
	UserBusiness userBusiness = null;
	
	BASE64Decoder myBase64Decoder = null;

	@Autowired
	UserLoginDAO userLoginDAO;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest myRequest, ServletResponse myResponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)myRequest;
		HttpServletResponse response = (HttpServletResponse)myResponse;

		ServletContext myServletContext = request.getSession().getServletContext();
	   	// getting the application context
    		IWMainApplication mainApplication = IWMainApplication.getIWMainApplication(myServletContext);
    		boolean doCheck = mainApplication.getIWApplicationContext().getApplicationSettings().getBoolean(this.DO_BASIC_AUTHENTICATION, true);

    		if (doCheck) {
    			if (! requestIsValid(request)) {
    				//send a 403 error
    				response.sendError(HttpServletResponse.SC_FORBIDDEN);
    				return;
    			}
    		} else {    			
			boolean isValid = false;
    			try {
    				String validIP = mainApplication.getIWApplicationContext().getApplicationSettings().getProperty(this.VALID_IP, "");
    				String[] ips = validIP.split("\\;");
    				for (int i = 0; i < ips.length; i++) {
    					if (ips[i].equals(request.getRemoteAddr())) {
    						isValid = true;
    						break;
    					}
    				}
    			} catch (Exception e) {
    				isValid = false;
    			}
    			
    			if (!isValid) {
    				//send a 403 error
    				response.sendError(HttpServletResponse.SC_FORBIDDEN);
    				return;    				
    			}
    		}
    		
		chain.doFilter(request, response);
	}
		
		
	private boolean requestIsValid(HttpServletRequest request) {
		String decodedNamePassword = getDecodedNamePassword(request);
		if (decodedNamePassword == null) {
			return false;
		}
		int delimiterIndex = decodedNamePassword.indexOf(":");
		if (delimiterIndex < 0) {
			return false;
		}
		String name = decodedNamePassword.substring(0, delimiterIndex);
		String password = decodedNamePassword.substring(delimiterIndex + 1);
		if (! StringHandler.isNotEmpty(name)) {
			return false;
		}
		if (! StringHandler.isNotEmpty(password)) {
			return false;
		}
		return checkUserPasswordAndRole(request, name, password);
	}
	
	private boolean checkUserPasswordAndRole(HttpServletRequest myRequest, String name, String password) {
		ServletContext myServletContext = myRequest.getSession().getServletContext();

    	IWMainApplication mainApplication = IWMainApplication.getIWMainApplication(myServletContext);
    	IWApplicationContext iwac = mainApplication.getIWApplicationContext();

		UserLogin login = getUserLoginDAO().findLoginByUsername(name);
		if (login != null) {
    		if (! hasRole(login, mainApplication)) {
    			return false;
    		}
    		return getLoginBusiness(iwac).verifyPassword(login, password);
		}
		else {
			return false;
		}
	}
	
	private boolean hasRole(UserLogin userLogin, IWMainApplication iwMainApplication) {
		User user = userLogin.getUser();
		List groups = user.getUserRepresentative().getParentGroups();
		if (groups != null) {
			Iterator groupIterator = groups.iterator();
			AccessController accessController = iwMainApplication.getAccessController();
			while (groupIterator.hasNext()) {
				Group group = (Group) groupIterator.next();
				if (accessController.hasRole(this.WEB_SERVICE_USER_ROLE, group, null)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getDecodedNamePassword(HttpServletRequest request) {
		String basicNamePassword = request.getHeader("Authorization");
		if (basicNamePassword == null) {
			return null;
		}
		basicNamePassword = basicNamePassword.trim();
		if (! basicNamePassword.startsWith("Basic")) {
			return null;
		}
		if (basicNamePassword.length() < 6 ) {
			return null;
		}
		String namePassword = basicNamePassword.substring(6);
		try {
			byte[] decodedNamePasswordArray = this.myBase64Decoder.decodeBuffer(namePassword);
			ByteBuffer wrappedDecodedNamePasswordArray = ByteBuffer.wrap(decodedNamePasswordArray);
			Charset charset = Charset.forName("ISO-8859-1");
			CharBuffer buffer = charset.decode(wrappedDecodedNamePasswordArray);
			return buffer.toString();
		}
		catch (IOException ex) {
			return null;
		}
	}
    	
    private LoginBusinessBean getLoginBusiness(IWApplicationContext iwac) {
    	if (this.loginBusiness == null) { 
        	this.loginBusiness = LoginBusinessBean.getLoginBusinessBean(iwac);
    	}
    	return this.loginBusiness;
	}
		
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		this.myBase64Decoder = new BASE64Decoder();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// noting to destroy
	}
	
	public UserLoginDAO getUserLoginDAO() {
		if (this.userLoginDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return userLoginDAO;
	}
}