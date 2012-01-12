/*
 * $Id: ListItem.java,v 1.5 2008/04/24 23:44:18 laddi Exp $
 * Created on 16.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;


/**
 * <p>
 * An item to render out a "ListItem" or <code>li</code> html tag.
 * This class is used by the Lists class.
 * </p>
 *  Last modified: $Date: 2008/04/24 23:44:18 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.5 $
 */
public class ListItem extends PresentationObjectContainer {
	
	public ListItem(){
		setTransient(false);
	}
	
	
	@Override
	public void print(IWContext iwc) throws Exception {
		print("<li " + getMarkupAttributesString() + ">");

		List<UIComponent> theObjects = this.getChildren();
		if (theObjects != null) {
			for (Iterator<UIComponent> iter = theObjects.iterator(); iter.hasNext();) {
				UIComponent item = iter.next();
				renderChild(iwc,item);
			}
		}
		println("</li>");
	}
	
	/**
	 * Sets the style attributes on all list items.
	 * @param attributeString
	 */
	public void setListItemStyleAttributes(String attributeString){
		getMarkupAttributes().put("style",attributeString);
	}
	
	public String getListItemStyleAttributes(){
		return getMarkupAttributes().get("style");
	}

	/**
	 * Sets the style attributes on all list items.
	 * @param attributeString
	 */
	public void setListItemStyleClass(String attributeString){
		getMarkupAttributes().put("class",attributeString);
	}
	
	public String getListItemStyleClass(){
		return getMarkupAttributes().get("class");
	}
	
	public void setOnClick(String action) {
		setMarkupAttributeMultivalued(CLICK, action);
	}
	
}
