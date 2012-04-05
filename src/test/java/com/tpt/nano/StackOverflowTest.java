package com.tpt.nano;

import java.util.ArrayList;
import java.util.List;

import com.tpt.nano.annotation.Element;
import com.tpt.nano.annotation.RootElement;

import junit.framework.TestCase;

public class StackOverflowTest extends TestCase {
	
	private static final int ITERATIONS = 1000;
	   
	private static final String NEW_BENEFIT = 
	   "<newBenefit>"+
	   "   <office>AAAAA</office>"+
	   "   <recordNumber>1046</recordNumber>"+
	   "   <type>A</type>"+
	   "</newBenefit>";
	   
	private static final String BENEFIT_MUTATION = 
	   "<benefitMutation>"+
	   "   <office>AAAAA</office>"+
	   "   <recordNumber>1046</recordNumber>"+
	   "   <type>A</type>"+
	   "   <comment>comment</comment>"+
	   "</benefitMutation>";	
	
   @RootElement
   public static class Delivery {

      @Element(name = "newBenefit")
      private List<NewBenefit> listNewBenefit = new ArrayList<NewBenefit>();

      @Element(name = "benefitMutation")
      private List<BenefitMutation> listBenefitMutation = new ArrayList<BenefitMutation>();

   }	

   public static class NewBenefit {

      @Element
      private String office;

      @Element
      private String recordNumber;

      @Element
      private String type;
   }

   public static class BenefitMutation extends NewBenefit {

      @Element
      private String comment;
   }
   
   public void testStackOverflow() throws Exception {
	   StringBuilder builder = new StringBuilder();
	   builder.append("<delivery>");
	   
	   boolean isNewBenefit = true;
	   for(int i = 0; i < ITERATIONS; i++) {
		   String text = null;
		   
		   if (isNewBenefit) {
			   text = NEW_BENEFIT;
		   } else {
			   text = BENEFIT_MUTATION;
		   }
		   isNewBenefit = !isNewBenefit;
		   builder.append(text);
	   }
	   builder.append("</delivery>");
	   
	   IReader xmlReader = NanoFactory.getXMLReader();
	   Delivery delivery = xmlReader.read(Delivery.class, builder.toString());
	   
	   assertEquals(delivery.listBenefitMutation.size() + delivery.listNewBenefit.size(), ITERATIONS);
	   
   }

}
