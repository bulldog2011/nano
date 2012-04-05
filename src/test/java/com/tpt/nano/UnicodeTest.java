package com.tpt.nano;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.tpt.nano.annotation.Attribute;
import com.tpt.nano.annotation.Element;
import com.tpt.nano.annotation.RootElement;

import junit.framework.TestCase;

public class UnicodeTest extends TestCase {
	
   private static final String SOURCE =     
	   "<?xml version='1.0' encoding='UTF-8'?>\n"+      
	   "<example>\n"+
	   "      <unicode origin=\"Australia\" name=\"Nicole Kidman\">\n"+
	   "         <text>Nicole Kidman</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Austria\" name=\"Johann Strauss\">\n"+
	   "         <text>Johann Strau脽</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Canada\" name=\"Celine Dion\">\n"+
	   "         <text>C茅line Dion</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Democratic People's Rep. of Korea\" name=\"LEE Sol-Hee\">\n"+
	   "         <text>鞚挫劋頋</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Denmark\" name=\"Soren Hauch-Fausboll\">\n"+
	   "         <text>S酶ren Hauch-Fausb酶ll</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Denmark\" name=\"Soren Kierkegaard\">\n"+
	   "         <text>S酶ren Kierkeg氓rd</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Egypt\" name=\"Abdel Halim Hafez\">\n"+
	   "         <text>锘嬶簯锖簫锘狅海锘燂怀锘?锖わ簬锘擄粎</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Egypt\" name=\"Om Kolthoum\">\n"+
	   "         <text>锖冿弧 锘涳粺锖涳画锘</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Eritrea\" name=\"Berhane Zeray\">\n"+
	   "         <text>釆め埈釅滇埆</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Ethiopia\" name=\"Haile Gebreselassie\">\n"+
	   "         <text>釆⑨壍釈尩釈</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"France\" name=\"Gerard Depardieu\">\n"+
	   "         <text>G茅rard Depardieu</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"France\" name=\"Jean Reno\">\n"+
	   "         <text>Jean R茅no</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"France\" name=\"Camille Saint-Saens\">\n"+
	   "         <text>Camille Saint-Sa毛ns</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"France\" name=\"Mylene Demongeot\">\n"+
	   "         <text>Myl猫ne Demongeot</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"France\" name=\"Francois Truffaut\">\n"+
	   "         <text>Fran莽ois Truffaut</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Germany\" name=\"Rudi Voeller\">\n"+
	   "         <text>Rudi V枚ller</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Germany\" name=\"Walter Schultheiss\">\n"+
	   "         <text>Walter Schulthei脽</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Greece\" name=\"Giorgos Dalaras\">\n"+
	   "         <text>螕喂蠋蟻纬慰蟼 螡蟿伪位维蟻伪蟼</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Iceland\" name=\"Bjork Gudmundsdottir\">\n"+
	   "         <text>Bj枚rk Gu冒mundsd贸ttir</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"India (Hindi)\" name=\"Madhuri Dixit\">\n"+
	   "         <text>啶ぞ啶о啶班 啶︵た啶涏た啶</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Ireland\" name=\"Sinead O'Connor\">\n"+
	   "         <text>Sin茅ad O'Connor</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Israel\" name=\"Yehoram Gaon\">\n"+
	   "         <text>讬讛讜专诐 讙讗讜谉</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Italy\" name=\"Fabrizio DeAndre\">\n"+
	   "         <text>Fabrizio De Andr茅</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Japan\" name=\"KUBOTA Toshinobu\">\n"+
	   "         <text>涔呬繚鐢奥?聽 鍒╀几</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Japan\" name=\"HAYASHIBARA Megumi\">\n"+
	   "         <text>鏋楀師 銈併亹銇</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Japan\" name=\"Mori Ogai\">\n"+
	   "         <text>妫窏澶</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Japan\" name=\"Tex Texin\">\n"+
	   "         <text>銉嗐偗銈?銉嗐偗銈点兂</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Norway\" name=\"Tor Age Bringsvaerd\">\n"+
	   "         <text>Tor 脜ge Bringsv忙rd</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Pakistan (Urdu)\" name=\"Nusrat Fatah Ali Khan\">\n"+
	   "         <text>賳氐乇鬲 賮鬲丨 毓賱蹖 禺丕賳</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"People's Rep. of China\" name=\"ZHANG Ziyi\">\n"+
	   "         <text>绔犲瓙鎬</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"People's Rep. of China\" name=\"WONG Faye\">\n"+
	   "         <text>鐜嬭彶</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Poland\" name=\"Lech Walesa\">\n"+
	   "         <text>Lech Wa艂臋sa</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Puerto Rico\" name=\"Olga Tanon\">\n"+
	   "         <text>Olga Ta帽贸n</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Rep. of China\" name=\"Hsu Chi\">\n"+
	   "         <text>鑸掓穱</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Rep. of China\" name=\"Ang Lee\">\n"+
	   "         <text>鏉庡畨</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Rep. of Korea\" name=\"AHN Sung-Gi\">\n"+
	   "         <text>鞎堨劚旮</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Rep. of Korea\" name=\"SHIM Eun-Ha\">\n"+
	   "         <text>鞁潃頃</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Russia\" name=\"Mikhail Gorbachev\">\n"+
	   "         <text>袦懈褏邪懈谢 袚芯褉斜邪褔褢胁</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Russia\" name=\"Boris Grebenshchikov\">\n"+
	   "         <text>袘芯褉懈褋 袚褉械斜械薪褖懈泻芯胁</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Syracuse (Sicily)\" name=\"Archimedes\">\n"+
	   "         <text>峒埾佅囄刮坚降未畏蟼</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"Thailand\" name=\"Thongchai McIntai\">\n"+
	   "         <text>喔樴竾喙勦笂喔?喙佮浮喙囙竸喔复喔權箘喔曕涪喙</text>\n"+
	   "      </unicode>\n"+
	   "      <unicode origin=\"U.S.A.\" name=\"Brad Pitt\">\n"+
	   "         <text>Brad Pitt</text>\n"+
	   "      </unicode>\n"+
	   "</example>\n";
   
