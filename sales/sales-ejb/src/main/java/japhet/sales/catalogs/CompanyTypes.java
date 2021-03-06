package japhet.sales.catalogs;

/**
 * 
 * @author David Israel Garcia Alcazar
 *
 */
public enum CompanyTypes {
	GOODS((short) 1, "catalog.companyType.goods"),
	SERVICES((short) 2, "catalog.companyType.services");
	
	private short companyTypeId;
	private String name;
	
	CompanyTypes(short companyTypeId, String name) {
		this.companyTypeId = companyTypeId;
		this.name= name;
	}

	public short getCompanyTypeId() {
		return companyTypeId;
	}

	public void setCompanyTypeId(short companyTypeId) {
		this.companyTypeId = companyTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
