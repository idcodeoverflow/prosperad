package japhet.sales.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import japhet.sales.data.GenericDAO;
import japhet.sales.data.QueryNames;
import japhet.sales.except.DataLayerException;
import japhet.sales.model.impl.User;

import org.apache.log4j.Logger;

@Stateless
public class UserDAO extends GenericDAO<User, Long> {

	@Inject
	private Logger logger;
	
	public UserDAO() {
		super(User.class, Long.class);
	}	
	
	public boolean doesUserExists(User user) 
			throws DataLayerException {
		boolean userExists = false;
		try {
			logger.info("Validating credentials...");
			List<User> users = new ArrayList<>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("username", user.getUsername());
			params.put("passw", user.getPassw());
			users = executeQuery(QueryNames.EXISTS_USER, params);
			userExists = users != null && users.size() > 0;
		} catch (Exception e) {
			final String errorMsg = "Exception occurred searching the user credentials into the DB.";
			logger.fatal(errorMsg, e);
			throw new DataLayerException(errorMsg, e);
		}
		return userExists;
	}
}
