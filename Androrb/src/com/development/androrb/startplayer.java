package com.development.androrb;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class startplayer extends Activity
{
	/** Called when the activity is first created. */

	private static final String TAG = "Orband";
	Intent getIntent = getIntent();
	String rurl, sid, Mid, mediakind;
	ImageView splashimage;
	String html;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		splashimage = (ImageView) findViewById(R.id.splashscreen);
		rurl = getIntent().getStringExtra("rurl");
		sid = getIntent().getStringExtra("sid");
		Mid = getIntent().getStringExtra("mid");
		mediakind = getIntent().getStringExtra("mediakind");

		checkUpdate.start();

	}

	private Thread checkUpdate = new Thread()
	{
		public void run()
		{
			try
			{	
				
				
			    
				if ((mediakind.compareTo("video")==0) || (mediakind.compareTo("audio.file")==0) || (mediakind.compareTo("audio.web")==0))
				{
					Log.i(TAG, " *------ VIDEO !!! ----*");
					html = loaddata(rurl);
					int status = Integer.parseInt(GetXmlInnerNr("status","code", html, 1));
					String uril = GetXmlInnerNr("item","url",html,1);
					
					if (status == 0)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						Uri u = Uri.parse(uril);
						i.setData(u);
						//i.setDataAndType(u, "audio/*");
						startActivity(i);
						startplayer.this.finish();
					}

				}
				else
				{
					Log.i(TAG, " *------ PHOTO !!! ----*");
					String uril;
					int status = 0;
					uril = "http://api.orb.com/orb/data/image.jpg?sid=" + sid
							+ "&mediumId=" + Mid
							+ "&maxWidth=480&maxHeight=320";
					
					if (status == 0)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						Uri u = Uri.parse(uril);
						i.setData(u);
						startActivity(i);
						startplayer.this.finish();
					} 
				}

			} catch (Exception e)
			{
			}

		}

	};

	private String loaddata(String Urli)
	{
		try
		{
			// Log.i(TAG, " *------ 3 -----*: ");
			String mediaUrl = Urli;
			URLConnection conn;
			// Log.i(TAG, " *------ 3.1 -----*: "+mediaUrl);
			conn = new URL(mediaUrl).openConnection();
			// Log.i(TAG, " *------ 3.2 -----*: ");
			InputStream is = conn.getInputStream();
			// Log.i(TAG, " *------ 3.3 -----*: ");
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			// Log.i(TAG, " *------ 4 -----*: ");
			// loading part
			int current = 0;
			while ((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}
			// Log.i(TAG, " *------ Load Data done -----*: ");
			html = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			// Log.i(TAG, " *------ 5 -----*: ");
			// ------
		} catch (Exception e)
		{
			Toast.makeText(this, "Shit, Loading Error!", Toast.LENGTH_SHORT)
					.show();
		}

		return html;
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
			x1len = x1tag.length();
			x2tag = ">";

			xstart = xhtml.indexOf(x1tag);
			if (xstart < 0)
				return "";

			xend = xhtml.indexOf(x2tag, xstart);
			if (xend < 0)
				return "";

			// Log.i(TAG, "* xstart: " + xstart + " xend: " + xend);

			String Innerhtml = xhtml.substring(xstart + x1len, xend);

			// now search in the innerarea
			x1tag = Attrib + "=\"";
			x2tag = "\"";
			x1len = x1tag.length();
			xstart = Innerhtml.indexOf(x1tag);

			if (xstart < 0)
				return "";

			xcont = xend + 1;

			xend = Innerhtml.indexOf(x2tag, xstart + x1len);
			if (xend < 0)
				return "";
			retstr = Innerhtml.substring(xstart + x1len, xend);
			;

		} while (xcounter < pos);

		return retstr;
	}

}