// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice._2011_08_01.promotion;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;

public class Summary implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "PromotionId")
	public String promotionId;	
	
	@Element(name = "Category")
	public String category;	
	
	@Element(name = "StartDate")
	public String startDate;	
	
	@Element(name = "EndDate")
	public String endDate;	
	
	@Element(name = "EligibilityRequirementDescription")
	public String eligibilityRequirementDescription;	
	
	@Element(name = "BenefitDescription")
	public String benefitDescription;	
	
	@Element(name = "TermsAndConditions")
	public String termsAndConditions;	
	
    
}