/**
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 *
 * simple test case for {@link PdService}
 * 
 */

package org.puredata.android.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class PdTest extends Activity implements OnClickListener, OnEditorActionListener, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String PD_TEST = "Pd Test";

	private CheckBox left, right, mic;
	private EditText msg;
	private Button prefs;
	private TextView logs;

	private PdService pdService = null;
	private String patch = null;

	private void toast(final String msg) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), PD_TEST + ": " + msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void post(final String s) {
		runOnUiThread(new Runnable() {
			public void run() {
				logs.append(s + ((s.endsWith("\n")) ? "" : "\n"));
			}
		});
	}

	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			toast("Pure Data says, \"" + msg + "\"");
		}

		public void print(String s) {
			post(s);
		}

		public void receiveBang(String source) {
			pdPost("bang");
		}

		public void receiveFloat(String source, float x) {
			pdPost("float: " + x);
		}

		public void receiveList(String source, Object... args) {
			pdPost("list: " + Arrays.toString(args));
		}

		public void receiveMessage(String source, String symbol, Object... args) {
			pdPost("message: " + Arrays.toString(args));
		}

		public void receiveSymbol(String source, String symbol) {
			pdPost("symbol: " + symbol);
		}
	};

	private final ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			initPd();
		}

		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PdPreferences.initPreferences(getApplicationContext());
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
		initGui();
		bindService(new Intent(this, PdService.class), connection, BIND_AUTO_CREATE);		
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		startAudio();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		boolean bl = left.isChecked();
		boolean br = right.isChecked();
		boolean bm = mic.isChecked();
		CharSequence msgc = msg.getText();
		CharSequence logsc = logs.getText();
		initGui();
		left.setChecked(bl);
		right.setChecked(br);
		mic.setChecked(bm);
		msg.setText(msgc);
		logs.setText(logsc);
	}

	private void initGui() {
		setContentView(R.layout.main);
		left = (CheckBox) findViewById(R.id.left_box);
		left.setOnClickListener(this);
		right = (CheckBox) findViewById(R.id.right_box);
		right.setOnClickListener(this);
		mic = (CheckBox) findViewById(R.id.mic_box);
		mic.setOnClickListener(this);
		msg = (EditText) findViewById(R.id.msg_box);
		msg.setOnEditorActionListener(this);
		prefs = (Button) findViewById(R.id.pref_button);
		prefs.setOnClickListener(this);
		logs = (TextView) findViewById(R.id.log_box);
		logs.setMovementMethod(new ScrollingMovementMethod());
	}

	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.setReceiver(receiver);
			PdBase.subscribe("android");
			InputStream in = res.openRawResource(R.raw.test);
			patchFile = IoUtils.extractResource(in, "test.pd", getCacheDir());
			patch = PdUtils.openPatch(patchFile);
			startAudio();
		} catch (IOException e) {
			Log.e(PD_TEST, e.toString());
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}

	private void startAudio() {
		String name = getResources().getString(R.string.app_name);
		try {
			pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
			pdService.startAudio(new Intent(this, PdTest.class), R.drawable.icon, name, "Return to " + name + ".");
		} catch (IOException e) {
			toast(e.toString());
		}
	}

	private void cleanup() {
		if (patch != null) PdUtils.closePatch(patch);
		PdBase.release();
		try {
			unbindService(connection);
		} catch (IllegalArgumentException e) {
			// already unbound
			pdService = null;
		}
	}

	@Override
	public void finish() {
		cleanup();
		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pd_test_menu, menu);
		return true;
	} 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_item:
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle(R.string.about_title);
			ad.setMessage(R.string.about_msg);
			ad.setNeutralButton(android.R.string.ok, null);
			ad.setCancelable(true);
			ad.show();
			break;
		default:
			break;
		}
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_box:
			PdBase.sendFloat("left", left.isChecked() ? 1 : 0);
			break;
		case R.id.right_box:
			PdBase.sendFloat("right", right.isChecked() ? 1 : 0);
			break;
		case R.id.mic_box:
			PdBase.sendFloat("mic", mic.isChecked() ? 1 : 0);
			break;
		case R.id.pref_button:
			startActivity(new Intent(this, PdPreferences.class));
			break;
		default:
			break;
		}
	}

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		evaluateMessage(msg.getText().toString());
		return true;
	}

	private void evaluateMessage(String s) {
		String dest = "test", symbol = null;
		boolean isAny = s.length() > 0 && s.charAt(0) == ';';
		Scanner sc = new Scanner(isAny ? s.substring(1) : s);
		if (isAny) {
			if (sc.hasNext()) dest = sc.next();
			else {
				toast("Message not sent (empty recipient)");
				return;
			}
			if (sc.hasNext()) symbol = sc.next();
			else {
				toast("Message not sent (empty symbol)");
			}
		}
		List<Object> list = new ArrayList<Object>();
		while (sc.hasNext()) {
			if (sc.hasNextInt()) {
				list.add(new Float(sc.nextInt()));
			} else if (sc.hasNextFloat()) {
				list.add(sc.nextFloat());
			} else {
				list.add(sc.next());
			}
		}
		if (isAny) {
			PdBase.sendMessage(dest, symbol, list.toArray());
		} else {
			switch (list.size()) {
			case 0:
				PdBase.sendBang(dest);
				break;
			case 1:
				Object x = list.get(0);
				if (x instanceof String) {
					PdBase.sendSymbol(dest, (String) x);
				} else {
					PdBase.sendFloat(dest, (Float) x);
				}
				break;
			default:
				PdBase.sendList(dest, list.toArray());
				break;
			}
		}
	}
}
