package com.idega.servlet.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.servlet.filter.BaseFilter;


public class IWUrlRewriteFilter extends BaseFilter {
	
	public static String SLASH = "/";
	
	private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(?i)^(.*);jsessionid=[\\w\\.\\-]+(.*)");
	private static final String JSESSIONID_REPLACEMENT = "$1$2";
	

	private static final Pattern RESOURCE_PATTERN = Pattern.compile(".*\\.[\\w]+");
	
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		/* Remove session id from request url */
		String requestUrl = request.getRequestURI();
		Matcher sessionIdMatcher = JSESSIONID_PATTERN.matcher(requestUrl);
		if (sessionIdMatcher.matches()) {
			requestUrl = sessionIdMatcher.replaceFirst(JSESSIONID_REPLACEMENT);
		}
		
		if(requestUrl != null && !requestUrl.endsWith(SLASH)){
			boolean isResourceEnding = RESOURCE_PATTERN.matcher(requestUrl).matches();
			if(!isResourceEnding){
				String queryString = request.getQueryString();
				String redirectURI;
				if(queryString!=null){
					redirectURI = requestUrl+"/?"+queryString;
				}
				else{
					redirectURI = requestUrl + SLASH;
				}
				response.sendRedirect(redirectURI);
				return;
			} 
		}
		chain.doFilter(srequest,sresponse);
		
	}
}

