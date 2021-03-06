package japhet.sales.data.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import japhet.sales.data.GenericDAO;
import japhet.sales.except.DataLayerException;
import japhet.sales.model.impl.PaybackProtest;

/**
 * 
 * @author David Israel Garcia Alcazar
 *
 */

@Stateless
public class PaybackProtestDAO extends GenericDAO<PaybackProtest, Long> {

	@Inject
	private Logger logger;
	
	public PaybackProtestDAO() {
		super(PaybackProtest.class, Long.class);
	}
	
	public List<PaybackProtest> getPaybackProtestsByCompany(Map<String, Object> params)
			throws DataLayerException {
		List<PaybackProtest> paybackProtests = null;
		final Long COMP_ID = ((params != null && params.containsKey(COMPANY_ID)) ? (long)params.get(COMPANY_ID) : -1L);
		final String INFO_MSG = String.format("Selecting all the PaybackProtest by Company: %d...", COMP_ID);
		try {
			logger.info(INFO_MSG);
			paybackProtests = selectList(GET_ALL_PAYBACK_PROTEST_BY_COMPANY, params);
		} catch (Exception e) {
			final String ERROR_MSG = String
					.format("An error has occurred while selecting all the PaybackProtest by Company: %d.", COMP_ID);
			logger.fatal(ERROR_MSG, e);
			throw new DataLayerException(ERROR_MSG, e);
		}
		return paybackProtests;
	}

	public List<PaybackProtest> getPaybackProtestsByUser(Map<String, Object> params) 
			throws DataLayerException {
		List<PaybackProtest> paybackProtests = null;
		final Long COMP_ID = ((params != null && params.containsKey(USER_ID)) ? (long)params.get(USER_ID) : -1L);
		final String INFO_MSG = String.format("Selecting all the PaybackProtest by User: %d...", COMP_ID);
		try {
			logger.info(INFO_MSG);
			paybackProtests = selectList(GET_ALL_PAYBACK_PROTEST_BY_USER, params);
		} catch (Exception e) {
			final String ERROR_MSG = String
					.format("An error has occurred while selecting all the PaybackProtest by User:: %d.", COMP_ID);
			logger.fatal(ERROR_MSG, e);
			throw new DataLayerException(ERROR_MSG, e);
		}
		return paybackProtests;
	}

	public List<PaybackProtest> getPaybackProtestsByStatus(Map<String, Object> params) 
			throws DataLayerException {
		List<PaybackProtest> paybackProtests = null;
		final Long COMP_ID = ((params != null && params.containsKey(STATUS_ID)) ? (long)params.get(STATUS_ID) : -1L);
		final String INFO_MSG = String.format("Selecting all the PaybackProtest by Status: %d...", COMP_ID);
		try {
			logger.info(INFO_MSG);
			paybackProtests = selectList(GET_ALL_PAYBACK_PROTEST_BY_STATUS, params);
		} catch (Exception e) {
			final String ERROR_MSG = String
					.format("An error has occurred while selecting all the PaybackProtest by Status: %d.", COMP_ID);
			logger.fatal(ERROR_MSG, e);
			throw new DataLayerException(ERROR_MSG, e);
		}
		return paybackProtests;
	}

	public List<PaybackProtest> getAllPaybackProtests() 
			throws DataLayerException {
		List<PaybackProtest> paybackProtests = null;
		final String INFO_MSG = String.format("Selecting all the  PaybackProtest...");
		try {
			logger.info(INFO_MSG);
			paybackProtests = selectList(GET_ALL_PAYBACK_PROTEST, null);
		} catch (Exception e) {
			final String ERROR_MSG = String
					.format("An error has occurred while selecting all the PaybackProtest.");
			logger.fatal(ERROR_MSG, e);
			throw new DataLayerException(ERROR_MSG, e);
		}
		return paybackProtests;
	}
}
