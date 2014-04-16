import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.QueryParser;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.tagger.maxent.TaggerConfig;

@SuppressWarnings({ "deprecation" })
public class SearchIndex implements Properties {

	private Analyzer analyzer = null;
	private QueryParser parser = null;
	private TopScoreDocCollector collector = null;
	private ScoreDoc[] hits = null;
	private ScoreDoc[] hits1 = null;
	private String docPath = null;
	private IndexReader reader = null;
	private IndexSearcher searcher = null;
	private static RandomAccessFile r;
	private HashMap<String, Double> hm = new HashMap<String, Double>();
	private HashSet<String> stopWords = null;
	private HashSet<String> nextTerms = null;
	private PhraseQuery query = null;
	private HashMap<Integer, ArrayList<String>> tags = null;
	private HashSet<String> tags1 = null;
	private TaggerConfig config = null;
	private MaxentTagger tagger = null;

	public SearchIndex(String indexPath, String docPath) throws IOException {
		this.config = new TaggerConfig("-model", TAGGER);
		this.tagger = new MaxentTagger(TAGGER, config, false);

		this.query = new PhraseQuery();
		this.reader = IndexReader
				.open(NIOFSDirectory.open(new File(indexPath)));
		this.searcher = new IndexSearcher(reader);
		this.docPath = docPath;
		this.analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		this.parser = new QueryParser(Version.LUCENE_CURRENT, "content",
				analyzer);
		this.nextTerms = new HashSet<String>();
	}

	@SuppressWarnings("unchecked")
	public void readFromIndex( String queryInput)
			throws Exception {

		StringTokenizer st = new StringTokenizer(queryInput);
		stopWords = getStopWords("Stopword");
		String qTerms[] = new String[st.countTokens()];
		FileInputStream fis = new FileInputStream(TAG_FILE);
		ObjectInputStream ois = new ObjectInputStream(fis);
		tags = ((HashMap<Integer, ArrayList<String>>) ois.readObject());
		tags1= new HashSet<String>();
		ois.close();
		int i = 0;
		while (st.hasMoreTokens()) {

			qTerms[i++] = st.nextToken();
			query.add(new Term("content", qTerms[i - 1]));
		}

		query.setSlop(0);

		collector = TopScoreDocCollector.create(DOC_LIMIT, true);
		searcher.search(query, collector);
		hits = collector.topDocs().scoreDocs;

		int noOfInpWords=qTerms.length;
		if (hits.length != 0) {
			CalcScore4Queries(queryInput, nextWords(qTerms[i - 1], hits,noOfInpWords));
		} else {
			query.setSlop(15);
			collector = TopScoreDocCollector.create(DOC_LIMIT, true);
			searcher.search(query, collector);
			hits = collector.topDocs().scoreDocs;

			int ind=0;
			for (int i1 = 0; i1 < qTerms.length; i1++) {
				if (hits.length == 0) {
					ind=1;
					HashSet<String> h = nextWords(qTerms[i1],noOfInpWords);
					if (h != null)
						nextTerms.addAll(h);
				}
				nextTerms.addAll(nextWords(qTerms[i1], hits,noOfInpWords));
			}
			if(ind==0){
				CalcScore4Queries(queryInput, nextTerms);
			}
			else
				hm=null;
		}
		reader.close();
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	static File f = null;

	public static HashSet<String> getStopWords(String stopWordsFilename) {
		HashSet<String> stopWordsList = new HashSet<String>();

		f = new File(stopWordsFilename);
		Scanner stopWordsFile = null;
		try {
			stopWordsFile = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		while (stopWordsFile.hasNext())
			stopWordsList.add(stopWordsFile.next());
		stopWordsFile.close();
		return stopWordsList;
	}

	public HashSet<String> nextWords(String word, ScoreDoc[] hits,int noOfInpWords)
			throws Exception {

		HashSet<String> nextTerms = new HashSet<String>();
		for (int i = 0; i < DOC_LIMIT && i < hits.length; i++) {
			int scoreDoc = hits[i].doc;
			// System.out.println(scoreDoc);
			r = new RandomAccessFile(new File(docPath + File.separator
					+ "Cluster" + new DecimalFormat("000").format(scoreDoc)),
					"r");
			Terms terms = reader.getTermVector(scoreDoc, "content");
			TermsEnum termsEnum = terms.iterator(TermsEnum.EMPTY);
			BytesRef term = new BytesRef(word);

			while ((term = termsEnum.next()) != null) {
				String docTerm = term.utf8ToString();
				if (word.equals(docTerm)) {
					DocsAndPositionsEnum docPosEnum = termsEnum
							.docsAndPositions(null, null,
									DocsAndPositionsEnum.FLAG_OFFSETS);
					for (int j = 0; j < docPosEnum.freq(); j++) {
						docPosEnum.nextPosition();
						docPosEnum.startOffset();
						int end = docPosEnum.endOffset();
						r.seek(end + 1);
						char ch = 0;
						String t = "";
						while (true) {
							ch = (char) r.read();
							if (ch == ' ' || ch == 10 || ch == -1 || ch == '-'
									|| ch == '\'')
								break;
							if ((ch > 64 && ch < 92) || (ch > 96 && ch < 123))
								t += ch;
						}

						if (t.length() >= 3
								&& !stopWords.contains(t.toLowerCase())) {
							String pos = tagger.tagString(t).replaceAll(
									".*_(.*)?\\s?$", "$1");
							;
							pos = pos.trim();
							if (pos.equals("NN") || pos.equals("NNP"))
								nextTerms.add(t);
						}
					}
				}
			}
			if(noOfInpWords!=1)
				tags1.addAll(tags.get(scoreDoc));
		}
		return nextTerms;
	}

	private HashSet<String> nextWords(String string,int noOfInpWords) throws Exception {
		PhraseQuery pQ = new PhraseQuery();
		pQ.add(new Term("content", string));
		collector = TopScoreDocCollector.create(DOC_LIMIT, true);
		searcher.search(pQ, collector);
		hits = collector.topDocs().scoreDocs;
		return nextWords(string, hits,noOfInpWords);
	}

	public void CalcScore4Queries(String queryInput, HashSet<String> augTerms)
			throws IOException {
		for (String t : augTerms) {
			Query query = null;
			try {
				query = parser.parse(queryInput + " " + t);
			} catch (Exception e) {
				continue;
			}
			collector = TopScoreDocCollector.create(MAX_PER_CLUSTER, true);
			searcher.search(query, collector);
			hits1 = collector.topDocs().scoreDocs;
			double score = 0;
			for (int k = 0; k < hits1.length; k++) {
				score = score > hits1[k].score ? score : hits1[k].score;
			}
			hm.put(queryInput + " " + t, score);
		}

	}

	public static void main(String[] args) throws Exception {

		SearchIndex si = new SearchIndex(LUCENE_OP_FOLDER, CLUSTERS_FOLDER);
		System.out.println("Enter Query");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		{
			String query = br.readLine().toLowerCase();
			Date d = new Date();
			if (query != null)
				si.readFromIndex(query);

			if(si.hm!=null){
				Set<Entry<String, Double>> augmentedQueries=entriesSortedByValues(si.hm);
				//Set<String> augmentedQueries = si.hm.keySet();
				int i = 0;
				for (Entry<String,Double> s : augmentedQueries) {
					if (i++ <=15)
						System.out.println(s.getKey());
					else
						break;
				}
			}
			/*System.out.println("*****************");*/
			for(String s:si.tags1){
				System.out.println(query+" "+s);
			}
		//	System.out.println(new Date().getTime() - d.getTime());
		}
	}

}