   private static interface Entry {
	   public String getName();
   }

   @RootElement(name="unicode")
   private static class Unicode implements Entry {

      @Attribute(name="origin")
      private String origin;

      @Element(name="text")
      private String text;
      
      @Attribute
      private String name;
      
      public String getName() {
         return name;
      }
   }
   
   @RootElement(name="example")
   private static class UnicodeExample {

      @Element(name="unicode")
      private List<Unicode> list;

      public Unicode get(String name) {
         for(Unicode unicode : list) {
        	 if (name.equals(unicode.getName())) {
        		 return unicode;
        	 }
         }
         return null;          
      }
   }
   
   private IReader xmlReader;
   private IWriter xmlWriter;
   
   public void setUp() throws Exception {
	   xmlReader = NanoFactory.getXMLReader();
	   xmlWriter = NanoFactory.getXMLWriter();
   }
   
   public void testUnicode() throws Exception {
	   UnicodeExample example = xmlReader.read(UnicodeExample.class, SOURCE);
	   
	   assertUnicode(example);
   }
   
   public void assertUnicode(UnicodeExample example) throws Exception {
	   assertTrue("Data was not unicode", isUnicode(example));
   }
   
   public void testWriteUniicode() throws Exception {
	   UnicodeExample example = xmlReader.read(UnicodeExample.class, SOURCE);
	   
	   assertUnicode(example);
	   
	   String xmlStr = xmlWriter.write(example);
	   example = xmlReader.read(UnicodeExample.class, xmlStr);
	   
	   assertUnicode(example);
   }
   
   public void testUnicodeFromByteStream() throws Exception {
	   byte[] data = SOURCE.getBytes("UTF-8");
	   InputStream is = new ByteArrayInputStream(data);
	   UnicodeExample example = xmlReader.read(UnicodeExample.class, is);
	   
	   assertUnicode(example);
   }
   
   public void testIncorrectEncoding() throws Exception {
	   byte[] data = SOURCE.getBytes();
	   InputStream is = new ByteArrayInputStream(data);
	   UnicodeExample example = xmlReader.read(UnicodeExample.class, new InputStreamReader(is, "ISO-8859-1"));
	   
	   assertFalse("Encoding of ISO-8859-1 did not work", isUnicode(example));
   }
   
