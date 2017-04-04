package QuickEcoLife.util;

import javafx.scene.image.ImageView;

public class PreviewImage {
	private ImageView imageView;
	private boolean dirtyImage;
	private PreviewImageCombo previewImageCombo;
	
	public PreviewImage(ImageView imageView, boolean dirtyImage) {
		this.imageView = imageView;
		this.dirtyImage = dirtyImage;
		previewImageCombo = null;
	}
	
	public ImageView getImageView() {
		return imageView;
	}
	
	public boolean isDirtyImage() {
		return dirtyImage;
	}
	
	public void setPointedPreviewImageCombo(PreviewImageCombo previewImageCombo) {
		this.previewImageCombo = previewImageCombo;
	}
	
	public PreviewImageCombo getPointedPreviewImageCombo() {
		return previewImageCombo;
	}
	
	public boolean isNotPointedToPreviewImageCombo() {
		return previewImageCombo == null;
	}
}
