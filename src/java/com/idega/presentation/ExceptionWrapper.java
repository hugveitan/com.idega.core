/*
 * $Id: ExceptionWrapper.java,v 1.8 2003/12/11 15:07:10 tryggvil Exp $
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
  private String _thrower;

  public ExceptionWrapper() {
  	super();
  }

	public ExceptionWrapper(Exception ex) {
		super();
		setException(ex);
	}

  public ExceptionWrapper(Exception ex, PresentationObject thrower) {
    super();
    setException(ex);
    _thrower = thrower.getClassName();
    //add(thrower);
  }

	protected void initialize(IWContext iwc) {
		super.initialize(iwc);
		IWResourceBundle iwrb = getBundle().getResourceBundle(iwc);
		
		String errorMessage = iwrb.getLocalizedString("error.exception_occurred","The following exception occurred");
		Text error = new Text(errorMessage);
		if (_exception != null) {
			PreformattedText stackTrace = new PreformattedText(getStackTrace(_exception));
			if (_errorStyle != null)
				stackTrace.setStyleAttribute(_errorStyle);
			add(stackTrace);
			String exceptionFullClassName = _exception.getClass().getName();
			String exceptionShortClassName = exceptionFullClassName.substring(exceptionFullClassName.lastIndexOf(".") + 1);
			String exceptionMessage = _exception.getMessage();
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
			if (i > 0)
				trace.append("\n        ");
			trace.append(elements[i].toString());
		}
		return trace.toString();
	}

  public void setException(Exception exception) {
    this._exception = exception;
  }

	public void setErrorStyle(String style) {
		_errorStyle = style;
	}
}