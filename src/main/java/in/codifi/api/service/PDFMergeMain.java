package in.codifi.api.service;
import java.io.File;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFMergeMain {

    public static void main(String[] args) {
        // You can replace this with your actual path or load from properties
        String pennyDropPath = "C:\\Users\\kgowt\\Downloads\\_1207600000055997_encryptTempSigned (1).pdf";
        String secondDropPath=  "C:\\Users\\kgowt\\Downloads\\30240_6 Month Bank Statement.pdf";
        String outputPath = "C:\\Users\\kgowt\\Downloads\\Test04.pdf";

        // Load the main PDF document
        File secondDropFile = new File(secondDropPath);
		PDDocument document = PDDocument.load(secondDropFile);
		// Load the PDF to append
		File pennyDropFile = new File(pennyDropPath);
		PDDocument combine1 = PDDocument.load(pennyDropFile);

		// Merge the document
		
		document.setAllSecurityToBeRemoved(true); // Optional, helps with some PDF protections
		PDFMergerUtility merger = new PDFMergerUtility();
		merger.appendDocument(combine1,document);
		merger.mergeDocuments(null); // null = default memory settings

		// Save the merged document
		
		document.save(outputPath);

		// Close documents
		combine1.close();
		document.close();

		System.out.println("PDF merged successfully and saved to: " + outputPath);
    }
}
