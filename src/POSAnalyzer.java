import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;


class POSAnalyzer extends Analyzer implements Properties{
	@SuppressWarnings("deprecation")
	private Version version = Version.LUCENE_CURRENT;

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		
		StandardTokenizer source = new StandardTokenizer(version, reader);
		TokenStream filter = new StandardFilter(version, source);
		try {
			filter=new PartOfSpeechTaggingFilter(filter, TAGGER);
		} catch (IOException e) {
			e.printStackTrace();
		}

		filter = new StopFilter(version, filter,
				EnglishAnalyzer.getDefaultStopSet());
		filter = new LowerCaseFilter(version, filter);
		ArrayList<String> excludePOS = new ArrayList<String>();
		// Add the names of POSs to be ignored corresponding to the
		// pos-tag-set
		// used!
		excludePOS.add("NN");
		excludePOS.add("NNP");

		filter = new POSFilterOut(version, filter, excludePOS);
		return new TokenStreamComponents(source, filter);
	}
}