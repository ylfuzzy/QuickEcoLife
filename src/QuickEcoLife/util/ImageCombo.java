package QuickEcoLife.util;

public class ImageCombo {
	private String path_imgDirty = null;
	private String path_imgClean = null;
	
	public ImageCombo() {
		
	}
	
	public ImageCombo(String path_imgDirty, String path_imgClean) {
		this.path_imgDirty = path_imgDirty;
		this.path_imgClean = path_imgClean;
	}
	
	public void setPath_imgDirty(String path_imgDirty) {
		this.path_imgDirty = path_imgDirty;
	}
	
	public void setPath_imgClean(String path_imgClean) {
		this.path_imgClean = path_imgClean;
	}
	
	public String getPath_imgDirty() {
		return path_imgDirty;
	}
	
	public String getPath_imgClean() {
		return path_imgClean;
	}
	
	public boolean isComplete() {
		return (path_imgDirty != null && path_imgClean != null);
	}
}
