package dataWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Utils.ParseDate;
import Utils.PoiRecord;

/**
 * @author Zhenghong Dong
 */
public class Index implements PoiRecord {
	private final String			_ticker;
	private final HashSet<String>	_comanyList;
	// private final LinkedHashMap<String, List<CDSFields>> _price;
	private final long				_startDate;
	private int						_num;
	private List<String>			_missingCompany;
	private List<String>			_missingCDS;

	public Index(final String ticker, final String startDate) {
		_ticker = ticker;
		// _price = new LinkedHashMap<>();
		_startDate = ParseDate.longFromStandard( ParseDate.standardFromStringMonthTypeTwo( startDate ) );
		_comanyList = new HashSet<>();
		_num = 0;
		_missingCompany = new ArrayList<>();
		_missingCDS = new ArrayList<>();
	}

	public void addCompany(final String name) {
		_comanyList.add( name );
		_num++;
	}

	@Override
	public void writeNextForMultipleRecords(final Workbook wb, final Row row, final int index) {}

	@Override
	public int writeNextForSingleRecord(final Workbook wb, final Sheet sheet, int j) {
		final CreationHelper createHelper = wb.getCreationHelper();
		j = 0;
		Row row = sheet.createRow( 0 );
		// add header, now I disable this function. To enable, change j = 0 to j = 1
		row.createCell( 0 ).setCellValue( createHelper.createRichTextString( "StartDate:" ) );
		row.createCell( 1 ).setCellValue( _startDate );
		row.createCell( 2 ).setCellValue( createHelper.createRichTextString( "NumberComponents:" ) );
		row.createCell( 3 ).setCellValue( _num );

		for (final String company : _comanyList) {
			// only write those records with movement negative, those who we are really interested in
			row = sheet.createRow( j++ );
			row.createCell( 0 ).setCellValue( createHelper.createRichTextString( company ) );
		}
		return j;
	}

	public void addMissingCompany(String ticker) {
		_missingCompany.add( ticker );
	}

	public void addMissingCDS(String ticker) {
		_missingCDS.add( ticker );
	}
	
	public String getTicker() {
		return _ticker;
	}

	public int getNumberComponents() {
		return _num;
	}

	public Set<String> getCompanySet() {
		return _comanyList;
	}

	public long getStartDate() {
		return _startDate;
	}
	
	public List<String> getMissingCompany() {
		return _missingCompany;
	}
	
	public List<String> getMissingCDS() {
		return _missingCDS;
	}

}
