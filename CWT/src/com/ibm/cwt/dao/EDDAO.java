package com.ibm.cwt.dao;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

public class EDDAO {
	private static final Logger LOGGER = Logger.getLogger(EDDAO.class.getName());
	
	private SearchControls constraints;
	private InitialLdapContext ctx;
	private Hashtable<String, String> env;

	public EDDAO() {
		LOGGER.finest("Initializing LDAP DAO");
		env = new Hashtable<String, String>(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://bluepages.ibm.com");
		env.put(Context.SECURITY_AUTHENTICATION, "none");
		// env.put(Context.SECURITY_PROTOCOL, "ssl");
		// env.put("java.naming.ldap.factory.socket",
		// "javax.net.ssl.SSLSocketFactory");
		LOGGER.finest(env.toString());
		constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(new String[]{"mail","managerSerialNumber","managerCountryCode"});
	}

	
	public Map<String, String> searchManagerEmails(Set<String> employees) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> managerEmails = new HashMap<String, String>();
		
		try {
			ctx = new InitialLdapContext(env, null);
			for(String employeeUID : employees) {
				LOGGER.finer("Searching manager for " + employeeUID);
				try {
					NamingEnumeration<?> employeeResultEnum = ctx.search("ou=bluepages,o=ibm.com",
							"(&(objectclass=ibmperson)(uid="+employeeUID+"))", constraints);
					if(employeeResultEnum.hasMore()) {
						SearchResult result = (SearchResult) employeeResultEnum.next();
						String managerUID = result.getAttributes().get("managerSerialNumber").get().toString()+result.getAttributes().get("managerCountryCode").get().toString();
						employeeResultEnum.close();
						String managerEmail;
						if(managerEmails.containsKey(managerUID)){
							managerEmail = managerEmails.get(managerUID);
							map.put(employeeUID, managerEmail);
						} else {
							LOGGER.finer("Getting email for manager "+managerUID);
							NamingEnumeration<?> managerResultEnum = null;
							try {
								managerResultEnum = ctx.search("ou=bluepages,o=ibm.com",
										"(&(objectclass=ibmperson)(uid="+managerUID+"))", constraints);
								if(managerResultEnum.hasMore()){
									managerEmail = ((SearchResult)managerResultEnum.next()).getAttributes().get("mail").get().toString();
									managerEmails.put(managerUID, managerEmail);
									map.put(employeeUID, managerEmail);
								} else {
									LOGGER.warning("UID "+employeeUID+" doesn't have a manager");
									map.put(employeeUID, null);
								}
							} catch(NamingException e) {
								LOGGER.severe("Error when searching manager");
								e.printStackTrace();
							} finally {
								if(managerResultEnum!=null){
									managerResultEnum.close();
								}
							}
						}
												
					} else {
						LOGGER.warning("UID "+ employeeUID + " not found in ED");
					}
				} catch(NamingException e){
					LOGGER.severe("Error when searching employee in ED");
					e.printStackTrace();
				}
				
			}
			LOGGER.fine(map.toString());
			
		} catch (NamingException e) {
			LOGGER.severe("Error when connecting to ED");
			e.printStackTrace();
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return map;

	}

}
