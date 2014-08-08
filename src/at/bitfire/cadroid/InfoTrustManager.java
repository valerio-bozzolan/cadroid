package at.bitfire.cadroid;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import android.util.Log;

public class InfoTrustManager implements X509TrustManager {
	private static String TAG = "CAdroid.MyTrustManager";
	
	CertificateInfo info;
	
	
	InfoTrustManager(CertificateInfo info) {
		this.info = info;
	}
	

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		throw new CertificateException("Client certificates are not supported");
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		Log.d(TAG, "checkServerTrusted");
		
		if (chain.length != 1)
			throw new CertificateException("Currently, only self-signed certificates are supported.");
		
		info.certificate = chain[0];
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		Log.d(TAG, "getAcceptedIssuers");
		return null;
	}

}
