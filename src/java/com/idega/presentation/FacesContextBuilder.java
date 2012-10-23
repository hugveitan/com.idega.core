package com.idega.presentation;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
 
public class FacesContextBuilder {
	
	/**
	 * This method can be used to make FacesContext available outside of FacesServlet.
	 * However, the facesContext created in this way must be released before the FacesServlet runs, either by
	 * FacesContext#release() or better yet by FacesContextBuilder.removeFacesContext()
	 * @param request
	 * @param response
	 * @return
	 */
	public static FacesContext createFacesContext(final ServletRequest request, final ServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null)
        {
            return facesContext;
        }
 
        FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
                .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
 
        ServletContext servletContext = ((HttpServletRequest) request).getSession().getServletContext();
        facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);
        InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
        if (null == facesContext.getViewRoot())
        {
            facesContext.setViewRoot(new UIViewRoot());
        }
 
        return facesContext;
    }
    
    public static void removeFacesContext() {
        InnerFacesContext.setFacesContextAsCurrentInstance(null);
    }
 
    private abstract static class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(final FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }
}
