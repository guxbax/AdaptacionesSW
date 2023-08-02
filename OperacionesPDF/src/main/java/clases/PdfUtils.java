package clases;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfUtils {

	public static byte[] unirPDFs(String doc1, String doc2) {

		ArrayList<PDDocument> documentosPDF = new ArrayList<PDDocument>();

		try {
			PDDocument pdfDocument1 = PDDocument.load((Base64.getDecoder().decode(doc1)));
			documentosPDF.add(pdfDocument1);
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}
		
		try {
			PDDocument pdfDocument2 = PDDocument.load((Base64.getDecoder().decode(doc2)));
			documentosPDF.add(pdfDocument2);
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}
		
		PDDocument pdfUnido = new PDDocument();

		for (PDDocument document : documentosPDF) {
			// Copiamos todas las páginas del documento actual en el documento unido
			for (PDPage page : document.getPages()) {
				pdfUnido.addPage(page);
			}
		}
		return toByteArray(pdfUnido);
	}
	
	public static byte[] indicePDF(byte[] mergeFile) {
		// Cargamos el PDF unido
		PDDocument pdfUnido = null;
		try {
			pdfUnido = PDDocument.load(mergeFile);
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}

		// Creamos una nueva página para el índice
		PDPage paginaIndice = new PDPage(PDRectangle.A4);
		pdfUnido.addPage(paginaIndice);

		try {
			// Creamos el contenido del índice
			PDPageContentStream contenidoIndice = new PDPageContentStream(pdfUnido, paginaIndice);
			contenidoIndice.beginText();
			contenidoIndice.setFont(PDType1Font.HELVETICA_BOLD, 12);
			contenidoIndice.newLineAtOffset(50, 700);
			contenidoIndice.showText("INDICE");
			contenidoIndice.setFont(PDType1Font.HELVETICA, 10);
	
			int numeroPagina = 1;
			contenidoIndice.newLine();
			// Aquí puedes agregar los títulos manualmente o de alguna otra manera.
			// Por ejemplo, podrías tener un ArrayList con los títulos predefinidos.
			ArrayList<String> titulos = new ArrayList<String>();
			for (int i = 0; i < pdfUnido.getNumberOfPages(); i++) {
				titulos.add("PAGINA " + i);
			}
	
			for (String titulo : titulos) {
				contenidoIndice.showText(numeroPagina + ". " + titulo);
				contenidoIndice.newLine();
				numeroPagina++;
			}
	
			contenidoIndice.endText();
			contenidoIndice.close();
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}
		return toByteArray(pdfUnido);
	}

	public static byte[] toByteArray(PDDocument pdDoc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        	try {
                pdDoc.save(out);
                pdDoc.close();
            } catch (Exception ex) {
            	System.out.println(ex.fillInStackTrace());
            }            
        return out.toByteArray();
	}
	
	public static byte[] paginacion(byte[] mergeFile) {
		// Cargamos el PDF unido
		PDDocument pdfUnido = null;
		try {
			pdfUnido = PDDocument.load(mergeFile);
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}

		int numeroPagina = 1;
		try {
			// Recorremos todas las páginas del PDF unido para agregar la numeración
			for (PDPage page : pdfUnido.getPages()) {
				// Creamos el contenido para escribir el número de página en cada página
				PDPageContentStream contenidoPagina = new PDPageContentStream(pdfUnido, page,
						PDPageContentStream.AppendMode.APPEND, true, true);
				contenidoPagina.beginText();
				contenidoPagina.setFont(PDType1Font.HELVETICA_BOLD, 10);
				contenidoPagina.newLineAtOffset(page.getMediaBox().getWidth() - 60, 40);
				contenidoPagina.showText("Página " + numeroPagina);
				contenidoPagina.endText();
				contenidoPagina.close();
	
				numeroPagina++;
			}
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}

		return toByteArray(pdfUnido);
	}

	public static byte[] escalarPDF(byte[] mergeFile) {
		 // Cargamos el PDF unido
		PDDocument pdfUnido = null;
		try {
			pdfUnido = PDDocument.load(mergeFile);
		} catch (IOException e) {
			System.out.println(e.fillInStackTrace());
		}

        // Creamos una lista para almacenar las páginas redimensionadas
        ArrayList<PDPage> paginasRedimensionadas = new ArrayList<PDPage>();

        // Creamos el objeto PDFRenderer para renderizar las páginas del PDF
        PDFRenderer pdfRenderer = new PDFRenderer(pdfUnido);

        // Recorremos todas las páginas del PDF unido para redimensionarlas
        for (PDPage page : pdfUnido.getPages()) {
            // Obtenemos el tamaño de la página original
            PDRectangle originalPageSize = page.getMediaBox();

            // Calculamos las nuevas dimensiones con la escala proporcionada
            float newWidth = originalPageSize.getWidth() * 50;
            float newHeight = originalPageSize.getHeight() * 50;

            // Creamos una nueva página con las dimensiones redimensionadas
            PDPage resizedPage = new PDPage(new PDRectangle(newWidth, newHeight));}

            // Renderizamos el contenido de la página original a una imagen
          /*  BufferedImage image = pdfRenderer.renderImageWithDPI(page.getIndex(), 300);

            // Convertimos la imagen en un objeto PDImageXObject
            PDImageXObject pdImage = LosslessFactory.createFromImage(pdfUnido, image);

            // Creamos el contenido para escribir la imagen en la página redimensionada
            PDPageContentStream contentStream = new PDPageContentStream(pdfUnido, resizedPage);
            contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
            contentStream.close();

            // Agregamos la página redimensionada a la lista
            paginasRedimensionadas.add(resizedPage);
        }

        // Creamos un nuevo documento PDF con las páginas redimensionadas
        PDDocument pdfRedimensionado = new PDDocument();
        for (PDPage page : paginasRedimensionadas) {
            pdfRedimensionado.addPage(page);
        }

        // Guardamos el PDF redimensionado en un archivo
        pdfRedimensionado.save(new File("./pdfRedimensionado.pdf"));
        pdfRedimensionado.close();*/

        return toByteArray(pdfUnido);
	}

	public static byte[] compresionPDF(byte[] mergeFile) {
		return mergeFile;
	}
}
