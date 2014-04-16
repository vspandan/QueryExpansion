import org.apache.lucene.analysis.Analyzer;
/*import org.apache.lucene.analysis.core.StopAnalyzer;
 import org.apache.lucene.analysis.miscellaneous.PatternAnalyzer;
 */
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

@SuppressWarnings({ "deprecation" })
public class IndexClusters implements Properties{

	public void index(String docsPath, String indexPath, boolean create)
			throws IOException {

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out
					.println("Document directory '"
							+ docDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		/*
		 * Analyzer analyzer = new PatternAnalyzer(Version.LUCENE_CURRENT,
		 * PatternAnalyzer.NON_WORD_PATTERN, true,
		 * StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		 */
		Analyzer analyzer = new POSAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_CURRENT,
				analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		iwc.setCodec(new SimpleTextCodec());

		if (create) {
			iwc.setOpenMode(OpenMode.CREATE);
		} else {
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}

		File directory = new File(indexPath);
		if (!directory.exists())
			directory.mkdirs();
		Directory dir = FSDirectory.open(directory);
		IndexWriter writer = new IndexWriter(dir, iwc);

		if (docDir.isDirectory()) {
			String[] files = docDir.list();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File file = new File(docDir, files[i]);
					FileInputStream fis = new FileInputStream(file);

					Document doc = new Document();

					Field pathField = new StringField("path", file.getPath(),
							Field.Store.YES);
					doc.add(pathField);

					Field text = new Field("content", new BufferedReader(
							new InputStreamReader(fis)),
							Field.TermVector.WITH_POSITIONS_OFFSETS);
					doc.add(text);

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						System.out.println("indexing " + file);
						writer.addDocument(doc);
					} else {
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}
					fis.close();
				}
			}
		}
		writer.close();

	}

	public static void main(String[] args) {
		try {
			Date d = new Date();
			IndexClusters indexFiles = new IndexClusters();
			indexFiles.index(CLUSTERS_FOLDER, LUCENE_OP_FOLDER,
					true);
			System.out.println("Indexed in "
					+ (new Date().getTime() - d.getTime()) + "Seconds");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
