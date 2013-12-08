package dataWrapper;

/**
 * @author Zhenghong Dong
 */
public class CDSFields {

	private final float _ask;
	private final float _last;
	public CDSFields(float a, float l) {
		_ask = a;
		_last = l;
	}
	
	public float getAsk() { return _ask; }
	public float getLast() { return _last; }
	
	public boolean isNull() { return getAsk() == 0 || getLast() == 0; }

}
