import org.apache.lucene.util.Attribute;

    public interface PartOfSpeechAttribute extends Attribute {  
        
        public void setPartOfSpeech(String pos);  
        
        public String getPartOfSpeech();  
      }  