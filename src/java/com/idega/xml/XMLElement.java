/*
 * $Id: XMLElement.java,v 1.24 2006/04/09 12:13:14 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.Filter;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLElement implements Serializable{
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4076776738191516564L;
private Element _element = null;

  public XMLElement(String name) {
    this._element = new Element(name);
  }

  public XMLElement(String name, XMLNamespace namespace) {
    this._element = new Element(name, (Namespace)namespace.getNamespace());
  }

  /**
   * This object really only accepts a org.jdom.Element type but is declared Object becaluse of jdom dependency issues.
   * @param oElement an Element instance
   * @throws ClassCastException if object not of correct type
   */
  public XMLElement(Object oElement) {
  	Element element = (Element)oElement;
    this._element = element;
    if (this._element == null) {
			System.out.println("Creating XMLElement with element == null");
		}
  }
  /**
   * This object really returns a org.jdom.Element type but is declared Object becaluse of jdom dependency issues.
   * @return the set Element instance
   */
  public Object getElement() {
    return(this._element);
  }
  /**
   * This object really only accepts a org.jdom.Element type but is declared Object becaluse of jdom dependency issues.
   * @param oElement an Element instance
   * @throws ClassCastException if object not of correct type
   */
  public void setElement(Object oElement) {
  	Element element = (Element)oElement;
    this._element = element;
  }

  public void setAttribute(String name, String value) {
    if (this._element != null) {
			this._element.setAttribute(name,value);
		}
  }

  public XMLElement addContent(XMLElement element) {
    if (this._element != null) {
      Element el = (Element)element.getElement();
      if (el != null) {
      		this._element.addContent(el.detach());
      }
    }
    return this;
  }

  public XMLElement getChild(String name) {
    if (this._element != null) {
      Element el = this._element.getChild(name);
      if (el != null) {
				return(new XMLElement(el));
			}
    }

    return(null);
  }

  public XMLElement getChild(String name, XMLNamespace namespace) {
    if (this._element != null) {
      Element el = this._element.getChild(name,(Namespace)namespace.getNamespace());
      if (el != null) {
				return(new XMLElement(el));
			}
    }

    return(null);
  }

  public boolean hasChildren() {
    if (this._element != null) {
			return(! this._element.getChildren().isEmpty());
		}

    return(false);
  }

  public List getChildren() {
    if (this._element != null) {
      List li = this._element.getChildren();
      ArrayList res = new ArrayList();
      Iterator it = li.iterator();

      while (it.hasNext()) {
        Element el = (Element)it.next();
        if (el != null) {
          XMLElement xmlel = new XMLElement(el);
          res.add(xmlel);
        }
      }

      return(res);
    }

    return(null);
  }

  public List getAttributes() {
    if (this._element != null) {
      List li = this._element.getAttributes();
      ArrayList res = new ArrayList();
      Iterator it = li.iterator();

      while (it.hasNext()) {
        Attribute at = (Attribute)it.next();
        XMLAttribute xmlat = new XMLAttribute(at);
        res.add(xmlat);
      }

      return(res);
    }

    return(null);
  }

  public String getAttributeValue(String name) {
  	XMLAttribute attribute = getAttribute(name);
  	if (attribute == null) {
  		return null;
  	}
  	return attribute.getValue();
  }

  public XMLAttribute getAttribute(String name) {
    if (this._element != null) {
      Attribute at = this._element.getAttribute(name);
      if (at != null) {
				return(new XMLAttribute(at));
			}
			else {
				return(null);
			}
    }

    return(null);
  }

  public String getName() {
    if (this._element != null) {
			return(this._element.getName());
		}

    return(null);
  }

  public String getTextTrim() {
    if (this._element != null) {
			return(this._element.getTextTrim());
		}

    return(null);
  }

  public String getTextTrim(String name) {
  	XMLElement element  = this.getChild(name);
  	return (element == null) ? null : element.getTextTrim();
  }

  public String getText(String name) {
  	XMLElement element = this.getChild(name);
  	return (element == null) ? null : element.getText();
  }

  public String getText() {
    if (this._element != null) {
			return(this._element.getText());
		}

    return(null);
  }

  public List getChildrenRecursive(final String name) {
  	if (this._element != null) {
  		Filter filter = new Filter() {
			@Override
			public boolean matches(Object object) {
				if (object instanceof Element) {
					String elementName = ((Element) object).getName();
					return name.equals(elementName);
				}
				return false;
			}
  		};
  		List list = new ArrayList();
  		Iterator iterator = this._element.getDescendants(filter);
  		while (iterator.hasNext()) {
  			Element el = (Element) iterator.next();
  			XMLElement element = new XMLElement(el);
  			list.add(element);
  		}
  		return list;
  	}
  	return null;
  }


  public List getChildren(String name) {
    if (this._element != null) {
      List li = this._element.getChildren(name);
      ArrayList res = new ArrayList();
      Iterator it = li.iterator();

      while (it.hasNext()) {
        Element el = (Element)it.next();
        XMLElement xmlel = new XMLElement(el);
        res.add(xmlel);
      }

      return(res);
    }

    return(null);
  }

  public List <XMLElement> getChildren(String name, XMLNamespace namespace ) {
	    if (this._element != null) {
	      List li = this._element.getChildren(name, (Namespace)namespace.getNamespace());
	      ArrayList res = new ArrayList();
	      Iterator it = li.iterator();

	      while (it.hasNext()) {
	        Element el = (Element)it.next();
	        XMLElement xmlel = new XMLElement(el);
	        res.add(xmlel);
	      }

	      return(res);
	    }

	    return(null);
  }

  public boolean removeContent(XMLElement element) {
    if (this._element != null) {
      Element el = (Element)element.getElement();
      if (el != null) {
				return(this._element.removeContent(el));
			}
    }

    return(false);
  }

  public XMLElement setText(String text) {
    if (this._element != null) {
			this._element.setText(text);
		}

    return this;
  }

  public XMLElement addContent(String text) {
    if (this._element != null) {
			this._element.addContent(text);
		}

    return this;
  }

  public XMLElement addContent(XMLCDATA data) {
  	if (this._element != null) {
			this._element.addContent(data.getContentData());
		}

  	return this;
  }

  /** sets the content of the first child or creates a child if no child exists
   * @author thomas
   */
  public XMLElement setContent(String name, String value) {
  	XMLElement child = getChild(name);
  	if (child == null) {
  		child = new XMLElement(name);
  	}
  	// do not use addContent!
  	child.setText(value);
  	return this;
  }

  /** adds a child with the specified value
   * @author Thomas
   */
  public XMLElement addContent(String name, String value) {
  	XMLElement element = new XMLElement(name);
  	element.addContent(value);
  	return addContent(element);
  }

  /**
   * A method that returns the first instance of CDATA that exists in this Element.
   * Return null if none is found. Should rather use getContent and check for
   * all CDATA content.
   *
   * @return The first CDATA instance in the content for this Element, null otherwise.
   */
  public XMLCDATA getXMLCDATAContent() {
  	if (this._element == null) {
			return null;
		}

		List li = this._element.getContent();
		Iterator it = li.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof CDATA) {
				return new XMLCDATA((CDATA)obj);
			}
		}

		return null;
  }

  public List getContent() {
  	if (this._element == null) {
			return null;
		}

		List ret = new ArrayList();
		List li = this._element.getContent();
		Iterator it = li.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Element) {
				XMLElement el = new XMLElement(obj);
				ret.add(el);
			}
			else if (obj instanceof CDATA) {
				XMLCDATA data = new XMLCDATA((CDATA)obj);
				ret.add(data);
			}
			else if (obj instanceof Text) {
				String text = ((Text)obj).getText();
				ret.add(text);
			}
		}

		return ret;
  }

  public XMLElement setAttribute(XMLAttribute attribute) {
    if (this._element != null) {
      Attribute at = attribute.getAttribute();
      if (at != null) {
				this._element.setAttribute(at);
			}
    }

    return this;
  }

  public boolean removeAttribute(String name) {
    if (this._element != null) {
			return(this._element.removeAttribute(name));
		}

    return(false);
  }

  public boolean removeChild(String name) {
    if (this._element != null) {
			return(this._element.removeChild(name));
		}

    return(false);
  }

  public boolean removeChildren() {
    if (this._element != null) {
      return(this._element.removeContent()) != null;
    }
    return(false);
  }

  public boolean removeParent() {
	if (this._element != null) {
		Element parent = this._element.getParentElement();
		if(parent!=null){
			return parent.removeChild(this._element.getName());
		}
		return true;
	}
	return(false);
  }

  public XMLElement setChildren(List children) {
    if (this._element != null) {
      if (children != null) {
        Iterator it = children.iterator();
        ArrayList res = new ArrayList();
        while (it.hasNext()) {
          XMLElement xmlel = (XMLElement)it.next();
          Element el = (Element)xmlel.getElement();
          if (el != null) {
						res.add(el);
					}
        }

        this._element.setContent(res);
      }
    }

    return this;
  }

  @Override
