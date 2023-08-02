package clases;

public class Operaciones {
	
	public byte[] mergePdfs(String doc1, String doc2) {
		
		byte[] mergeFile = PdfUtils.unirPDFs(doc1, doc2);
		mergeFile = PdfUtils.indicePDF(mergeFile);
		mergeFile = PdfUtils.paginacion(mergeFile);
		mergeFile = PdfUtils.escalarPDF(mergeFile);
		mergeFile = PdfUtils.compresionPDF(mergeFile);
		
		return mergeFile;
	}
	
	

}
