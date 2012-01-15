package com.idega.servlet.filter;

import java.io.IOException;

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
	
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		String requestUri = request.getRequestURI();
		if(requestUri != null && !requestUri.endsWith(SLASH)){
			boolean isResourceEnding = requestUri.matches(".*\\.[\\w]+");
			if(!isResourceEnding){
				String queryString = request.getQueryString();
				String redirectURI;
				if(queryString!=null){
					redirectURI = requestUri+"/?"+queryString;
				}
				else{
					redirectURI = requestUri + SLASH;
				}
				response.sendRedirect(redirectURI);
				return;
			} 
		}
		chain.doFilter(srequest,sresponse);
		
	}
}

