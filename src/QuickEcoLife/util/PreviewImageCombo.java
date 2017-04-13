package QuickEcoLife.util;

public class PreviewImageCombo {
	private ImageCombo imageCombo;
	private PreviewImage dirty_previewImage;
	private PreviewImage clean_previewImage;
	private boolean flag_inspectJournal;
	
	public PreviewImageCombo(PreviewImage dirty_previewImage, PreviewImage clean_previewImage, boolean flag_inspectJournal) {
		this.imageCombo = new ImageCombo();
		this.dirty_previewImage = dirty_previewImage;
		this.clean_previewImage = clean_previewImage;
		this.flag_inspectJournal = flag_inspectJournal;
	}
	
	public ImageCombo getImageCombo() {
		return imageCombo;
	}
	
	public PreviewImage getDirtyPI() {
		return dirty_previewImage;
	}
	
	public PreviewImage getCleanPI() {
		return clean_previewImage;
	}
	
	public boolean isForInspectJournal() {
		return flag_inspectJournal;
	}
}