public synchronized Object clone() {
    if (this._element == null) {
			return(null);
		}

    Element el = (Element)this._element.clone();
    XMLElement element = new XMLElement(el);
    return element;
  }

  /**
   * Returns parent, or null if this element is currently not attached to another element.
   * @author Thomas
   */
  public XMLElement getParent() {
  	if (this._element == null) {
  		return null;
  	}
  	Element parent = this._element.getParentElement();
  	return (parent == null) ? null : new XMLElement(parent);
  }

  /** Returns an iterator of all children (recursive) of this element.
   * 	 The order of the returned elements corresponds to the result of a breadth first search.
   * @author Thomas
   */
  public Iterator allChildrenBreadthFirstIterator() {
  	return new Iterator() {

  		private Iterator iterator = null;

  		@Override
		public Object next() {
  			checkInitialization();
  			return this.iterator.next();
  		}

  		@Override
		public boolean hasNext() {
  			checkInitialization();
  			return this.iterator.hasNext();
  		}

  		@Override
		public void remove() {
  			checkInitialization();
  			this.iterator.remove();
  		}

			private void checkInitialization() {
  			 if (this.iterator == null) {
  			 	List allChildren = new ArrayList();
  			 	collectChildrenBreadthFirstMethod(XMLElement.this, allChildren);
  			 	this.iterator = allChildren.iterator();
  			 }
  		}


			private void collectChildrenBreadthFirstMethod(XMLElement element, List allChildren) {
					// breadth first search
  				allChildren.add(element);
  				List localChildren = element.getChildren();
  				Iterator iterator = localChildren.iterator();
  				while (iterator.hasNext()) {
  					XMLElement elementItem = (XMLElement) iterator.next();
  					collectChildrenBreadthFirstMethod(elementItem, allChildren);
  				}
  			}
  	};
  }

  /**do not remove that method even if it is not used at the moment - it's so hard to find that method.
   * Use this method to get really rid of the parent of an element.
   * @author Thomas
   */
  public XMLElement detach()	{
  	if (this._element != null)	{
  		return new XMLElement(this._element.detach());
  	}
  	return null;
  }

  public void setName(String newName) {
  	this._element.setName(newName);
  }

  public void addNamespaceDeclaration(XMLNamespace namespace){
  	this._element.addNamespaceDeclaration((Namespace)namespace.getNamespace());
  }

  /**
   * <p>
   * Gets the textual contents of this element without xml tags.
   * This is the same as getText() but traverses down child elements.
   * </p>
   * @return
   */
  public String getValue(){
	  return this._element.getValue();
  }

  /**
   * <p>
   * Gets the contents (child elements and text) of this element as a String
   * </p>
   * @return
   */
  public String getContentAsString(){
	  XMLOutput output = new XMLOutput();
	  String xmlString = output.outputString(this);
	  String tagName = getName();
	  StringBuffer ret= new StringBuffer();
	  //These patterns are for the begin and end tags of this element
	  Pattern beginPattern = Pattern.compile("(<"+tagName+"[^>])([^>]+>\r\n)",Pattern.CASE_INSENSITIVE);
	  Pattern endPattern = Pattern.compile("(\r\n</"+tagName+">)",Pattern.CASE_INSENSITIVE);
	  Matcher matcher;
	  matcher = beginPattern.matcher(xmlString);
	  if(matcher.find()){
		  //first check the pattern with a newline end:
	  }
	  else{
		  //if a newline is not found make a pattern without newline:
		  beginPattern = Pattern.compile("(<"+tagName+"[^>])([^>]+>)",Pattern.CASE_INSENSITIVE);
		  matcher = beginPattern.matcher(xmlString);
		  matcher.find();
	  }
	  matcher.appendReplacement(ret,"");
	  matcher.appendTail(ret);
	  xmlString = ret.toString();
	  ret = new StringBuffer();


	  matcher = endPattern.matcher(xmlString);
	  if(matcher.find()){
		  //first check the pattern with a newline end:
	  }
	  else{
		  //if a newline is not found make a pattern without newline:
		  endPattern = Pattern.compile("(</"+tagName+">)",Pattern.CASE_INSENSITIVE);
		  matcher = endPattern.matcher(xmlString);
		  matcher.find();
	  }
	  matcher.appendReplacement(ret,"");
	  matcher.appendTail(ret);
	  xmlString = ret.toString();
	  return ret.toString();
  }
}
