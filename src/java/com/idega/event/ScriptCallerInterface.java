/**
 *
 */
package com.idega.event;

import com.idega.business.SpringBeanName;

/**An interface whose implementation should be able to send scripts outside
 * of DWR, by providing to it HTTP session ID and script
 * @author aleksandras10
 *
 */

@SpringBeanName(ScriptCallerInterface.BEAN_NAME)
public interface ScriptCallerInterface {
	
	public static final String BEAN_NAME = "scriptCallerInterface";
	
	public void executeScript(String httpSessionId, String script);
	
	/**Script should be set if calling this method.
	 * It sends script to execute in browser.
	 */
	public void run();

	public void setUri(String uri);

	public void setScript(String script);

	/**
	 * Sets HttpSession Id
	 * @param sessionId
	 */
	public void setSessionId(String sessionId);

}
