import org.apache.lucene.util.AttributeImpl;

public final class PartOfSpeechAttributeImpl extends AttributeImpl implements
		PartOfSpeechAttribute {

	private String pos = "";

	public void setPartOfSpeech(String pos) {
		this.pos = pos;
	}

	public String getPartOfSpeech() {
		return pos;
	}

	@Override
	public void clear() {
		pos = "";
	}

	@Override
	public void copyTo(AttributeImpl target) {
		((PartOfSpeechAttribute) target).setPartOfSpeech(pos);
	}

}