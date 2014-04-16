import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

@SuppressWarnings({ "deprecation" })
public class QueryDocuments implements Properties {

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

	public QueryDocuments(String indexPath, String docPath) throws IOException {
		this.reader = IndexReader
				.open(NIOFSDirectory.open(new File(indexPath)));
		this.searcher = new IndexSearcher(reader);
		this.analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		this.parser = new QueryParser(Version.LUCENE_CURRENT, "content",
				analyzer);
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

		QueryDocuments si = new QueryDocuments(LUCENE_OP_FOLDER1, DOC_FOLDER);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Sample sheet");

		FileReader fr = new FileReader(
				"E:\\IRE_MP\\Group-06\\Queryexpansion_results.txt");
		BufferedReader br1 = new BufferedReader(fr);
		
		boolean flag = false;
		String userQ = "";
		String t = "";
		
		Row r = null;
		Cell c = null;
		int rowNum = 0;
		
		r=sheet.createRow(rowNum++);
		
		c=r.createCell(0);
		c.setCellValue("Query");
		c=r.createCell(1);
		c.setCellValue("New Precision");
		c=r.createCell(2);
		c.setCellValue("Old Precision");
		c=r.createCell(3);
		c.setCellValue("Precision Change");
		c=r.createCell(4);
		c.setCellValue("New Recall");
		c=r.createCell(5);
		c.setCellValue("Old Recall");
		c=r.createCell(6);
		c.setCellValue("Recall Change");
		
		while ((t = br1.readLine()) != null) {
			if (!t.equals("Enter Query")) {
				if (flag && t.length() >= 3) {
					userQ = t;
					si.readFromIndex(t, true);
					flag = false;
				}
				if (t.length() > 3) {
					si.readFromIndex(t, flag);
				}
			} else {
				flag = true;
				Set<String> queries = si.hm.keySet();
				if (userQ != null && userQ.length() > 3) {
					r = sheet.createRow(rowNum++);
					c = r.createCell(0,5);
					c.setCellValue(userQ);
				}
				for (String s : queries) {
					if (!s.equals(userQ)) {
						r = sheet.createRow(rowNum++);
						ArrayList<String> al = si.hm.get(s);

						c = r.createCell(0);
						c.setCellValue(s);

						c = r.createCell(1);
						c.setCellValue((double) al.size() / RETRIEVE_DOC_NO);

						int relDocC = relDocsCount.get(s);

						c = r.createCell(4);
						if (relDocC != 0)
							c.setCellValue((double) al.size() / relDocC);
						else
							c.setCellValue(-1);

						al.retainAll(si.hm.get(userQ));

						c = r.createCell(2);
						c.setCellValue((double) al.size() / RETRIEVE_DOC_NO);

						c = r.createCell(5);
						if (relDocC != 0)
							c.setCellValue((double) al.size() / relDocC);
						else
							c.setCellValue(-1);

						double v1=0;
						double v2=0;
						c = r.createCell(3);
						v1=r.getCell(1).getNumericCellValue();
						v2=r.getCell(2).getNumericCellValue();
						if(v2!=0.0)
							c.setCellValue((v1-v2)/v2*100);
						else
							c.setCellValue((v1-v2)*100);
						
						c = r.createCell(6);
						v1=r.getCell(4).getNumericCellValue();
						v2=r.getCell(5).getNumericCellValue();
						if(v2!=0.0)
							c.setCellValue((v1-v2)/v2*100);
						else
							c.setCellValue((v1-v2)*100);
						
					}
				}
				r = sheet.createRow(rowNum++);
				si.hm.clear();
				relDocsCount.clear();
			}
		}
		br1.close();
		wb.write(new FileOutputStream(EVAL_RESULTS_FILE));
	}
}
