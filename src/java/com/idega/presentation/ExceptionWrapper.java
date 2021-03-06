/*
 * $Id: ExceptionWrapper.java,v 1.11 2006/05/09 14:47:18 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.presentation;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.text.PreformattedText;
import com.idega.presentation.text.Text;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class ExceptionWrapper extends ExpandContainer {

  private Exception _exception;
  private String _errorStyle;

  public ExceptionWrapper() {
  	super();
	setStyleClass("errormessage");
  }

	public ExceptionWrapper(Exception ex) {
		this();
		setException(ex);
	}

  public ExceptionWrapper(Exception ex, PresentationObject thrower) {
    this();
    setException(ex);
  }

	protected void initialize(IWContext iwc) {
		super.initialize(iwc);
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		String errorMessage = iwrb.getLocalizedString("error.exception_occurred","The following exception occurred");
		Text error = new Text(errorMessage);
		if (this._exception != null) {
			PreformattedText stackTrace = new PreformattedText(getStackTrace(this._exception));
			if (this._errorStyle != null) {
				stackTrace.setStyleAttribute(this._errorStyle);
			}
			add(stackTrace);
			String exceptionFullClassName = this._exception.getClass().getName();
			String exceptionShortClassName = exceptionFullClassName.substring(exceptionFullClassName.lastIndexOf(".") + 1);
			String exceptionMessage = this._exception.getMessage();
			if(exceptionMessage==null){
				error.addToText(": "+exceptionShortClassName);
			}
			else{
				error.addToText(": "+exceptionMessage+" ("+exceptionShortClassName+")");
			}
		}
		setHeader(error);
	}
	
	private String getStackTrace(Exception exception) {
		exception.printStackTrace(System.err);
		
		StackTraceElement[] elements = exception.getStackTrace();
		StringBuffer trace = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				trace.append("\n        ");
			}
			trace.append(elements[i].toString());
		}
		return trace.toString();
	}

  public void setException(Exception exception) {
    this._exception = exception;
  }

	public void setErrorStyle(String style) {
		this._errorStyle = style;
	}
}