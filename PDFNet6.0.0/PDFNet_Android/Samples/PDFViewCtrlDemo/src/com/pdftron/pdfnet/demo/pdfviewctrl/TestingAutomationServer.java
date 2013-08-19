package com.pdftron.pdfnet.demo.pdfviewctrl;

/**
 * Created by IntelliJ IDEA.
 * User: iroth
 * Date: 7/24/13
 * Time: 1:13 PM
 */

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class TestingAutomationServer extends NanoHTTPD {

	private TestingAutomationCallbackInterface _callbackHandler;
	private boolean _inMeasurement;
	private ArrayList<Measurement> _measurements;
	private long _startTime;

	public TestingAutomationServer(TestingAutomationCallbackInterface handler) {
		super(8080);
		_callbackHandler = handler;
		_inMeasurement = false;
	}

	public void startMeasurements() {
		_inMeasurement = true;
		_measurements = new ArrayList<Measurement>();
		_startTime = System.currentTimeMillis();
		addMeasurement("start");
	}

	public void endMeasurements() {
		if (_inMeasurement) {
			addMeasurement("end");
			_inMeasurement = false;
		}
	}

	public void addMeasurement(String label) {
		if (_inMeasurement) {
			long delta = System.currentTimeMillis() - _startTime;
			_measurements.add(new Measurement(label, delta));
		}
	}

	private String oneMeasurementToHTML(Measurement m) {
		return String.format("<tr><td>%s</td><td>%d</td></tr>\n", m.label, m.time);
	}

	private String measurementsToHTML() {
		StringBuilder sb = new StringBuilder();
		sb = sb.append("<table><tr><th>Label</th><th>Time</th></tr>\n");
		for (Measurement m : _measurements) {
			sb = sb.append(oneMeasurementToHTML(m));
		}
		sb = sb.append("</table>");
		return sb.toString();
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("httpd", ex.toString());
		}
		return null;
	}

	@Override
	public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
		for (Map.Entry<String, String> kv : parms.entrySet()) {
			String key = kv.getKey();
			String strValue = kv.getValue();
			if (key.compareTo("open") == 0) {
				_callbackHandler.TA_openDoc(strValue);
				break;
			}
			else if (key.compareTo("address") == 0) {
				_callbackHandler.TA_openDoc(strValue);
				String htmlFlip = String.format("<html><head><head><body><h1>Address %s</h1></body></html>", getLocalIpAddress());
				return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, htmlFlip);
			}
			else if (key.compareTo("flip") == 0) {
				try {
					int val = Integer.parseInt(strValue);
					startMeasurements();
					_callbackHandler.TA_flipToPage(val);
					while (_inMeasurement) {
						try {
							Thread.sleep(1L, 0);
							long delta = System.currentTimeMillis() - _startTime;
							if (delta > 8000L) { // timeout after 8 seconds
								addMeasurement("timeout");
								endMeasurements();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
						}
					}
					String htmlFlip = String.format("<html><head><head><body><h1>Page Flip %d</h1>%s</body></html>", val, measurementsToHTML());
					return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, htmlFlip);
				} catch(NumberFormatException nfe) {
					Log.d("httpd", "failed parsing page num: " + strValue);
					break;
				}
			}
			else {
				Log.d("httpd", "Invalid param: " + key);
			}
		}

		final String html = "<html><head><head><body><h1>Hello, World</h1></body></html>";
		return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, html);
	}

	public interface TestingAutomationCallbackInterface {
		void TA_openDoc(String doc);
		void TA_flipToPage(int page);
	}

	private class Measurement {
		public String label;
		public long time;

		private Measurement(String label, long time) {
			this.label = label;
			this.time = time;
		}
	}
}
