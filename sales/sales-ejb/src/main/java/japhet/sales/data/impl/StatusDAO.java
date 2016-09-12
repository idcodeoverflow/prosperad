package japhet.sales.data.impl;

import javax.ejb.Stateless;

import japhet.sales.data.GenericDAO;
import japhet.sales.model.impl.Status;

@Stateless
public class StatusDAO extends GenericDAO<Status, Short> {

	public StatusDAO(Class<Status> type, Class<Short> key) {
		super(type, key);
	}

}
