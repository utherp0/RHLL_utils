package org.rhll.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rhll.utils.sax.ODPSaxParser;

import com.logica.lamplight.parsers.IParser;
import com.logica.lamplight.parsers.utils.TextHandler;

/**
 * Basic ODP parser - this works by extracting two files from the archive (ODP file),
 * meta.xml and content.xml. The content.xml contains all textual information (which is
 * extracted using the {@link org.rhll.utils.sax.ODPSaxParser org.rhll.utils.sax.ODPSaxParser}.
 * @author Ian Lawson
 *
 */
public class BasicODPParser implements IParser
{
  private Map<String,String> _additionalComponents = null;
  
  private ArrayList<String> performContentSAXParse( InputStream inputStream ) throws IOException, SAXException
  {
    ArrayList<String> workingTokens = new ArrayList<String>();
    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();
    ODPSaxParser handler = new ODPSaxParser();
    saxParser.parse(inputStream, handler);

    String[] components = handler.getTextualContent().split( "[ ]");

    for( String component : components )
    {
      if( component.length() > 1 )
      {
        workingTokens.add( component );
      }
      else if( TextHandler.isValidWord(component))
      {
        workingTokens.add( component );
      }
    }
    
    return workingTokens;    
  }

  @Override
  public List<String> parse(InputStream inputStream) throws IOException
  {
    // Initialise the additionalComponents
    _additionalComponents = new HashMap<String,String>();
    
    List<String> workingTokens = new ArrayList<String>();
    
    // Create a zipinput stream from the given input stream
    ZipInputStream zipReader = new ZipInputStream( inputStream );
    
    boolean contentSatisfied = false;
    boolean metadataSatisfied = false;
    
    ZipEntry entryRead = null;
    
    while( ( entryRead = zipReader.getNextEntry()) != null )
    {
      if( entryRead.getName().endsWith("content.xml"))
      {
        
        contentSatisfied = true;
      }
      else if( entryRead.getName().endsWith("meta.xml"))
      {
        
        metadataSatisfied = true;
      }
    }

    return null;
  }

  @Override
  public List<String> parse(String input)
  {
    // Initialise the additionalComponents
    _additionalComponents = new HashMap<String,String>();

    return null;
  }

  @Override
  public List<String> parse(File file) throws IOException
  {
    // Initialise the additionalComponents
    _additionalComponents = new HashMap<String,String>();

    try
    {
      ZipFile zipFile = new ZipFile( file );
      
      ArrayList<String> tokensDiscovered = null;

      Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zipFile.entries();

      while( entries.hasMoreElements())
      {
        ZipEntry entry = (ZipEntry)entries.nextElement();
        
        // Handle the textual contents
        if( entry.getName().endsWith("content.xml"))
        {
          InputStream xmlInput = zipFile.getInputStream(entry);

          SAXParserFactory factory = SAXParserFactory.newInstance();
          SAXParser saxParser = factory.newSAXParser();
          ODPSaxParser handler = new ODPSaxParser();
          saxParser.parse(xmlInput, handler);

          tokensDiscovered = new ArrayList<String>();

          String[] components = handler.getTextualContent().split( "[ ]");

          for( String component : components )
          {
            if( component.length() > 1 )
            {
              tokensDiscovered.add( component );
            }
            else if( TextHandler.isValidWord(component))
            {
              tokensDiscovered.add( component );
            }
          }
        }
        // Extract the meta-data from the appropriate file
        else if( entry.getName().endsWith("meta.xml"))
        {
          
        }
      }
      
      zipFile.close();

      return tokensDiscovered;
    }
    catch( Exception exc )
    {
      throw new IOException( "Failed to process " + file.getCanonicalPath() + " due to " + exc.toString());
    }
  }

  @Override
  public Map<String, String> additionalComponents()
  {
    // TODO Auto-generated method stub
    return _additionalComponents;
  }

}
