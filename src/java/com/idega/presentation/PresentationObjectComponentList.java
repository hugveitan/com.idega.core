/*
 * $Id: PresentationObjectComponentList.java,v 1.2 2004/11/16 02:20:13 tryggvil Exp $ Created on
 * 14.11.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;

/**
 * Overrided from JSFs standard Children because of the clone() issue.
 * 
 * Last modified: $Date: 2004/11/16 02:20:13 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.2 $
 */
class PresentationObjectComponentList extends AbstractList implements Serializable,Cloneable {

	private UIComponent _component;

	private List _list = new ArrayList();

	PresentationObjectComponentList(UIComponent component) {
		_component = component;
	}

	public Object get(int index) {
		return _list.get(index);
	}

	public int size() {
		return _list.size();
	}

	public Object set(int index, Object value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		UIComponent child = (UIComponent) _list.set(index, value);
		if (child != null)
			child.setParent(null);
		return child;
	}

	public boolean add(Object value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		return _list.add(value);
	}

	public void add(int index, Object value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		_list.add(index, value);
	}

	public Object remove(int index) {
		UIComponent child = (UIComponent) _list.remove(index);
		if (child != null)
			child.setParent(null);
		return child;
	}

	private void setNewParent(UIComponent child) {
		//UIComponent oldParent = child.getParent();
		//if (oldParent != null) {
		//	oldParent.getChildren().remove(child);
		//}
		child.setParent(_component);
	}

	private void checkValue(Object value) {
		if (value == null)
			throw new NullPointerException("value");
		if (!(value instanceof UIComponent))
			throw new ClassCastException("value is not a UIComponent");
	}
	
	public Object clone(){
		Object newObject = null;
		try {
			newObject = super.clone();
			PresentationObjectComponentList componentList = (PresentationObjectComponentList)newObject;
			componentList._list=(List) ((ArrayList)this._list).clone();
		}
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newObject;
	}
	
	
	/**
	 * @return Returns the _component.
	 */
	UIComponent getComponent() {
		return _component;
	}
	/**
	 * @param _component The _component to set.
	 */
	void setComponent(UIComponent _component) {
		this._component = _component;
	}
}