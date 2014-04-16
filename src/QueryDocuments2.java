import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

@SuppressWarnings({ "deprecation" })
public class QueryDocuments2 implements Properties {

	private Analyzer analyzer = null;
	private QueryParser parser = null;
	private TopScoreDocCollector collector = null;
	private TopScoreDocCollector collector1 = null;
	private ScoreDoc[] hits = null;
	private IndexReader reader = null;
	private IndexSearcher searcher = null;
	private PhraseQuery query1 = null;
	private ArrayList<String> mainList = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, Integer> relDocsCount = new HashMap<String, Integer>();

	private static FileWriter op_p = null;
	private static FileWriter op_r = null;

	public QueryDocuments2(String indexPath, String docPath) throws IOException {
		this.reader = IndexReader
				.open(NIOFSDirectory.open(new File(indexPath)));
		this.searcher = new IndexSearcher(reader);
		this.analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		this.parser = new QueryParser(Version.LUCENE_CURRENT, "content",
				analyzer);
		op_p = new FileWriter(PRECISION_FILE);
		op_r = new FileWriter(RECALL_FILE);
	}

	public void readFromIndex(String queryInput, boolean flag) throws Exception {

		parser.parse(queryInput);
		query1 = new PhraseQuery();
		StringTokenizer st = new StringTokenizer(queryInput);
		while (st.hasMoreTokens()) {
			query1.add(new Term("content", st.nextToken().toLowerCase()));

		}
		query1.setSlop(100000000);
		collector = TopScoreDocCollector.create(RETRIEVE_DOC_NO, true);
		searcher.search(query1, collector);
		collector1 = TopScoreDocCollector.create(9000, true);
		searcher.search(query1, collector1);
		relDocsCount.put(queryInput, collector1.topDocs().scoreDocs.length);

		hits = collector.topDocs().scoreDocs;
		ArrayList<String> mainList1 = new ArrayList<String>();
		mainList.clear();
		for (int i = 0; i < hits.length; i++) {
			Document d = searcher.doc(hits[i].doc);
			if (flag)
				mainList.add(d.get("path"));
			else {
				mainList1.add(d.get("path"));
			}

		}
		if (flag)
			hm.put(queryInput, mainList);
		else
			hm.put(queryInput, mainList1);

	}

	public static void main(String[] args) throws Exception {

		QueryDocuments2 si = new QueryDocuments2(LUCENE_OP_FOLDER1, DOC_FOLDER);
		File f = new File("E:\\IRE_MP\\Group-06\\Queryexpansion_results.txt");
		FileReader fr = new FileReader(f);
		BufferedReader br1 = new BufferedReader(fr);
		String t = "";
		boolean flag = false;
		String userQ = "";
		while ((t = br1.readLine()) != null) {

			if (!t.equals("Enter Query")) {
				if (flag && t.length() >= 3) {
					userQ = t;
					si.readFromIndex(t, true);
					flag = false;
				}
				if (t.length() >= 3) {
					si.readFromIndex(t, flag);
				}
			} else {
				op_p = new FileWriter(PRECISION_FILE, true);
				op_r = new FileWriter(RECALL_FILE, true);
				flag = true;
				Set<String> queries = si.hm.keySet();
				op_p.write("**************" + userQ + "\n");
				op_r.write("**************" + userQ + "\n");
				for (String s : queries) {
					if (!s.equals(userQ)) {
						ArrayList<String> al = si.hm.get(s);
						op_p.write(s + "\t:\t" + (double) al.size()
								/ RETRIEVE_DOC_NO);
						op_r.write(s + "\t:\t" + (double) al.size()
								/ relDocsCount.get(s));
						al.retainAll(si.hm.get(userQ));
						op_p.write("\t:\t" + (double) al.size()
								/ RETRIEVE_DOC_NO + "\n");
						op_r.write("\t:\t" + (double) al.size()
								/ relDocsCount.get(s) + "\n");
					}
				}
				si.hm.clear();
				op_p.write("\n");
				op_p.close();
				op_r.write("\n");
				op_r.close();
				relDocsCount.clear();
			}
		}
		br1.close();
	}
}
