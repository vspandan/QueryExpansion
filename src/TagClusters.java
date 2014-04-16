import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.tagger.maxent.TaggerConfig;

public class TagClusters implements Properties {
	@SuppressWarnings({ "deprecation", "resource" })
	public void TagCluster() throws IOException, ParseException {
		

		IndexReader reader = IndexReader.open(NIOFSDirectory.open(new File(
				LUCENE_OP_FOLDER)));
		IndexSearcher searcher = new IndexSearcher(reader);

		TaggerConfig config = new TaggerConfig("-model",
				TAGGER);
		MaxentTagger tagger = new MaxentTagger(
				TAGGER, config,
				false);
		File clustersFolder = new File(CLUSTERS_FOLDER);
		File[] clustersFiles = clustersFolder.listFiles();

		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "content",
				analyzer);

		HashMap<Integer, ArrayList<String>> hm = new HashMap<Integer, ArrayList<String>>();
		for (int i = 0; i < clustersFiles.length; i++) {
			HashMap<String, Integer> docHm = new HashMap<String, Integer>();
			HashMap<String, Double> highScoreTErms = new HashMap<String, Double>();
			Terms terms = reader.getTermVector(i, "content");
			TermsEnum termsEnum = terms.iterator(TermsEnum.EMPTY);
			BytesRef term;
			int termFreq = 0;
			while ((term = termsEnum.next()) != null) {
				
				termFreq = reader.docFreq(new Term("content", term));
				docHm.put(term.utf8ToString(), termFreq);
			}
			int count = 0;

			Query q = null;
			ScoreDoc hits[] = null;
			TopScoreDocCollector collector = null;
			;
			Set<String> keys=docHm.keySet();
			for (String key: keys) {
				q = parser.parse(key);
				collector = TopScoreDocCollector.create(clustersFiles.length,
						true);
				searcher.search(q, collector);
				hits = collector.topDocs().scoreDocs;
				double temp = 0;
				if (hits.length != 0) {
					for (int i1 = 0; i1 < hits.length; i1++) {
						if (i1 == i) {
							temp = hits[i1].score;
							break;
						}
					}
				}
				highScoreTErms.put(key, temp);
			}
			docHm.clear();
			ArrayList<String> tags = new ArrayList<String>();
			count = 0;
			for (Entry<String, Double> e : entriesSortedByValues(highScoreTErms)) {
				count++;
				if (count<35) {
					String pos = tagger.tagString(e.getKey()).replaceAll(
							".*_(.*)?\\s?$", "$1");
					pos = pos.trim();
					if (pos.equals("NN"))
						tags.add(e.getKey());
				}
				else
					break;
			}
			HashSet<String> stopWords = SearchIndex.getStopWords("Stopword");
			tags.remove(stopWords);
			hm.put(i, tags);
		}
		hm=RemoveTags(hm);
		/*	Set<Integer> docIDs=hm.keySet();
		for(Integer i:docIDs)
		{
			ArrayList<String> aL=new ArrayList<String>();
			aL=hm.get(i);
			System.out.println(aL.size()+":"+aL);
		}*/
		
		FileOutputStream fos = new FileOutputStream(new File(TAG_FILE));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(hm);
	}

	public HashMap<Integer, ArrayList<String>> RemoveTags(
			HashMap<Integer, ArrayList<String>> hm) {
		HashMap<Integer, ArrayList<String>> hm2=new HashMap<Integer, ArrayList<String>>();
		HashMap<String,Integer> hm1=new HashMap<String, Integer>();
		HashSet<String> stopWords = SearchIndex.getStopWords("Stopword");
		
		ArrayList<String> aL=new ArrayList<String>();
		ArrayList<String> unWantedTags=new ArrayList<String>();
		Set<Integer> docIDs=hm.keySet();
		for(Integer i:docIDs)
		{
			aL=hm.get(i);
			for(String s:aL){
				if(hm1.containsKey(s))
				{
					int count=hm1.get(s);
					hm1.put(s, count+1);
				}
				else
					hm1.put(s, 1);
			}
		}
		Set<String> tags=hm1.keySet();
		for(String s:tags){
			if(hm1.get(s)>0.3*DOC_LIMIT||s.length()<=3){
				unWantedTags.add(s);
			}
		}
		for(Integer i:docIDs)
		{
			aL=hm.get(i);
			aL.removeAll(unWantedTags);
			aL.removeAll(stopWords);
			hm2.put(i, aL);
		}
		return hm2;
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

	public static void main(String[] args) {
		TagClusters tc = new TagClusters();
		Date d = new Date();
		try {
			tc.TagCluster();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Clusters are labelled. Time taken: "
				+ (new Date().getTime() - d.getTime()));
	}
}
