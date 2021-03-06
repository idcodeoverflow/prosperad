package japhet.sales.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import japhet.sales.catalogs.Statuses;
import japhet.sales.data.impl.CompanyDAO;
import japhet.sales.data.impl.UserDAO;
import japhet.sales.except.BusinessServiceException;
import japhet.sales.model.impl.Company;
import japhet.sales.model.impl.User;
import japhet.sales.service.ICompanyService;

@LocalBean
@Stateless
public class CompanyService implements ICompanyService {

	/**
	 * Maven generated.
	 */
	private static final long serialVersionUID = 6344195007440839444L;

	@Inject
	private Logger logger;
	
	@EJB
	private CompanyDAO companyDAO;
	
	@EJB
	private UserDAO userDAO;
	
	@Override
	public List<Company> getAllAvailableCompanies() 
			throws BusinessServiceException {
		List<Company> companies = null;
		List<Short> validStatuses = new ArrayList<>();
		try {
			logger.info("Getting all available companies...");
			//Define valid statuses
			validStatuses.add(Statuses.ACTIVE.getId());
			//Get valid companies
			companies = companyDAO.getAllAvailableCompanies(validStatuses);
		} catch(Exception e) {
			final String ERROR_MSG = "Error while getting all available companies...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
		return companies;
	}

	@Override
	public List<Company> getAllAvailableCompaniesOfType(Short companyTypeId)
			throws BusinessServiceException {
		final String LOGGER_MSG = String.format("Getting all available companies of type: %d...", 
				companyTypeId);
		List<Company> companies = null;
		List<Short> validStatuses = new ArrayList<>();
		try {
			logger.info(LOGGER_MSG);
			//Define valid statuses
			validStatuses.add(Statuses.ACTIVE.getId());
			//Get valid companies
			companies = companyDAO.getAllAvailableCompaniesOfType(validStatuses, companyTypeId);
		} catch(Exception e) {
			final String ERROR_MSG = String.format("Error while getting all available companies of type: %d.", 
					companyTypeId);
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
		return companies;
	}
	
	@Override
	public List<Company> getAllCompanies() 
			throws BusinessServiceException {
		List<Company> companies = null;
		try {
			logger.info("Getting all companies...");
			companies = companyDAO.getAllCompanies();
		} catch(Exception e) {
			final String ERROR_MSG = "Error while getting all companies...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
		return companies;
	}

	@Override
	public Company getCompanyByUserId(Map<String, Object> parameters) 
			throws BusinessServiceException {
		Company company = null;
		try {
			logger.info("Getting company by userId...");
			company = companyDAO.getCompanyByUserId(parameters);
		} catch(Exception e) {
			final String ERROR_MSG = "Error while getting company by userId...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
		return company;
	}
	
	@Override
	public Company selectCompany(Long companyId) 
			throws BusinessServiceException {
		Company company = null;
		try {
			logger.info("Getting company: " + companyId + "...");
			company = companyDAO.select(companyId);
		} catch(Exception e) {
			final String ERROR_MSG = "Error while getting company: " + companyId + "...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
		return company;
	}

	@Override
	public boolean updateCompany(Company company) 
			throws BusinessServiceException {
		try {
			logger.info("Updating company: " + company.getCompanyId() + "...");
			companyDAO.update(company);
			return true;
		} catch(Exception e) {
			final String ERROR_MSG = "Error while updating company: " + 
					company.getCompanyId() + "...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
	}

	@Override
	public boolean deleteCompany(Company company) 
			throws BusinessServiceException {
		try {
			logger.info("Deleting company: " + company.getCompanyId() + "...");
			companyDAO.delete(company);
			return true;
		} catch(Exception e) {
			final String ERROR_MSG = "Error while getting company: " + 
					company.getCompanyId() + "...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
	}

	@Override
	public boolean insertCompany(Company company) 
			throws BusinessServiceException {
		try {
			logger.info("Inserting company: " + company.getUser().getName() + "...");
			companyDAO.insert(company);
			return true;
		} catch(Exception e) {
			final String ERROR_MSG = "Error while inserting company: " + 
					company.getUser().getName() + "...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean insertCompany(Company company, User user)
			throws BusinessServiceException, Exception {
		try {
			logger.info("Inserting company: " + company.getUser().getName() + "...");
			userDAO.insert(user);
			companyDAO.insert(company);
			return true;
		} catch(Exception e) {
			final String ERROR_MSG = "Error while inserting company: " + 
					company.getUser().getName() + ". Rolling back transaction...";
			logger.fatal(ERROR_MSG, e);
			throw new BusinessServiceException(ERROR_MSG, e);
		}
	}
}
