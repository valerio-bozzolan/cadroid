package at.bitfire.cadroid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class VerifyFragment extends Fragment {
	public static final String TAG = "cadroid.VerifyFragment";
	
	CertificateInfo info;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_verify, container, false);
		setHasOptionsMenu(true);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View v = getView();
		
		MainActivity main = (MainActivity)getActivity();
		main.onShowFragment(TAG);
		
		info = main.getCertificateInfo();
		X509Certificate cert = info.certificate;
		
		
		// show certificate details
		
		String cn = cert.getSubjectDN().getName();
		((TextView)v.findViewById(R.id.cert_cn)).setText(cn);
		
		String serialNo = cert.getSerialNumber().toString(16);
		((TextView)v.findViewById(R.id.cert_serial)).setText(serialNo);

		((TextView)v.findViewById(R.id.cert_sig_sha1)).setText(getSignature(cert, "SHA-1"));
		((TextView)v.findViewById(R.id.cert_sig_md5)).setText(getSignature(cert, "MD5"));
		
		((TextView)v.findViewById(R.id.cert_valid_from)).setText(cert.getNotBefore().toString());
		((TextView)v.findViewById(R.id.cert_valid_until)).setText(cert.getNotAfter().toString());

		int basicConstraints = cert.getBasicConstraints();
		String basicConstraintsInfo;
		switch (basicConstraints) {
		case -1:
			basicConstraintsInfo = "n/a";
			break;
		case Integer.MAX_VALUE:
			basicConstraintsInfo = "Unlimited path length";
			break;
		default:
			basicConstraintsInfo = "Max path length: " + basicConstraints;
		}
		((TextView)v.findViewById(R.id.cert_basic_constraints)).setText(basicConstraintsInfo);
		
		
		// show alerts
		
		// host name not matching
		TextView tvHostnameNotMatching = (TextView)v.findViewById(R.id.cert_hostname_not_matching);
		if (info.hostNameVerified)
			tvHostnameNotMatching.setVisibility(View.GONE);
		else {
			tvHostnameNotMatching.setText(getResources().getString(R.string.hostname_not_matching, info.hostName));
			tvHostnameNotMatching.setVisibility(View.VISIBLE);
		}
		
		// expired / not yet valid
		v.findViewById(R.id.cert_currently_not_valid).setVisibility(info.currentlyValid() ? View.GONE : View.VISIBLE);
		
		// basic constraints not set
		v.findViewById(R.id.cert_basic_constraints_not_set).setVisibility(info.basicConstraintsValid() ? View.GONE : View.VISIBLE);
	}

	
	// options menu (action bar)
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.simple_next, menu);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		boolean ok = false;
		if (info != null)
			ok = info.hostNameVerified && info.basicConstraintsValid();
		menu.findItem(R.id.next).setEnabled(ok);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.next:
			((MainActivity)getActivity()).showFragment(ExportFragment.TAG, true);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	
	// private methods
	
	private String getSignature(X509Certificate cert, String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(cert.getEncoded());
			String sig = "";
			for (byte b : digest.digest())
				sig += Integer.toHexString(b & 0xFF);
			return sig;
		} catch (NoSuchAlgorithmException e) {
			return "(algorithm not available on device)";
		} catch (CertificateEncodingException e) {
			return "(couldn't encode certificate)";
		}
	}
	
}
