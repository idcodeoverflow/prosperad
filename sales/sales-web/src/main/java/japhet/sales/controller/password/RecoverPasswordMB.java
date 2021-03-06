package japhet.sales.controller.password;

import static japhet.sales.mailing.MailingParameters.*;
import static japhet.sales.mailing.MailingTemplates.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import japhet.sales.controller.GenericMB;
import japhet.sales.except.InvalidAccountException;
import japhet.sales.internationalization.IInternationalizationService;
import japhet.sales.mailing.ContentTypes;
import japhet.sales.mailing.service.IMailingService;
import japhet.sales.model.impl.User;
import japhet.sales.service.IRandomPasswordService;
import japhet.sales.service.IUserService;

/**
 * 
 * @author David Israel Garcia Alcazar
 *
 */
@ManagedBean
@RequestScoped
public class RecoverPasswordMB extends GenericMB {

	/**
	 * Maven generated.
	 */
	private static final long serialVersionUID = 6742270684047428883L;

	@Inject
	private Logger logger;
	
	//EJB's
	@EJB
	private IInternationalizationService internationalizationService;
	
	@EJB
	private IUserService userService;
	
	@EJB
	private IMailingService mailingService;
	
	@EJB
	private IRandomPasswordService randomPasswordService;
	
	//View attributes
	private String userEmail;
	
	/**
	 * Initializes the fields required for this module.
	 */
	@PostConstruct
	private void init() {
		final String INFO_MSG = "Initializing RecoverPasswordMB...";
		try {
			logger.info(INFO_MSG);
		} catch(Exception e) {
			final String ERROR_MSG = "An error has occured while initializing RecoverPasswordMB.";
			logger.error(ERROR_MSG, e);
			showStartupMbExceptionMessage();
		}
	}
	
	/**
	 * Send the request to the Services Layer to generate
	 * a new password for the specified User.
	 */
	public void restorePassword() {
		final String INFO_MSG = String.format("Restoring the password for the User: %s...", this.userEmail);
		Map<String, Object> params = new HashMap<>();
		try {
			logger.info(INFO_MSG);
			//Set parameters
			params.put(USERNAME, this.userEmail);
			final User user = userService.getUserByUsername(params);
			//Verify if the specified email exists otherwise show a message
			if(user == null) {
				throw new InvalidAccountException("The specified account doesn't exist.");
			}
			//Set a new random password to the user
			final String password = randomPasswordService.generateRandomPassword((short)10);
			params.clear();
			//Set mailing parameters
			params.put(NAME, user.getName());
			params.put(PASSWORD, password);
			user.setPassw(password);
			userService.updateUser(user);
			//Send email notification
			mailingService.sendMessage(RECOVER_PASSWORD.getSubject(), 
					user.getEmail(), 
					RECOVER_PASSWORD, 
					ContentTypes.TEXT_HTML, 
					params);
			showInfoMessage(internationalizationService.
					getI18NMessage(CURRENT_LOCALE, getRECOVER_PASSWORD_SUCCESSFUL()), "");
		} catch(InvalidAccountException e) {
			final String ERROR_MSG = String.format("The specified User: %s doesn't exist.", this.userEmail);
			logger.error(ERROR_MSG, e);
			showInvalidEmailMessage();
		} catch(Exception e) {
			final String ERROR_MSG = String
					.format("An error has occurred while restoring the password for the User: %s"
							, this.userEmail);
			logger.error(ERROR_MSG, e);
			showGeneralExceptionMessage();
		}
	}
	
	/**
	 * Shows an error message to notify that the email 
	 * entered is not valid or doesn't exists.
	 * @throws Exception
	 */
	public void showInvalidEmailMessage() {
		try {
			final String ERROR_MSG = internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getINVALID_EMAIL());
			showErrorMessage(ERROR_MSG, "");
		} catch(Exception e) {
			final String ERROR_MSG = "An error has occurred while showInvalidEmailMessage() was executed.";
			logger.error(ERROR_MSG, e);
		}
	}

	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}
