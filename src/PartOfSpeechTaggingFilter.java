import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.tagger.maxent.TaggerConfig;

public class PartOfSpeechTaggingFilter extends TokenFilter {
    PartOfSpeechAttribute posAtt = addAttribute(PartOfSpeechAttribute.class);
    CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    MaxentTagger tagger;
   
    public PartOfSpeechTaggingFilter(TokenStream in,String modelFile) throws IOException {
      super(in);
      TaggerConfig config = new TaggerConfig("-model", modelFile);
      tagger=new MaxentTagger(modelFile, config, false);
    }
    
    public boolean incrementToken() throws IOException {
      if (!input.incrementToken()) {
    	  return false;
      }
      else{
      	  CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);
      	  PartOfSpeechAttribute posAttribute = addAttribute(PartOfSpeechAttribute.class);
    	  String posTagged = tagger.tagString(charTermAttribute.toString().toLowerCase());
          String pos = posTagged.replaceAll(".*_(.*)?\\s?$", "$1");
          pos=pos.trim();
          posAttribute.setPartOfSpeech(pos);
          return true;
      }
    }
    public boolean incrementTokenSpecial() throws IOException {
        if (!input.incrementToken()) {
      	  return false;
        }
      	 return true;
      }
  }