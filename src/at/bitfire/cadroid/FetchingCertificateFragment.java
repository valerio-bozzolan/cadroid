package at.bitfire.cadroid;

import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FetchingCertificateFragment extends DialogFragment implements LoaderCallbacks<CertificateInfo> {
	final static String EXTRA_AUTHORITY = "authority";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
		setCancelable(false);
		
		Loader<CertificateInfo> loader = getLoaderManager().initLoader(0, getArguments(), this);
		if (savedInstanceState == null)		// http://code.google.com/p/android/issues/detail?id=14944
			loader.forceLoad();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_query_server, container, false);
		return v;
	}


	@Override
	public Loader<CertificateInfo> onCreateLoader(int id, Bundle args) {
		return new CertificateInfoLoader(getActivity(), args.getString(EXTRA_AUTHORITY));
	}

	@Override
	public void onLoadFinished(Loader<CertificateInfo> loader, CertificateInfo info) {
		
		if (info.getErrorMessage() == null) {
			MainActivity main = (MainActivity)getActivity();
			main.setCertificateInfo(info);
			main.showFragment(VerifyFragment.TAG, true);
		} else
			Toast.makeText(getActivity(), info.getErrorMessage(), Toast.LENGTH_LONG).show();
		
		getDialog().dismiss();
	}
	
	@Override
	public void onLoaderReset(Loader<CertificateInfo> loader) {
	}

}
