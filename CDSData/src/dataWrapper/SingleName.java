package dataWrapper;

import java.util.List;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Utils.PoiRecord;

/**
 * @author Zhenghong Dong
 */
public class SingleName implements PoiRecord {
	private String									_ticker;
//	private LinkedHashMap<String, List<CDSFields>>	_price;

	// private int _count;
	// private String _lastDay;

	// private static final int smoothWindowSize = 7;

	public SingleName(String ticker) {
		_ticker = ticker;
	//	_price = new LinkedHashMap<>();
		// _count = 0;
	}

	public void addRecord(final String date, final List<CDSFields> price) throws Exception {
//		_price.put( date, price );
	}

	public static int size() {
		return 1;
	}

	@Override
	public void writeNextForMultipleRecords(final Workbook wb, final Row row, final int index) {
		int i = index;
		final CreationHelper createHelper = wb.getCreationHelper();
		row.createCell( i++ ).setCellValue( createHelper.createRichTextString( _ticker ) );
	}

	@Override
	public int writeNextForSingleRecord(final Workbook wb, final Sheet sheet, final int rowNum) {
		return 0;
	}

	public String getTicker() {
		return _ticker;
	}

/*	public LinkedHashMap<String, List<CDSFields>> getPrice() {
		return _price;
	}*/
}
