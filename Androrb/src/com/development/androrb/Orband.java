/*
 * OrbAnd 0.1 Alpha
 * 
 * Copyright (C) 2009 Christian Albert Müller
 * http://www.christian-albert-mueller.com
 *
 * This Source Code is Freeware, OpenSource as you like to call it.
 * I do not take any responsibility for its usage and also I dont have
 * Time to explain the Source.
 * 
 * If you like to Develop this Version you are welcome to send me your
 * Updates and I will check them and keep the right to publish it for
 * other Users on my Page.
 * 
 * If you can make some Explanations for other Users may be helpful.
 * 
 * Should you distribute this Version you need a Commercial Key from Orb
 * 
 * I am not related with ORB and its not an official Work from ORB
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * You can extend this Copyright with your Changes and keep Responsible
 * for your Work.
 * 
 *
 * To start:
 * - go to: https://mycast.orb.com/orb/html/createAPIKey.html
 *   - Enter your ORB Login and request an Developer API Key
 *   - Enter your Login, Password and API Key into the Source Code Bellow
 *   - Compile the Source ... and Enjoy Orb on your Android Mobile :)
 *
 * Infos about Error Codes and XML Requests at: http://developer.orb.com
 *
 */

package com.development.androrb;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Orband extends ListActivity
{
	/** Called when the activity is first created. */

	private static final String TAG = "Orband";

	String html;
	String dummystr;
	String sid;
	String TextUpdate;
	String mediakind = "video";
	String loadingmediadata = "Loading Media Data";
	int globalcount, globalstart, listcount;

	// Initializing the ListView
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mStrings = new ArrayList<String>();

	// Just a small Status Line in the Top
	TextView toptext, splashtext;
	ImageView splashimage;
	static int countlistentries;
	static Bitmap[] inipics;
	int aresults;

	// ArrayList for Orb Responses (MediaTitle, Url ...)
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> item = new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//Intent getIntent = getIntent();
		mediakind = getIntent().getStringExtra("mediakind");
		sid = getIntent().getStringExtra("sid");

		// globalstart=Integer.parseInt(getIntent().getStringExtra("start"));
		dummystr = getIntent().getStringExtra("start");
		globalstart = Integer.parseInt(dummystr);
		
		dummystr = getIntent().getStringExtra("max");
		globalcount = Integer.parseInt(dummystr);
		
		//Load Preferences
	    //SharedPreferences preferences = getPreferences(MODE_WORLD_READABLE);
	    //globalcount = Integer.parseInt(preferences.getString("MAX", "10"));


		splashtext = (TextView) findViewById(R.id.splashload);
		splashimage = (ImageView) findViewById(R.id.splashscreen);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStrings);

		setListAdapter(mAdapter);
		ListView MyOrbList = getListView();

		MyOrbList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id)
			{

				if (list.get(position).get("more") != "1")
				{
					String Mid = list.get(position).get("id");
					String rurl = "http://api.orb.com/orb/xml/stream?sid="
							+ sid + "&mediumId=" + Mid
							+ "&streamFormat=3gp&type=pda&width=480&height=360";
					
					Intent mainIntent = new Intent(Orband.this,
							startplayer.class);
					mainIntent.putExtra("rurl", rurl);
					mainIntent.putExtra("sid", sid);
					mainIntent.putExtra("mid", Mid);
					mainIntent.putExtra("mediakind", mediakind);
					
					Orband.this.startActivity(mainIntent);
				} else
				{
					Intent mainIntent = new Intent(Orband.this, Orband.class);
					mainIntent.putExtra("mediakind", mediakind);
					mainIntent.putExtra("sid", sid);
					mainIntent.putExtra("start", ""
							+ (globalstart + globalcount));
					mainIntent.putExtra("max", "" + globalcount);
					Orband.this.startActivity(mainIntent);
				}
			}
		});

		checkUpdate.start();

	}

	private Thread checkUpdate = new Thread()
	{
		public void run()
		{
			try
			{
				do_main();
			} catch (Exception e)
			{
			}

		}

	};

	private void do_main()
	{
		TextUpdate = "Refreshing Session...";
		runOnUiThread(showTextUpdate);

		// Log.i(TAG, " *------ Refreshing Session -----*: ");
		// html =
		// loaddata("http://api.orb.com/orb/xml/session.login?apiKey="+apikey+"&l="+Username+"&password="+Password+"");

		// int status = Integer.parseInt(GetXmlInnerNr("status", "code", html,
		// 1));
		// sid = GetXmlNr("orbSessionId", html, 1);

		int status;

		TextUpdate = loadingmediadata + " (0 kb)";
		runOnUiThread(showTextUpdate);

		//Log.i(TAG, " *------ Loading Media Data-----*: ");
		html = loaddata("http://api.orb.com/orb/xml/media.search?sid=" + sid
				+ "&q=mediaType%3D" + mediakind + "&sortBy=title&start="
				+ globalstart + "&count=" + globalcount + "");
		status = Integer.parseInt(GetXmlInnerNr("status", "code", html, 1));

		if (status != 0)
		{
			
			if (status == 1)
			{
			    TextUpdate="> Unhandled exception, unknown error <";
			}
			else if (status == 2)
			{
				TextUpdate="> Session expired <";	
			}
			else if (status == 3)
			{
				TextUpdate="> Invalid Parameters <";	
		    }
			else if (status == 4)
			{
				TextUpdate="> Permission denied <";	
		    }
			else if (status == 5)
			{
				TextUpdate="Wrong login/password - ";
				TextUpdate=TextUpdate+"Press MENU for Setup";
		    }
			else if (status == 6)
			{
				TextUpdate="Wrong API-Key - ";	
				TextUpdate=TextUpdate+"Press MENU for Setup";
		    }
			else if (status == 7)
			{
				TextUpdate="> Invalid Session <";	
		    }
			else if (status == 8)
			{
				TextUpdate="> User login already exists <";	
		    }
			else if (status == 9)
			{
			    TextUpdate="Invalid password - ";	
			    TextUpdate=TextUpdate+"Press MENU for Setup";
		    }
			else if (status == 101)
			{
			    TextUpdate="> User PC (Orb) not connected <";	
		    }
			else if (status != 0)
			{   
				TextUpdate = "> Search Error : " + status+" <";
			    TextUpdate=TextUpdate+"\n(see https://developer.orb.com/wiki/Error_codes)";	
		    }
			
			runOnUiThread(showTextUpdate);
			
		} else
		{
			aresults = Integer.parseInt(GetXmlInnerNr("searchResult",
					"itemCount", html, 1));
			runOnUiThread(showUpdate);

		}

	}

	private Runnable showTextUpdate = new Runnable()
	{
		public void run()
		{
			splashtext.setText(TextUpdate);
		}
	};

	private Runnable showUpdate = new Runnable()
	{
		public void run()
		{
			//Log.i(TAG, " *------ Parsing Media Data: ");

			splashtext.setText(aresults + " Results found");
			splashimage.setVisibility(View.GONE);
			int intcounter = 0;
			for (int i = 1; i < globalcount + 1; i++)
			{
				dummystr = GetXmlNr("field name=\"title\"", html, i);
				if (dummystr != "!NULL!")
				{
					if (dummystr.length() > 20)
						dummystr = dummystr.substring(0, 20) + " ...";

					mAdapter.add((globalstart + i) + ": " + dummystr);
					
					item = new HashMap<String, String>();
					item.clear();
					//item.put("title", dummystr);
					item.put("id", GetXmlInnerNr("item", "orbMediumId", html, i));
					//item.put("id","xx");
					list.add(item);
					
					intcounter++;
				} else
				{
					break;
				}
			}

			if ((intcounter + globalstart) < aresults)
			{
				mAdapter.add(">> More Items");
				item = new HashMap<String, String>();
				item.clear();
				item.put("more", "1");
				list.add(item);
			}

			//Log.i(TAG, " *------- DONE -------* ");
			mAdapter.notifyDataSetChanged();

		}
	};

	private String loaddata2(String Urli)
	{
		String html2="";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(Urli);
		try
		{
		HttpResponse response = client.execute(request);
		html2 =  TextHelper.GetText(response);
		}
		catch(Exception ex)
		{
		//content.setText(”Fail!”);
		}
		return html2;
		}
	
	private String loaddata(String Urli)
	{
		try
		{
			//Log.i(TAG, " *------ Load Data: "+Urli);
			URLConnection conn;
			conn = new URL(Urli).openConnection();

			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(8192);

			// loading part
			int current = 0;
			int countix = 0;
			int kbread = 0;
			while ((current = bis.read()) != -1)
			{
				countix++;
				baf.append((byte) current);

				if (countix >= 1000)
				{
					countix = 0;
					kbread++;
					TextUpdate = loadingmediadata + " (" + kbread + " kb)";
					runOnUiThread(showTextUpdate);
				}

			}
			// Log.i(TAG, " *------ Load Data done -----*: ");
			html = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			// ------
		} catch (Exception e)
		{
			TextUpdate = "Ups, check your Connection ...";
			runOnUiThread(showTextUpdate);
		}

		return html;
	}

	public String GetXmlNr(String xtag, String xhtml, int pos)
	{
		// Log.i(TAG, " *xhtml: " + xhtml);
		String x1tag = "";
		String x2tag = "";
		String retstr = "";
		int xstart = 0;
		int dummy = 0;
		int xend = 0;
		int xcont = 0;
		int xcounter = 0;

		x1tag = "<" + xtag;
		dummy = xtag.indexOf(" ");
		if (dummy != -1)
			xtag = xtag.substring(0, dummy);
		x2tag = "</" + xtag + ">";

		do
		{
			xcounter++;
			xhtml = xhtml.substring(xcont);	
			xstart = xhtml.indexOf(x1tag);
			if (xstart < 0)
				return "!NULL!";
			
			xcont=xstart+1;
		
		} while (xcounter < pos);			
			
			xstart = xhtml.indexOf(">", xstart);
			if (xstart < 0)
				return "!NULL!";
			xend = xhtml.indexOf(x2tag, xstart);
			if (xend < 0)
				return "!NULL!";

			retstr = xhtml.substring(xstart + 1, xend);

			//xcont = xend + x2tag.length();

		

		return retstr;
	}

	public String GetXmlInnerNr(String xtag, String Attrib, String xhtml,
			int pos)
	{

		String x1tag;
		int x1len;
		String x2tag;
		String retstr;
		int xstart;
		int xend;
		int xcont = 0;
		int xcounter = 0;

		do
		{
			xcounter++;
			xhtml = xhtml.substring(xcont);
			// Log.i(TAG, "* xhtml: " + xhtml);
			x1tag = "<" + xtag;
			xstart = xhtml.indexOf(x1tag);
			if (xstart < 0)
				return "";
			xcont = xstart+1;
		} while (xcounter < pos);
		
			x1len = x1tag.length();
			x2tag = ">";		

			xend = xhtml.indexOf(x2tag, xstart);
			if (xend < 0)
				return "";

			String Innerhtml = xhtml.substring(xstart + x1len, xend);

			// now search in the innerarea
			x1tag = Attrib + "=\"";
			x2tag = "\"";
			x1len = x1tag.length();
			xstart = Innerhtml.indexOf(x1tag);

			if (xstart < 0)
				return "";

			xend = Innerhtml.indexOf(x2tag, xstart + x1len);
			if (xend < 0)
				return "";
			retstr = Innerhtml.substring(xstart + x1len, xend);
			
		return retstr;
	}

}