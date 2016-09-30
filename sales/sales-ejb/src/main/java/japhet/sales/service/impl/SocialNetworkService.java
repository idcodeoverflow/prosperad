package japhet.sales.service.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import japhet.sales.data.impl.SocialNetworkDAO;
import japhet.sales.model.impl.SocialNetwork;
import japhet.sales.service.ISocialNetwork;

@LocalBean
@Stateless
public class SocialNetworkService implements ISocialNetwork {

	/**
	 * Maven generated.
	 */
	private static final long serialVersionUID = 1791506380978459987L;

	@Inject
	private Logger logger;
	
	@EJB
	private SocialNetworkDAO socialNetworkDAO;
	
	@Override
	public List<SocialNetwork> getAllSocialNetworks() {
		logger.info("Obtaining all social networks...");
		return socialNetworkDAO.getAllSocialNetworks();
	}

	@Override
	public List<SocialNetwork> getAllAvailableSocialNetworks() {
		logger.info("Obtaining all available social networks...");
		return socialNetworkDAO.getAllAvailableSocialNetworks();
	}
}