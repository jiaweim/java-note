package mjw.woodstox;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: kris
 * Date: 15/09/2012
 * Time: 13:06
 */
public class WSXmlParser {

    private static final Logger logger = Logger.getLogger(WSXmlParser.class.getName());

    //objects...
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String TAG = "tag";
    public static final String TRACK = "track";
    //objects.. end


    XMLInputFactory2 xmlif2 = null;
    int count = 0;

    public int parse(String fileName, XMLInputFactory2 factory2) {
        xmlif2 = factory2;
        logger.info("Starting to parse " + fileName);
        try {
            XMLStreamReader2 reader = (XMLStreamReader2) xmlif2.createXMLStreamReader(fileName, new FileInputStream(fileName));
            int eventType = 0;
            String curElement = "";

            Artist artist = null;
            Album album = null;
            Track track = null;
            Tag tag = null;

            while (reader.hasNext()) {
                eventType = reader.next();
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        curElement = reader.getName().toString();
                        if (ARTIST.equals(curElement)) {
                            artist = new Artist();
                        } else if (ALBUM.equals(curElement)) {
                            album = new Album();
                        } else if (TRACK.equals(curElement)) {
                            track = new Track();
                        } else if (TAG.equals(curElement)) {
                            tag = new Tag();
                        }
                        break;
                    case XMLEvent.CHARACTERS:
                        String content = reader.getText();
                        Object obj = null;
//                        if ((obj = getCurrentlyActiveObject(tag, track, album, artist)) != null)
//                            PropertyUtils.setProperty(obj, curElement, content);
                        break;
                    case XMLEvent.END_ELEMENT:
                        curElement = reader.getName().toString();
                        if (ARTIST.equals(curElement)) {
                            processArtist(artist);
                            artist = null;
                        } else if (ALBUM.equals(curElement)) {
                            artist.addAlbum(album);
                            album = null;
                        } else if (TRACK.equals(curElement)) {
                            album.addTrack(track);
                            track = null;
                        } else if (TAG.equals(curElement)) {
                            track.addTag(tag);
                            tag = null;
                        }
                        break;
                    case XMLEvent.END_DOCUMENT:
                        logger.info("document parsing finishing..");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return count;
    }

    private Object getCurrentlyActiveObject(Tag tag, Track track, Album album, Artist artist) {
        if (tag != null) {
            return tag;
        } else if (track != null) {
            return track;
        } else if (album != null) {
            return album;
        } else if (artist != null) {
            return artist;
        }
        return null;
    }

    private void processArtist(Artist artist) {
        count++;
    }
}