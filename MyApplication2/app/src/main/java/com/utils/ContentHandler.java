package com.utils;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by chen on 2016/9/25.
 */

//xml解析句柄
public class ContentHandler extends DefaultHandler

{
    public  static String s ;
    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
    }
    @Override
    public void endDocument() throws SAXException
    {
        super.endDocument();
    }
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, qName, attributes);
    }
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        super.endElement(uri, localName, qName);
    }
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        super.characters(ch, start, length);

        s=String.valueOf(ch);
        Log.v("hehe", s.toString());

    }

}
