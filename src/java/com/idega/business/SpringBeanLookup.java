package com.idega.business;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.springframework.context.ApplicationContext;

import com.idega.idegaweb.IWApplicationContext;

/**
 * <p>Glue code for legacy non-spring beans. Lookup spring bean by interface. Strive as much not to use this class.</p>
 * <p>This class should be mainly used for legacy code, which used to lookup bean by using IBOLookup.getSessionInstance</p>
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.6 $
 *
 * Last modified: $Date: 2007/12/12 10:36:36 $ by $Author: civilis $
 *
 */
public class SpringBeanLookup {
	
	private static SpringBeanLookup me;
	
	public static synchronized SpringBeanLookup getInstance() {
		
		if (me == null)
			me = new SpringBeanLookup();
		
		return me;
	}
	
	protected SpringBeanLookup() { 	}
	
	/**
	 * 
	 * Additionaly retrieves ServletContext from HttpSession instance
	 * 
	 * @param session - current http session
	 * @param clazz - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public <T>T getSpringBean(HttpSession session, Class<T> clazz) {
		
		return getSpringBean(session.getServletContext(), clazz);
	}
	
	/**
	 * 
	 * @param ctx - current ServletContext
	 * @param clazz - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public <T>T getSpringBean(ServletContext ctx, Class<T> clazz) {
		
		if(!clazz.isAnnotationPresent(SpringBeanName.class))
			throw new RuntimeException("Interface is not annotated with "+SpringBeanName.class.getName()+" annotation");
		
		SpringBeanName bname = (SpringBeanName)clazz.getAnnotation(SpringBeanName.class);
		
		ApplicationContext ac = org.springframework.web.context.support.WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
		@SuppressWarnings("unchecked")
		T bean = (T)ac.getBean(bname.value());
		return bean;
	}
	
	/**
	 * Retrieves ServletContext from IWMainApplication instance
	 * 
	 * @param iwac - current IWApplicationContext
	 * @param clazz - interface class, annotated with com.idega.business.SpringBeanName 
	 * @return Spring managed bean. Null if bean not found or interface not annotated.
	 */
	public <T>T getSpringBean(IWApplicationContext iwac, Class<T> clazz) {
		
		return getSpringBean(iwac.getIWMainApplication().getServletContext(), clazz);
	}
}