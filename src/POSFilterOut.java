import java.util.ArrayList;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

public final class POSFilterOut extends FilteringTokenFilter {

	
  private final ArrayList<String> stopPOS;
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final PartOfSpeechAttribute posAtt=addAttribute(PartOfSpeechAttribute.class);

  public POSFilterOut(Version matchVersion, TokenStream in, ArrayList<String> stopPOS) {
    super(matchVersion, in);
    this.stopPOS = stopPOS;
  }
  
  @Override
  protected boolean accept() {
	if(termAtt.toString().length()>=3)
		return stopPOS.contains(posAtt.getPartOfSpeech());
	else
		return false;
  }

}