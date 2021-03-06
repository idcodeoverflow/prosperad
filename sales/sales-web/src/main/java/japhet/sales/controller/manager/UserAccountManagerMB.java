package japhet.sales.controller.manager;

import static japhet.sales.mailing.MailingParameters.*;
import static japhet.sales.mailing.MailingTemplates.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import japhet.sales.catalogs.Roles;
import japhet.sales.catalogs.Statuses;
import japhet.sales.controller.GenericMB;
import japhet.sales.dto.UserBudget;
import japhet.sales.except.BusinessServiceException;
import japhet.sales.except.InvalidBuyProofException;
import japhet.sales.internationalization.IInternationalizationService;
import japhet.sales.mailing.ContentTypes;
import japhet.sales.mailing.MailingParameters;
import japhet.sales.mailing.service.IMailingService;
import japhet.sales.model.impl.BuyProof;
import japhet.sales.model.impl.PaybackProtest;
import japhet.sales.model.impl.PaymentRequest;
import japhet.sales.model.impl.Status;
import japhet.sales.model.impl.User;
import japhet.sales.model.impl.UserProductHistorial;
import japhet.sales.service.IBuyProofService;
import japhet.sales.service.IPaybackProtestService;
import japhet.sales.service.IPaymentRequestService;
import japhet.sales.service.IUserProductHistorialService;
import japhet.sales.service.IUserService;
import japhet.sales.service.IUtilService;

@ManagedBean
@ViewScoped
public class UserAccountManagerMB extends GenericMB {

	/**
	 * Maven generated
	 */
	private static final long serialVersionUID = -5288053365897420779L;
	
	@Inject
	private Logger logger;
	
	//EJB's
	@EJB
	private IUserService userService;
	
	@EJB
	private IInternationalizationService internationalizationService;
	
	@EJB
	private IPaymentRequestService paymentRequestService;
	
	@EJB
	private IBuyProofService buyProofService;
	
	@EJB
	private IUserProductHistorialService userProductHistorialService;
	
	@EJB
	private IUtilService utilService;
	
	@EJB
	private IPaybackProtestService paybackProtestService;
	
	@EJB
	private IMailingService mailingService;
	
	//Validation properties
	private final int MAX_MEDIA_SIZE = 1000000;
	private final double MIN_PAYMENT_REQUEST = 200.0;
	
	//View properties
	private byte[] buyProofBytes;
	private PaymentRequest paymentRequest;
	private BuyProof buyProof;
	private List<PaymentRequest> paymentRequestHistory;
	private List<BuyProof> buyProofsHistory;
	private List<UserProductHistorial> userProductHistorials;
	private List<PaybackProtest> paybackProtests;
	private double onwaitAmount;
	private double readyAmount;
	
	//Query parameters
	private Map<String, Object> params;
	private User user;
	
