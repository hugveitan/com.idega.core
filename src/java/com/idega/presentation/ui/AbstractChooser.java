/*
 * $Id: AbstractChooser.java,v 1.48 2009/02/07 14:33:35 valdas Exp $
 * Copyright (C) 2001 Idega hf. All Rights Reserved. This software is the
 * proprietary information of Idega hf. Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.net.URLEncoder;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.util.AbstractChooserBlock;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>,
 *         <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public abstract class AbstractChooser extends PresentationObjectContainer {
	static final String CHOOSER_SELECTION_PARAMETER = "iw_ch_p";
	static final String DISPLAYSTRING_PARAMETER = "iw_ch_d";
	static final String VALUE_PARAMETER = "iw_ch_v";
	static final String DISPLAYSTRING_PARAMETER_NAME = "iw_ch_d_n";
	static final String VALUE_PARAMETER_NAME = "iw_ch_v_n";
	static final String FORM_ID_PARAMETER = "iw_ch_ch_p";
	static final String SCRIPT_SUFFIX_PARAMETER = "iw_ch_s";
	public static final String FILTER_PARAMETER = "iw_filter";

	public String chooserParameter = VALUE_PARAMETER;
	public String displayInputName = DISPLAYSTRING_PARAMETER;
	private boolean _addForm = true;
	private boolean _addTextInput = true;
	private Form _form = null;
	private Image _buttonImage = null;
	protected String _style = IWConstants.BUILDER_FONT_STYLE_INTERFACE;
	protected String _stringValue;
	protected String _stringDisplay;
	private String _attributeValue;
	private String _attributeName;
	private String filter = "";
	private Link link = null;
	protected boolean disabled = true;
	private IWBundle _bundle;
	private IWResourceBundle _iwrb;
	private int _inputLength = -1;
	
	private String styleClassName;
	private boolean isStyleClassSet = false;
	private boolean usePublicWindowOpener = false;
	
	private boolean addSaveButton = true;
	private boolean useOldLogic = true;
	private boolean needsReload = true;
	
	private String hiddenInputAttribute = null;
	private String instanceId = null;
	private String method = null;
	private String actionAfterPropertySaved = null;

	/**
	 * @param aDisabled -
	 *          the new value for disabled
	 */
	public void setDisabled(boolean aDisabled) {
		this.disabled = aDisabled;
	}

	public void setInputLength(int length) {
		this._inputLength = length;
	}
	
	/**
	 *
	 */
	public AbstractChooser() {
		this(true);
	}
	
	public AbstractChooser(boolean useOldLogic) {
		this.useOldLogic = useOldLogic;
	}
	
	public AbstractChooser(boolean needsReload, boolean useOldLogic, String actionAfterPropertySaved) {
		this(useOldLogic);
		this.needsReload = needsReload;
		this.actionAfterPropertySaved = actionAfterPropertySaved;
	}
	
	/**
	 * @see UIComponentBase#setId(java.lang.String)
	 */
	@Override
	public void setId(String id){
	  super.setId(id);
	  setChooserParameter(id);
	}

	/**
	 *
	 */
	public abstract Class<?> getChooserWindowClass();

	/**
	 *
	 */
	public String getChooserParameter() {
		return (this.chooserParameter);
	}

	/**
	 *
	 */
	public void setChooserParameter(String parameterName) {
		this.chooserParameter = parameterName;
		if (this.displayInputName.equals(DISPLAYSTRING_PARAMETER)) {
			this.displayInputName = parameterName + "_displaystring";
		}
	}

	public void setChooserValue(String displayString, String valueString) {
		this._stringValue = valueString;
		this._stringDisplay = displayString;
	}

	protected void setChooserValue(String displayString, int valueInt) {
		setChooserValue(displayString, Integer.toString(valueInt));
	}

	public void setValue(Object objectValue) {
		setValue(objectValue.toString());
	}

	public void setValue(String stringValue) {
		setChooserValue(stringValue, stringValue);
	}

	public String getChooserValue() {
		return this._stringValue;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 *
	 */
	@Override
	public void setName(String name) {
		this.displayInputName = name;
		if (this.chooserParameter.equals(VALUE_PARAMETER)) {
			this.chooserParameter = name;// + "_chooser";
		}
	}

	/**
	 *
	 */
	@Override
	public String getName() {
		return (this.displayInputName);
	}

	/**
	 *
	 */
	@Override
	public void _main(IWContext iwc) throws Exception {
		super._main(iwc);
		
		this._bundle = getBundle(iwc);
		this._iwrb = getResourceBundle(iwc);
		if (this._addForm) {
			this._form = new Form();
			this._form.setWindowToOpen(getChooserWindowClass());
			add(this._form);
			this._form.add(getChooser(iwc, this._bundle));
		}
		else {
			add(getChooser(iwc, this._bundle));
			this._form = getParentForm();
		}

		if (!isUseOldLogic()) {	// This is needed only for new choosers
			if (isAddSaveButton()) {	//	Some choosers may not need save button
				add(getSaveButton());
			}
		}
	}
	
	public abstract String getChooserHelperVarName();
	
	private GenericButton getSaveButton() {
		GenericButton save = new GenericButton("save", _iwrb.getLocalizedString("save", "Save"));
		
		StringBuffer preAction = null;
		if (actionAfterPropertySaved != null) {
			preAction = new StringBuffer("var actionAfter = function() {").append(actionAfterPropertySaved).append("}; ");
		}
		
		StringBuffer action = new StringBuffer();
		if (preAction != null) {
			action.append(preAction.toString());
		}
		action.append(getChooserHelperVarName()).append(getChooserHelperVarName() != null ? "." : "").append("saveSelectedValues('").append(getResourceBundle().getLocalizedString("saving", "Saving...")).append("', ");
		if (getInstanceId() == null) {
			action.append("null, ");
		}
		else {
			action.append("'").append(getInstanceId()).append("', ");
		}
		if (getMethod() == null) {
			action.append("null");
		}
		else {
			action.append("'").append(getMethod()).append("'");
		}
		action.append(", ").append(needsReload).append(", '").append(getResourceBundle().getLocalizedString("reload", "Reloading..."));
		action.append("', ");
		if (preAction == null) {
			action.append("null");
		}
		else {
			action.append("actionAfter");
		}
		action.append(");");
		
		save.setOnClick(action.toString());
		return save;
	}

	/**
	 *
	 */
	public PresentationObject getChooser(IWContext iwc, IWBundle bundle) {
		if (useOldLogic) {	//	Old chooser
			Table table = new Table(2, 1);
			table.setCellpadding(0);
			table.setCellspacing(0);

			Parameter value = new Parameter(getChooserParameter(), "");
			if (this._stringValue != null) {
				value.setValue(this._stringValue);
			}
			table.add(value);

			PresentationObject object = getPresentationObject(iwc);

			table.add(new Parameter(VALUE_PARAMETER_NAME, value.getName()));
			//GenericButton button = new
			// GenericButton("chooserbutton",bundle.getResourceBundle(iwc).getLocalizedString(chooserText,"Choose"));
			if (this._addForm) {
				SubmitButton button = new SubmitButton(this._iwrb.getLocalizedString("choose", "Choose"));
				table.add(button, 2, 1);
				this._form.addParameter(CHOOSER_SELECTION_PARAMETER, getChooserParameter());
				this._form.addParameter(FORM_ID_PARAMETER, "window.opener.document.getElementById(\"" + this._form.getID() +"\").");
				this._form.addParameter(SCRIPT_SUFFIX_PARAMETER, "value");
				if (this.filter != null) {
					if (this.filter.length() > 0) {
						this._form.addParameter(FILTER_PARAMETER, this.filter);
					}
				}
				addParametersToForm(this._form);
			}
			else {
				getLink(this._iwrb);

				if (getUsePublicWindowOpener()) {
					this.link.setPublicWindowToOpen(getChooserWindowClass());
				} else {
					this.link.setWindowToOpen(getChooserWindowClass());
				}
				this.link.addParameter(CHOOSER_SELECTION_PARAMETER, getChooserParameter());

				this.link.addParameter(FORM_ID_PARAMETER, getParentFormID());

				//TODO Make the javascript work for other objects than form elements,
				// e.g. a Link
				/*
				 * if(object instanceof Layer){
				 * link.addParameter(SCRIPT_SUFFIX_PARAMETER,"title"); }
				 */
				this.link.addParameter(SCRIPT_SUFFIX_PARAMETER, "value");
				//}
				
				//this was object.getID() but the id could change if this object was kept in session but the form changed
				//by using getName() the reference is not lost, however we might need to add extra steps for handling more than one
				//chooser of the same type in the same form.
				this.link.addParameter(DISPLAYSTRING_PARAMETER_NAME, object.getName());
				this.link.addParameter(VALUE_PARAMETER_NAME, value.getName());
				if (this._attributeName != null && this._attributeValue != null) {
					this.link.addParameter(this._attributeName, this._attributeValue);
				}
				if (this.filter != null) {
					if (this.filter.length() > 0) {
						this.link.addParameter(FILTER_PARAMETER, this.filter);
					}
				}
				
				
				addParametersToLink(this.link);
				
				table.add(this.link, 2, 1);
			}

			table.add(object, 1, 1);
			table.add(new Parameter(DISPLAYSTRING_PARAMETER_NAME, "151324213"));
			return (table);
		}
		else {	//	New chooser
			Layer container = new Layer();
			
			String chooserObject = getChooserHelperVarName();
			if (chooserObject == null) {
				chooserObject = AbstractChooserBlock.GLOBAL_HELPER_NAME;
			}
			else {
				add(PresentationUtil.getJavaScriptAction(PresentationUtil.getJavaScriptLinesLoadedLazily(CoreUtil.getResourcesForChooser(iwc),
					new StringBuilder("if (!").append(chooserObject).append(") var ").append(chooserObject).append(" = new ChooserHelper();").toString())));
			}
			
			PresentationObject object = getPresentationObject(iwc);
			container.add(object);
			
			Image chooser = getChooser(bundle);
			chooser.setStyleClass("chooserStyle");
			
			chooser.setMarkupAttribute("choosername", chooserObject);
			
			//	OnClick action
			StringBuffer action = new StringBuffer("addChooserObject('").append(chooser.getId()).append("', '");
			action.append(getChooserWindowClass().getName());
			if (getHiddenInputAttribute() == null) {
				action.append("', null, '");
			}
			else {
				action.append("', '").append(getHiddenInputAttribute()).append("', '");
			}
			action.append(ICBuilderConstants.CHOOSER_VALUE_VIEWER_ID_ATTRIBUTE).append("', '");
			action.append(getResourceBundle().getLocalizedString("loading", "Loading...")).append("', ")
			.append(_stringValue == null ? "null" : new StringBuilder("'").append(_stringValue).append("'").toString()).append(", ")
			.append(_stringDisplay == null ? "null" : new StringBuilder("'").append(_stringDisplay).append("'").toString()).append(");");
			chooser.setOnClick(action.toString());
			
			container.add(chooser);
			
			return container;
		}
	}

	/**
	 * Override this method to add extra parameters to the chooser link
	 * @param form
	 */
	protected void addParametersToLink(Link link) {
	}

	/**
	 * Override this method to add extra parameters to the chooser form
	 * @param form
	 */
	protected void addParametersToForm(Form form) {
	}

	public String getParentFormID() {
		return getParentFormID(this);
	}

	public PresentationObject getPresentationObject(IWContext iwc) {
		if (this._addTextInput) {
			TextInput input = new TextInput(this.displayInputName+"_chooser");
			input.setDisabled(this.disabled);
			input.setId(input.getName());
			
			if (this._buttonImage != null) {
				this._buttonImage.setMarkupAttribute(ICBuilderConstants.CHOOSER_VALUE_VIEWER_ID_ATTRIBUTE, input.getId());
			}
			
			if (this._inputLength > 0) {
				input.setLength(this._inputLength);
			}

			if (this._style != null) {
				input.setMarkupAttribute("style", this._style);
			}
			if(this.isStyleClassSet) {
				input.setStyleClass(this.styleClassName);
			}

			if (this._stringDisplay != null) {
				input.setValue(this._stringDisplay);
			}

			input.setStyleClass(CoreConstants.BUILDER_PORPERTY_SETTER_STYLE_CLASS);
			return input;
		}
		else {
			HiddenInput input = new HiddenInput(this.displayInputName);
			if (this._stringDisplay != null) {
				input.setValue(this._stringDisplay);
			}
			return input;
		}
	}

	/*
	 *
	 */
	private String getParentFormID(PresentationObject obj) {
		String returnString = "";
		UIComponent parent = obj.getParent();
		if (parent != null) {
			if(parent instanceof PresentationObject){
				if (!(parent instanceof Form)) {
					returnString = getParentFormID((PresentationObject) parent);
				}
				else {
					returnString = ((PresentationObject) parent).getID();
				}
			}
		}

		return (returnString);
	}

	public void setInputStyle(String style) {
		this._style = style;
	}
	
	public void setStyleClassName(String styleClassName) {
		this.styleClassName = styleClassName;
		this.isStyleClassSet = true;
	}

	public void addForm(boolean addForm) {
		this._addForm = addForm;
	}

	public void setChooseButtonImage(Image buttonImage) {
		this._buttonImage = buttonImage;
	}

	public void setParameterValue(String attributeName, String attributeValue) {
		this._attributeName = attributeName;
		this._attributeValue = URLEncoder.encode(attributeValue);
	}

	public void addParameterToChooserLink(String param, String value) {
		getLink(null).addParameter(param, value);
	}
	
	private Image getChooser(IWBundle bundle) {
		if (this._buttonImage == null) {
			setChooseButtonImage(bundle.getImage("open.gif", "Choose"));
		}
		return this._buttonImage;
	}

	private Link getLink(IWResourceBundle iwrb) {
		if (this.link == null) {
			if (this._buttonImage == null) {
				this.link = new Link(iwrb.getLocalizedString("choose", "Choose"));
			}
			else {
				this._buttonImage.setHorizontalSpacing(3);
				this.link = new Link(this._buttonImage);
			}
		}

		return this.link;
	}

	public void addTextInput(boolean addTextInput) {
		this._addTextInput = addTextInput;
	}

	/**
	 * @return Returns the bundle.
	 */
	protected IWBundle getBundle() {
		return this._bundle;
	}
	/**
	 * @return Returns the resource bundle.
	 */
	protected IWResourceBundle getResourceBundle() {
		return this._iwrb;
	}
	
	protected boolean getUsePublicWindowOpener() {
		return this.usePublicWindowOpener;
	}

	public boolean isAddSaveButton() {
		return addSaveButton;
	}

	public void setAddSaveButton(boolean addSaveButton) {
		this.addSaveButton = addSaveButton;
	}
	
	public String getHiddenInputAttribute() {
		return hiddenInputAttribute;
	}

	public void setHiddenInputAttribute(String hiddenInputAttribute) {
		this.hiddenInputAttribute = hiddenInputAttribute;
	}

	public void setUseOldLogic(boolean useOldLogic) {
		this.useOldLogic = useOldLogic;
	}

	public boolean isUseOldLogic() {
		return useOldLogic;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
}
