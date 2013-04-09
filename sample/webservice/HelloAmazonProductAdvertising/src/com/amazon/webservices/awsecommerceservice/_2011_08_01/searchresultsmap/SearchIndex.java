// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.amazon.webservices.awsecommerceservice._2011_08_01.searchresultsmap;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;
import java.math.BigInteger;
import java.util.List;
import com.amazon.webservices.awsecommerceservice._2011_08_01.CorrectedQuery;

public class SearchIndex implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "IndexName")
	public String indexName;	
	
	@Element(name = "Results")
	public BigInteger results;	
	
	@Element(name = "Pages")
	public BigInteger pages;	
	
	@Element(name = "CorrectedQuery")
	public CorrectedQuery correctedQuery;	
	
	@Element(name = "RelevanceRank")
	public BigInteger relevanceRank;	
	
	@Element(name = "ASIN")
	public List<String> asin;	
	
    
}