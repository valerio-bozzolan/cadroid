package at.bitfire.cadroid;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

class CertificateInfoLoader extends AsyncTaskLoader<CertificateInfo> {
	private final static String TAG = "cadroid.CertificateInfoLoader";
	URL url;
	
	public CertificateInfoLoader(Context context, String authority) {
		super(context);
		
		try {
			this.url = new URL("https://" + authority);
		} catch (MalformedURLException e) {
			Log.wtf(TAG, "Invalid URL", e);
		}
	}

	@Override
	public CertificateInfo loadInBackground() {
		CertificateInfo info = new CertificateInfo();
		
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			InfoTrustManager tm = new InfoTrustManager(info);
			sc.init(null, new X509TrustManager[] { tm }, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
			urlConnection.setHostnameVerifier(new InfoHostnameVerifier(info));
			urlConnection.setSSLSocketFactory(sc.getSocketFactory());
			
			InputStream in = urlConnection.getInputStream();
			try {
				@SuppressWarnings("unused")
				int c = in.read();
				Log.i(TAG, "Read HTTPS resource successfully");
			} finally {
				in.close();
			}

		} catch (Exception e) {
			Log.w(TAG, "Exception while retrieving URL", e);
			info.errorMessage = e.getMessage();
		}

		return info;
	}
	
}