	/**
	 * Initializes the User Account Manager.
	 */
	@PostConstruct
	private void init() {
		try {
			logger.info("Initializing user account manager...");
			//If the logged user hasn't a simple user role exit of the process
			if(getLoggedUser().getRole().getRoleId() != Roles.USER.getId()) {
				return;
			}
			//Initialize session properties
			params = new HashMap<>();
			user = getLoggedUser();
			params.put(USER_ID, user.getUserId());
			//Initialize elements
			initializeBuyProof();
			initializePaymentRequest();
			//Update elements
			updateBuyProofsListHistory();
			updateUserProductHistorial();
			updatePaymentRequests();
			updateUserBudget();
			updatePaybackProtests();
		} catch (Exception e) {
			logger.error("Error while initializing user account manager.", e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getSTARTUP_MB_ERROR()), "");
		}
	}
	
	/**
	 * Handles the files upload for the buy proofs.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		try {
			Map<String, Object> params = new HashMap<>();
			buyProofBytes = utilService.getBiteArrayFromStream(
					event.getFile().getInputstream());
			showInfoMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getFILE_READY()), "");
			logger.info("Uploading buy proof file...");
			//Persist into the DB
			buyProof.setTicketImage(buyProofBytes);
			buyProof.setFileName(event.getFile().getFileName());
			buyProof.setContentType(event.getFile().getContentType());
			//Validate and insert
			userProductHistorialService.verifyTotalAmounts(buyProof);
			buyProofService.insertBuyProof(buyProof);
			//Update elements
			updateBuyProofsListHistory();
			updateUserBudget();
			initializeBuyProof();
			//Send notification email
			int latestBProofIndex = this.buyProofsHistory.size() - 1;
			BuyProof latestBuyProof = this.buyProofsHistory.get(latestBProofIndex);
			params.put(NAME, user.getName());
			params.put(BUYPROOF_ID, latestBuyProof.getBuyProofId());
			mailingService.sendMessage(BUYPROOF_UPLOADED.getSubject(), 
					user.getEmail(), 
					BUYPROOF_UPLOADED, 
					ContentTypes.TEXT_HTML, 
					params);
		} catch (InvalidBuyProofException e) {
			logger.error("The buy proof 'Total Amount' doesn't match the finger print 'Total'.", e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getBUY_PROOF_INVALID()), 
						event.getFile().getFileName());
		} catch (Exception e) {
			logger.error("There was an error uploading the buy proof file to the server.", e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getFILE_UPLOAD_ERROR()), 
					event.getFile().getFileName());
		}
	}
	
	/**
	 * Generates a new payment request.
	 */
	public void requestPaymentRequest() {
		try {
			logger.info("Generating a payment request...");
			Map<String, Object> params = new HashMap<>();
			List<BuyProof> buyProofsToUpdate = userProductHistorialService.obtainBuyProofsReadyToPay(user);
			this.updateUserBudget();
			long paymentRequestId = paymentRequest.getNewSequenceValue();
			double paymentRequestAmount = this.readyAmount;
			paymentRequest.setPaymentRequestId(paymentRequestId);
			paymentRequest.setAmount(paymentRequestAmount);
			//Populate parameters
			params.put(PAYMENT_REQUEST, paymentRequest);
			params.put(BUY_PROOFS_TO_UPDATE, buyProofsToUpdate);
			params.put(PAYMENT_REQUEST_ID, paymentRequestId);
			paymentRequestService.generatePaymentRequest(params);
			//Update payment request data table and user budget
			updatePaymentRequests();
			updateBuyProofsListHistory();
			updateUserBudget();
			//Send notification email
			int latestPRequestIndex = this.paymentRequestHistory.size() - 1;
			PaymentRequest latestPaymentRequest = this.paymentRequestHistory.get(latestPRequestIndex);
			params.clear();
			params.put(NAME, user.getName());
			params.put(MailingParameters.PAYMENT_REQUEST_ID, latestPaymentRequest.getPaymentRequestId());
			params.put(CURP, user.getCurp());
			mailingService.sendMessage(PAYMENT_REQUEST_STARTED.getSubject(), 
					user.getEmail(), 
					PAYMENT_REQUEST_STARTED, 
					ContentTypes.TEXT_HTML, 
					params);
		} catch (BusinessServiceException e) {
			final String ERROR_MSG = "A service error has ocurred while generating a payment request.";
			logger.error(ERROR_MSG, e);
			showGeneralExceptionMessage();
		} catch (Exception e) {
			final String ERROR_MSG = "An error has ocurred while generating a payment request.";
			logger.error(ERROR_MSG, e);
			showGeneralExceptionMessage();
		}
	}
	
	/**
	 * Updates the content of the buy proofs list.
	 */
	public void updateBuyProofsListHistory() {
		try {
			logger.info("Updating the buy proofs list...");
			buyProofsHistory = buyProofService.getBuyProofsByUser(params);
		} catch (Exception e) {
			logger.error("Error while updating the buy proofs list.", e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getGENERAL_ERROR()), "");
		}
	}
	
	/**
	 * Updates the list of fingerprints from the completed buys.
	 */
	public void updateUserProductHistorial() {
		try {
			logger.info("Updating the user product historial list...");
			userProductHistorials = userProductHistorialService
					.getCompletedHistorialByUser(params);
		} catch (Exception e) {
			logger.error("Error while updating the user product historial list.", e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getGENERAL_ERROR()), "");
		}
	}
	
	/**
	 * Updates the list of payment requests made by the user.
	 */
	public void updatePaymentRequests() {
		try {
			logger.info("Updating the user product historial list...");
			paymentRequestHistory = paymentRequestService.
					getPaymentRequestsByUser(user.getUserId());
		} catch (Exception e) {
			logger.error("Error while updating the user product historial list.", e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getGENERAL_ERROR()), "");
		}
	}
	
	/**
	 * This method initializes the list with the PaybackProtests for this user.
	 * @throws Exception
	 */
	private void updatePaybackProtests() throws Exception {
		final Long P_USER_ID = ((this.user != null && this.user.getUserId() != null) ? this.user.getUserId() : -1L);
		final String INFO_MSG = String.format("Updating the PaybackProtests by User: %d...", P_USER_ID);
		try {
			logger.info(INFO_MSG);
			Map<String, Object> params = new HashMap<>();
			params.put(USER_ID, this.user.getUserId());
			this.paybackProtests = paybackProtestService.getPaybackProtestsByUser(params);
			if(this.paybackProtests == null) {
				this.paybackProtests = new ArrayList<>();
			}
		} catch (Exception e) {
			final String ERROR_MSG = String
					.format("An error has occurred while updating the PaybackProtests by User: %d.", P_USER_ID);
			logger.error(ERROR_MSG, e);
			throw new Exception(ERROR_MSG, e);
		}
	}
	
	/**
	 * Instantiates a new Buy Proof object.
	 */
	public void initializeBuyProof() {
		this.buyProof = new BuyProof();
		Status status = new Status();
		status.setStatusId(Statuses.VALIDATION_PENDING.getId());
		//Set object values
		this.buyProof.setUser(user);
		this.buyProof.setRegisteredOn(new Date());
		this.buyProof.setLastUpdate(new Date());
		this.buyProof.setPaybackApplied(false);
		this.buyProof.setStatus(status);
	}
	
	/**
	 * Instantiates a new Payment Request object.
	 */
	public void initializePaymentRequest() {
		this.paymentRequest = new PaymentRequest();
		Status status = new Status();
		status.setStatusId(Statuses.VALIDATION_PENDING.getId());
		this.paymentRequest.setUser(user);
		this.paymentRequest.setLastUpdate(new Date());
		this.paymentRequest.setRequestDate(new Date());
		this.paymentRequest.setStatus(status);
	}
	
	/**
	 * Downloads the buy proof files from the specified object.
	 * @param buyProof
	 * @return
	 */
	public StreamedContent downloadBuyProofObject(BuyProof buyProof) {
		logger.info("Downloading buy proof file...");
		StreamedContent streamedContent = null;
		try {
			InputStream inpStream = new ByteArrayInputStream(buyProof.getTicketImage());
			streamedContent = new DefaultStreamedContent(inpStream, buyProof.getContentType(), 
				buyProof.getFileName());
		} catch (Exception e) {
			logger.error("An error has ocurred while downloading the buy proof :" 
					+ buyProof.getBuyProofId(), e);
			showErrorMessage(internationalizationService
					.getI18NMessage(CURRENT_LOCALE, getGENERAL_ERROR()), "");
		}
		return streamedContent;
	}
	
	/**
	 * Updates the amount of money in each section of the user's budget.
	 */
	public void updateUserBudget() {
		try {
			UserBudget userBudget = userProductHistorialService.obtainReadyOnWaitPaybackAmounts(user);
			this.readyAmount = userBudget.getAmountReadyToPay();
			this.onwaitAmount = userBudget.getAmountOnHold();
		} catch (BusinessServiceException e) {
			showGeneralExceptionMessage();
		}
	}
	
	/**
	 * Validates if a payment request has a resolution date.
	 * @param paymentRequest
	 * @return
	 */
	public boolean pymntReqResolutionDateExist(PaymentRequest paymentRequest) {
		return paymentRequest != null && paymentRequest.getResolutionDate() != null; 
	}
	
	/**
	 * Validates if there are money available for a payment request.
	 * @return
	 */
	public boolean isBugetForDepositsAvailable() {
		return readyAmount > 0.0 && readyAmount >= MIN_PAYMENT_REQUEST;
	}

	public int getMAX_MEDIA_SIZE() {
		return MAX_MEDIA_SIZE;
	}

	public PaymentRequest getPaymentRequest() {
		return paymentRequest;
	}

	public void setPaymentRequest(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

	public List<PaymentRequest> getPaymentRequestHistory() {
		return paymentRequestHistory;
	}

	public void setPaymentRequestHistory(List<PaymentRequest> paymentRequestHistory) {
		this.paymentRequestHistory = paymentRequestHistory;
	}

	public List<PaybackProtest> getPaybackProtests() {
		return paybackProtests;
	}

	public void setPaybackProtests(List<PaybackProtest> paybackProtests) {
		this.paybackProtests = paybackProtests;
	}

	public BuyProof getBuyProof() {
		return buyProof;
	}

	public void setBuyProof(BuyProof buyProof) {
		this.buyProof = buyProof;
	}
	
	public List<BuyProof> getBuyProofsHistory() {
		return buyProofsHistory;
	}

	public List<UserProductHistorial> getUserProductHistorials() {
		return userProductHistorials;
	}
	
	public List<UserProductHistorial> getUpdatedUserProductHistorials() {
		updateUserProductHistorial();
		return userProductHistorials;
	}
	
	public double getOnwaitAmount() {
		return onwaitAmount;
	}

	public double getReadyAmount() {
		return readyAmount;
	}
}
