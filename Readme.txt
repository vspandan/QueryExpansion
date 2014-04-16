Steps to run

1. Set the properties.java with path variables depending on where the input data set is present
   and where the results need to be stored.
   
2. Run Doc2MatInput.java :This converts the dataset to format that is required by doc2mat perl script

3. Run doc2mat perl script : This generates the matrix file representation of data set
	Ex: perl E:\doc2mat-1.0\doc2mat -nostem E:\QueryEpansion_ver1_results\Output_ver1\Doc2MatInput\File E:\QueryEpansion_ver1_results\Output_ver1\Doc2MatInput\File.mat
		perl "location of perl script" -nostem "Inputfile" "Outputfile"
4. Run Cluto for clustering the documents.The last argument denotes the number of clusters to be formed
	Ex: vcluster.exe -clmethod="rb" E:\QueryEpansion_ver1_results\Output_ver1\Doc2MatInput\File.mat 150
		vcluster.exe -clmethod="rd" "Input matrix file" "No of clusters"

5. Update Properties File if needed

6. Run Cluster.java which put the documents into clusters

7. Run IndexClusters.java which indexes the clusters using lucene

8. Run tagclusters.java which associated tags to each cluster

9. Run SearchIndex.java : Enter you initial query here. Expanded queries are displayed.

For Evaluation:


1. Save all the input in text file. Start with string "Enter Query" followed by input query and augmented queries in next lines.

2. Run IndexDocuments.java which indexes the clusters using lucene

3. Run QueryDocuments.java which outputs excel sheet of precision and recall values.

Note: Change Properties.java interface accordingly