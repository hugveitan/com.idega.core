/*
 * $Id: ViewNode.java,v 1.3 2005/02/28 17:26:06 gummi Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Collection;
import java.util.Locale;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * Base interface for "view nodes".<br>
 * A view node is a node in a hierarchial (tree) structure that represents both URLs and references to
 * UserInterface functions.<br>
 * There is an instance of a view node for each part of a URL structure and is separated by the '/' character.
 * So that for example in the URL "/myapp/workspace/builder" there is one ViewNode instance for both
 * 'workspace' and 'builder parts of the URL. <br>
 * ViewNodes are accessed and managed by the ViewManager instance.
 * 
 *  Last modified: $Date: 2005/02/28 17:26:06 $ by $Author: gummi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public interface ViewNode {
	
	//ViewNode tree strucuture
	public String getViewId();
	public void addChildViewNode(ViewNode node);
	public Collection getChildren();
	/**
	 * Returns the primary URI up the tree hierarchy and includes the webapplications context path if any.
	 */
	public String getURI();
	/**
	 * This method returns the child ViewNode instance hierarchially down in the tree from this node.<br>
	 * The '/' character acts as a separator. This means that the value 'idegaweb' will try tro return the direct child of this node. 
	 * The value 'idegaweb/login' will try to get the child of with id 'login' from the child 'idegaweb' of this node.<br>
	 * The special value '/' will return this node instance and otherwise the '/' characters in the beginning and end of the string are stripped away.
	 * 
	 * @param childViewId
	 * @return The child node found under this node or null if nothing found.
	 */
	public ViewNode getChild(String childViewId);
	public ViewNode getParent();
	
	//ViewHandler/JSF properties
	public ViewHandler getViewHandler();
	/**
	 * Returns true if the node is resource based wich means that it will be served (dispatched) from a resource
	 * (e.g. a JSP page) on a given URI on the sever. A viewNode can not be both resource based or component based at the same time.
	 * @return
	 */
	public boolean isResourceBased();
	/**
	 * Returns true if the node represents a component (UIComponent) and will be created with createComponent() 
	 * typically called from a ViewHandler.
	 * A viewNode can not be both resource based or component based at the same time.
	 * @return
	 */
	public boolean isComponentBased();
	public String getResourceURI();
	//public Class getComponentClass();
	/**
	 * Creates a new instance of the component for this node.
	 */
	public UIComponent createComponent(FacesContext context);
	
	
	//Accesscontrol properties
	public Collection getAuthorizedRoles();
	
	//UI properties
	public Icon getIcon();
	public String getName();
	public String getLocalizedName(Locale locale);
	public KeyboardShortcut getKeyboardShortcut();
	public ToolTip getToolTip(Locale locale);
	
	public boolean isRendered();
	
}