   public boolean isUnicode(UnicodeExample example) throws Exception {
      // Ensure text remailed unicode           
      if(!example.get("Nicole Kidman").text.equals("Nicole Kidman")) return false;
      if(!example.get("Johann Strauss").text.equals("Johann Strau脽")) return false;
      if(!example.get("Celine Dion").text.equals("C茅line Dion")) return false;
      if(!example.get("LEE Sol-Hee").text.equals("鞚挫劋頋")) return false;
      if(!example.get("Soren Hauch-Fausboll").text.equals("S酶ren Hauch-Fausb酶ll")) return false;
      if(!example.get("Soren Kierkegaard").text.equals("S酶ren Kierkeg氓rd")) return false;
      if(!example.get("Abdel Halim Hafez").text.equals("锘嬶簯锖簫锘狅海锘燂怀锘?锖わ簬锘擄粎")) return false;
      if(!example.get("Om Kolthoum").text.equals("锖冿弧 锘涳粺锖涳画锘")) return false;
      if(!example.get("Berhane Zeray").text.equals("釆め埈釅滇埆")) return false;
      if(!example.get("Haile Gebreselassie").text.equals("釆⑨壍釈尩釈")) return false;
      if(!example.get("Gerard Depardieu").text.equals("G茅rard Depardieu")) return false;
      if(!example.get("Jean Reno").text.equals("Jean R茅no")) return false;
      if(!example.get("Camille Saint-Saens").text.equals("Camille Saint-Sa毛ns")) return false;
      if(!example.get("Mylene Demongeot").text.equals("Myl猫ne Demongeot")) return false;
      if(!example.get("Francois Truffaut").text.equals("Fran莽ois Truffaut")) return false;
      //if(!example.get("Rudi Voeller").text.equals("Rudi V枚ller")) return false;
      if(!example.get("Walter Schultheiss").text.equals("Walter Schulthei脽")) return false;
      if(!example.get("Giorgos Dalaras").text.equals("螕喂蠋蟻纬慰蟼 螡蟿伪位维蟻伪蟼")) return false;
      if(!example.get("Bjork Gudmundsdottir").text.equals("Bj枚rk Gu冒mundsd贸ttir")) return false;
      if(!example.get("Madhuri Dixit").text.equals("啶ぞ啶о啶班 啶︵た啶涏た啶")) return false;
      if(!example.get("Sinead O'Connor").text.equals("Sin茅ad O'Connor")) return false;
      if(!example.get("Yehoram Gaon").text.equals("讬讛讜专诐 讙讗讜谉")) return false;
      if(!example.get("Fabrizio DeAndre").text.equals("Fabrizio De Andr茅")) return false;
      if(!example.get("KUBOTA Toshinobu").text.equals("涔呬繚鐢奥?聽 鍒╀几")) return false;
      if(!example.get("HAYASHIBARA Megumi").text.equals("鏋楀師 銈併亹銇")) return false;
      if(!example.get("Mori Ogai").text.equals("妫窏澶")) return false;
      if(!example.get("Tex Texin").text.equals("銉嗐偗銈?銉嗐偗銈点兂")) return false;
      if(!example.get("Tor Age Bringsvaerd").text.equals("Tor 脜ge Bringsv忙rd")) return false;
      if(!example.get("Nusrat Fatah Ali Khan").text.equals("賳氐乇鬲 賮鬲丨 毓賱蹖 禺丕賳")) return false;
      if(!example.get("ZHANG Ziyi").text.equals("绔犲瓙鎬")) return false;
      if(!example.get("WONG Faye").text.equals("鐜嬭彶")) return false;
      if(!example.get("Lech Walesa").text.equals("Lech Wa艂臋sa")) return false;
      if(!example.get("Olga Tanon").text.equals("Olga Ta帽贸n")) return false;
      if(!example.get("Hsu Chi").text.equals("鑸掓穱")) return false;
      if(!example.get("Ang Lee").text.equals("鏉庡畨")) return false;
      if(!example.get("AHN Sung-Gi").text.equals("鞎堨劚旮")) return false;
      if(!example.get("SHIM Eun-Ha").text.equals("鞁潃頃")) return false;
      if(!example.get("Mikhail Gorbachev").text.equals("袦懈褏邪懈谢 袚芯褉斜邪褔褢胁")) return false;
      if(!example.get("Boris Grebenshchikov").text.equals("袘芯褉懈褋 袚褉械斜械薪褖懈泻芯胁")) return false;
      if(!example.get("Archimedes").text.equals("峒埾佅囄刮坚降未畏蟼")) return false;
      if(!example.get("Thongchai McIntai").text.equals("喔樴竾喙勦笂喔?喙佮浮喙囙竸喔复喔權箘喔曕涪喙")) return false;
      if(!example.get("Brad Pitt").text.equals("Brad Pitt")) return false;
      return true;
   }
   
}
