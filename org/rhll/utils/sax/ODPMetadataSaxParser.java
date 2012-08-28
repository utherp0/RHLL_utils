package org.rhll.utils.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ODPMetadataSaxParser extends DefaultHandler
{
  private StringBuffer _textualContent = new StringBuffer();
  private String _content = null;
  
  public String getTextualContent()
  {
    return _textualContent.toString();
  }
  
	@Override
	public void startDocument() throws SAXException
	{
	}

	@Override
	public void endDocument() throws SAXException
	{
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
	  if( "text:p".equals( qName ) )
	  {
	    _textualContent.append( _content );
	    _textualContent.append( " " );
	    
	    _content = null;
	  }
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException
	{
	  if( _content == null )
	  {
	    _content = new String(ch, start, length );
	  }
	  else
	  {
	    _content = _content + " " + new String( ch, start, length );
	  }
	}
}
