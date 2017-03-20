package QuickEcoLife.util;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ButtonTextFieldCombo {
	private Button btn_dirty;
	private TextField tf_dirty;
	private Button btn_clean;
	private TextField tf_clean;
	private ImageCombo imageCombo;
	private boolean flag_inspectJournal;
	
	public ButtonTextFieldCombo(Button btn_dirty, TextField tf_dirty, Button btn_clean, TextField tf_clean,
			boolean flag_inspectJournal) {
		imageCombo = new ImageCombo();
		this.btn_dirty = btn_dirty;
		this.tf_dirty = tf_dirty;
		this.btn_clean = btn_clean;
		this.tf_clean = tf_clean;
		this.flag_inspectJournal = flag_inspectJournal;
	}
	
	public boolean isForInspectJournal() {
		return flag_inspectJournal;
	}
	
	public ImageCombo getImageCombo() {
		return imageCombo;
	}
	
	public Button getDirtyBtn() {
		return btn_dirty;
	}
	
	public Button getCleanBtn() {
		return btn_clean;
	}
	
	public TextField getDirtyTF() {
		return tf_dirty;
	}
	
	public TextField getCleanTF() {
		return tf_clean;
	}
}
