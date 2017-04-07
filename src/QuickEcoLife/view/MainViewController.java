package QuickEcoLife.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.activation.MimetypesFileTypeMap;

import org.openqa.selenium.WebDriverException;

import QuickEcoLife.model.AutomationModel;
import QuickEcoLife.util.ImageCombo;
import QuickEcoLife.util.MetadataExtractor;
import QuickEcoLife.util.PreviewImage;
import QuickEcoLife.util.PreviewImageCombo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainViewController {
	@FXML
	private TextField tf_ID;
	@FXML
	private PasswordField tf_passwd;
	@FXML
	private Button btn_go;
	
	// Test new UI design
	@FXML
	private ImageView imageView_inspect_00;
	@FXML
	private ImageView imageView_inspect_01;
	
	@FXML
	private ImageView imageView_inspect_10;
	@FXML
	private ImageView imageView_inspect_11;
	
	@FXML
	private ImageView imageView_inspect_20;
	@FXML
	private ImageView imageView_inspect_21;
	
	@FXML
	private ImageView imageView_inspect_30;
	@FXML
	private ImageView imageView_inspect_31;
	
	@FXML
	private ImageView imageView_clear_00;
	@FXML
	private ImageView imageView_clear_01;
	
	@FXML
	private ImageView imageView_clear_10;
	@FXML
	private ImageView imageView_clear_11;
	
	@FXML
	private ImageView imageView_clear_20;
	@FXML
	private ImageView imageView_clear_21;
	
	@FXML
	private ImageView imageView_clear_30;
	@FXML
	private ImageView imageView_clear_31;
	
	private PreviewImageCombo[] previewImageCombos;
	
	private boolean fileChooser_available = true;
	
	// Directory of the last selected image
	private File image_directory = null;
	
	// The number of ButteonTextField combos on inspect & clear tap panes.
	// Each tab pane possess 4 combos.
	private final int NUM_PREVIEW_IMAGE = 8;
	
	private final String TITLE_ALERT = "訊息";
	private final String MESSAGE_LOGIN_FAILED = "登入失敗！";
	private final String MESSAGE_TROUBLE_OCCURRED = "發生問題，上傳失敗！";
	private final String MESSAGE_NO_IMG = "沒有照片可以上傳！";
	private final String MESSAGE_EXIF_ERR = "此照片無法使用，因為日期資訊不存在！";
	private final String MESSAGE_FINISHED = "照片上傳完畢！";
	
	// Default ImageView "drop image hear"
	private final Image DROP_IMAGE_HERE = new Image(this.getClass().getResource("img/drop_here.png").toString());
	
	// CSS shadow effect
	private final String CSS_SHADOW = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {		
		// for new UI test
		previewImageCombos = new PreviewImageCombo[NUM_PREVIEW_IMAGE];
		previewImageCombos[0] = new PreviewImageCombo(new PreviewImage(imageView_inspect_00, true), new PreviewImage(imageView_inspect_01, false), true);
		previewImageCombos[1] = new PreviewImageCombo(new PreviewImage(imageView_inspect_10, true), new PreviewImage(imageView_inspect_11, false), true);
		previewImageCombos[2] = new PreviewImageCombo(new PreviewImage(imageView_inspect_20, true), new PreviewImage(imageView_inspect_21, false), true);
		previewImageCombos[3] = new PreviewImageCombo(new PreviewImage(imageView_inspect_30, true), new PreviewImage(imageView_inspect_31, false), true);
		previewImageCombos[4] = new PreviewImageCombo(new PreviewImage(imageView_clear_00, true), new PreviewImage(imageView_clear_01, false), false);
		previewImageCombos[5] = new PreviewImageCombo(new PreviewImage(imageView_clear_10, true), new PreviewImage(imageView_clear_11, false), false);
		previewImageCombos[6] = new PreviewImageCombo(new PreviewImage(imageView_clear_20, true), new PreviewImage(imageView_clear_21, false), false);
		previewImageCombos[7] = new PreviewImageCombo(new PreviewImage(imageView_clear_30, true), new PreviewImage(imageView_clear_31, false), false);
	}
	
	private String getID() {
		return (tf_ID.getText() == null) ? "" : tf_ID.getText();
	}
	
	private String getPasswd() {
		return (tf_passwd.getText() == null) ? "" : tf_passwd.getText();
	}
	
	@FXML
	private void handleButtonGO(ActionEvent e) {
		//AutomationModel automationModel;
		List<ImageCombo> imageCombos_inspect = new ArrayList<ImageCombo>();
		List<ImageCombo> imageCombos_clear = new ArrayList<ImageCombo>();
		
		// Find out the image sets that can be uploaded.
		// Only upload the imageCombos that both dirty and clean images have set up.
		for (int i = 0; i < previewImageCombos.length; i++) {
			if (previewImageCombos[i].getImageCombo().isComplete()) {
				ImageCombo clone_imageCombo = new ImageCombo(previewImageCombos[i].getImageCombo());
				if (previewImageCombos[i].isForInspectJournal()) {
					imageCombos_inspect.add(clone_imageCombo);
				} else {
					imageCombos_clear.add(clone_imageCombo);
				}
				System.out.println("dirty: " + previewImageCombos[i].getImageCombo().getPath_imgDirty());
				System.out.println("clean: " + previewImageCombos[i].getImageCombo().getPath_imgClean());
			}
		}
		
		// Upload the imageCombos which are not empty.
		if (!imageCombos_inspect.isEmpty() || !imageCombos_clear.isEmpty()) {
			// Show an alert for the user to re-confirm
			// whether the images to be uploaded are correct.
			String message_ready2upload
				= String.format("即將上傳：%n巡檢照片：%d\t組%n清理照片：%d\t組",
						imageCombos_inspect.size(), imageCombos_clear.size());
			if (isReadyToUpload(message_ready2upload)) {
				Runnable task = () -> {
					AutomationModel automationModel = new AutomationModel();
					try {
//						AutomationModel automationModel = new AutomationModel();
						if (automationModel.loginSuccessfully(getID(), getPasswd())) {
							// Ready to upload images
							if (!imageCombos_inspect.isEmpty()) {
								automationModel.automateJournalPost(imageCombos_inspect, "inspect");
							}
							if (!imageCombos_clear.isEmpty()) {
								automationModel.automateJournalPost(imageCombos_clear, "clear");
							}
							Platform.runLater(() -> showAlert(MESSAGE_FINISHED));
							
							// Debug info.
							System.out.println("Thread " + Thread.currentThread().getName() + " successfully finished!");
						} else {
							Platform.runLater(() -> showAlert(MESSAGE_LOGIN_FAILED));
						}
					} catch (NullPointerException | WebDriverException ex) {
						System.out.println("Thread: " + Thread.currentThread().getName() + " is quit.");
						automationModel.quitChrome();
						Platform.runLater(() -> showAlert(MESSAGE_TROUBLE_OCCURRED));
						ex.printStackTrace();
					}
				};
				Thread thread = new Thread(task);
				thread.start();
			}
		} else {
			showAlert(MESSAGE_NO_IMG);
			// Debug info
			System.out.println("no image can be uploaded.");
		} 
	}
	
	@FXML
	private void mouseEntered(MouseEvent e) {
		System.out.println("DEBUG: MouseEntered");
		getCurrentPreviewImage(e).getImageView().setStyle(CSS_SHADOW);
	}
	
	@FXML
	private void mouseExited(MouseEvent e) {
		System.out.println("DEBUG: MouseExited");
		getCurrentPreviewImage(e).getImageView().setStyle(null);
	}
	
	@FXML
	private void mouseClicked(MouseEvent e) {
		System.out.println("DEBUG: mouseClicked");
		System.out.println(fileChooser_available);
		if (fileChooser_available) {
			fileChooser_available = false;
			handleImage(getCurrentPreviewImage(e), getImgFileFromFileChooser());
			fileChooser_available = true;
		}
	}
	
	@FXML
	private void dragOver(DragEvent e) {
		System.out.println("DEBUG: dragOver");
		getCurrentPreviewImage(e).getImageView().setStyle(CSS_SHADOW);
		Dragboard db = e.getDragboard();
		if (db.hasFiles() && isImage(db.getFiles().get(0))) {
			e.acceptTransferModes(TransferMode.COPY);
		}
	}
	
	@FXML
	private void dragExited(DragEvent e) {
		System.out.println("DEBUG: dragExited");
		getCurrentPreviewImage(e).getImageView().setStyle(null);
	}
	
	@FXML
	private void dragDropped(DragEvent e) {
		System.out.println("DEBUG: dragDropped");
		Dragboard db = e.getDragboard();
		PreviewImage currentPreviewImage = getCurrentPreviewImage(e);
		try {
	        if (db.hasFiles() && isImage(db.getFiles().get(0))) {
	        		handleImage(currentPreviewImage, db.getFiles().get(0));
	        }
		} catch(IllegalArgumentException exception) {
			System.out.println("cannot be display");
		} finally {
			e.setDropCompleted(true);
			currentPreviewImage.getImageView().setStyle(null);
		}
	}
	
	private boolean isImage(File file) {
		String mimetype= new MimetypesFileTypeMap().getContentType(file);
        String type = mimetype.split("/")[0];
        return type.equals("image");
	}
	
	private void setImageView(ImageView imageView, Image img) {
		imageView.setImage(img);
        imageView.setPreserveRatio(true);
	}
	
	private PreviewImage getCurrentPreviewImage(MouseEvent e) {
		PreviewImage previewImage = null;
		for (int i = 0; i < previewImageCombos.length; i++) {
			if (e.getSource() == previewImageCombos[i].getDirtyPI().getImageView()) {
				previewImage = previewImageCombos[i].getDirtyPI();
				if (previewImage.isNotPointedToPreviewImageCombo()) {
					previewImage.setPointedPreviewImageCombo(previewImageCombos[i]);
				}
				break;
			}
			if (e.getSource() == previewImageCombos[i].getCleanPI().getImageView()) {
				previewImage = previewImageCombos[i].getCleanPI();
				if (previewImage.isNotPointedToPreviewImageCombo()) {
					previewImage.setPointedPreviewImageCombo(previewImageCombos[i]);
				}
				break;
			}
		}
		return previewImage;
	}
	
	private PreviewImage getCurrentPreviewImage(DragEvent e) {
		PreviewImage previewImage = null;
		for (int i = 0; i < previewImageCombos.length; i++) {
			if (e.getSource() == previewImageCombos[i].getDirtyPI().getImageView()) {
				previewImage = previewImageCombos[i].getDirtyPI();
				if (previewImage.isNotPointedToPreviewImageCombo()) {
					previewImage.setPointedPreviewImageCombo(previewImageCombos[i]);
				}
				break;
			}
			if (e.getSource() == previewImageCombos[i].getCleanPI().getImageView()) {
				previewImage = previewImageCombos[i].getCleanPI();
				if (previewImage.isNotPointedToPreviewImageCombo()) {
					previewImage.setPointedPreviewImageCombo(previewImageCombos[i]);
				}
				break;
			}
		}
		return previewImage;
	}
	
	private void handleImage(PreviewImage previewImage, File selectedImg) {
		if (selectedImg != null) {
			String[] info_imgValidity
				= getImgValidity(selectedImg.getAbsolutePath(), previewImage.getPointedPreviewImageCombo().getImageCombo(),
					previewImage.isDirtyImage());
			boolean validImg = Boolean.valueOf(info_imgValidity[0]);
			String message_alert = info_imgValidity[1];
			if (validImg) {
				setUpImgPath(previewImage, selectedImg);
			} else {
				showAlert(message_alert);
			}
		} else {
			setUpImgPath(previewImage, null);
		}
	}
	
	private File getImgFileFromFileChooser() {
		// Modify to make the FileChooser remember the last directory it opened.
		FileChooser fileChooser = new FileChooser();
		if (image_directory != null && image_directory.isDirectory()) {
			fileChooser.setInitialDirectory(image_directory);
		}
		fileChooser.getExtensionFilters().addAll(
	         new ExtensionFilter("Image Files", "*.png", "*.jpg"));
		File selectedImg = fileChooser.showOpenDialog(null);
		if (selectedImg != null) {
			image_directory = selectedImg.getParentFile();
		}
		
		return selectedImg;
	}
	
	private String[] getImgValidity(String path_img, ImageCombo imageCombo, boolean dirtyClicked) {
		String[] info_imgValidity = new String[2];

		// First we need to check whether the image's exif exists.
		if (!MetadataExtractor.exifExists(path_img)) {
			info_imgValidity[0] = String.valueOf(false);
			info_imgValidity[1] = MESSAGE_EXIF_ERR;
			
			return info_imgValidity;
		}
		
		// Now we are sure that exif exists, but we still need to know
		// if the imageCombo is complete, whether both dirty and clean images are on the same day?
		// The EcoLife website accepts only images taken on the same day.
		ImageCombo tmp_ic = new ImageCombo(imageCombo);
		if (dirtyClicked) {
			tmp_ic.setPath_imgDirty(path_img);
		} else {
			tmp_ic.setPath_imgClean(path_img);
		}
		if (tmp_ic.isComplete()) {
			if (MetadataExtractor.areOnTheSameDay(tmp_ic.getPath_imgDirty(), tmp_ic.getPath_imgClean())) {
				info_imgValidity[0] = String.valueOf(true);
			} else {
				String type = (dirtyClicked) ? "沒垃圾" : "有垃圾";
				String message_notSameDay = String.format("此照片無法使用，因為拍攝日期與「%s」的照片不同", type);
				
				info_imgValidity[0] = String.valueOf(false);
				info_imgValidity[1] = message_notSameDay; 
			}
		} else {
			// Since the imageCombo is not complete, there is no need to worry about date problem.
			info_imgValidity[0] = String.valueOf(true);
		}
		
		return info_imgValidity;
	}
	
	private void setUpImgPath(PreviewImage previewImage, File selectedImg) {
		String img_path = null;
		Image image = DROP_IMAGE_HERE;
		if (selectedImg != null) {
			img_path = selectedImg.getAbsolutePath();
			image = new Image(selectedImg.toURI().toString());
		}
		if (previewImage.isDirtyImage()) {
			previewImage.getPointedPreviewImageCombo().getImageCombo().setPath_imgDirty(img_path);
		} else {
			previewImage.getPointedPreviewImageCombo().getImageCombo().setPath_imgClean(img_path);
		}
		setImageView(previewImage.getImageView(), image);
	}
	
	private boolean isReadyToUpload(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		ButtonType btn_ok = new ButtonType("確認");
		ButtonType btn_cancel = new ButtonType("取消");
		alert.getButtonTypes().setAll(btn_ok, btn_cancel);
		alert.setTitle(TITLE_ALERT);
		alert.setHeaderText(null);
		alert.setContentText(message);
		Optional<ButtonType> result = alert.showAndWait();
		
		return (result.get() == btn_ok);
	}
	
	private void showAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(TITLE_ALERT);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
