package japhet.sales.model.impl;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import japhet.sales.data.QueryNames;
import japhet.sales.model.IEntity;

@Cacheable(value = true)
@Entity
@Table(name = "TB_STATUS")
@NamedQueries({
	@NamedQuery(name = QueryNames.GET_ALL_AVAILABLE_STATUS,
			query = "SELECT s FROM Status s WHERE s.availableDate <= CURRENT_TIMESTAMP AND s.endDate >= CURRENT_TIMESTAMP"),
	@NamedQuery(name = QueryNames.GET_ALL_STATUS,
			query = "SELECT s FROM Status s")
})
public class Status implements IEntity {

	/**
	 * Maven generated.
	 */
	private static final long serialVersionUID = -7934429898953967056L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STATUS_ID")
	private Short statusId;
	
	@Column(name = "NAME")
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AVAILABLE_DATE")
	private Date availableDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	
	public Status() {}

	public Status(Short statusId, String name, Date availableDate, 
			Date endDate) {
		super();
		this.statusId = statusId;
		this.name = name.toUpperCase();
		this.availableDate = availableDate;
		this.endDate = endDate;
	}

	public Short getStatusId() {
		return statusId;
	}

	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.toUpperCase();
	}

	public Date getAvailableDate() {
		return availableDate;
	}

	public void setAvailableDate(Date availableDate) {
		this.availableDate = availableDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
		
}
