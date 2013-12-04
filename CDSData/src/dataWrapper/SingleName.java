package dataWrapper;

import java.util.LinkedHashMap;
import java.util.List;

import Utils.CollectionUtils;

/**
 * @author Zhenghong Dong
 */
public class SingleName {
	private final String						_name;
	private LinkedHashMap<String, List<Double>>	_price;
	private int									_count;
	private String								_lastDay;
	private static final int					smoothWindowSize	= 7;

	public SingleName(String name) {
		_name = name;
		_price = new LinkedHashMap<>();
		_count = 0;
	}

	public void addRecord(final String date, final List<Double> price) throws Exception {
		if (++_count == 0) {
			_price.put( date, price );
			_lastDay = date;
		} else if (_count < smoothWindowSize) {
			_price.put( _lastDay, CollectionUtils.addDoubleList( _price.get( _lastDay ), price ) );
		} else {
			_price.put( _lastDay, CollectionUtils.divideDoubleList( _price.get( _lastDay ), smoothWindowSize ) );
			_count = 0;
		}
	}

	public String getName() {
		return _name;
	}

	public LinkedHashMap<String, List<Double>> getPrice() {
		return _price;
	}
}
