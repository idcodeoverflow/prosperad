package japhet.sales.model.impl;

import static japhet.sales.data.QueryParameters.*;
import static japhet.sales.util.StringUtils.urlCompletion;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import japhet.sales.data.QueryNames;
import japhet.sales.model.IEntity;
import japhet.sales.util.Encryption;
import japhet.sales.util.StreamUtil;

@Cacheable(value = true)
@Entity
@Table(name = "TB_PRODUCT")
@NamedQueries({
		@NamedQuery(name = QueryNames.GET_AVAILABLE_PRODUCTS,
				query = "SELECT p FROM Product p WHERE p.startDate <= CURRENT_TIMESTAMP AND p.endDate >= CURRENT_TIMESTAMP"),
		@NamedQuery(name = QueryNames.GET_SEARCHED_PRODUCTS,
				query = "SELECT p FROM Product p WHERE p.startDate <= CURRENT_TIMESTAMP AND p.endDate >= CURRENT_TIMESTAMP AND (UPPER(p.title) LIKE :" + SEARCHED_WORDS + " OR UPPER(p.description) LIKE :" + SEARCHED_WORDS + ")"),
		@NamedQuery(name = QueryNames.GET_PRODUCT_BY_KEY,
				query = "SELECT p FROM Product p WHERE p.startDate <= CURRENT_TIMESTAMP AND p.endDate >= CURRENT_TIMESTAMP AND p.productKey = :" + PRODUCT_KEY),
		@NamedQuery(name = QueryNames.GET_AVAILABLE_PRODUCTS_BY_COMPANY,
				query = "SELECT p FROM Product p WHERE p.endDate >= CURRENT_TIMESTAMP AND p.company.companyId = :" + COMPANY_ID),
		@NamedQuery(name = QueryNames.GET_AVAILABLE_PRODUCTS_BY_CATEGORY,
				query = "SELECT p FROM Product p WHERE p.startDate <= CURRENT_TIMESTAMP AND p.endDate >= CURRENT_TIMESTAMP AND p.category.categoryId = :" + CATEGORY_ID)
})
public class Product extends StreamUtil 
	implements IEntity {
	
	/**
	 * Maven generated.
	 */
	private static final long serialVersionUID = -2885883717993765366L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRODUCT_ID")
	private Long productId;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PRICE")
	private Double price;
	
	@Column(name = "PAYBACK_PERCENT")
	private Double paybackPercent;
	
	@Lob
	@Column(name = "IMAGE")
	private byte[] image;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPLOAD_DATE")
	private Date uploadDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COMPANY_ID")
	private Company company;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;
	
	@Column(name = "REDIRECT_NUMBER")
	private Integer redirectNumber;
	
	@Column(name = "URL")
	private String url;
	
	@Column(name = "PRODUCT_KEY",
			unique = true)
	private String productKey;
	
	final transient Double COMISION_PERCENT = 0.5;
	
	public Product() {
		this.redirectNumber = 0;
		this.uploadDate = new Date();
	}

	public Product(Long productId, String title, String description, 
			Double price, Double paybackPercent, byte[] image, 
			Date uploadDate, Date startDate, Date endDate, 
			Company company, Category category, Integer redirectNumber,
			String url, String productKey) {
		super();
		this.productId = productId;
		this.title = title;
		this.description = description;
		this.price = price;
		this.paybackPercent = paybackPercent;
		this.image = image;
		this.uploadDate = uploadDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.company = company;
		this.category = category;
		this.redirectNumber = redirectNumber;
		this.url = url;
		this.productKey = productKey;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPaybackPercent() {
		return paybackPercent;
	}

	public void setPaybackPercent(Double paybackPercent) {
		this.paybackPercent = paybackPercent;
	}
	
	public Double getEndUserPaybackPercent() {
		return paybackPercent - (paybackPercent * COMISION_PERCENT);
	}

	public byte[] getImage() {
		return image;
	}
	
	public Image getImageFile() throws IOException {
		return convertByteArrayToFile(image);
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	public void setImage(File file) throws IOException {
		this.image = convertFileTobytesArray(file);
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getRedirectNumber() {
		return redirectNumber;
	}

	public void setRedirectNumber(Integer redirectNumber) {
		this.redirectNumber = redirectNumber;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = urlCompletion(url);
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}
	
	/**
	 * Generates a safe access encrypted key to 
	 * refer the product outside the system.
	 * @throws Exception  
	 */
	public void generateProductKey() throws Exception {
		final int RAND_FACTOR = 1000000;
		//Generate a random seed
		int randomSeed = (int)(Math.random() * RAND_FACTOR);
		//Add basic values to the seed
		StringBuilder seed = new StringBuilder();
		seed.append(this.productId);
		seed.append(this.title);
		seed.append(this.description);
		seed.append(this.url);
		seed.append(randomSeed);
		//Generated code encrypted and assign it
		this.productKey = Encryption.SHA256(seed.toString());
	}
}
