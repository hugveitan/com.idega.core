/*
 * $Id: PresentationObjectContainer.java,v 1.59 2009/05/27 16:08:46 valdas Exp $
 *
 * Created in 2001 by Tryggvi Larusson
 *
 * Copyright (C) 2001-2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 *
 */
package com.idega.presentation;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.event.IWPresentationState;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.text.Text;
/**
 * A base class for Containers of PresentationObjects (i.e. that can have children).<br>
 * As of JSF this class is basically obsolete, as all UIComponents are "containers".<br>
 * <br>
 * Last modified: $Date: 2009/05/27 16:08:46 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.59 $
 */
public class PresentationObjectContainer extends PresentationObject
{
	//private List children;

	//Legacy temporary variable:
	protected transient List<UIComponent> allObjects = null;

	//protected boolean goneThroughMain = false;
	protected boolean _locked = true;
	protected String _label = null;
	/**
	 * Default constructor.
	 * Should only be called by sublasses.
	 */
	public PresentationObjectContainer()
	{
	}

	@Override
	public List<UIComponent> getChildren(){
		/*if (this.children == null)
		{
			this.children = new PresentationObjectList(this);
			//this.children=new ArrayList();
		}
		return this.children;*/
		return super.getChildren();
	}

	/**
	 * Add an object inside this container
	 */
	protected void add(int index, PresentationObject modObject)
	{
		try
		{
			if (modObject != null)
			{
				//modObject.setParentObject(this);
				//        modObject.setLocation(this.getLocation());
				getChildren().add(index, modObject);
			}
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	/**
	 * Add an object inside this container
	 */
	public void add(PresentationObject modObject)
	{
		try
		{
			if (modObject != null)
			{
				//modObject.setParentObject(this);
				//        modObject.setLocation(this.getLocation());
				getChildren().add(modObject);
			}
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	public void add(UIComponent component)
	{
		try
		{
			getChildren().add(component);
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	public void add(Object moduleObject)
	{
		if (moduleObject instanceof PresentationObject)
		{
			add((PresentationObject) moduleObject);
		}
		else
		{
			System.err.println(
				"Not instance of PresentationObject and therefore cannot be added to PresentationObjectContainer: " + moduleObject);
		}
	}
	public void addAtBeginning(PresentationObject modObject)
	{
		modObject.setParentObject(this);
		//    modObject.setLocation(this.getLocation());
		getChildren().add(0, modObject);
	}
	/**
	 * Add an object inside this container - same as the add() function
	 *
	 * @deprecated replaced by the add function
	 */
	@Deprecated
	public void addObject(PresentationObject modObject)
	{
		add(modObject);
	}
	/**
	 * Adds an simple string (Creates a Text object around it)
	 */
	public void add(String theText)
	{
		add(new Text(theText));
	}
	/**
	 * Adds an array of strings and creates an end of line character after each
	 * element
	 */
	public void add(String[] theTextArray)
	{
		for (int i = 0; i < theTextArray.length; i++)
		{
			add(theTextArray[i]);
			addBreak();
		}
	}
	public void addBreak()
	{
		Text text = Text.getBreak();
		add(text);
	}
	public void addText(String theText)
	{
		add(new Text(theText));
	}
	public void addText(String theText, String format)
	{
		Text text = new Text();
		if (format != null)
		{
			if (format.equals("bold"))
			{
				text.setBold();
			}
			else if (format.equals("italic"))
			{
				text.setItalic();
			}
			else if (format.equals("underline"))
			{
				text.setUnderline();
			}
		}
		add(text);
	}
	public void addText(int integerToInsert)
	{
		addText(Integer.toString(integerToInsert));
	}

	public UIComponent getContainedObject(Class<?> objectClass) {
		List<UIComponent> objects = getChildren();
		if (objects != null) {
			Iterator<UIComponent> iter = objects.iterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				if (element.getClass() == objectClass) {
					return (UIComponent) element;
				}
			}
		}
		return null;
	}
	public List<UIComponent> getChildrenRecursive()
	{
		if (this.allObjects == null)
		{
			List<UIComponent> toReturn = null;
			List<UIComponent> children = this.getChildren();
			if (children != null)
			{
				toReturn = new ArrayList<UIComponent>();
				toReturn.containsAll(children);
				Iterator<UIComponent> iter = children.iterator();
				while (iter.hasNext())
				{
					UIComponent item = iter.next();
					if (item instanceof PresentationObjectContainer)
					{
						toReturn.add(item);
						//if(!toReturn.contains(item)){
						List<UIComponent> tmp = ((PresentationObjectContainer) item).getChildrenRecursive();
						if (tmp != null)
						{
							toReturn.addAll(tmp);
						}
						//}
					}
					else
					{
						toReturn.add(item);
					}
				}
			}
			this.allObjects = toReturn;
		}
		return this.allObjects;
	}
	public void resetAllContainedObjectsRecursive()
	{
		this.allObjects = null;
	}
	public boolean isEmpty()
	{
		return getChildren().isEmpty();
	}

	@Override
	public void _main(IWContext iwc) throws Exception {
		if (!this.initializedInMain) {
			this.initInMain(iwc);
		}

		if (mayGoThroughMain()) {
			try	{
				main(iwc);
			} catch (NotLoggedOnException noex)	{
				//throw the exception further:
				throw noex;
			} catch (Exception ex) {
				add(new ExceptionWrapper(ex, this));
				getLogger().log(Level.WARNING, "Some error occured rendering " + this, ex);
			}

			if (IWMainApplication.useJSF) {
				//Do not go through the children in JSF as that is done through the encode/begin/children methods:
			} else {
				int numberOfObjects = numberOfObjects();
				for (int index = 0; index < numberOfObjects; index++) {
					try {
						PresentationObject tempobj = (PresentationObject) objectAt(index);
						try {
							if (tempobj != null) {
								if (tempobj != this) {
									tempobj._main(iwc);
								}
							}
						} catch (Exception ex) {
							add(new ExceptionWrapper(ex, this));
							getLogger().log(Level.WARNING, "Some error occured rendering " + tempobj, ex);
						}
					} catch (Exception e) {
						getLogger().log(Level.WARNING, "Exception in PressentationObjectContainer._main() catched: "+e.getClass()+" : "+e.getMessage(), e);
					}
				}
			}
		}
		setGoneThroughMain();
	}

	/**
	 *
	 * @uml.property name="children"
	 */
	protected void setChildren(List<UIComponent> newChildren) {
		//this.children = newChildren;
		this.getChildren().addAll(newChildren);
	}

	/*
	 * protected void prepareClone(PresentationObject newObjToCreate){ int
	 * number = numberOfObjects(); for (int i = 0; i < number; i++) {
	 * PresentationObject tempObj = this.objectAt(i);
	 * ((PresentationObjectContainer)newObjToCreate).add((PresentationObject)tempObj.clone()); }
	 *  // if (this.theObjects!=null){
	 * //((PresentationObjectContainer)newObjToCreate).setObjects((Vector)this.theObjects.clone()); // }
	 */

	/*public void _print(IWContext iwc) throws Exception
	{
		goneThroughMain = false;
		super._print(iwc);
	}*/
	/**
	 * The default implementation for the print function for a container.
	 *
	 * This function is invoked on each request by the user for each
	 * PresentationObject instance (after main(iwc)).
	 *
	 * Override this function where it is needed to print out the specified
	 * content. This function should only be overrided in idegaWeb Elements.
	 */

	@Override
	public void print(IWContext iwc) throws Exception
	{
		initVariables(iwc);
		//Workaround for JRun - JRun has hardcoded content type text/html in
		// JSP pages
		//if(this.doPrint(iwc)){
		if (iwc.getMarkupLanguage().equals("WML"))
		{
			iwc.setContentType("text/vnd.wap.wml");
		}
		//if (!isEmpty())
		//{
			/*int numberofObjects = numberOfObjects();
			for (int index = 0; index < numberofObjects; index++)
			{
				UIComponent tempobj = objectAt(index);
				try
				{
					if (tempobj != null)
					{
						//TL JSF Change:
						//tempobj._print(iwc);
						this.renderChild(iwc,tempobj);
						flush();
					}
				}
				catch (Exception ex)
				{
					ExceptionWrapper exep = new ExceptionWrapper(ex, this);
					exep._print(iwc);
				}
			}*/
			Iterator<UIComponent> iter = this.getChildren().iterator();
			while(iter.hasNext()){
				UIComponent child = iter.next();
				renderChild(iwc,child);
			}
		//}
	}


	/**
	 * @see com.idega.presentation.PresentationObject#initVariables(com.idega.presentation.IWContext)
	 */
	@Override
	public void initVariables(IWContext iwc) throws IOException {
		//goneThroughMain = false;
		//This is a legacy fix to make sure the goneThroughMain variable is reset back for
		// components that are stored in session.
		// For the JSF environment this is done instead in the encodeEnd method.
		if(!IWMainApplication.useJSF){
			resetGoneThroughMain();
		}
		super.initVariables(iwc);
	}

	/**
	 *
	 */
	public UIComponent getContainedObject(String instanceId){
		try{
			try{
				//is it a region or a pure UIComponent?
				boolean isRegion = (instanceId.indexOf(".")>=0);
				if(isRegion){
					//Try to assume that the objectInstanceID is in format 1234.2.2 (icobjectinstanceid.xpox.ypos)
					String regionOwnerInstanceId = instanceId.substring(0, instanceId.indexOf("."));
					String index = instanceId.substring(instanceId.indexOf(".") + 1, instanceId.length());

					if (index.indexOf(".") == -1){
						//not a table...don't actually now what kind of object this might be...eiki
						return (((PresentationObjectContainer) getContainedObject(regionOwnerInstanceId)).objectAt(Integer.parseInt(index)));
					}
					else{
						//A region that is a table..
						int xindex = Integer.parseInt(index.substring(0, index.indexOf(".")));
						int yindex = Integer.parseInt(index.substring(index.indexOf(".") + 1, index.length()));
						try {
							return (((Table) getContainedObject(regionOwnerInstanceId)).containerAt(xindex, yindex));
						} catch (ClassCastException e1) {
							e1.printStackTrace();
							return (null);
						}
					}
				}
				else{
//					Not a region
					try{
						//Try to interpret the objectInstanceID as an integer
						//backward compatability for PresentationObjects
						int instanceIdINT = Integer.parseInt(instanceId);

						Iterator<UIComponent> iter = this.getFacetsAndChildren();

						while (iter.hasNext()){
							UIComponent item = iter.next();
							if ( item instanceof PresentationObject &&  ((PresentationObject) item).getICObjectInstanceID() == instanceIdINT){
								return item;
							}
							else if (item instanceof PresentationObjectContainer){
								UIComponent theReturn = ((PresentationObjectContainer) item).getContainedObject(instanceId);
								if (theReturn != null){
									return theReturn;
								}
							}
						}
						return null;
					}
					catch (NumberFormatException nfe)
					{
						//must be one of those spiffy new UIComponents and what'not's
						Iterator<UIComponent> iter = this.getFacetsAndChildren();

						while (iter.hasNext()){
							UIComponent item = iter.next();
							if(instanceId.equals(item.getId())){
								return item;
							}
						}
						return null;
					}

				}
			}
			catch(StringIndexOutOfBoundsException se){
				return null;
			}


		}
		catch (NullPointerException ex)
		{
			return (null);
		}
	}
	/**
	 *
	 */
	public UIComponent getContainedLabeledObject(String label){
		Iterator<UIComponent> iter = this.getFacetsAndChildren();
		while (iter.hasNext())
		{
			UIComponent item = iter.next();
			if (item instanceof PresentationObjectContainer)
			{
				String itemLabel = ((PresentationObjectContainer) item).getLabel();
				if (itemLabel != null){
					if (itemLabel.equals(label)){
						return (item);
					}
				}
				UIComponent theReturn = ((PresentationObjectContainer) item).getContainedLabeledObject(label);
				if (theReturn != null)
				{
					return (theReturn);
				}
			}
		}
		return (null);
	}

	/*
	 * public PresentationObject getContainedObject(String objectTreeID) { if
	 * (objectTreeID.indexOf(".") == -1) { return
	 * objectAt(Integer.parseInt(objectTreeID)); } else { String newString =
	 * objectTreeID.substring(objectTreeID.indexOf(".") +
	 * 1,objectTreeID.length()); String index =
	 * objectTreeID.substring(0,objectTreeID.indexOf("."));
	 *
	 * PresentationObject obj = objectAt(Integer.parseInt(index)); if (obj
	 * instanceof PresentationObjectContainer){ return
	 * ((PresentationObjectContainer)obj).getContainedObject(newString); } else {
	 * return obj; } }
	 */
	/*
	 * public void updateTreeIDs() { if (!isEmpty()) { String thisTreeID =
	 * this.getTreeID(); int numberOfObjects = numberOfObjects(); for(int index = 0;
	 * index < numberOfObjects; index++) { PresentationObject tempobj =
	 * objectAt(index); if (tempobj != null) { if (tempobj != this) { try { if
	 * (thisTreeID == null) { String treeID = Integer.toString(index);
	 * tempobj.setTreeID(treeID); } else { String treeID = thisTreeID + "." +
	 * index; tempobj.setTreeID(treeID); } } catch(Exception ex) {
	 * ExceptionWrapper exep = new ExceptionWrapper(ex,this); add(exep); } } } } }
	 */
	/*
	 * public void setTreeID(String ID) { super.setTreeID(ID); updateTreeIDs();
	 */
	public int numberOfObjects()
	{
		return getChildren().size();
	}
	protected UIComponent objectAt(int index)
	{
		return getChildren().get(index);
	}
	public int getIndex(PresentationObject ob)
	{
		return getChildren().indexOf(ob);
	}
	/**
	 * Insert element at specified index
	 */
	public void insertAt(PresentationObject modObject, int index)
	{
		try
		{
			if (modObject != null)
			{
				modObject.setParentObject(this);
				//        modObject.setLocation(this.getLocation());
				getChildren().add(index, modObject);
			}
		}
		catch (Exception ex)
		{
			//ExceptionWrapper exep = new ExceptionWrapper(ex,this);
		}
	}
	/**
	 * Replace element at specified index
	 */
	/*
	 * public void setAt(PresentationObject modObject, int index) { try { if
	 * (theObjects == null) { this.theObjects = new ArrayList(); } if
	 * (modObject != null) { <<<<<<< PresentationObjectContainer.java
	 * modObject.setParentObject(this); //
	 * modObject.setLocation(this.getLocation());
	 * theObjects.setElementAt(modObject,index); =======
	 * modObject.setParentObject(this);
	 * theObjects.setElementAt(modObject,index); >>>>>>> 1.13 } }
	 * catch(Exception ex) { ExceptionWrapper exep = new
	 * ExceptionWrapper(ex,this); }
	 */
	public void removeAll(Collection<UIComponent> c)
	{
		getChildren().removeAll(c);
	}
	/*public void _setIWContext(IWContext iwc)
	{
		setIWContext(iwc);
		//if (!isEmpty())
		//{
			for (int index = 0; index < numberOfObjects(); index++)
			{
				PresentationObject tempobj = (PresentationObject)objectAt(index);
				if (tempobj != null)
				{
					if (tempobj != this)
					{
						tempobj._setIWContext(iwc);
					}
				}
			}
		//}
	}*/
	/**
	 * This method is overrided from the PresentationObject superclass here
	 * to call clone(iwc,askForPermission) if askForPermission is true instead of plain clone() to handle children
	 */
	@Override
	public Object clonePermissionChecked(IWUserContext iwc, boolean askForPermission)
	{
		if (askForPermission || iwc != null)
		{
			if (iwc.getApplicationContext().getIWMainApplication().getAccessController().hasViewPermission(this,iwc))
			{
				return this.clone(iwc, askForPermission);
			}
			else
			{
				return NULL_CLONE_OBJECT;
			}
		}
		else
		{
			return this.clone();
		}
	}
	@Override
	public Object clone()
	{
		return this.clone(null, false);
	}
	/**
	 * This method can be overridden in subclasses to handle clone of the children inside this container
	 * @param iwc
	 * @param askForPermission
	 * @return
	 */
	public Object clone(IWUserContext iwc, boolean askForPermission)
	{
		PresentationObjectContainer obj = null;
		try
		{
			obj = (PresentationObjectContainer) super.clone();
			obj._locked = this._locked;
			//if(!(this instanceof Table)){
			//if (this.theObjects != null)
			//{
				//obj.setObjects((Vector)this.theObjects.clone());

				/**TL:
				 * Disabled cloning of the list, it shouldn't be necessary:
				 *
					ArrayList alChildren = (ArrayList)myChildren;
					List clonedChildren = (List)alChildren.clone();
					if(clonedChildren instanceof PresentationObjectList){
						PresentationObjectList pList = (PresentationObjectList)clonedChildren;
						pList.setParent(obj);
					}
					obj.setChildren(clonedChildren);
				*/
			cloneJSFChildrenAndFacets(obj,iwc,askForPermission);

				//}
			//}
		}
		catch (Exception ex)
		{
			//obj.theObjects = new ArrayList();
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	private void cloneJSFChildrenAndFacets(PresentationObject obj,IWUserContext iwc,boolean askForPermission){
		//Cloning the JSF Facets:
		cloneJSFChildren(obj,iwc,askForPermission);
		cloneJSFFacets(obj,iwc,askForPermission);
		//TODO: move the cloning of this to PresentationObject. Now it is inside PresentationObjectContainer

	}

	protected void cloneJSFChildren(PresentationObject obj,IWUserContext iwc,boolean askForPermission){
		//Cloning the JSF children:
		if(this.childrenList!=null){
			//First clone the children List instance itself:
			obj.childrenList=(List) ((PresentationObjectComponentList)this.childrenList).clone();
			((PresentationObjectComponentList)obj.childrenList).setComponent(obj);

			//Iterate over the children to clone each child:
			ListIterator<UIComponent> iter = obj.getChildren().listIterator();
			while (iter.hasNext())
			{
				int index = iter.nextIndex();
				UIComponent item = iter.next();
				//Object item = obj.theObjects.elementAt(index);
				if (item instanceof PresentationObject){
					PresentationObject newObject = (PresentationObject) ((PresentationObject) item).clonePermissionChecked(iwc, askForPermission);
					//newObject.setParentObject(obj);
					//newObject.setLocation(this.getLocation());
					obj.getChildren().set(index, newObject);
					//newObject.setParent(obj);
				}
				else {
					//create a copy from the IBXML
					try {
						BuilderService builderService = getBuilderService(IWMainApplication.getDefaultIWApplicationContext());
						UIComponent newUIObject = builderService.getCopyOfUIComponentFromIBXML(item);
						//insert the new item
						obj.getChildren().set(index, newUIObject);
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

	protected void cloneJSFFacets(PresentationObject obj,IWUserContext iwc,boolean askForPermission){
		//First clone the facet Map:
		if(this.facetMap!=null){
			obj.facetMap=(Map) ((PresentationObjectComponentFacetMap)this.facetMap).clone();
			((PresentationObjectComponentFacetMap)obj.facetMap).setComponent(obj);

			//Iterate over the children to clone each child:
			for (Iterator<String> iter = getFacets().keySet().iterator(); iter.hasNext();) {
				String key = iter.next();
				UIComponent component = getFacet(key);
				if(component instanceof PresentationObject){
					PresentationObject newObject = (PresentationObject)((PresentationObject)component).clonePermissionChecked(iwc,askForPermission);
					newObject.setParentObject(obj);
					newObject.setLocation(this.getLocation());
					obj.getFacets().put(key,newObject);
				}
			}
		}
	}


	public boolean remove(PresentationObject obj)
	{
		return getChildren().remove(obj);
	}
	/**
	 * index lies from 0,length-1
	 */
	public Object set(int index, PresentationObject o)
	{
		o.setParentObject(this);
		//    o.setLocation(this.getLocation());
		return this.getChildren().set(index, o);
	}
	/**
	 *
	 */
	public void lock()
	{
		this._locked = true;
	}
	/**
	 *
	 */
	public void unlock()
	{
		this._locked = false;
	}
	/**
	 *
	 */
	public boolean isLocked()
	{
		return (this._locked);
	}
	/**
	 *
	 */
	public void setLabel(String label)
	{
		this._label = label;
	}
	/**
	 *
	 */
	public String getLabel()
	{
		return (this._label);
	}

	@Override
	public void setLocation(IWLocation location, IWUserContext iwuc)
	{
		super.setLocation(location, iwuc);
			Iterator<UIComponent> iter = this.getFacetsAndChildren();
			while (iter.hasNext())
			{
				UIComponent item = iter.next();
				if (item instanceof PresentationObject)
				{
					((PresentationObject) item).setLocation(location, iwuc);
				}
				if (item instanceof StatefullPresentation)
				{
					IWPresentationState state = ((StatefullPresentation) item).getPresentationState(iwuc);
					if (state != null)
					{
						state.setLocation(location);
					}
				}
			}
	}

	/*
	 * Overrided methods from JSF's UIComponent:
	 */




	@Override
	public void addChild(UIComponent child){
		this.add((PresentationObject)child);
	}

	@Override
	public void addChild(int index,UIComponent child){
		this.add(index,(PresentationObject)child);
	}

	public void clearChildren(){
		this.empty();
	}

	public UIComponent getChild(int index){
		return getChildren().get(index);
	}

	public int getChildrenCount(){
		return this.getChildren().size();
	}

	public void removeChild(int index){
		this.getChildren().remove(index);
	}

	public void remove(UIComponent child){
		this.remove((PresentationObject)child);
	}

	@Override
	public void encodeBegin(FacesContext context)throws IOException{
		callMain(context);
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException{
		//super.encodeChildren(context);
		callPrint(context);
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	@Override
	public boolean isContainer() {
		return true;
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		/*try{
			super.restoreState(context, values[0]);
		}
		catch(ClassCastException cce){
			cce.printStackTrace();
		}*/
		super.restoreState(context, values[0]);
		//this.goneThroughMain = ((Boolean) values[1]).booleanValue();
		this._locked = ((Boolean) values[1]).booleanValue();
		this._label = (String) values[2];
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[3];
		values[0] = super.saveState(context);
		//values[1] = Boolean.valueOf(this.goneThroughMain);
		values[1] = Boolean.valueOf(this._locked);
		values[2] = this._label;
		return values;
	}

	public void setZIndex(int zIndex) {
		setStyleAttribute("z-index", String.valueOf(zIndex));
	}
}