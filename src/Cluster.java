import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Date;

public class Cluster implements Properties {

	public void generateCLusterFIles(String ClusMapFIle, String DocFile,
			String outputFolderPath, int noOfClusters) throws IOException {
		File file[] = new File[noOfClusters];
		File outputFolder = new File(outputFolderPath);
		if (!outputFolder.exists()) {
			outputFolder.mkdirs();
		} else {
			File[] files = outputFolder.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
		for (int i = 0; i < noOfClusters; i++) {

			DecimalFormat formatter = new DecimalFormat("000");
			file[i] = new File(outputFolderPath + "\\Cluster"
					+ formatter.format(i));
			file[i].createNewFile();
		}
		File clusterMappingFIle = new File(ClusMapFIle);
		File dataSetFile = new File(DocFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(clusterMappingFIle)));
		BufferedReader br1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(dataSetFile)));
		String ClusterNo = null;

		FileWriter fw = null;
		String temp = null;
		while (true) {
			ClusterNo = br.readLine();
			if (ClusterNo == null) {
				break;
			}
			int clusterNo = Integer.parseInt(ClusterNo);
			temp = br1.readLine();
			if (clusterNo != -1) {
				fw = new FileWriter(file[clusterNo], true);
				if (temp != null) {
					fw.write(temp);
					fw.write(10);
				}
				fw.close();
			}

		}
		br.close();
		br1.close();
	}

	public static void main(String[] args) throws IOException {

		try {
			Date d = new Date();
			Cluster cluster = new Cluster();
			cluster.generateCLusterFIles(CLUST_MAT__REP_FILE,
					DOC2MATINPUT_FILE, CLUSTERS_FOLDER,
					NO_OF_CLUSTERS);

			System.out.println("Clustered in "
					+ (new Date().getTime() - d.getTime()) + "Seconds");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
