package japhet.sales.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import japhet.sales.data.impl.StateDAO;
import japhet.sales.model.impl.State;
import japhet.sales.service.IStateService;

@Singleton
@Startup
public class StateServiceImpl implements IStateService {

	@Inject
	private Logger logger;
	
	@EJB
	private StateDAO stateDAO;
	
	private Map<Short, State> allStates;
	
	@PostConstruct
	public void init(){
		logger.info("Obtaining all states from the DB..");
		List<State> states = stateDAO.getAllStates();
		allStates = new HashMap<>();
		for (State state : states) {
			allStates.put(state.getStateId(), state);
		}
	}
	
	/**
	 * Maven generated.
	 */
	private static final long serialVersionUID = 1036437709475395893L;

	@Override
	public List<State> getAllStates() {
		logger.info("Obtaining all states..");
		return new ArrayList<>(allStates.values());
	}

	@Override
	public State getState(Short stateId) {
		State state = null;
		logger.info("Obtaining state: " + stateId);
		try {
			state = allStates.get(stateId);
		} catch (Exception e) {
			logger.severe("Error obtaining state: " + stateId + 
					"\n" + e.getStackTrace());
		}
		return state;
	}

}