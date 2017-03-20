package QuickEcoLife.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class MetadataExtractor {
	public static boolean exifExists(String path_img) {
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(new File(path_img));
			//metadata.containsDirectoryOfType(ExifSubIFDDirectory.class);
			ExifSubIFDDirectory directory
		    		= metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			Date date
		    		= directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, TimeZone.getTimeZone("GMT+8:00"));
			String pattern = "yyyy/M/d/H/m"; // "yyyy/M/d/H/m"
		    SimpleDateFormat formatted_data = new SimpleDateFormat(pattern);   
		    	formatted_data.format(date).split("\\/", -1);
		    	
		    	return true;
		} catch (ImageProcessingException | IOException | NullPointerException e) {
			return false;
		}
	}
	
	public static boolean areOnTheSameDay(String path_dirtyImg, String path_cleanImg) {
		String[] info_dirtyImg = extractImgExif(path_dirtyImg);
		String[] info_cleanImg = extractImgExif(path_cleanImg);
		
		int count = 0;
		for (int i = 0; i < 3; i++) {
			if (info_dirtyImg[i].equals(info_cleanImg[i])) {
				count++;
			}
		}
		
		return (count == 3);
	}
	
	public static String[] extractImgExif(String path_img) {
		Metadata metadata;
		String[] date_info = new String[5];
		try {
			metadata = ImageMetadataReader.readMetadata(new File(path_img));
			ExifSubIFDDirectory directory
			    = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

			// query the tag's value
			Date date
			    = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, TimeZone.getTimeZone("GMT+8:00"));
			String pattern = "yyyy/M/d/H/m"; // "yyyy/M/d/H/m"
		    SimpleDateFormat formatted_data = new SimpleDateFormat(pattern);
		    date_info = formatted_data.format(date).split("\\/", -1);
		    
		    return date_info;
		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static String[] getSuitableTimeRange(String[] dateInfo_dirtyImg, String[] dateInfo_cleanImg) {
		int idx_hour = 3;
		int idx_minute = 4;
		
		int hour_dirtyImg = Integer.parseInt(dateInfo_dirtyImg[idx_hour]);
		int minute_dirtyImg = Integer.parseInt(dateInfo_dirtyImg[idx_minute]);
		
		int hour_cleanImg = Integer.parseInt(dateInfo_cleanImg[idx_hour]);
		int minute_cleanImg = Integer.parseInt(dateInfo_cleanImg[idx_minute]);
		
		int hour_begin = 8;
		int minute_begin = 0;
		int hour_end = 17;
		int minute_end = 30;
		if (Math.min(hour_dirtyImg, hour_cleanImg) < 8) {
			hour_begin = Math.min(hour_dirtyImg, hour_cleanImg);
			minute_begin = Math.min(minute_dirtyImg, minute_cleanImg);
		}
		if (Math.max(hour_dirtyImg, hour_cleanImg) >= 17) {
			hour_end = Math.max(hour_dirtyImg, hour_cleanImg);
			minute_end = Math.max(minute_dirtyImg, minute_cleanImg);
		}
		String[] time_range = {dateInfo_dirtyImg[0], dateInfo_dirtyImg[1], dateInfo_dirtyImg[2],
								String.valueOf(hour_begin), String.valueOf(minute_begin),
									String.valueOf(hour_end), String.valueOf(minute_end)};
		
		return time_range;
	}
}
