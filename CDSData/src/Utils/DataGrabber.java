package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.pdfbox.ExtractText;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author Zhenghong Dong
 */
public class DataGrabber {
	private static final String[]	subIndexlist	= { "FIN", "INDU", "ENRG", "CONS", "TMT" };
	private List<String>			_fileName;

	DataGrabber(List<String> fileName) {
		_fileName = fileName;
	}

	public void PDFToTxt() throws Exception {
		String[] input = new String[ 1 ];
		for (ListIterator<String> iterator = _fileName.listIterator(); iterator.hasNext();) {
			input[ 0 ] = iterator.next();
			ExtractText.main( input );
			iterator.set( input[ 0 ].replace( "pdf", "txt" ) );
		}
	}

	public void TxtTickerToXls(String outFile) throws Exception {
		FileOutputStream fileOut = null;
		BufferedReader in = null;
		List<List<? extends PoiRecord>> tickers = new ArrayList<>();
		Workbook wb = null;
		Sheet sheet = null;
		CreationHelper createHelper = null;
		try {
			wb = new HSSFWorkbook();
			sheet = wb.createSheet( "ticker" );
			createHelper = wb.getCreationHelper();
			Row row = sheet.createRow( (short) 0 );

			for (int i = 0; i < _fileName.size(); i++) {
				String index = _fileName.get( i );
				index = index.substring( index.lastIndexOf( "\\" ) + 1, index.lastIndexOf( "." ) - 1 );
				row.createCell( i * 2 ).setCellValue( createHelper.createRichTextString( index ) );
			}

			fileOut = new FileOutputStream( outFile );
			wb.write( fileOut );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String str = null;
		try {
			for (int i = 0; i < _fileName.size(); i++) {
				in = new BufferedReader( new FileReader( _fileName.get( i ) ) );
				List<tickerRecord> tmp = new ArrayList<>();
				while ((str = in.readLine()) != null) {
					if (Character.isDigit( str.charAt( 0 ) )) {
						if (StringUtils.firstOccuranceOfArray( str, subIndexlist ) == -1) continue;
						// get company name
						String name = str.substring( str.indexOf( " " ) + 1,
								StringUtils.firstOccuranceOfArray( str, subIndexlist ) - 1 );
						// get company name without corp,llc,inc stuff
						if (name.lastIndexOf( " " ) > -1) {
							name = name.substring( 0, name.lastIndexOf( " " ) );
						}
						// get rid of last posible ,
						if (name.endsWith( "," )) {
							name = name.substring( 0, name.lastIndexOf( "," ) );
						}
						tmp.add( new tickerRecord( name + " CDS SR 5Y CORP" ) );
					}
				}
				tickers.add( tmp );
			}

			final List<Integer> sizeList = new ArrayList<>();
			for (int i = 0; i < _fileName.size(); i++) {
				sizeList.add( 1 );	
			}

			WriteXls.appendMultipleRecords( outFile, "ticker", tickers, sizeList );

		} catch (IOException e) {
			System.err.println( "Fail to parse file " + _fileName );
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			System.err.println( "Fail to parse file on line:" + str );
			e.printStackTrace();
		} finally {
			in.close();
			fileOut.close();
		}

	}

	class tickerRecord implements PoiRecord {
		String	name;

		tickerRecord(String n) {
			name = n;
		}

		public int size() {
			return 1;
		}

		@Override
		public void writeNextForMultipleRecords(Workbook wb, Row row, int index) {
			int i = index;
			final CreationHelper createHelper = wb.getCreationHelper();
			row.createCell( i++ ).setCellValue( createHelper.createRichTextString( name ) );
		}

		@Override
		public int writeNextForSingleRecord(Workbook wb, Sheet sheet, int rowNum) {
			return 0;
		}
	}

	public static void main(String[] args) throws Exception {

		List<String> pdfs = new ArrayList<>();
		pdfs.add( "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\CDX NA IG 21 V1.pdf" );
		pdfs.add( "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\CDX.NA.IG.ENRG.21 V1.pdf" );

		DataGrabber grabber = new DataGrabber( pdfs );
		// pdf to txt
		grabber.PDFToTxt();

		// tickerFromTxt
		String tickerFile = "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\cds competition\\CDS Data.xls";
		grabber.TxtTickerToXls( tickerFile );
	}

}
