package net.basilwang.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.basilwang.config.College;
import net.basilwang.config.CurriculumConfig;
import net.basilwang.config.Result;
import net.basilwang.config.SAXParse;
import net.basilwang.config.TAConfiguration;
import net.basilwang.config.UrlMap;
import net.basilwang.entity.Curriculum;
import net.basilwang.enums.TAHelperDownloadPhrase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TAHelper {
	private static TAHelper _helper = new TAHelper();
	private HttpClient client;
	private static TAConfiguration config;
	private GetMethod getMethod;
	private PostMethod postMethod;

	public static void init() {
		MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
		_helper.client = new HttpClient(mgr);
		TAHelper.config = SAXParse.getTAConfiguration();
		_helper.getMethod = new GetMethod(config.getSelectedHost());
		_helper.postMethod = new PostMethod(config.getSelectedHost());
		// 2012-09-26 basilwang to get sessionid
		College college = config.getSelectedCollege();
		if (college.cookieless.equals("yes")
				&& TAContext.Instance().getSessionid() == null) {

			try {
				_helper.setSessionID();
			} catch (SessionIDNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private TAHelper() {
	}

	public static TAHelper Instance() {
		if (config == null) {
			init();
		}
		return _helper;

	}

	public TAResult webRequestFactory(String urlMapKey, NameValuePair[] data) {
		return webRequestFactory(urlMapKey, data, null,
				TAHelperDownloadPhrase.NoPhrase);
	}

	public TAResult webRequestFactory(String urlMapKey, NameValuePair[] data,
			final OnDownloadProgressListener listener,
			TAHelperDownloadPhrase phrase) {
		UrlMap urlMap = config.getUrlMap(urlMapKey);
		int statusCode = 0; // 2012-04-08 basil only use when successType is
							// redirect
		String url = constructUrl(urlMap.getDetail());
		// 2012-04-08 basil can't work without host
		String referer = config.getSelectedHost()
				+ constructUrl(urlMap.getReferer());
		if (urlMap.getRequestType().equals("get")) {
			webRequestGet(url, referer);
		} else if (urlMap.getRequestType().equals("post")) {
			statusCode = webRequestPost(url, data, referer);
		}
		Pattern pattern = getUrlMapPattern(urlMap);
		String targetToBeCompared = null;
		TAResult res = new TAResult();
		Matcher matcher = null;
		if (urlMap.getSuccessType().equals("url")) {
			targetToBeCompared = getMethod.getPath();
			matcher = pattern.matcher(targetToBeCompared);
			res.setOK(matcher.find());
			Log.v("qwewewqewqewqewqewqewqe1", "1");
			res.setSuccessCheckMatcher(matcher);
		} else if (urlMap.getSuccessType().equals("redirect")) {

			Log.v("qwewewqewqewqewqewqewqe1", statusCode + "");
			if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				targetToBeCompared = postMethod.getResponseHeader("location")
						.getValue();
				matcher = pattern.matcher(targetToBeCompared);
				res.setOK(matcher.find());
				res.setSuccessCheckMatcher(matcher);

			} else {
				res.setOK(false);
				Log.v("qwewewqewqewqewqewqewqe2", "2");
			}
		} else if (urlMap.getSuccessType().equals("file")) {
			res.setOK(true);
			try {
				res.setImageContent(getMethod.getResponseBody());
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (urlMap.getSuccessType().equals("content")) {
			Reader reader = null;
			BufferedReader br = null;
			try {
				reader = getReader(urlMap);
				br = new BufferedReader(reader);
				long contentLength;
				// 2012-11-25 basilwang seems to forget to add postMethod
				if (urlMap.getRequestType().equals("post")) {
					contentLength = postMethod.getResponseContentLength();
				} else {
					contentLength = getMethod.getResponseContentLength();
				}

				targetToBeCompared = fetchPageInfo(br, contentLength, listener,
						phrase);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeBufferedReaderAndReader(br, reader);
			}
			setContentResult(urlMap, res, targetToBeCompared);
		}
		releaseConnection(urlMap);
		return res;

	}

	/*------------private methods for webRequestFactory below-----------*/
	private void closeBufferedReaderAndReader(BufferedReader br, Reader reader) {
		try {
			if (br != null) {
				br.close();
			}
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void releaseConnection(UrlMap urlMap) {

		if (urlMap.getRequestType().equals("get")) {
			getMethod.releaseConnection();
		} else {
			postMethod.releaseConnection();
		}
	}

	private String fetchPageInfo(BufferedReader br, long contentLength,
			final OnDownloadProgressListener listener,
			TAHelperDownloadPhrase phrase) throws UnsupportedEncodingException,
			IOException {
		String body = "";
		int readBytes = 0;
		String line = "";
		while ((line = br.readLine()) != null) {
			body += line;
			readBytes += line.getBytes("ISO-8859-2").length + 2; // CRLF
			setProgressBarPercent(readBytes, contentLength, listener, phrase);
		}
		// 2012-11-24 basilwang add 100
		if (listener != null) {
			listener.onDownloadProgress(100, (int) readBytes,
					(int) contentLength, phrase);
		}

		return body;
	}

	private void setProgressBarPercent(int readBytes, long contentLength,
			final OnDownloadProgressListener listener,
			TAHelperDownloadPhrase phrase) {
		int percent = (int) (((float) readBytes / contentLength) * 100);
		// 2012-11-25 basilwang discard some caculation. add percent%5==0
		if (listener != null && percent % 5 == 0) {
			listener.onDownloadProgress(percent, (int) readBytes,
					(int) contentLength, phrase);
		}
	}

	private Reader getReader(UrlMap urlMap)
			throws UnsupportedEncodingException, IOException {
		if (urlMap.getRequestType().equals("get")) {
			return new InputStreamReader(getMethod.getResponseBodyAsStream(),
					getMethod.getResponseCharSet());
		} else
			return new InputStreamReader(postMethod.getResponseBodyAsStream(),
					postMethod.getResponseCharSet());

	}

	/**
	 * Use result nodes in taconfig.xml to put URL content in TAContext or
	 * TAResult's content
	 * 
	 */
	private void setContentResult(UrlMap urlMap, TAResult res,
			String targetToBeCompared) {
		Pattern pattern = getUrlMapPattern(urlMap);
		Matcher matcher = pattern.matcher(targetToBeCompared);

		if (matcher.find()) {
			for (Result result : urlMap.getResults()) {

				int index = Integer.valueOf(result.getIndex());
				if (result.getType().equals("context")) {
					if (urlMap.getResults().size() <= matcher.groupCount()) {
						TAContext.Instance().getContextMap()
								.put(result.getContent(), matcher.group(index));
					}

				} else {
					res.setContent(matcher.group(index));
				}
			}
			res.setOK(true);
		}

	}

	private Pattern getUrlMapPattern(UrlMap urlMap) {
		return Pattern.compile(urlMap.getPattern());
	}

	/*-------private methods for  webRequestFactory above----------*/

	public void setSessionID() throws SessionIDNotFoundException {
		TAResult res = webRequestFactory("getsession", null);
		if (!res.isOK())
			throw new SessionIDNotFoundException("can't get session id");
		TAContext.Instance().setSessionid(res.getSuccessCheckMatcher().group());

	}

	public boolean logOn(String studentNum, String pwd, String checkCode,
			OnDownloadProgressListener listener) {
		TAResult res = null;
		if (TAContext.Instance().getLogonViewStateForPost() == null) {
			Log.v("TAhelper", "1");
			res = webRequestFactory("init", null, listener,
					TAHelperDownloadPhrase.GetLogonViewStateForPostPhrase);

		}
		if (config.getSelectedCollege().hascheckcode.equals("yes")) {
			Log.v("TAhelper", "2.1");
			NameValuePair[] data = {
					new NameValuePair("__VIEWSTATE", TAContext.Instance()
							.getLogonViewStateForPost()),
					new NameValuePair("txtUserName", studentNum),
					new NameValuePair("TextBox2", pwd),
					new NameValuePair("txtSecretCode", checkCode),
					new NameValuePair("RadioButtonList1", "%D1%A7%C9%FA"),
					new NameValuePair("Button1", ""),
					new NameValuePair("lbLanguage", "") };
			res = webRequestFactory("default", data, listener,
					TAHelperDownloadPhrase.LogonPhrase);

		} else {
			NameValuePair[] data = {
					new NameValuePair("__VIEWSTATE", TAContext.Instance()
							.getLogonViewStateForPost()),
					new NameValuePair("txtUserName", studentNum),
					new NameValuePair("TextBox2", pwd),
					new NameValuePair("RadioButtonList1", "%D1%A7%C9%FA"),
					new NameValuePair("Button1", ""),
					new NameValuePair("lbLanguage", "") };
			res = webRequestFactory("default", data, listener,
					TAHelperDownloadPhrase.LogonPhrase);
			Log.v("TAhelper", "2.2");
		}
		if (TAContext.Instance().getGNMKDM() == null) {
			TAContext.Instance().setGNMKDM(
					config.getSelectedCollege().getGnmkdm());
			Log.v("TAhelper", "3");
		}
		if (res.isOK()) {
			// 2012-04-08 basil set student number to context
			TAContext.Instance().setStudentNum(studentNum);
			res = webRequestFactory("xs_main", null, listener,
					TAHelperDownloadPhrase.GetXSMainPhrase);
			if (res.isOK()) {
				TAContext.Instance().setName(TAContext.Instance().getName());
			}
			Log.v("TAhelper", "4");
		}
		return res.isOK();
	}

	public Bitmap getCheckCode() {
		Bitmap bitmap = null;
		TAResult res = webRequestFactory("checkcode", null, null,
				TAHelperDownloadPhrase.NoPhrase);
		byte[] b = res.getImageContent();
		if (b != null && b.length != 0) {
			bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return bitmap;

	}

	private boolean setScoreViewStateForPost(OnDownloadProgressListener listener) {
		TAResult res = webRequestFactory("score", null, listener,
				TAHelperDownloadPhrase.SetScoreViewStateForPostPhrase);
		if (res.isOK()) {
			TAContext.Instance().setScoreViewStateForPost(
					TAContext.Instance().getScoreViewStateForPost());
			return true;
		} else
			return false;
	}

	public String getScore(OnDownloadProgressListener listener,
			String cemesterYear, String cemesterIndex) {
		String scoreViewStateForPost = TAContext.Instance()
				.getScoreViewStateForPost();
		if (scoreViewStateForPost == null) {
			setScoreViewStateForPost(listener);
		}

		NameValuePair[] data = {
				new NameValuePair("__VIEWSTATE", TAContext.Instance()
						.getScoreViewStateForPost()),
				new NameValuePair("ddlXN", cemesterYear),
				new NameValuePair("ddlXQ", cemesterIndex),
				new NameValuePair("Button1", "%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF") };
		TAResult res = webRequestFactory("scoreagain", data, listener,
				TAHelperDownloadPhrase.GetScorePhrase);
		String scoreStr = res.getContent();
		return scoreStr;
	}

	private boolean setCurriculumViewStateForPost(
			OnDownloadProgressListener listener) {
		TAResult res = webRequestFactory("curriculum", null, listener,
				TAHelperDownloadPhrase.SetCurriculumViewStateForPostPhrase);
		if (res.isOK()) {
			TAContext.Instance().setCurriculumViewStateForPost(
					TAContext.Instance().getCurriculumViewStateForPost());
			TAContext.Instance().setCemesterYear(
					TAContext.Instance().getCemesterYear());
			TAContext.Instance().setCemesterIndex(
					TAContext.Instance().getCemesterIndex());
			TAContext.Instance().setCurrentCurriculumStr(
					TAContext.Instance().getCurrentCurriculumStr());
			return true;
		} else
			return false;
	}

	public String getCurriculumBySemesterIndex(String semesterYear,
			String semesterIndex, OnDownloadProgressListener listener) {
		// 2012-07-10 basilwang don't know why xml change another type dash,
		// fuck!! waste 5 hours !!
		semesterYear = semesterYear.replace('–', '-');
		String curriculumStr = "";
		String curriculumViewStateForPost = TAContext.Instance()
				.getCurriculumViewStateForPost();
		if (curriculumViewStateForPost == null) {
			if (setCurriculumViewStateForPost(listener) == false) {
				return "";
			}
		}
		if (TAContext.Instance().getCemesterYear().equals(semesterYear)
				&& TAContext.Instance().getCemesterIndex()
						.equals(semesterIndex)) {
			curriculumStr = TAContext.Instance().getCurrentCurriculumStr();
		} else {
			// "dDwtMTY3ODA2Njg2OTt0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDI+O2k8ND47aTw3PjtpPDk+O2k8MTE+O2k8MTM+O2k8MTU+O2k8MjE+O2k8MjM+O2k8MjU+O2k8Mjc+O2k8Mjk+O2k8MzE+Oz47bDx0PHA8cDxsPFRleHQ7PjtsPFxlOz4+Oz47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhuO3huOz4+Oz47dDxpPDM+O0A8MjAxMS0yMDEyOzIwMTAtMjAxMTsyMDA5LTIwMTA7PjtAPDIwMTEtMjAxMjsyMDEwLTIwMTE7MjAwOS0yMDEwOz4+O2w8aTwwPjs+Pjs7Pjt0PHQ8OztsPGk8MD47Pj47Oz47dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoyMDA5MDEwMzE2Oz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrmm7nmtKrljYc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou+8mueuoeeQhuenkeWtpuS4juW3peeoi+WtpumZojs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w85LiT5Lia77ya5L+h5oGv566h55CG5LiO5L+h5oGv57O757ufKOeHleWxsSk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOihjOaUv+ePre+8muS/oeaBrzA5MDI7Pj47Pjs7Pjt0PDtsPGk8MT47PjtsPHQ8QDA8Ozs7Ozs7Ozs7Oz47Oz47Pj47dDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtsPGk8MT47PjtsPHQ8QDA8Ozs7Ozs7Ozs7Oz47Oz47Pj47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE+O2k8MD47aTwwPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs+Ozs+O3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs+O2w8aTwxPjtpPDA+O2k8MD47bDw+Oz4+Oz47Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA+O2w8Pjs+Pjs+Ozs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE+O2k8Mz47aTwzPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs+O2w8aTwwPjs+O2w8dDw7bDxpPDE+O2k8Mj47aTwzPjs+O2w8dDw7bDxpPDA+O2k8MT47aTwyPjtpPDM+O2k8ND47PjtsPHQ8cDxwPGw8VGV4dDs+O2w8MjAxMS0yMDEyOz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxOz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDzigJzkuKTor77igJ3lrp7ot7XmlZnlrabkuI7npL7kvJrlrp7ot7VJSTs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8546L5qGC5pyIOz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxLjU7Pj47Pjs7Pjs+Pjt0PDtsPGk8MD47aTwxPjtpPDI+O2k8Mz47aTw0Pjs+O2w8dDxwPHA8bDxUZXh0Oz47bDwyMDExLTIwMTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOeuoeeQhuS/oeaBr+ezu+e7n+ino+WGs+aWueahiOiuvuiuoTs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w85YiY5L2N6b6ZOz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDwyLjA7Pj47Pjs7Pjs+Pjt0PDtsPGk8MD47aTwxPjtpPDI+O2k8Mz47aTw0Pjs+O2w8dDxwPHA8bDxUZXh0Oz47bDwyMDExLTIwMTI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDE7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOW9ouWKv+S4juaUv+etllY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOeOi+abvDs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w8MC41Oz4+Oz47Oz47Pj47Pj47Pj47Pj47Pj47PtbhPek5FtyfdB3kPytRxJ077KVG"),
			// Log.v("net.basilwang",String.valueOf(cemesterYear.equals("2011-2012")));

			NameValuePair[] data = {
					new NameValuePair("__EVENTTARGET", "xqd"),
					new NameValuePair("__EVENTARGUMENT", ""),
					new NameValuePair("__VIEWSTATE", TAContext.Instance()
							.getCurriculumViewStateForPost()),
					new NameValuePair("xnd", semesterYear),
					new NameValuePair("xqd", semesterIndex) };
			TAResult res = webRequestFactory("curriculumagain", data, listener,
					TAHelperDownloadPhrase.GetCurriculumByCemesterIndexPhrase);
			curriculumStr = res.getContent();
		}
		return curriculumStr;
	}

	private String constructUrl(String oldurl) {
		Pattern pattern = Pattern.compile("\\{\\w*\\}");
		Matcher matcher;
		while ((matcher = pattern.matcher(oldurl)).find()) {
			String t = matcher.group(0).replace("{", "").replace("}", "");
			String replacement = TAContext.Instance().getContextMap().get(t);
			oldurl = matcher.replaceFirst(replacement);
		}
		String sessionid = TAContext.Instance().getSessionid();
		sessionid = (sessionid == null) ? "" : sessionid + "/";

		return "/" + sessionid + oldurl;
	}

	private void webRequestGet(String url, String referer) {
		getMethod = new GetMethod(config.getSelectedHost());
		getMethod.setPath(url);
		getMethod.setRequestHeader("Referer", referer);
		try {
			client.executeMethod(getMethod);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// getMethod.releaseConnection();
	}

	private int webRequestPost(String url, NameValuePair[] data, String referer) {
		int statusCode = 0;
		postMethod = new PostMethod(config.getSelectedHost());
		postMethod.setPath(url);
		postMethod.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");

		// if(url.indexOf("xskbcx")!=-1)
		// {
		// String mydata="";
		// for(NameValuePair d : data)
		// {
		// if(d.getName().equals("__VIEWSTATE"))
		// {
		// d.setValue(URLEncoder.encode(d.getValue()));
		// }
		// mydata+=d.getName()+"="+d.getValue()+"&";
		// }
		// mydata=mydata.substring(0, mydata.length()-1);
		//
		// postMethod.setRequestBody(mydata);
		//
		// }
		// else
		// {
		// postMethod.setRequestBody(data);
		// }
		// if(url.indexOf("xskbcx")!=-1)
		// {
		// String
		// mydata="__EVENTTARGET=xnd&__EVENTARGUMENT=&__VIEWSTATE=dDwtMTY3ODA2Njg2OTt0PDtsPGk8MT47PjtsPHQ8O2w8aTwxPjtpPDI%2BO2k8ND47aTw3PjtpPDk%2BO2k8MTE%2BO2k8MTM%2BO2k8MTU%2BO2k8MjE%2BO2k8MjM%2BO2k8MjU%2BO2k8Mjc%2BO2k8Mjk%2BO2k8MzE%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPFxlOz4%2BOz47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhuO3huOz4%2BOz47dDxpPDQ%2BO0A8MjAxMi0yMDEzOzIwMTEtMjAxMjsyMDEwLTIwMTE7MjAwOS0yMDEwOz47QDwyMDEyLTIwMTM7MjAxMS0yMDEyOzIwMTAtMjAxMTsyMDA5LTIwMTA7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDw7O2w8aTwwPjs%2BPjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpuWPt%2B%2B8mjIwMDkwMTAzMTY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWnk%2BWQje%2B8muabuea0quWNhzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85a2m6Zmi77ya566h55CG56eR5a2m5LiO5bel56iL5a2m6ZmiOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzkuJPkuJrvvJrkv6Hmga%2FnrqHnkIbkuI7kv6Hmga%2Fns7vnu58o54eV5bGxKTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w86KGM5pS%2F54%2Bt77ya5L%2Bh5oGvMDkwMjs%2BPjs%2BOzs%2BO3Q8O2w8aTwxPjs%2BO2w8dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwxPjs%2BO2w8dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MT47aTwxPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDE%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0PjtpPDU%2BO2k8Nj47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w84oCc5Lik6K%2B%2B4oCd5a6e6Le15pWZ5a2m5LiO56S%2B5Lya5a6e6Le1SUlJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlhq%2FpgZPmnbA7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEuNTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MDItMjA7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDk5MDAxMTs%2BPjs%2BOzs%2BOz4%2BOz4%2BOz4%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwxPjtpPDE%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47bDxpPDA%2BOz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BOz47bDx0PHA8cDxsPFRleHQ7PjtsPDIwMTItMjAxMzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w84oCc5Lik6K%2B%2B4oCd5a6e6Le15pWZ5a2m5LiO56S%2B5Lya5a6e6Le1SUlJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlhq%2FpgZPmnbA7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEuNTs%2BPjs%2BOzs%2BOz4%2BOz4%2BOz4%2BOz4%2BOz4%2BOz4GymO2omFGZmZh%2B2q71tYkO6VNYw%3D%3D&xnd=2011-2012&xqd=2";
		//
		//
		// postMethod.addRequestHeader("Content-Length",
		// String.valueOf(mydata.length()));
		// postMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
		// //postMethod.setRequestBody(mydata);
		// postMethod.setRequestBody(mydata);
		//
		//
		// }
		// else
		// {
		// //postMethod.removeRequestHeader("Referer");
		// postMethod.setRequestBody(data);
		// }
		// // post.addParameter(__VIEWSTATE);
		// // post.setFollowRedirects(true);
		//
		postMethod.removeRequestHeader("Content-Length");
		// postMethod.addRequestHeader("Content-Type","application/x-www-form-urlencoded");
		// postMethod.addRequestHeader("Content-Length",
		// String.valueOf(mydata.length()));
		// postMethod.setRequestBody(mydata);
		postMethod.setRequestBody(data);
		postMethod.removeRequestHeader("Referer");
		postMethod.addRequestHeader("Referer", referer);
		try {
			statusCode = client.executeMethod(postMethod);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// postMethod.releaseConnection();
		return statusCode;

	}

	public Curriculum[] getCurriculums(String str) {
		return new ParseCurriculumInfo(getCurriculumConfig())
				.getCurriculums(str);
	}

	private CurriculumConfig getCurriculumConfig() {
		return config.getSelectedCollege().getCurriculumConfig();
	}
}