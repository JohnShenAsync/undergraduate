package net.basilwang.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParse extends DefaultHandler {
	private static TAConfiguration config;
	private static SAXParse saxParse;
	private UrlMap urlMap;
	private College college;
	private boolean mark = false;// 做college节点是否开始解析的标记
	private String nodeName;// 存放当前遍历的节点名称
	private String parentTagName = "taconfiguration";// 存放当前遍历的节点的父节点名称
	private StringBuffer strBuffer;

	private enum tagName {
		taconfiguration, urlmap, college, result
	};

	public static TAConfiguration getTAConfiguration() {
		if (config == null) {
			config = new TAConfiguration();
			parseStart();
		}
		return config;
	}

	private SAXParse() {
		nodeName = null;
		parentTagName = null;
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		strBuffer = new StringBuffer();
		try {
			switch (tagName.valueOf(qName)) {
			case urlmap:
				urlMap = new UrlMap();
				parentTagName = "urlmap";
				break;
			case college:
				college = new College(config.urlMaps);
				setCollegeBegins(true);
				parentTagName = "college";
				break;
			case result:
				urlMap.setResultNode(attributes.getValue(0),
						attributes.getValue(1));
				nodeName = qName;
				break;
			default:
				break;
			}
		} catch (Exception e) {// 不在枚举的类型之内
			nodeName = qName;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			switch (tagName.valueOf(qName)) {
			case urlmap:
				taconfigOrCollegeAddUrlMapNode(urlMap);
				break;
			case college:
				config.addColleageNode(college);
				parentTagName = "taconfiguration";
			case result:
				urlMap.addResultNode();
			default:
				break;
			}
		} catch (IllegalArgumentException e) {// 处理不存在枚举类型中的节点.
			if (parentTagName == "college") {
				college.addListNode(qName);
			}
			nodeName = null;
			return;
		}
		nodeName = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (nodeName != null) {
			String content = new String(ch, start, length);
			strBuffer.append(content);
			if (parentTagName == "urlmap") {
				urlMap.setProperty(nodeName, strBuffer.toString());
			} else if (parentTagName == "college") {
				college.setProperty(nodeName, strBuffer.toString());
			}
		}
	}

	/**
	 * xml文件解析开始
	 * 
	 * @param saxParse
	 */
	public static void parseStart() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		saxParse = new SAXParse();
		try {
			parser = factory.newSAXParser();
			// InputStream input = this.getClass().getClassLoader()
			// .getResourceAsStream(context.getString(
			// R.xml.taconfig));
			// InputSource input = new InputSource(context.getResources()
			// .openRawResource(R.xml.taconfig));
			InputStream input = saxParse.getClass().getClassLoader()
					.getResourceAsStream("taconfig.xml");
			parser.parse(input, saxParse);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// private methods
	private void taconfigOrCollegeAddUrlMapNode(UrlMap urlMap) {
		if (collegeNotBegins()) {// taconfig增加urlmap节点
			config.addUrlMapNode(urlMap);
		} else {// college增加urlmap节点
			college.addUrlMapNode(urlMap);
			parentTagName = "college";
		}
	}

	private void setCollegeBegins(boolean mark) {
		this.mark = mark;
	}

	private boolean collegeNotBegins() {
		return !mark;
	}
}
