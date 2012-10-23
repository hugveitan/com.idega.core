/*
 * $Id: ICObjectDAOImpl.java 1.1 Sep 21, 2009 laddi Exp $
 * Created on Sep 21, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.component.dao.ICObjectDAO;
import com.idega.core.component.data.bean.ICObject;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;

@Scope("singleton")
@Repository("icObjectDAO")
@Transactional(readOnly = true)
public class ICObjectDAOImpl extends GenericDaoImpl implements ICObjectDAO {

	public ICObject findObject(Integer objectID) {
		return find(ICObject.class, objectID);
	}
	
	public ICObject findByClass(Class objectClass) {
		Param param = new Param("className", objectClass.getName());
		
		return getSingleResult("object.findAllByClass", ICObject.class, param);
	}
	
	public ICObject findByClassName(String objectClassName) {
		Param param = new Param("className", objectClassName);
		
		return getSingleResult("object.findAllByClass", ICObject.class, param);
	}
}